package jdz.NZXN.res;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {
	public static BufferedImage appIcon, bannerImage, infoIcon, PSFlag;
	public static String notificationSound = "/jdz/NZXN/res/NotificationSound.wav";
	
	static{
		try {
			infoIcon = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/res/InfoIcon.gif"));
			bannerImage = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/res/NZXLogo.png"));
			appIcon = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/res/AppIcon.png"));
			PSFlag = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/res/PriceSensitiveFlag.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
