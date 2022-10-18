package GUI;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Container;

public class TimeTableEnquiry extends Enquiry {
	private int date;
//	private Connection con;
//	private PreparedStatement ps;
//	private ResultSet rs;
	private Object[][] northboundTrainsINFO;
	private Object[][] southboundTrainsINFO;

	public TimeTableEnquiry() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
		this.date = Integer.valueOf(dateFormat.format(date));
	}

//	public TimeTableEnquiry(String date) {
//		this.date = date;
//	}

//	private void connectDB() {
//		try {
//			Class.forName("org.sqlite.JDBC");
//			con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db"); // connecting to our database
//			System.out.println("Connected!");
//		} catch (ClassNotFoundException | SQLException e) {
//			System.out.println(e + "");
//		}
//	}

	/**
	 * Get today's time table from database
	 */
	@Override
	protected void getINFO() {
		// Comparator of northboundList
		class NorthboundTrainsINFOSort implements Comparator<Object[]> {
			// 利用到達台中的時間排序
			@Override
			public int compare(Object[] o1, Object[] o2) {
				int a = Integer.valueOf(((String) o1[7]).replace(":", ""));
				int b = Integer.valueOf(((String) o2[7]).replace(":", ""));
				if (a < 100)
					a += 2400;
				if (b < 100)
					b += 2400;
				return a - b;
			}
		}
		// Comparator of southboundList
		class SouthboundTrainsINFOSort implements Comparator<Object[]> {
			// 利用到達台中的時間排序
			@Override
			public int compare(Object[] o1, Object[] o2) {
				int a = Integer.valueOf(((String) o1[8]).replace(":", ""));
				int b = Integer.valueOf(((String) o2[8]).replace(":", ""));
				if (a < 100)
					a += 2400;
				if (b < 100)
					b += 2400;
				return a - b;
			}
		}

		ArrayList<Object[]> northboundList = new ArrayList<Object[]>();
		ArrayList<Object[]> southboundList = new ArrayList<Object[]>();

		try {
			String sql = "SELECT * FROM trains WHERE 日期 = " + date;
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				Object[] temp = new Object[14];
				if (rs.getInt("方向") == 0) {
					temp[0] = rs.getInt("車次");
					temp[1] = String.valueOf(rs.getInt("日期") / 100) + "/" + String.valueOf(rs.getInt("日期") % 100);
					temp[2] = (rs.getInt("南港") == -1) ? "-"
							: String.format("%02d", rs.getInt("南港") / 100) + ":"
									+ String.format("%02d", rs.getInt("南港") % 100);
					temp[3] = (rs.getInt("台北") == -1) ? "-"
							: String.format("%02d", rs.getInt("台北") / 100) + ":"
									+ String.format("%02d", rs.getInt("台北") % 100);
					temp[4] = (rs.getInt("板橋") == -1) ? "-"
							: String.format("%02d", rs.getInt("板橋") / 100) + ":"
									+ String.format("%02d", rs.getInt("板橋") % 100);
					temp[5] = (rs.getInt("桃園") == -1) ? "-"
							: String.format("%02d", rs.getInt("桃園") / 100) + ":"
									+ String.format("%02d", rs.getInt("桃園") % 100);
					temp[6] = (rs.getInt("新竹") == -1) ? "-"
							: String.format("%02d", rs.getInt("新竹") / 100) + ":"
									+ String.format("%02d", rs.getInt("新竹") % 100);
					temp[7] = (rs.getInt("苗栗") == -1) ? "-"
							: String.format("%02d", rs.getInt("苗栗") / 100) + ":"
									+ String.format("%02d", rs.getInt("苗栗") % 100);
					temp[8] = (rs.getInt("台中") == -1) ? "-"
							: String.format("%02d", rs.getInt("台中") / 100) + ":"
									+ String.format("%02d", rs.getInt("台中") % 100);
					temp[9] = (rs.getInt("彰化") == -1) ? "-"
							: String.format("%02d", rs.getInt("彰化") / 100) + ":"
									+ String.format("%02d", rs.getInt("彰化") % 100);
					temp[10] = (rs.getInt("雲林") == -1) ? "-"
							: String.format("%02d", rs.getInt("雲林") / 100) + ":"
									+ String.format("%02d", rs.getInt("雲林") % 100);
					temp[11] = (rs.getInt("嘉義") == -1) ? "-"
							: String.format("%02d", rs.getInt("嘉義") / 100) + ":"
									+ String.format("%02d", rs.getInt("嘉義") % 100);
					temp[12] = (rs.getInt("台南") == -1) ? "-"
							: String.format("%02d", rs.getInt("台南") / 100) + ":"
									+ String.format("%02d", rs.getInt("台南") % 100);
					temp[13] = (rs.getInt("左營") == -1) ? "-"
							: String.format("%02d", rs.getInt("左營") / 100) + ":"
									+ String.format("%02d", rs.getInt("左營") % 100);
					southboundList.add(temp);
				} else {
					temp[0] = rs.getInt("車次");
					temp[1] = String.valueOf(rs.getInt("日期") / 100) + "/" + String.valueOf(rs.getInt("日期") % 100);
					temp[2] = (rs.getInt("左營") == -1) ? "-"
							: String.format("%02d", rs.getInt("左營") / 100) + ":"
									+ String.format("%02d", rs.getInt("左營") % 100);
					temp[3] = (rs.getInt("台南") == -1) ? "-"
							: String.format("%02d", rs.getInt("台南") / 100) + ":"
									+ String.format("%02d", rs.getInt("台南") % 100);
					temp[4] = (rs.getInt("嘉義") == -1) ? "-"
							: String.format("%02d", rs.getInt("嘉義") / 100) + ":"
									+ String.format("%02d", rs.getInt("嘉義") % 100);
					temp[5] = (rs.getInt("雲林") == -1) ? "-"
							: String.format("%02d", rs.getInt("雲林") / 100) + ":"
									+ String.format("%02d", rs.getInt("雲林") % 100);
					temp[6] = (rs.getInt("彰化") == -1) ? "-"
							: String.format("%02d", rs.getInt("彰化") / 100) + ":"
									+ String.format("%02d", rs.getInt("彰化") % 100);
					temp[7] = (rs.getInt("台中") == -1) ? "-"
							: String.format("%02d", rs.getInt("台中") / 100) + ":"
									+ String.format("%02d", rs.getInt("台中") % 100);
					temp[8] = (rs.getInt("苗栗") == -1) ? "-"
							: String.format("%02d", rs.getInt("苗栗") / 100) + ":"
									+ String.format("%02d", rs.getInt("苗栗") % 100);
					temp[9] = (rs.getInt("新竹") == -1) ? "-"
							: String.format("%02d", rs.getInt("新竹") / 100) + ":"
									+ String.format("%02d", rs.getInt("新竹") % 100);
					temp[10] = (rs.getInt("桃園") == -1) ? "-"
							: String.format("%02d", rs.getInt("桃園") / 100) + ":"
									+ String.format("%02d", rs.getInt("桃園") % 100);
					temp[11] = (rs.getInt("板橋") == -1) ? "-"
							: String.format("%02d", rs.getInt("板橋") / 100) + ":"
									+ String.format("%02d", rs.getInt("板橋") % 100);
					temp[12] = (rs.getInt("台北") == -1) ? "-"
							: String.format("%02d", rs.getInt("台北") / 100) + ":"
									+ String.format("%02d", rs.getInt("台北") % 100);
					temp[13] = (rs.getInt("南港") == -1) ? "-"
							: String.format("%02d", rs.getInt("南港") / 100) + ":"
									+ String.format("%02d", rs.getInt("南港") % 100);
					northboundList.add(temp);
				}
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
		Collections.sort(southboundList, new SouthboundTrainsINFOSort());
		Collections.sort(northboundList, new NorthboundTrainsINFOSort());
		southboundTrainsINFO = listToArray(southboundList, 14);
		northboundTrainsINFO = listToArray(northboundList, 14);
	}

	/**
	 * Show today's time table
	 */
	@Override
	public void show() {
		connectDB("trainDatabase/TrainsINFO.db");
		getINFO();
		//Aligning the table data centrally.
		DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
		tableRenderer.setHorizontalAlignment(JLabel.CENTER); 
		// 生成南下的時刻表
		JFrame frame_southbound = new JFrame("南下車次: " + date);
		frame_southbound.setSize(1000, 800);
		//frame_southbound.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane_southbound = frame_southbound.getContentPane();
		String[] title_southbound = {"車次", "日期", "南港", "台北", "板橋", "桃園", "新竹", "苗栗", "台中", "彰化", "雲林", "嘉義", "台南",
				"左營"};
		JTable table_southbound = new JTable(southboundTrainsINFO, title_southbound);		
		table_southbound.setDefaultRenderer(Object.class, tableRenderer);
		table_southbound.getTableHeader().setDefaultRenderer(tableRenderer);
		contentPane_southbound.add(new JScrollPane(table_southbound));
		// 生成北上的時刻表
		JFrame frame_northbound = new JFrame("北上車次: " + date);
		frame_northbound.setSize(1000, 800);
		//frame_northbound.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane_northbound = frame_northbound.getContentPane();
		String[] title_northbound = {"車次", "日期", "左營", "台南", "嘉義", "雲林", "彰化", "台中", "苗栗", "新竹", "桃園", "板橋", "台北",
				"南港"};
		JTable table_northbound = new JTable(northboundTrainsINFO, title_northbound);
		table_northbound.setDefaultRenderer(Object.class, tableRenderer);
		table_northbound.getTableHeader().setDefaultRenderer(tableRenderer);
		contentPane_northbound.add(new JScrollPane(table_northbound));

		frame_southbound.setVisible(true);
		frame_northbound.setVisible(true);
	}
}
