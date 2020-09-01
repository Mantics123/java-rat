package me.sebsb;

import java.io.Serializable;
import java.util.List;

public class Packet implements Serializable {
	public int action;
	public List<String> data;
}
