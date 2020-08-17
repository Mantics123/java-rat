package me.sebsb.utils;

public enum MessageType {

	GUI_TEXT(0),
	INFO(1),
	DESKTOP(2),
	WEBCAM(3),
	KEYLOG(4);
	
	private int id;
	
	MessageType(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public static MessageType getTypeByID(int id) {
		for (MessageType t : values()) {
			if (t.getID() == id) {
				return t;
			}
		}
		return null;
	}
	
	public static String getLineSeparator() {
		return ":LINE_SEPARATOR:";
	}
}
