package GUI;

import java.awt.Container;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JOptionPane;
public class BookingHistoryEnquiry extends Enquiry {
	private String uid;
	private String id;
	private int type;			// 0: 普通艙, 1: 商務艙
	private String errorMessage;
	private Object[] INFOArray;
	private ArrayList<String> title2;
	private ArrayList<String> title3;
	private static final String[] seatID = {"A", "B", "C", "D", "E"};
	
	public BookingHistoryEnquiry() {}
	
	public BookingHistoryEnquiry(String uid, String id) {
		this.uid = uid;
		this.id = id;
	}
	
	/**
	 * 得到打折形式
	 * @param discount
	 * @return String (早鳥、大學生、一般票)
	 */
	private String getDiscountFormat(float discount) {
		String str = null;
		switch ((int) (discount * 100)) {
            case 30:
				str = "愛心票";
				break;
            case 45:
				str = "敬老票";
				break;
			case 50:
				str = "大學生 5折";
				break;
            case 60:
				str = "兒童票";
				break;
			case 75:
				str = "大學生 75折";
				break;
			case 88:
				str = "大學生 88折";
				break;
			case 65:
				str = "早鳥 65折";
				break;
			case 80:
				str = "早鳥 8折";
				break;
			case 90:
				str = "早鳥 9折";
				break;
			default:
				str = "一般票";
				break;
			}
		return str;
	}
	
	/**
	 * Get specific booking history from database
	 */
	@Override
	protected void getINFO() {
		try {
			// 先檢查身份識別號碼是否正確
			String sql = "SELECT * FROM tickets WHERE UID = '" + uid + "'";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if (!rs.next()) {
				errorMessage = "您輸入的身份識別號碼有誤，請重新輸入";
				return;
			}			
			
			sql = "SELECT * FROM tickets WHERE 訂單編號 = " + id + " AND UID = '" + uid + "'";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				ArrayList<Object> temp1 = new ArrayList<Object>();
				ArrayList<Object> temp2 = new ArrayList<Object>();
				ArrayList<Object> temp3 = new ArrayList<Object>();
				INFOArray = new Object[3];
				type = (rs.getInt("座位1") / 100) != 6 ? 0 : 1;
				// temp1儲存訂單編號、UID、車廂種類、總金額
				temp1.add(String.format("%05d", rs.getInt("訂單編號")));
				temp1.add(rs.getString("UID"));				
				if (type == 0)
					temp1.add("標準車廂");
				else
					temp1.add("商務車廂");
				temp1.add(rs.getInt("總金額"));	
				temp1.add("2021/" + String.valueOf(rs.getInt("付款期限") / 100) + "/" + String.valueOf(rs.getInt("付款期限") % 100));
				INFOArray[0] = listToArray(temp1);
				
				// temp2儲存去程的車票資料
				// title2為去程資料表格標頭(因為長度不固定所以用list新增)
				title2 = new ArrayList<String>();
				temp2.add("");
				title2.add("去程:");
				temp2.add(String.valueOf(rs.getInt("日期") / 100) + "/" + String.valueOf(rs.getInt("日期") % 100));
				title2.add("日期");
				temp2.add(rs.getInt("車次"));
				title2.add("車次");
				temp2.add(rs.getString("起始站"));
				title2.add("起始站");
				temp2.add(rs.getString("終點站"));
				title2.add("終點站");
				temp2.add(String.format("%02d", rs.getInt("出發時間") / 100) + ":"
						+ String.format("%02d", rs.getInt("出發時間") % 100));
				title2.add("出發時間");
				temp2.add(String.format("%02d", rs.getInt("抵達時間") / 100) + ":"
						+ String.format("%02d", rs.getInt("抵達時間") % 100));
				title2.add("抵達時間");
				temp2.add(rs.getInt("座位數"));
				title2.add("座位數");
				temp2.add((rs.getInt("座位1") / 100) + "車 " + (((rs.getInt("座位1") % 100)) / 5 + 1)
						+ seatID[((rs.getInt("座位1") % 100)) % 5]);
				title2.add("座位1");
				for (int i = 2; i < 11; ++i) {
					if (rs.getObject("座位" + i) != null) {
						temp2.add((rs.getInt("座位" + i) / 100) + "車 " + (((rs.getInt("座位" + i) % 100)) / 5 + 1)
								+ seatID[((rs.getInt("座位" + i) % 100)) % 5]);
						title2.add("座位" + i);
					} else
						break;
				}
				// 取得票種優惠
				temp2.add(getDiscountFormat(rs.getFloat("折扣形式")));
				title2.add("優惠");
				INFOArray[1] = listToArray(temp2);
				
				// 檢查是否有訂購回程票
				if (rs.getObject("回程日期") == null)
					break;
				// temp3儲存回程的車票資料
				// title3為回程資料表格標頭(因為長度不固定所以用list新增)
				title3 = new ArrayList<String>();
				temp3.add("");
				title3.add("回程:");
				temp3.add(String.valueOf(rs.getInt("回程日期") / 100) + "/" + String.valueOf(rs.getInt("回程日期") % 100));
				title3.add("日期");
				temp3.add(rs.getInt("回程車次"));
				title3.add("車次");
				temp3.add(rs.getString("回程起始站"));
				title3.add("起始站");
				temp3.add(rs.getString("回程終點站"));
				title3.add("終點站");
				temp3.add(String.format("%02d", rs.getInt("回程出發時間") / 100) + ":"
						+ String.format("%02d", rs.getInt("回程出發時間") % 100));
				title3.add("出發時間");
				temp3.add(String.format("%02d", rs.getInt("回程抵達時間") / 100) + ":"
						+ String.format("%02d", rs.getInt("回程抵達時間") % 100));
				title3.add("抵達時間");
				temp3.add(rs.getInt("回程座位數"));
				title3.add("座位數");
				temp3.add((rs.getInt("回程座位1") / 100) + "車 " + (((rs.getInt("回程座位1") % 100)) / 5 + 1)
						+ seatID[((rs.getInt("回程座位1") % 100)) % 5]);
				title3.add("座位1");
				for (int i = 2; i < 11; ++i) {
					if (rs.getObject("回程座位" + i) != null) {
						temp3.add((rs.getInt("回程座位" + i) / 100) + "車 " + (((rs.getInt("回程座位" + i) % 100)) / 5 + 1)
								+ seatID[((rs.getInt("回程座位" + i) % 100)) % 5]);
						title3.add("座位" + i);
					} else
						break;
				}
				temp3.add(getDiscountFormat(rs.getFloat("回程折扣形式")));
				title3.add("優惠");
				INFOArray[2] = listToArray(temp3);
			}
			// 如果身份識別號碼正確而訂位代號不正確
			if (INFOArray == null && errorMessage == null)
				errorMessage = "您輸入的訂位代號有誤，請重新輸入";
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
	}
	
	/**
	 * Use GUI JTable show the corresponding booking history
	 */
	@Override
	public void show() {
		connectDB("ticketInfo/TicketINFO.db");
		getINFO();
		// 如果沒查到資料，則印出錯誤訊息
		if (INFOArray == null) {
			// System.out.println(errorMessage);
			JOptionPane.showMessageDialog(null, errorMessage);
			return;
		}

		// 利用JTable將結果用表格的方式呈現
		JFrame frame = new JFrame("訂票紀錄");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		//Aligning the table data centrally.
		DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
		tableRenderer.setHorizontalAlignment(JLabel.CENTER); 
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		String[] title1 = {"訂單編號", "取票識別碼", "車廂", "總金額", "付款期限"};
		JTable table1 = new JTable((Object[][]) INFOArray[0], title1);			
		table1.setDefaultRenderer(Object.class, tableRenderer);		
		table1.getTableHeader().setDefaultRenderer(tableRenderer);		
		p.add(table1.getTableHeader());
		p.add(table1);
		
		String[] t2 = new String[title2.size()];
		t2 = title2.toArray(t2);
		JTable table2 = new JTable((Object[][]) INFOArray[1], t2);		
		table2.setDefaultRenderer(Object.class, tableRenderer);	
		table2.getTableHeader().setDefaultRenderer(tableRenderer);
		p.add(table2.getTableHeader());
		p.add(table2);
		
		if (INFOArray[2] != null) {
			String[] t3 = new String[title3.size()];
			t3 = title3.toArray(t3);
			JTable table3 = new JTable((Object[][]) INFOArray[2], t3);		
			table3.setDefaultRenderer(Object.class, tableRenderer);	
			table3.getTableHeader().setDefaultRenderer(tableRenderer);
			p.add(table3.getTableHeader());
			p.add(table3);
		}
		
		contentPane.add(new JScrollPane(p));
		frame.pack();
		frame.setVisible(true);
	}
}
