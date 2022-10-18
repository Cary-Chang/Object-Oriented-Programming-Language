package GUI;

import java.awt.Container;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class GUI {
	private static JFrame frame;
	/**
	 * Subtract two given times
	 * @param start
	 * @param end
	 * @return The result of subtraction
	 */
	private static String subtractTime(String start, String end){
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
        
        return String.format("%d小時 %d分鐘", diffHours, diffMinutes);
	}
	
	/**
	 * 依據傳入的車次list去生成視窗顯示使用者的搜尋結果
	 * @param date		ex. 626
	 * @param trainList ex. ArrayList<Integer>
	 * @param start		ex. "台北"
	 * @param end		ex. "台中"
	 */
	public static void show(int date, ArrayList<Integer> trainList, String start, String end) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;
		Object[][] trainsINFO = new Object[trainList.size()][5];
		// Connect to the database
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db"); // connecting to our database
			//System.out.println("Connected!");
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e + "");
		}
		for (int i = 0; i < trainList.size(); ++i) {
			trainsINFO[i][0] = trainList.get(i);
			trainsINFO[i][1] = String.valueOf(date / 100) + "/" + String.valueOf(date % 100);
			try {
				sql = "SELECT " + start + "," + end + " FROM trains WHERE 日期 = " + date + " AND 車次 = " + trainList.get(i);
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				trainsINFO[i][2] = String.format("%02d", rs.getInt(start) / 100) + ":"
						+ String.format("%02d", rs.getInt(start) % 100);
				trainsINFO[i][3] = String.format("%02d", rs.getInt(end) / 100) + ":"
						+ String.format("%02d", rs.getInt(end) % 100);
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
			trainsINFO[i][4] = subtractTime((String) trainsINFO[i][2], (String) trainsINFO[i][3]);
		}
		// Close the database
		try {
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
		// Sort trainsINFO by departure time
		Arrays.sort(trainsINFO, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				int a = Integer.valueOf(((String) o1[2]).replace(":", ""));
				int b = Integer.valueOf(((String) o2[2]).replace(":", ""));
				return a - b;
			}			
		});
		// Create JFrame and show the searching results
		frame = new JFrame("查詢結果");
		frame.setSize(500, 800);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		String[] title = {"車次", "日期", "起站: " + start, "迄站: " + end, "乘車時間"};
		JTable table = new JTable(trainsINFO, title);
		//Aligning the table data centrally.
		DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
		tableRenderer.setHorizontalAlignment(JLabel.CENTER); 
		table.setDefaultRenderer(Object.class, tableRenderer);
		table.getTableHeader().setDefaultRenderer(tableRenderer);

		contentPane.add(new JScrollPane(table));		
		frame.setVisible(true);
	}

    public static void disshow() {
        frame.setVisible(false);
        frame.dispose();
    }
}
