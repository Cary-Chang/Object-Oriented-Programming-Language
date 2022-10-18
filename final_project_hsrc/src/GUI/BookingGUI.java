package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Book.*;
public class BookingGUI extends JDialog {
    private static String s = "";
    private static int type = 0;
    private static String str2 = "";
	private final JPanel contentPanel = new JPanel();
	private final static Map<Integer, String> stations = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;
	{      
	    put(0, "南港");put(1, "台北");put(2, "板橋");put(3, "桃園");put(4, "新竹");put(5, "苗栗");
	    put(6, "台中");put(7, "彰化");put(8, "雲林");put(9, "嘉義");put(10, "台南");put(11, "左營");  
	}}; 
	private JTextField textFieldUID;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
		    BookingGUI dialog = new BookingGUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public BookingGUI() {
		setBounds(100, 100, 550, 550);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lbluqul = new JLabel("訂票");
		lbluqul.setFont(new Font("新細明體", Font.PLAIN, 36));
		JLabel lblNewLabel_2 = new JLabel("乘車日期");
		lblNewLabel_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		JComboBox<String> comboBoxDate = new JComboBox<String>();
		
		JLabel lblNewLabel_2_3 = new JLabel("出發站");
		lblNewLabel_2_3.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxStart = new JComboBox<String>();
		
		JLabel lblNewLabel_2_3_1 = new JLabel("抵達站");
		lblNewLabel_2_3_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxEnd = new JComboBox<String>();
		
		JLabel lblNewLabel_2_1 = new JLabel("張數");
		lblNewLabel_2_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<Integer> comboBoxNum = new JComboBox<Integer>();
		
		JLabel lblNewLabel_2_1_1 = new JLabel("票種");
		lblNewLabel_2_1_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxType = new JComboBox<String>();
		
		JLabel lblNewLabel_2_1_2 = new JLabel("單程/來回");
		lblNewLabel_2_1_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JRadioButton rdbtnOneway = new JRadioButton("單程票", true);
		rdbtnOneway.setFont(new Font("新細明體", Font.PLAIN, 16));
				
		JRadioButton rdbtnReturntrip = new JRadioButton("來回票");
		rdbtnReturntrip.setFont(new Font("新細明體", Font.PLAIN, 16));
		
		ButtonGroup buttonGroup_1 = new ButtonGroup();
		buttonGroup_1.add(rdbtnOneway);
		buttonGroup_1.add(rdbtnReturntrip);
		
		JLabel lblNewLabel_2_1_2_1 = new JLabel("靠窗/走道");
		lblNewLabel_2_1_2_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		lblNewLabel_2_1_2_1.setVisible(false);
		
		JRadioButton rdbtnWindow = new JRadioButton("靠窗");
		rdbtnWindow.setFont(new Font("新細明體", Font.PLAIN, 16));
		rdbtnWindow.setVisible(false);
		
		JRadioButton rdbtnAisle = new JRadioButton("走道");
		rdbtnAisle.setFont(new Font("新細明體", Font.PLAIN, 16));
		rdbtnAisle.setVisible(false);		

		JRadioButton rdbtnNone = new JRadioButton("無", true);
		rdbtnNone.setFont(new Font("新細明體", Font.PLAIN, 16));
		rdbtnNone.setVisible(false);
		
		ButtonGroup buttonGroup_2 = new ButtonGroup();
		buttonGroup_2.add(rdbtnWindow);
		buttonGroup_2.add(rdbtnAisle);	
		buttonGroup_2.add(rdbtnNone);

		JLabel lblNewLabel_2_2 = new JLabel("回程日期");
		lblNewLabel_2_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		lblNewLabel_2_2.setVisible(false);
		
		JComboBox<String> comboBoxDate_1 = new JComboBox<String>();
		comboBoxDate_1.setVisible(false);
		
		rdbtnOneway.addActionListener((ActionListener) new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if ((int) comboBoxNum.getSelectedItem() == 1) {
	        		lblNewLabel_2_1_2_1.setVisible(true);
	        		rdbtnAisle.setVisible(true);
	        		rdbtnWindow.setVisible(true);
	        		rdbtnNone.setVisible(true);
	        	}
	        	else {
	        		lblNewLabel_2_1_2_1.setVisible(false);
	        		rdbtnAisle.setVisible(false);
	        		rdbtnWindow.setVisible(false);
	        		rdbtnNone.setVisible(false);
	        	}
	        	lblNewLabel_2_2.setVisible(false);
				comboBoxDate_1.setVisible(false);
	        }
	    });
		
		rdbtnReturntrip.addActionListener((ActionListener) new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lblNewLabel_2_1_2_1.setVisible(false);
				rdbtnAisle.setVisible(false);
				rdbtnWindow.setVisible(false);
				rdbtnNone.setVisible(false);
				String dateStart = String.valueOf(comboBoxDate.getSelectedItem());
				String dateEnd = comboBoxDate.getItemAt(27);
				
				comboBoxDate_1.removeAllItems();
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
				Date date1 = null;
				Date date2 = null;
				try {
					date1 = dateFormat.parse(dateStart);
					date2 = dateFormat.parse(dateEnd);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(date1);
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(date2);
				String a = dateFormat.format(date1);
				String b = dateFormat.format(date2);
				System.out.println(b);
				comboBoxDate_1.addItem(a);
				while (!a.equals(b)) {
					rightNow.add(Calendar.DAY_OF_YEAR,1);
					date1 = rightNow.getTime();
					a = dateFormat.format(date1);
					comboBoxDate_1.addItem(a);
				}

				lblNewLabel_2_2.setVisible(true);
				comboBoxDate_1.setVisible(true);
			}
		});
		
		comboBoxNum.addActionListener((ActionListener) new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if ((int) comboBoxNum.getSelectedItem() == 1) {
	        		lblNewLabel_2_1_2_1.setVisible(true);
	        		rdbtnAisle.setVisible(true);
	        		rdbtnWindow.setVisible(true);
	        		rdbtnNone.setVisible(true);
	        	}
	        	else {
	        		lblNewLabel_2_1_2_1.setVisible(false);
	        		rdbtnAisle.setVisible(false);
	        		rdbtnWindow.setVisible(false);
	        		rdbtnNone.setVisible(false);
	        	}
	        }
	    });
		
		JLabel lblNewLabel_2_1_3 = new JLabel("請選擇車次");
		lblNewLabel_2_1_3.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<Integer> comboBoxTrainNo = new JComboBox<Integer>();
		
		JButton btnOrder = new JButton("訂票");
		
		JLabel lblNewLabel_2_1_3_2 = new JLabel("回程車次");
		lblNewLabel_2_1_3_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		lblNewLabel_2_1_3_2.setVisible(false);
		
		JComboBox<Integer> comboBoxTrainNoReturn = new JComboBox<Integer>();
		comboBoxTrainNoReturn.setVisible(false);
		
		JButton btnOrderReturn = new JButton("訂回程票");	
		btnOrderReturn.setVisible(false);
		
		JButton btnFindTrain = new JButton("查詢符合車次");
		btnFindTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String date = String.valueOf(comboBoxDate.getSelectedItem());
				String returnDate = String.valueOf(comboBoxDate_1.getSelectedItem());
				String start = String.valueOf(comboBoxStart.getSelectedItem());
				String end = String.valueOf(comboBoxEnd.getSelectedItem());
				int num = (int) comboBoxNum.getSelectedItem();
				switch (String.valueOf(comboBoxType.getSelectedItem())) {
				case "普通車廂":
					type = 0;
					break;
				case "商務車廂":
					type = 1;
					break;
				case "敬老票":
					type = 2;
					break;
				case "大學生":
					type = 5;
					break;
				case "愛心票":
					type = 4;
					break;
				case "孩童票":
					type = 3;
					break;
				}
				String str1 = null;
				Enumeration<AbstractButton> elements = buttonGroup_1.getElements();
				while (elements.hasMoreElements()) {
					AbstractButton button = (AbstractButton) elements.nextElement();
					if (button.isSelected()) {
						str1 = button.getText();
						break;
					}
				}
				elements = buttonGroup_2.getElements();
				while (elements.hasMoreElements()) {
					AbstractButton button = (AbstractButton) elements.nextElement();
					if (button.isSelected()) {
						str2 = button.getText();
						break;
					}
				}
				
				if (str1.equals("單程票") && str2.equals("無") && type != 5) {
					ArrayList<Integer> temp = Booking.normalBookingA("", Integer.valueOf(date), start, end, type, num);
					comboBoxTrainNo.removeAllItems();
					for (int i : temp)
						comboBoxTrainNo.addItem(i);
					for (ActionListener al : btnOrder.getActionListeners())
					    btnOrder.removeActionListener(al);
					btnOrder.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int no = (int) comboBoxTrainNo.getSelectedItem();
							String UID = textFieldUID.getText();
							Booking.normalBookingB(no, UID, Integer.valueOf(date), start, end, type, num);
						}
					});
				}
				else if ((str2.equals("靠窗") || str2.equals("走道")) && str1.equals("單程票")) {
					ArrayList<Integer> temp = ConditionalBooking.AisleOrWindow("", Integer.valueOf(date), start, end, type, num, str2.equals("走道"));
					comboBoxTrainNo.removeAllItems();
					for (int i : temp)
						comboBoxTrainNo.addItem(i);
					for (ActionListener al : btnOrder.getActionListeners())
					    btnOrder.removeActionListener(al);
					btnOrder.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int no = (int) comboBoxTrainNo.getSelectedItem();
							String UID = textFieldUID.getText();
							ConditionalBooking.AisleOrWindowSelected(UID, Integer.valueOf(date), start, end, type, num, str2.equals("走道"), no);
						}
					});
				}
				else if (type == 5 && str1.equals("單程票")) {
					ArrayList<Integer> temp = ConditionalBooking.StudentTicket("", Integer.valueOf(date), start, end, num);
					comboBoxTrainNo.removeAllItems();
					for (int i : temp)
						comboBoxTrainNo.addItem(i);
					for (ActionListener al : btnOrder.getActionListeners())
					    btnOrder.removeActionListener(al);
					btnOrder.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int no = (int) comboBoxTrainNo.getSelectedItem();
							String UID = textFieldUID.getText();
							ConditionalBooking.StudentTicketSelected(UID, Integer.valueOf(date), start, end, num, no);
						}
					});
				}
				else if (str1.equals("來回票")) {
					ArrayList<Integer> temp1 = ConditionalBooking.RoundTripOutbound("", Integer.valueOf(date), Integer.valueOf(returnDate), start, end, type, num);
					comboBoxTrainNo.removeAllItems();
					for (int i : temp1)
						comboBoxTrainNo.addItem(i);
					//String id = "";
					for (ActionListener al : btnOrder.getActionListeners())
					    btnOrder.removeActionListener(al);
					btnOrder.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int no = (int) comboBoxTrainNo.getSelectedItem();
							String UID = textFieldUID.getText();
							s = ConditionalBooking.RoundTripOutboundSelected(UID, Integer.valueOf(date), start, end, type, num, no);
							lblNewLabel_2_1_3_2.setVisible(true);
							comboBoxTrainNoReturn.setVisible(true);
							btnOrderReturn.setVisible(true);
						}
					});
					ArrayList<Integer> temp2 = ConditionalBooking.RoundTripInbound("", Integer.valueOf(date), Integer.valueOf(returnDate), end, start, type, num, s);
					comboBoxTrainNoReturn.removeAllItems();
					for (int i : temp2)
						comboBoxTrainNoReturn.addItem(i);
					for (ActionListener al : btnOrder.getActionListeners())
					    btnOrder.removeActionListener(al);
					btnOrderReturn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int no = (int) comboBoxTrainNoReturn.getSelectedItem();
							String UID = textFieldUID.getText();   // peng
							ConditionalBooking.RoundTripInboundSelected(UID, Integer.valueOf(date), Integer.valueOf(returnDate), end, start, type, num, s, no);
							lblNewLabel_2_1_3_2.setVisible(true);
							comboBoxTrainNoReturn.setVisible(true);
						}
					});
				}
			}
		});
		
		JLabel lblNewLabel_2_1_3_1 = new JLabel("請輸入身分證/護照 號碼");
		lblNewLabel_2_1_3_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		textFieldUID = new JTextField();
		textFieldUID.setColumns(10);
			
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lbluqul)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(lblNewLabel_2_1_2)
										.addComponent(lblNewLabel_2_1_2_1, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(rdbtnWindow, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
										.addComponent(rdbtnOneway, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE))
									.addGap(22))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addComponent(lblNewLabel_2_1, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(comboBoxNum, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(lblNewLabel_2_1_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(comboBoxDate, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(lblNewLabel_2_3)))
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(rdbtnReturntrip, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(rdbtnAisle, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(rdbtnNone, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE))
								.addComponent(comboBoxType, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
									.addComponent(btnFindTrain)
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(comboBoxStart, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
										.addGap(4)
										.addComponent(lblNewLabel_2_3_1)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(comboBoxEnd, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblNewLabel_2_2, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(comboBoxDate_1, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblNewLabel_2_1_3)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(comboBoxTrainNo, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
							.addComponent(btnOrder, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
									.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
										.addComponent(lblNewLabel_2_1_3_2)
										.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(comboBoxTrainNoReturn, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
									.addComponent(lblNewLabel_2_1_3_1, Alignment.LEADING))
								.addGap(18)
								.addComponent(textFieldUID, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE))
							.addComponent(btnOrderReturn, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(36, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbluqul, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2_3, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2_3_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxEnd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_2_1_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(rdbtnReturntrip)
								.addComponent(rdbtnOneway))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblNewLabel_2_1_2_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
									.addComponent(rdbtnWindow, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
									.addComponent(rdbtnAisle, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
									.addComponent(rdbtnNone))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_2_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(comboBoxNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_2_1_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(comboBoxType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(82)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_2_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxDate_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(16)
					.addComponent(btnFindTrain)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2_1_3, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxTrainNo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2_1_3_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldUID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnOrder)
					.addGap(10)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2_1_3_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxTrainNoReturn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOrderReturn)
					.addGap(35))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("取消");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		Date dt = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(dt);
		String str = dateFormat.format(dt);
		comboBoxDate.addItem(str);
		for (int i = 1; i < 28; ++i) {
			rightNow.add(Calendar.DAY_OF_YEAR,1);
			dt = rightNow.getTime();
			str = dateFormat.format(dt);
			comboBoxDate.addItem(str);
		}		
		for (int i = 0; i < 12; ++i) {
			comboBoxStart.addItem(stations.get(i));
			comboBoxEnd.addItem(stations.get(i));
		}
		for (int i = 1; i < 11; ++i)
			comboBoxNum.addItem(i);
		comboBoxType.addItem("普通車廂");
		comboBoxType.addItem("商務車廂");
		comboBoxType.addItem("大學生");
		comboBoxType.addItem("敬老票");
		comboBoxType.addItem("愛心票");
		comboBoxType.addItem("孩童票");
	}
}
