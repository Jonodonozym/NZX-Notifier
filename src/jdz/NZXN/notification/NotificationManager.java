/**
 * ConfigWindow.java
 *
 * Created by Jaiden Baker on Jul 6, 2017 5:17:35 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 17, 2017 1:46:41 PM
 */

package jdz.NZXN.notification;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.SwingUtilities;

import jdz.NZXN.config.Config;
import jdz.NZXN.res.Resources;
import jdz.NZXN.utils.FileLogger;

/**
 * Static class that manages notifications by sorting them vertically on the bottom-right side
 * of the screen, automatically re-positioning them when a notification below others is closed.
 * 
 * i.e. when closing b from  [ a ]
 *                           [ b ]
 *                           
 *      it becomes    [ big empty space ]
 *      					 [ a ]
 *      
 *      instead of           [ a ]
 *                    [ big empty space ]
 *
 * Also plays a soothing tune to lull the user to sleep when a notification is added
 *
 * @author Jaiden Baker
 */
public class NotificationManager {
	private static List<Notification> notifications = new ArrayList<Notification>();
	
	private static Dimension screenSize = null;
	private static int taskBarHeight, y, x;
	private static final int yGap = 16;
	private static final Timer detectScreenChange = new Timer();
	static{
		detectScreenChange.schedule(new TimerTask() {
			@Override
			public void run() {
				if (screenSize == null || !screenSize.equals(Toolkit.getDefaultToolkit().getScreenSize())){
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					taskBarHeight = Toolkit.getDefaultToolkit().getScreenSize().height - GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
					y = screenSize.height - taskBarHeight - yGap;
					x = screenSize.width - Notification.width -yGap;
					for(Notification n: notifications){
						y -= n.getHeight();
						n.setLocation(x, y);
						y -= yGap;
					}
				}
			}
		}, 0, 1000);
	}

	/**
	 * Takes a list of notifications and arranges them above any existing notifications, bottom to top
	 * @param notifications
	 */
	public static void add(List<Notification> notifications) {
		if (notifications.isEmpty())
			return;
		Config config = Config.loadConfig();
		for (Notification n: notifications){
			NotificationManager.notifications.add(n);
			n.setAlwaysOnTop(!config.getMuted());
			y -= n.getHeight();
			n.setLocation(x, y);
			y -= yGap;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (Notification n: notifications)
					n.setVisible(true);
			}
		});
		if (!config.getMuted())
			playSound();
	}
	
	/**
	 * Deletes a notification
	 * Automatically relocates floating notifications above it to right above the taskbar / other notifications
	 * @param n
	 */
	public static void delete(Notification n){
		int index = notifications.indexOf(n);
		notifications.remove(index);
		int dy = n.getHeight()+yGap;
		for (int i=index; i<notifications.size(); i++){
			Notification n2 = notifications.get(i);
			n2.setLocation(x, n2.getY()+dy);
		}
		y += dy;
	}
	
	/**
	 * Sets all of the notifications to always be on top or not
	 * @param alwaysOnTop
	 */
	public static void setAlwaysOnTop(boolean alwaysOnTop){
		for(Notification n: notifications)
			n.setAlwaysOnTop(alwaysOnTop);
	}

	/**
	 * Summons demons from hell and unleashes them on the world, enacting judgment on sinners and saints alike
	 * Seriously, it's called playSound. What did you expect it to do?
	 */
	private static void playSound() {
		try {
			InputStream is = NotificationManager.class.getResourceAsStream(Resources.notificationSound);
			AudioInputStream stream = AudioSystem.getAudioInputStream(is);

			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
		}
		catch (Exception e) { FileLogger.createErrorLog(e); }
	}
}
