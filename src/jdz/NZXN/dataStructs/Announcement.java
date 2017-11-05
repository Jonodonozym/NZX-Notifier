/**
 * Announcement.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 8:44:23 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 9:17:49 AM
 */

package jdz.NZXN.dataStructs;

import lombok.Data;

@Data
public class Announcement {
	private final String company, companyURL, notification, url, type, time;
	private final AnnouncementFlag flag;
	
	public enum AnnouncementFlag{
		NONE,
		PRICE_SENSITIVE,
		THIRD_PARTY;
	}
}
