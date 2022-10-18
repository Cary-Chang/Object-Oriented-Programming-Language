package GUI;

import java.awt.Container;
import java.io.FileReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DiscountTimeTableEnquiry extends Enquiry {
	private String date;
	private int time;
	private int type;					// 0: 用出發時間查詢, 1: 用抵達時間查詢
	private String start;
	private String end;
	private int direction;				// 0: southbound, 1: northbound
	private String dayOfWeek;
	private Object[][] trainsINFO;
	private final static Map<String, Integer> stations = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
	{      
	    put("南港", 990);put("台北", 1000);put("板橋", 1010);put("桃園", 1020);put("新竹", 1030);put("苗栗", 1035);
	    put("台中", 1040);put("彰化", 1043);put("雲林", 1047);put("嘉義", 1050);put("台南", 1060);put("左營", 1070);  
	}}; 
	
	public DiscountTimeTableEnquiry() {}

	public DiscountTimeTableEnquiry(String date, int type, String time, String start, String end) {
		this.date = date;
		this.type = type;
		this.time = Integer.valueOf(time.replace(":", ""));
		this.start = start;
		this.end = end;
		if (stations.get(start) - stations.get(end) < 1)
			this.direction = 0;
		else
			this.direction = 1;
		// 用日期找出是星期幾
		LocalDate localDate = LocalDate.of(2021, Integer.valueOf(date) / 100, Integer.valueOf(date) % 100);
        this.dayOfWeek = localDate.getDayOfWeek().toString();
        this.dayOfWeek = this.dayOfWeek.substring(0, 1) + this.dayOfWeek.substring(1, this.dayOfWeek.length()).toLowerCase();
	}
	
	/**
	 * Subtract two given times
	 * @param start
	 * @param end
	 * @return The result of subtraction
	 */
	private String subtractTime(String start, String end){
		// Custom date format
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");  
        end = end.replace("00:", "24:");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(start);
            d2 = format.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }            
        // Get msec from each, and subtract.
        long diff = (d2.getTime() - d1.getTime()) / (60 * 1000);       
        long diffMinutes = diff % 60;         
        long diffHours = diff / 60; 
        return String.format("%d hour(s) %d minute(s)", diffHours, diffMinutes);
	}
	
	/**
	 * Get the matching data from database
	 * @return ArrayList
	 */
	private ArrayList<Object[]> getDataOfDB() {
		ArrayList<Object[]> trainsList = new ArrayList<Object[]>();
		try {
			String sql = "";
			if (type == 0)
				sql = String.format(
					"SELECT 車次,日期,%s,%s FROM trains WHERE 日期 = %s AND 方向 = %d AND %s >= %d AND %s != -1",
					start, end, date, direction, start, time, end);
			else
				sql = String.format(
						"SELECT 車次,日期,%s,%s FROM trains WHERE 日期 = %s AND 方向 = %d AND %s != -1 AND (%s >= %d OR %s < 100) AND %s != -1",
						start, end, date, direction, start, end, time, end, end);

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				Object[] temp = new Object[6];
				temp[0] = rs.getInt("車次");
				temp[1] = String.valueOf(rs.getInt("日期") / 100) + "/" + String.valueOf(rs.getInt("日期") % 100);
				temp[2] = String.format("%02d", rs.getInt(start) / 100) + ":"
								+ String.format("%02d", rs.getInt(start) % 100);
				temp[3] = String.format("%02d", rs.getInt(end) / 100) + ":"
								+ String.format("%02d", rs.getInt(end) % 100);
				temp[4] = subtractTime((String) temp[2], (String) temp[3]);
				temp[5] = "";
				trainsList.add(temp);
			}
		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			try {
				rs.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
		}
		return trainsList;		
	}
	
	/**
	 * Get the matching data from earlyDiscount.json
	 * @return ArrayList
	 */
	private ArrayList<Object[]> getDataOfEarly() {
		ArrayList<Object[]> earlyList = new ArrayList<Object[]>();
		try (FileReader reader = new FileReader("data/earlyDiscount.json"))
        {
        	JSONArray List = (JSONArray) (new JSONObject(new JSONTokener(reader))).get("DiscountTrains");
            
            for (Object o1 : List) {
            	JSONObject t1 = (JSONObject) o1;            	
            	try {
            		// 確認該班次在本日有沒有早鳥優惠
            		((JSONObject) t1.get("ServiceDayDiscount")).getInt(dayOfWeek);
            	} catch(Exception e) {
            		Object[] temp = new Object[2];
            		temp[0] = Integer.valueOf((String) t1.get("TrainNo"));
            		temp[1] = "早鳥優惠";
            		for (Object o2 : (JSONArray) ((JSONObject) t1.get("ServiceDayDiscount")).get(dayOfWeek)) {
            			JSONObject t2 = (JSONObject) o2;
            			if (t2.getFloat("discount") == 0.65f) 
            				temp[1] += " 65折";
            			else if (t2.getFloat("discount") == 0.8f)
            				temp[1] += " 8折";
            			else if (t2.getFloat("discount") == 0.9f)
            				temp[1] += " 9折";
            		}
            		earlyList.add(temp);
            	}            	
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
		return earlyList;
	}
	
	/**
	 * Get the matching data from universityDiscount.json
	 * @return ArrayList
	 */
	private ArrayList<Object[]> getDataOfUniversity() {
		ArrayList<Object[]> universityList = new ArrayList<Object[]>();
		try (FileReader reader = new FileReader("data/universityDiscount.json"))
        {
			JSONArray List = (JSONArray) (new JSONObject(new JSONTokener(reader))).get("DiscountTrains");
			for (Object o1 : List) {
				JSONObject t1 = (JSONObject) o1;
				float i = ((JSONObject) t1.get("ServiceDayDiscount")).getFloat(dayOfWeek);
				if (i != 0f & i != 1f) {
					Object[] temp = new Object[2];
					temp[0] = Integer.valueOf((String) t1.get("TrainNo"));
					temp[1] = "大學生優惠";
					if (i == 0.5f)
						temp[1] += " 5折";
					else if (i == 0.75f)
						temp[1] += " 75折";
					else if (i == 0.88f)
						temp[1] += " 88折";
					universityList.add(temp);
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
		return universityList;
	}
	
	/**
	 * Find the set of trainsList, earlyList, and universityList, and store this set into trainsINFO
	 */
	@Override
	protected void getINFO() {
		// Comparator of list
		class listSort implements Comparator<Object[]> {
			// Sort by departure time
			@Override
			public int compare(Object[] o1, Object[] o2) {
				int a = Integer.valueOf(((String) o1[2]).replace(":", ""));
				int b = Integer.valueOf(((String) o2[2]).replace(":", ""));
				return a - b;
			}

		}
		ArrayList<Object[]> trainsList = getDataOfDB();
		ArrayList<Object[]> earlyList = getDataOfEarly();
		ArrayList<Object[]> universityList = getDataOfUniversity();
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		
		// Use linear search to find the set
		for (Object[] obj1 : trainsList) {
			for (Object[] obj2 : earlyList) {
				if ((int)obj1[0] == (int)obj2[0]) {
					obj1[5] += (String) obj2[1];
					list.add(obj1);
					break;
				}
			}
			for (Object[] obj2 : universityList) {
				if ((int)obj1[0] == (int)obj2[0]) {
					if ((int) list.get(list.size() - 1)[0] == (int) obj1[0]) {
						obj1[5] += "    " + (String) obj2[1];
						list.get(list.size() - 1)[5] = obj1[5];
					}
					else {
						obj1[5] += (String) obj2[1];
						list.add(obj1);
					}
					break;
				}
			}
		}
		Collections.sort(list, new listSort());
		trainsINFO = listToArray(list, 6);
	}
	
	/**
	 * Show the discount trains by using JTable
	 */
	@Override
	public void show() {
		connectDB("trainDatabase/TrainsINFO.db");
		getINFO();
		// 查無資料直接顯示訊息
		if (trainsINFO.length == 0) {
			// System.out.println("查無資料");
			JOptionPane.showMessageDialog(null, "查無資料");
			return;
		}

		JFrame frame = new JFrame(date + " 的優惠車次");
		frame.setSize(1500, 800);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		String[] title = {"車次", "日期", "起站: " + start, "迄站: " + end, "乘車時間", "適用優惠"};
		JTable table = new JTable(trainsINFO, title);
		//Aligning the table data centrally.
		DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
		tableRenderer.setHorizontalAlignment(JLabel.CENTER); 
		table.setDefaultRenderer(Object.class, tableRenderer);
		table.getTableHeader().setDefaultRenderer(tableRenderer);
		
		contentPane.add(new JScrollPane(table));
		frame.setVisible(true);
	}
}
