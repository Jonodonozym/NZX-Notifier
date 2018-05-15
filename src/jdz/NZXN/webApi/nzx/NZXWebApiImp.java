/**
 * NZXWebAPI.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 8:43:41 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 6:18:49 AM
 */

package jdz.NZXN.webApi.nzx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.dataStructs.Announcement.AnnouncementFlag;
import jdz.NZXN.utils.debugging.FileLogger;
import jdz.NZXN.webApi.Websites;

public class NZXWebApiImp implements NZXWebApi{
	public static DateFormat formatter = new SimpleDateFormat("d MMM yyyy, h:mma");
	
	private static final String announcementsTable = "table.table-to-list.announcements-table";
	private static final String instrumentSite = "https://www.nzx.com/instruments/";

	@Override
	@Deprecated // currently, jsoup cannot run javascript which loads the time
	public LocalDateTime getDateTime() {
		return LocalDateTime.now();
		/*
		try {
			Document doc = Jsoup.connect(Websites.NZXannouncementsURL).get();

			Elements span = doc.select("div#snapshot-clock");
			System.out.println(doc.select("span[data-reactroot]"));
			System.out.println(span.size());
			String time = span.text();
			for(Element e: span)
				time = time+" "+e.text();
			System.out.println(time);
			return LocalDateTime.parse(time, formatter2);
		}
		catch (IOException e) { return null; }*/
	}

	@Override
	public List<Announcement> getMarketAnnouncements() {

		Document doc;
		try {
			doc = Jsoup.connect(Websites.NZXannouncementsURL).get();
		} catch (IOException e) {
			return new ArrayList<Announcement>();
		}

		Element table = doc.select(announcementsTable).select("tbody").get(0);

		List<Announcement> announcements = new ArrayList<Announcement>();

		List<String> secWhitelist = ConfigProperty.SECURITY_WHITELIST.get();
		List<String> typeWhitelist = ConfigProperty.TYPE_WHITELIST.get();
		List<String> descWhitelist = ConfigProperty.DESCRIPTION_WHITELIST.get();
		List<String> secBlacklist = ConfigProperty.SECURITY_BLACKLIST.get();
		List<String> typeBlacklist = ConfigProperty.TYPE_BLACKLIST.get();
		List<String> descBlacklist = ConfigProperty.DESCRIPTION_BLACKLIST.get();

		for (Element row : table.select("tbody").select("tr")) {
			Elements cells = row.select("td");

			if (cells.isEmpty())
				continue;

			// gets the announcement time
			// stops processing if the time has already been checked
			String dateText = cells.select("td[data-title=Date]").select("span").first().ownText();
			try {
				long time = formatter.parse(dateText.replaceAll("pm", "PM").replaceAll("am", "AM")).getTime();
				if (time < ConfigProperty.LAST_CHECK.get())
					break;
			} catch (ParseException e) {
				FileLogger.createErrorLog(e);
			}


			// Get the company
			// Ensures that the url is a company
			String companyURL = cells.select("td[data-title=Company]").select("span").select("a").attr("href");
			String company = companyURL.substring(companyURL.lastIndexOf("/")+1);
			String type = cells.select("td[data-title=Type]").select("span").text();
			String notification = cells.select("td[data-title=Title]").select("span").select("a").text();

			boolean descWLContains = false;
			for (String s : descWhitelist)
				if (notification.toLowerCase().contains(s.toLowerCase())) {
					descWLContains = true;
					break;
				}
			boolean descBLContains = false;
			for (String s : descBlacklist) {
				if (notification.toLowerCase().contains(s.toLowerCase())) {
					descBLContains = true;
					break;
				}
			}

			// applying filters
			if (!(secWhitelist.isEmpty() && typeWhitelist.isEmpty() && descWhitelist.isEmpty())
					&& !secWhitelist.contains(company.toUpperCase()) && !descWLContains
					&& !typeWhitelist.contains(type.toUpperCase())
					|| (secBlacklist.contains(company.toUpperCase()) || typeBlacklist.contains(type.toUpperCase())
							|| descBLContains))
				continue;

			// url
			String url = Websites.NZXsite + cells.select("td[data-title=Title]").select("span").select("a").attr("href");

			AnnouncementFlag flag = AnnouncementFlag.NONE;
			Element flagCell = cells.select("td[data-title=Title]").first();
			if (!flagCell.ownText().equals("P"))
				flag = AnnouncementFlag.PRICE_SENSITIVE;
			else if (!flagCell.ownText().equals("3"))
				flag = AnnouncementFlag.THIRD_PARTY;
			
			announcements.add(new Announcement(company, companyURL, notification, url, type, dateText, flag));
		}

		return announcements;
	}

	@Override
	public float getSecurityValue(String securityCode) {
		try{
			Document doc = Jsoup.connect(Websites.NZXsecurityURL + securityCode).get();
			return Float.parseFloat(doc.select("span.value").text().replace("$", ""))*100;
		}
		catch (IOException | NumberFormatException | NullPointerException e){
			FileLogger.createErrorLog(e);
		}
		return -1;
	}

	@Override
	public void downloadAttatchments(List<Announcement> announcements) {
		for (Announcement a : announcements) {
			Document doc;
			try {
				doc = Jsoup.connect(a.getUrl()).get();
			} catch (IOException e) {
				return;
			}

			Elements attatchments = doc.select("footer#attachments").get(0).select("ul").select("li");
			for (Element e : attatchments) {
				String fileURL = e.select("a").attr("href");
				String fileName = e.select("a").text() + fileURL.substring(fileURL.lastIndexOf("."));
				try {
					String dirPath = new File(
							new NZXWebApiImp().getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
							+ File.separator + "Past Announcements" + File.separator + a.getNotification() + "--" + a.getTime();
					new File(dirPath).mkdir();
					URL url = new URL("https://www.nzx.com" + fileURL);
					InputStream in = url.openStream();
					Files.copy(in, Paths.get(dirPath + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);
					in.close();
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean canConnect(){
		try {
			new URL("https://www.nzx.com/").openConnection().getContent();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValidSecutiry(String securityCode) {
		Document doc;
		try {
			doc = Jsoup.connect(instrumentSite+securityCode).get();
		} catch (IOException e) {
			return false;
		}

		return doc.select("div[class=small-12.medium-9.columns.content]").isEmpty();
	}
}
