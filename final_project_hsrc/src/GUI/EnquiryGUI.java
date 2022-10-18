package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class EnquiryGUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
//	private static EnquiryGUI dialog;
	private final static Map<Integer, String> stations = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;
	{      
	    put(0, "南港");put(1, "台北");put(2, "板橋");put(3, "桃園");put(4, "新竹");put(5, "苗栗");
	    put(6, "台中");put(7, "彰化");put(8, "雲林");put(9, "嘉義");put(10, "台南");put(11, "左營");  
	}}; 
	private JTextField textFieldUID;
	private JTextField textFieldID;
	private JTextField textFieldUID_2;
	private JTextField textFieldTrainNO;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EnquiryGUI dialog = new EnquiryGUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EnquiryGUI() {
		setBounds(100, 100, 700, 550);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);
		JLabel lblNewLabel = new JLabel("當日時刻表查詢");
		lblNewLabel.setFont(new Font("新細明體", Font.PLAIN, 36));
		JButton btnTimeTableEnquiry = new JButton("查詢");
		btnTimeTableEnquiry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TimeTableEnquiry obj = new TimeTableEnquiry();
				obj.show();
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel("查詢 - 優惠車次");
		lblNewLabel_1.setFont(new Font("新細明體", Font.PLAIN, 36));
		
		JLabel lblNewLabel_2 = new JLabel("乘車日期");
		lblNewLabel_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxDate = new JComboBox<String>();
		
		JLabel lblNewLabel_2_1 = new JLabel("出發/抵達時間");
		lblNewLabel_2_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxDepartureOrArrive = new JComboBox<String>();
		
		JComboBox<String> comboBoxHours = new JComboBox<String>();
		
		JLabel lblNewLabel_2_2 = new JLabel(":");
		lblNewLabel_2_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxMinutes = new JComboBox<String>();
		
		JLabel lblNewLabel_2_3 = new JLabel("出發站");
		lblNewLabel_2_3.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxStart = new JComboBox<String>();
		
		JLabel lblNewLabel_2_3_1 = new JLabel("抵達站");
		lblNewLabel_2_3_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxEnd = new JComboBox<String>();
		
		JButton btnDiscountTimeTableEnquiry = new JButton("查詢");
		btnDiscountTimeTableEnquiry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String date_1 = String.valueOf(comboBoxDate.getSelectedItem());
				int type_1 = (String.valueOf(comboBoxDepartureOrArrive.getSelectedItem()).equals("出發")) ? 0 : 1;
				String h = String.valueOf(comboBoxHours.getSelectedItem());
				String m = String.valueOf(comboBoxMinutes.getSelectedItem());
				String time_1 = h + ":" + m;
				String start = String.valueOf(comboBoxStart.getSelectedItem());
				String end = String.valueOf(comboBoxEnd.getSelectedItem());
				
				DiscountTimeTableEnquiry obj = new DiscountTimeTableEnquiry(date_1, type_1, time_1, start, end);
				obj.show();
			}
		});
		
		JLabel lblNewLabel_1_1 = new JLabel("查詢訂票紀錄");
		lblNewLabel_1_1.setFont(new Font("新細明體", Font.PLAIN, 36));
		JLabel lblNewLabel_2_4 = new JLabel("身分證/護照 號碼");
		lblNewLabel_2_4.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		textFieldUID = new JTextField();
		textFieldUID.setColumns(10);
		
		JLabel lblNewLabel_2_4_1 = new JLabel("訂單編號");
		lblNewLabel_2_4_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		textFieldID = new JTextField();
		textFieldID.setColumns(10);
		
		JButton btnBookingHistoryEnquiry = new JButton("查詢");
		btnBookingHistoryEnquiry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String UID_1 = textFieldUID.getText();
				String ID_1 = textFieldID.getText();
				BookingHistoryEnquiry obj = new BookingHistoryEnquiry(UID_1, ID_1);
				obj.show();
			}
		});
		
		JLabel lblNewLabel_1_1_1 = new JLabel("查詢訂票紀錄");
		lblNewLabel_1_1_1.setFont(new Font("新細明體", Font.PLAIN, 36));
		
		JLabel lblNewLabel_2_4_2 = new JLabel("身分證/護照 號碼");
		lblNewLabel_2_4_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		textFieldUID_2 = new JTextField();
		textFieldUID_2.setColumns(10);
		
		JLabel lblNewLabel_2_5 = new JLabel("乘車日期");
		lblNewLabel_2_5.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxDate_2 = new JComboBox<String>();
		
		JLabel lblNewLabel_2_3_2 = new JLabel("出發站");
		lblNewLabel_2_3_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxStart_2 = new JComboBox<String>();
		
		JLabel lblNewLabel_2_3_1_1 = new JLabel("抵達站");
		lblNewLabel_2_3_1_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		JComboBox<String> comboBoxEnd_2 = new JComboBox<String>();
		
		JLabel lblNewLabel_2_4_1_1 = new JLabel("車次");
		lblNewLabel_2_4_1_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		
		textFieldTrainNO = new JTextField();
		textFieldTrainNO.setColumns(10);
		
		JButton btnReservationNumbersEnquiry = new JButton("查詢");
		btnReservationNumbersEnquiry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String UID_2 = textFieldUID_2.getText();
				int ID_2 = Integer.valueOf(textFieldTrainNO.getText());
				String start_2 = String.valueOf(comboBoxStart_2.getSelectedItem());
				String end_2 = String.valueOf(comboBoxEnd_2.getSelectedItem());
				String date_2 = String.valueOf(comboBoxDate_2.getSelectedItem());
				ReservationNumbersEnquiry obj = new ReservationNumbersEnquiry(UID_2, start_2, end_2, date_2, ID_2);
				obj.show();
			}
		});
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
							.addComponent(btnTimeTableEnquiry, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblNewLabel_2_4_1)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(textFieldID, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblNewLabel_2_4)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(textFieldUID))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblNewLabel_2)
									.addGap(10)
									.addComponent(comboBoxDate, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(lblNewLabel_2_1))
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
									.addComponent(lblNewLabel_1_1, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(lblNewLabel_2_3, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(comboBoxStart, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(lblNewLabel_2_3_1, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE))))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
									.addComponent(comboBoxEnd, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
									.addComponent(btnDiscountTimeTableEnquiry, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
								.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
									.addComponent(comboBoxDepartureOrArrive, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBoxHours, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblNewLabel_2_2)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBoxMinutes, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
								.addComponent(btnBookingHistoryEnquiry, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)))
						.addComponent(lblNewLabel_1_1_1, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblNewLabel_2_5, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(comboBoxDate_2, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNewLabel_2_3_2, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(comboBoxStart_2, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNewLabel_2_3_1_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(comboBoxEnd_2, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
							.addGap(37)
							.addComponent(btnReservationNumbersEnquiry, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblNewLabel_2_4_2, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textFieldUID_2, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNewLabel_2_4_1_1)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textFieldTrainNO, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 110, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(btnTimeTableEnquiry))
					.addGap(18)
					.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(comboBoxDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxDepartureOrArrive, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxHours, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxMinutes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel_2_3, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
							.addComponent(comboBoxStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblNewLabel_2_3_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(comboBoxEnd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnDiscountTimeTableEnquiry)))
					.addGap(18)
					.addComponent(lblNewLabel_1_1, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_2_4, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(2)
							.addComponent(textFieldUID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_2_4_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(textFieldID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnBookingHistoryEnquiry)))
					.addGap(18)
					.addComponent(lblNewLabel_1_1_1, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_2_4_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
							.addComponent(textFieldUID_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_2_4_1_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(textFieldTrainNO, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2_5, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxDate_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2_3_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxStart_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnReservationNumbersEnquiry)
						.addComponent(lblNewLabel_2_3_1_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxEnd_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		String str = dateFormat.format(date);
		comboBoxDate.addItem(str);
		comboBoxDate_2.addItem(str);
		for (int i = 1; i < 28; ++i) {
			rightNow.add(Calendar.DAY_OF_YEAR,1);
			date = rightNow.getTime();
			str = dateFormat.format(date);
			comboBoxDate.addItem(str);
			comboBoxDate_2.addItem(str);
		}
		
		comboBoxDepartureOrArrive.addItem("出發");
		comboBoxDepartureOrArrive.addItem("抵達");
		
		for (int i = 0; i < 24; ++i) 
			comboBoxHours.addItem(String.format("%02d", i));
		for (int i = 0; i < 60; ++i) 
			comboBoxMinutes.addItem(String.format("%02d", i));
		for (int i = 0; i < 12; ++i) {
			comboBoxStart.addItem(stations.get(i));
			comboBoxEnd.addItem(stations.get(i));
			comboBoxStart_2.addItem(stations.get(i));
			comboBoxEnd_2.addItem(stations.get(i));
		}
		
	}
}
