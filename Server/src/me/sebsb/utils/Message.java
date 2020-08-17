package me.sebsb.utils;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private List<String> message;
	private MessageType type;
	
	public Message(List<String> text, MessageType type) {
		this.message = text;
		this.type = type;
	}
	
	public Message(String text, MessageType type) {
		this.message = new ArrayList<String>();
		this.message.add(text);
		this.type = type;
	}
	
	public List<String> getText() {
		return this.message;
	}
	
	public MessageType getType() {
		return this.type;
	}
}
