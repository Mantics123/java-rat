package me.sebsb.gui;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class GuiText extends JFrame {

	private JPanel contentPane;

	public GuiText(List<String> text) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 455, 270);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTextArea txtrSfsf = new JTextArea();
		txtrSfsf.setLineWrap(true);
		StringBuilder sb = new StringBuilder(new Date().toString());
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		for (String str : text) {
			sb.append(str);
			sb.append(System.lineSeparator());
		}
		txtrSfsf.setText(sb.toString());
		txtrSfsf.setEditable(false);
		contentPane.add(txtrSfsf, BorderLayout.CENTER);
		
		this.setVisible(true);
	}

}
