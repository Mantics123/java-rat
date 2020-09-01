package me.sebsb.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import me.sebsb.ConnectedClient;
import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;
import me.sebsb.utils.Packet;

public class GuiMessageBox extends JFrame {

	private JPanel contentPane;
	private JTextField Title;
	private JTextField textField;
	
	public GuiMessageBox(JList<ConnectedClient> clients) {
		if (clients == null) {
			return;
		}
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Send Message Box");
		lblNewLabel.setBounds(5, 5, 440, 16);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel);
		
		Title = new JTextField();
		Title.setToolTipText("Title");
		Title.setBounds(160, 65, 130, 26);
		contentPane.add(Title);
		Title.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Title");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(160, 51, 130, 16);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Text");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setBounds(160, 92, 130, 16);
		contentPane.add(lblNewLabel_1_1);
		
		textField = new JTextField();
		textField.setToolTipText("Text");
		textField.setColumns(10);
		textField.setBounds(160, 106, 130, 26);
		contentPane.add(textField);
		String[] options = new String[] {
				"ERROR_MESSAGE",
				"INFORMATION_MESSAGE",
				"WARNING_MESSAGE",
				"QUESTION_MESSAGE",
				"PLAIN_MESSAGE"
		};
		JComboBox<String> comboBox = new JComboBox<String>(options);
		comboBox.setBounds(160, 151, 130, 27);
		contentPane.add(comboBox);
		
		JLabel lblNewLabel_2 = new JLabel("Mode");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(160, 132, 130, 16);
		contentPane.add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("Preview");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, textField.getText(), Title.getText(), getMode(comboBox.getSelectedItem().toString()));
					}
					
				}).start();
			}
			
		});
		btnNewButton.setBounds(80, 201, 130, 60);
		contentPane.add(btnNewButton);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(238, 201, 130, 60);
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					Packet packet = new Packet();
					packet.data = Arrays.asList(new String[] {Title.getText(), textField.getText(), getMode(comboBox.getSelectedItem().toString()) + ""});
					packet.action = MessageType.MESSAGE_BOX.getID();
					for (ConnectedClient cc : clients.getSelectedValuesList()) {
						NetUtils.sendMessage(packet, cc.getPrintWriter());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		contentPane.add(btnSend);
	}
	
	private int getMode(String name) {
		if (name == null) {
			return -1;
		}
		if (name.equals("ERROR_MESSAGE")) {
			return 0;
		}
		if (name.equals("INFORMATION_MESSAGE")) {
			return 1;
		}
		if (name.equals("WARNING_MESSAGE")) {
			return 2;
		}
		if (name.equals("QUESTION_MESSAGE")) {
			return 3;
		}
		return -1;
	}
}
