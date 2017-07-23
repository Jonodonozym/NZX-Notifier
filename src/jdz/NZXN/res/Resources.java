package jdz.NZXN.res;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jdz.NZXN.Notification.Notification;
import jdz.NZXN.main.Main;

public class Resources {
	public static BufferedImage appIcon, bannerImage, infoIcon, PSFlag;
	public static String notificationSound = "/jdz/NZXN/res/NotificationSound.wav";
	
	static{
		try {
			infoIcon = ImageIO.read(Main.class.getResourceAsStream("/jdz/NZXN/res/InfoIcon.gif"));
			bannerImage = ImageIO.read(Notification.class.getResourceAsStream("/jdz/NZXN/res/NZXLogo.png"));
			appIcon = ImageIO.read(Main.class.getResourceAsStream("/jdz/NZXN/res/AppIcon.png"));
			PSFlag = ImageIO.read(Main.class.getResourceAsStream("/jdz/NZXN/res/PriceSensitiveFlag.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
