package me.sebsb.utils;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {

	public static String getRandomString(int size) {
		String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(s.charAt(ThreadLocalRandom.current().nextInt(s.length())));
		}
		return sb.toString();
	}
}
