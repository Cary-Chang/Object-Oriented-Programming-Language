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
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import TicketInfo.Ticket;

public class ReviseGUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldUID;
	private JTextField textFieldID;
	static String UID;
	static String id;
	static Ticket ticket;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ReviseGUI dialog = new ReviseGUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ReviseGUI() {
		setBounds(100, 100, 550, 350);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblNewLabel = new JLabel("退票/修改");
		lblNewLabel.setFont(new Font("新細明體", Font.PLAIN, 36));
		JLabel lblNewLabel_1 = new JLabel("請輸入身分證/護照 號碼");
		lblNewLabel_1.setFont(new Font("新細明體", Font.PLAIN, 18));
		textFieldUID = new JTextField();
		textFieldUID.setColumns(10);
		JLabel lblNewLabel_2 = new JLabel("請輸入訂票編號");
		lblNewLabel_2.setFont(new Font("新細明體", Font.PLAIN, 18));
		textFieldID = new JTextField();
		textFieldID.setColumns(10);
		JLabel lblNewLabel_3 = new JLabel("乘客人數更動");
		lblNewLabel_3.setFont(new Font("新細明體", Font.PLAIN, 18));
		lblNewLabel_3.setVisible(false);
		
		JComboBox<Integer> comboBoxNewNum = new JComboBox<Integer>();
		comboBoxNewNum.setVisible(false);
		
		JLabel lblNewLabel_4 = new JLabel("人");
		lblNewLabel_4.setFont(new Font("新細明體", Font.PLAIN, 18));
		lblNewLabel_4.setVisible(false);
		
		JButton btnSearch = new JButton("查詢");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UID = textFieldUID.getText();
				id = textFieldID.getText();
				ticket = new Ticket(id, UID);
				int oldNum = ticket.getNum();
				for (int i = oldNum; i >= 0; --i)
				    comboBoxNewNum.addItem(i);
				lblNewLabel_3.setVisible(true);
				comboBoxNewNum.setVisible(true);
				lblNewLabel_4.setVisible(true);
			}
		});
		
		JButton btnRevise = new JButton("變更");
		btnRevise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int newNum = (int) comboBoxNewNum.getSelectedItem();
				ticket.modifyTicket(newNum);
			}
		});
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_1)
								.addComponent(lblNewLabel_2)
								.addComponent(lblNewLabel_3))
							.addGap(84)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(textFieldUID, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBoxNewNum, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblNewLabel_4))
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
									.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
									.addComponent(textFieldID, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)
									.addComponent(btnRevise, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap(43, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(textFieldUID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(textFieldID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnSearch)
					.addGap(20)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_3)
						.addComponent(comboBoxNewNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_4))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnRevise)
					.addGap(24))
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
	}

}
