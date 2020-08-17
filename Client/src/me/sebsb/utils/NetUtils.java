package me.sebsb.utils;

import java.io.PrintWriter;
import java.util.ArrayList;

public class NetUtils {

	public static void sendMessage(String message, MessageType type, PrintWriter pw) {
		if (message == null || message.isEmpty()) {
			return;
		}
		pw.println(type.getID());
		pw.println(message);
	}
	
	public static void sendMessage(ArrayList<String> message, MessageType type, PrintWriter pw) {
		if (message == null || message.isEmpty()) {
			return;
		}
		pw.println(type.getID());
		StringBuilder sb = new StringBuilder();
		for (String str : message) {
			sb.append(str);
			if (!str.equals(message.get(message.size() - 1))) {
				sb.append(MessageType.getLineSeparator());
			}
		}
		pw.println(sb.toString());
	}
}
