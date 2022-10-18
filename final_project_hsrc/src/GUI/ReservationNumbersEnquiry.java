package GUI;

import java.awt.Container;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JOptionPane;
public class ReservationNumbersEnquiry extends Enquiry {
	private String uid;
	private String start;
	private String end;
	private int date;
	private int trainNO;
	private Object[][] reservationNumbersINFO;
	
	public ReservationNumbersEnquiry() {}
	
	public ReservationNumbersEnquiry(String uid, String start, String end, String date, int trainNO) {
		this.uid = uid;
		this.start = start;
		this.end = end;
		this.date = Integer.valueOf(date);
		this.trainNO = trainNO;
	}
	
	/**
	 * Get specific booking history from database
	 */
	@Override
	protected void getINFO() {
		Object[] temp = null;
		try {
			// Use sqlite statement to get satisfied data
			String sql = String.format(
					"SELECT 訂單編號,付款期限 FROM tickets WHERE 日期 = %d AND UID = '%s' AND (起始站 = '%s' OR 回程起始站 = '%s') AND (終點站 = '%s' OR 回程終點站 = '%s') AND 車次 = %d",
					date, uid, start, start, end, end, trainNO);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				temp = new Object[2];
				temp[0] = String.format("%05d", rs.getInt("訂單編號"));
				temp[1] = "2021/" + String.valueOf(rs.getInt("付款期限") / 100) + "/" + String.valueOf(rs.getInt("付款期限") % 100);
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
		if (temp == null)
			return;
		reservationNumbersINFO = new Object[1][temp.length];
		reservationNumbersINFO[0] = temp;
	}
	
	/**
	 * Use GUI JTable show the corresponding reservation numbers
	 */
	public void show() {
		connectDB("ticketInfo/TicketINFO.db");
		getINFO();
		// 如果沒查到資料，則印出錯誤訊息
		if (reservationNumbersINFO == null) {
			// System.out.println("查無記錄");
			JOptionPane.showMessageDialog(null, "查無記錄");
			return;
		}

		// 利用JTable將結果用表格的方式呈現
		JFrame frame = new JFrame("查詢訂位代號");
		frame.setSize(500, 77);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		String[] title = {"訂位代號", "繳款期限"};
		JTable table = new JTable(reservationNumbersINFO, title);
		//Aligning the table data centrally.
		DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
		tableRenderer.setHorizontalAlignment(JLabel.CENTER); 
		table.setDefaultRenderer(Object.class, tableRenderer);
		table.getTableHeader().setDefaultRenderer(tableRenderer);

		contentPane.add(new JScrollPane(table));
		frame.setVisible(true);
	}
}
