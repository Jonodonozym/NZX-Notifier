/**
 * SysTrayFrame.java
 *
 * Created by Jaiden Baker on Jul 4, 2017 4:34:01 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 13, 2017 6:08:43 AM
 */

package jdz.NZXN.utils.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

/**
 *
 * @author Mohammad Faisal, Jaiden Baker
 *         ermohammadfaisal.blogspot.com
 *         facebook.com/m.faisal6621
 *
 */

@SuppressWarnings("serial")
public class JSysTrayFrame extends JFrame {
	TrayIcon trayIcon;
	SystemTray tray;
	Container contentPane;
	String minimizeMessage;
	PopupMenu popup;

	public JSysTrayFrame(String title, String minimizeMessage, Container contentPane, Image frameIcon) {
		super(title);
		this.minimizeMessage = minimizeMessage;

		setContentPane(contentPane);

		if (SystemTray.isSupported()) {

			tray = SystemTray.getSystemTray();
			tray.getTrayIcons();

			popup = new PopupMenu();
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener((a) -> {
				exit();
			});
			addSysTrayMenuItem(exitItem);

			trayIcon = new TrayIcon(frameIcon, title, popup);
			trayIcon.setImageAutoSize(true);

			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
						setVisible(true);
						setExtendedState(NORMAL);
					}
				}
			});
		}

		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				sendToTray(e);
			}
		});

		setIconImage(frameIcon);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		setVisible(true);
		setSize(300, 200);
	}

	public void addSysTrayMenuItem(MenuItem item) {
		popup.add(item);
	}

	public void sendToTray(WindowEvent e) {
		sendToTray(e, true);
	}

	public void sendToTray(WindowEvent e, boolean displayMessage) {
		if (e.getNewState() == ICONIFIED) {
			try {
				tray.add(trayIcon);
				if (displayMessage && minimizeMessage != "")
					trayIcon.displayMessage(getTitle(), minimizeMessage, TrayIcon.MessageType.NONE);
				setVisible(false);
			}
			catch (AWTException ex) {}
		}

		if (e.getNewState() == 7) {
			try {
				tray.add(trayIcon);
				setVisible(false);
			}
			catch (AWTException ex) {
				System.out.println("unable to add to system tray");
			}
		}

		if (e.getNewState() == MAXIMIZED_BOTH) {
			tray.remove(trayIcon);
			setVisible(true);
		}
		if (e.getNewState() == NORMAL) {
			tray.remove(trayIcon);
			setVisible(true);
		}
	}

	public void exit() {
		dispose();
	}
}