/**
 * Announcement.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 8:44:23 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 9:17:49 AM
 */

package jdz.NZXN.utils;

public class Announcement {
	public final String company, companyURL, notification, url, type, time;
	public final boolean isPriceSensitive, isThirdParty;

	public Announcement(String company, String companyURL, String notification, String url, String type, String time, boolean isPriceSensitive, boolean isThirdParty) {
		this.company = company;
		this.companyURL = companyURL;
		this.notification = notification;
		this.url = url;
		this.type = type;
		this.time = time;
		this.isPriceSensitive = isPriceSensitive;
		this.isThirdParty = isThirdParty;
	}
}
