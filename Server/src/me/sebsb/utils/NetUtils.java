package me.sebsb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Arrays;

import me.sebsb.exception.ClientDisconnectedException;

public class NetUtils {

	public static void sendMessage(String message, PrintWriter pw) {
		pw.println(message);
	}
	
	public static Message readLine(BufferedReader reader) throws IOException, ClientDisconnectedException, NumberFormatException, NullPointerException {
		try {
			String newLine = reader.readLine();
			if (newLine == null) {
				throw new ClientDisconnectedException();
			}
			int id = Integer.parseInt(newLine);
			String message = reader.readLine();
			if (!message.contains(MessageType.getLineSeparator())) {
				return new Message(message, MessageType.getTypeByID(id));
			}
			String[] args = message.split(MessageType.getLineSeparator());
			return new Message(Arrays.asList(args), MessageType.getTypeByID(id));
		} catch (SocketException e) {
			throw new ClientDisconnectedException();
		}
	}
}
