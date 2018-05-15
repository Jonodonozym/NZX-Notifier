/**
 * Main.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 2:07:22 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 1:28:57 PM
 */

package jdz.NZXN;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import jdz.NZXN.gui.ConfigWindow;
import jdz.NZXN.tasks.CheckAnnouncementsTask;
import jdz.NZXN.utils.swing.JSplashFrame;

public class Main {
	public static void main(String[] args) {
		List<String> argsList = Arrays.asList(args);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Panel.background", Color.WHITE);
			UIManager.put("OptionPane.background", Color.WHITE);

			UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			UIManager.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			UIManager.put("TabbedPane.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			UIManager.put("ComboBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		if (!setAppLock()) {
			JOptionPane.showMessageDialog(new JFrame(), "Error: NXZ Notifier already running", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		JSplashFrame splashFrame = null;
		if (!argsList.contains("S"))
			splashFrame = new JSplashFrame();

		CheckAnnouncementsTask.getInstance().start();
		ConfigWindow window = new ConfigWindow(false);
		CheckAnnouncementsTask.getInstance().check();
		if (argsList.contains("S"))
			window.sendToTray(new WindowEvent(window, WindowEvent.WINDOW_ICONIFIED, 0, Frame.ICONIFIED), false);
		else {
			window.setVisible(true);
			splashFrame.dispose();
		}
	}

	public static boolean setAppLock() {
		boolean retBool = false;

		try {
			File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "NZXNAppLock");
			file.createNewFile();

			@SuppressWarnings("resource") FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
			FileLock lock = channel.tryLock();
			if (lock != null) {
				retBool = true;
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							lock.release();
							file.delete();
						}
						catch (IOException e) {}
					}
				}));
			}
		}
		catch (IOException e) {}

		return retBool;
	}

}