package me.sebsb.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import me.sebsb.ConnectedClient;
import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;
import me.sebsb.utils.Packet;

public class GuiOpenURL extends JFrame {

	private JPanel contentPane;
	private JTextField url;
	
	public GuiOpenURL(JList<ConnectedClient> clients) {
		if (clients == null) {
			return;
		}
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 250, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Open URL");
		lblNewLabel.setBounds(0, 0, 250, 20);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel);
		
		url = new JTextField();
		url.setToolTipText("URL");
		url.setBounds(0, 45, 250, 26);
		contentPane.add(url);
		url.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("URL");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(0, 30, 250, 16);
		contentPane.add(lblNewLabel_1);
		
		JButton btnSend = new JButton("Send");
		btnSend.setHorizontalAlignment(SwingConstants.CENTER);
		btnSend.setBounds(0, 80, 235, 60);
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (ConnectedClient client : clients.getSelectedValuesList()) {
							try {
								Packet packet = new Packet();
								packet.data = Arrays.asList(new String[] {url.getText()});
								packet.action = MessageType.OPEN_URL.getID();
								NetUtils.sendMessage(packet, client.getPrintWriter());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				}).start();
			}
			
		});
		contentPane.add(btnSend);
	}
}
