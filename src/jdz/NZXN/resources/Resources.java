package jdz.NZXN.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {
	public static BufferedImage appIcon, bannerImage, infoIcon, PSFlag, ANZLogo;
	public static String notificationSound = "/jdz/NZXN/resources/NotificationSound.wav";

	static {
		try {
			infoIcon = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/resources/InfoIcon.gif"));
			bannerImage = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/resources/NZXLogo.png"));
			appIcon = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/resources/AppIcon.png"));
			PSFlag = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/resources/PriceSensitiveFlag.png"));
			ANZLogo = ImageIO.read(Resources.class.getResourceAsStream("/jdz/NZXN/resources/ANZLogo.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
