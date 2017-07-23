/**
 * ConfigWindow.java
 *
 * Created by Jaiden Baker on Jul 6, 2017 5:17:35 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 17, 2017 1:46:41 PM
 */

package jdz.NZXN.Notification;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.SwingUtilities;

import jdz.NZXN.res.Resources;

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
	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final int taskBarHeight = Toolkit.getDefaultToolkit().getScreenSize().height - GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

	private static List<Notification> notifications = new ArrayList<Notification>();
	private static final int gap = 16;
	private static int y = screenSize.height - taskBarHeight - gap;
	private static final int x = screenSize.width - Notification.width -gap;

	/**
	 * Takes a list of notifications and arranges them above any existing notifications, bottom to top
	 * @param notifications
	 */
	public static void add(List<Notification> notifications) {
		if (notifications.isEmpty())
			return;
		for (Notification n: notifications){
			NotificationManager.notifications.add(n);
			y -= n.getHeight();
			n.setLocation(x, y);
			y -= gap;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (Notification n: notifications)
					n.setVisible(true);
			}
		});
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
		int dy = n.getHeight()+gap;
		for (int i=index; i<notifications.size(); i++){
			Notification n2 = notifications.get(i);
			n2.setLocation(x, n2.getY()+dy);
		}
		y += dy;
	}

	/**
	 * Summons demons from hell and unleashes them on the world, enacting judgment on sinners and saints alike
	 * Seriously, it's called playSound. What did you expect it to do?
	 */
	private static void playSound() {
		// despite the try / catch, this will always work unless some idiot messes with the resource incorrectly
		// please read the Resources class documentation before messing with the resources
		// or don't
		// I'm a comment not a police officer
		try {
			InputStream is = NotificationManager.class.getResourceAsStream(Resources.notificationSound);
			AudioInputStream stream = AudioSystem.getAudioInputStream(is);

			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
		}
		catch (Exception e) { }
	}
}
