import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import TrainInfo.DBWriter;
import TicketInfo.TicketInfoWriter;
import GUI.BookingGUI;
import GUI.ReviseGUI;
import GUI.EnquiryGUI;
public class MainGUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    TicketInfoWriter.createTicketDB();
	    TicketInfoWriter.bookingIDTable();
	    DBWriter.createTrainsTable();
	    DBWriter.addTrainsFromJson();
	    EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("高鐵訂票系統");
		lblNewLabel.setFont(new Font("新細明體", Font.PLAIN, 30));
		
		JLabel lblNewLabel_1 = new JLabel("請選擇您需要的服務:");
		lblNewLabel_1.setFont(new Font("新細明體", Font.PLAIN, 20));
		
		JButton btnBooking = new JButton("訂票");
		btnBooking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    BookingGUI newWindow = new BookingGUI();
			    newWindow.setVisible(true);
			}
		});
		
		JButton btnEnquiry = new JButton("查詢");
		btnEnquiry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    EnquiryGUI newWindow = new EnquiryGUI();
			    newWindow.setVisible(true);
			}
		});
		
		JButton btnExit = new JButton("離開");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JButton btnRefund = new JButton("退票/修改");
		btnRefund.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReviseGUI newWindow = new ReviseGUI();
				newWindow.setVisible(true);
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGap(137)
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
					.addGap(117))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_1)
					.addContainerGap(278, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(65)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnBooking, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnEnquiry, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 124, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnExit, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRefund, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE))
					.addGap(61))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(27)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel_1)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnRefund, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBooking, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
					.addGap(31)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnEnquiry, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(31, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
