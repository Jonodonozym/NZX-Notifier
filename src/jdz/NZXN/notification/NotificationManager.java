/**
 * ConfigWindow.java
 *
 * Created by Jaiden Baker on Jul 6, 2017 5:17:35 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 3, 2017 11:29:38 AM
 * 
 * Nov 3, 2017 11:29:41 AM - Fixed notification sound not playing
 */

package jdz.NZXN.notification;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.SwingUtilities;

import jdz.NZXN.config.ConfigChangeListener;
import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.resources.Resources;

/**
 * Static class that manages notifications by sorting them vertically on the
 * bottom-right side
 * of the screen, automatically re-positioning them when a notification below
 * others is closed.
 * 
 * i.e. when closing b from [ a ]
 * [ b ]
 * 
 * it becomes [ big empty space ]
 * [ a ]
 * 
 * instead of [ a ]
 * [ big empty space ]
 *
 * Also plays a soothing tune to lull the user to sleep when a notification is
 * added
 *
 * @author Jaiden Baker
 */
class NotificationManager {
	private static List<NotificationDialog> notifications = new ArrayList<NotificationDialog>();

	private static Dimension screenSize = null;
	private static int taskBarHeight, y, x;
	private static final int yGap = 16;

	static {
		if (screenSize == null || !screenSize.equals(Toolkit.getDefaultToolkit().getScreenSize())) {
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			taskBarHeight = Toolkit.getDefaultToolkit().getScreenSize().height
					- GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
			y = screenSize.height - taskBarHeight - yGap;
			x = screenSize.width - NotificationDialog.width - yGap;
		}

		ConfigChangeListener.register(ConfigProperty.IS_MUTED, (newValue) -> {
			for (NotificationDialog n : notifications)
				n.setAlwaysOnTop(newValue);
		});
	}

	/**
	 * Takes a list of notifications and arranges them above any existing
	 * notifications, bottom to top
	 * 
	 * @param notifications
	 */
	public static void add(NotificationDialog n) {
		NotificationManager.notifications.add(n);
		n.setAlwaysOnTop(!ConfigProperty.IS_MUTED.get());
		y -= n.getHeight();
		n.setLocation(x, y);
		y -= yGap;
		SwingUtilities.invokeLater(() -> {
			n.setVisible(true);
		});
		if (!ConfigProperty.IS_MUTED.get())
			playSound();
	}

	/**
	 * Deletes a notification
	 * Automatically relocates floating notifications above it to right above the
	 * taskbar / other notifications
	 * 
	 * @param n
	 */
	public static void delete(NotificationDialog n) {
		int index = notifications.indexOf(n);
		notifications.remove(index);
		int dy = n.getHeight() + yGap;
		for (int i = index; i < notifications.size(); i++) {
			NotificationDialog n2 = notifications.get(i);
			n2.setLocation(x, n2.getY() + dy);
		}
		y += dy;
	}

	private static boolean soundPlaying = false;
	private static final ScheduledExecutorService soundSheduler = Executors.newScheduledThreadPool(1);

	/**
	 * Summons demons from hell and unleashes them on the world, enacting judgment
	 * on sinners and saints alike
	 * Seriously, it's called playSound. What did you expect it to do?
	 */
	private static void playSound() {
		if (soundPlaying)
			return;

		try {
			InputStream is = NotificationManager.class.getResourceAsStream(Resources.notificationSound);
			BufferedInputStream bis = new BufferedInputStream(is);
			AudioInputStream stream = AudioSystem.getAudioInputStream(bis);

			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();

			soundPlaying = true;
			soundSheduler.schedule(() -> {
				soundPlaying = false;
			}, clip.getMicrosecondLength(), TimeUnit.MICROSECONDS);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
