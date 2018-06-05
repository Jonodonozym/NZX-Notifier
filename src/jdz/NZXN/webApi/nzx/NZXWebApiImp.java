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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.dataStructs.Announcement.AnnouncementFlag;
import jdz.NZXN.utils.debugging.FileLogger;
import jdz.NZXN.webApi.Websites;

class NZXWebApiImp extends NZXWebApi {
	public static DateFormat formatter = new SimpleDateFormat("d/M/yyyy, h:mm a");
	public static DateFormat dateExporter = new SimpleDateFormat("d-M-yyyy, h-mm a");

	private static final String announcementsTable = "table.table-to-list.announcements-table";
	private static final String instrumentSite = "https://www.nzx.com/instruments/";

	private static List<String> secWhitelist;
	private static List<String> typeWhitelist;
	private static List<String> descWhitelist;
	private static List<String> secBlacklist;
	private static List<String> typeBlacklist;
	private static List<String> descBlacklist;

	@Override
	@Deprecated // currently, jsoup cannot run javascript which loads the time
	public LocalDateTime getDateTime() {
		return LocalDateTime.now();
	}

	@Override
	public List<Announcement> getMarketAnnouncements(long lastCheck) {
		Document doc;
		try {
			doc = Jsoup.connect(Websites.NZXannouncementsURL).get();
		}
		catch (IOException e) {
			return new ArrayList<Announcement>();
		}

		reloadConfig();
		List<Announcement> announcements = new ArrayList<Announcement>();

		Element table = doc.select(announcementsTable).select("tbody").get(0);

		for (Element row : table.select("tr")) {
			Elements cells = row.select("td");

			if (cells.isEmpty())
				continue;

			// gets the announcement time
			// stops processing if the time has already been checked
			String dateText = cells.select("td[data-title=Date]").select("span").first().ownText();
			Date date;
			try {
				date = formatter.parse(dateText.replaceAll("pm", "PM").replaceAll("am", "AM"));
				long time = date.getTime();
				if (time < lastCheck)
					break;
			}
			catch (ParseException e) {
				FileLogger.createErrorLog(e);
				continue;
			}
			dateText = dateExporter.format(date);

			// Get the company
			// Ensures that the url is a company
			String companyURL = cells.select("td[data-title=Company]").select("span").select("a").attr("href");
			String company = companyURL.substring(companyURL.lastIndexOf("/") + 1);
			String type = cells.select("td[data-title=Type]").select("span").text();
			String notification = cells.select("td[data-title=Title]").select("span").select("a").text()
					.replaceAll("\\\\|/|:", "-").replaceAll("\"", "");

			if (!checkWhitelisted(notification, type, company))
				continue;

			// url
			String url = Websites.NZXsite
					+ cells.select("td[data-title=Title]").select("span").select("a").attr("href");

			AnnouncementFlag flag = AnnouncementFlag.NONE;
			Element flagCell = cells.select("td[data-title=Title]").first();
			if (!flagCell.ownText().equals("P"))
				flag = AnnouncementFlag.PRICE_SENSITIVE;
			else if (!flagCell.ownText().equals("3"))
				flag = AnnouncementFlag.THIRD_PARTY;

			announcements.add(new Announcement(company, companyURL, notification, url, type, date, dateText, flag));
		}

		return announcements;
	}

	private void reloadConfig() {
		secWhitelist = ConfigProperty.SECURITY_WHITELIST.get();
		typeWhitelist = ConfigProperty.TYPE_WHITELIST.get();
		descWhitelist = ConfigProperty.DESCRIPTION_WHITELIST.get();
		secBlacklist = ConfigProperty.SECURITY_BLACKLIST.get();
		typeBlacklist = ConfigProperty.TYPE_BLACKLIST.get();
		descBlacklist = ConfigProperty.DESCRIPTION_BLACKLIST.get();
	}

	private boolean checkWhitelisted(String notification, String type, String company) {
		for (String s : descBlacklist)
			if (notification.toLowerCase().contains(s.toLowerCase()))
				return false;

		if (secBlacklist.contains(company.toUpperCase()))
			return false;

		if (typeBlacklist.contains(type.toUpperCase()))
			return false;

		if (!descWhitelist.isEmpty()) {
			boolean descWLContains = false;
			for (String s : descWhitelist)
				if (notification.toLowerCase().contains(s.toLowerCase())) {
					descWLContains = true;
					break;
				}
			if (!descWLContains)
				return false;
		}

		if (!secWhitelist.isEmpty() && !secWhitelist.contains(company.toUpperCase()))
			return false;

		if (!typeWhitelist.isEmpty() && !typeWhitelist.contains(type.toUpperCase()))
			return false;

		return true;
	}

	@Override
	public float getSecurityDollarValue(String securityCode) {
		try {
			Document doc = Jsoup.connect(Websites.NZXsecurityURL + securityCode).get();
			return Float.parseFloat(doc.select("div.small-12.medium-5.columns").select("h1").text().replace("$", ""));
		}
		catch (IOException | NumberFormatException | NullPointerException e) {
			FileLogger.createErrorLog(e);
		}
		return -1;
	}


	private final Executor es = Executors.newCachedThreadPool();
	private final SimpleDateFormat monthFormatter = new SimpleDateFormat("LLLL", Locale.getDefault());

	@SuppressWarnings("deprecation")
	@Override
	public void downloadAttatchments(List<Announcement> announcements, File directory) {
		for (Announcement a : announcements) {
			es.execute(() -> {
				Document doc;
				try {
					doc = Jsoup.connect(a.getUrl()).get();
				}
				catch (IOException e) {
					return;
				}

				String extraDir = "";
				String year = "" + (a.getTime().getYear() + 1900);
				if (ConfigProperty.GROUP_BY_YEAR.get())
					extraDir = year + File.separator;
				else if (ConfigProperty.GROUP_BY_MONTH.get())
					extraDir += year + " " + monthFormatter.format(a.getTime()) + File.separator;
				File folder = new File(directory, extraDir + a.getCompany() + " - " + a.getNotification());

				int i = 0;
				while (folder.exists())
					folder = new File(directory, a.getCompany() + " - " + a.getNotification() + "(" + (++i) + ")");

				folder.mkdirs();

				Elements attatchments = doc.select("div.panel.module.documents").select("ul").select("li");
				for (Element e : attatchments) {
					String fileURL = e.select("a").attr("href");
					String fileName = e.select("a").text() + fileURL.substring(fileURL.lastIndexOf("."))
							.replaceAll("\\\\|/|:", "-").replaceAll("\"", "");
					fileName = fileName.replaceFirst("pdf document: ", "");
					try {
						URL url = new URL(fileURL);
						InputStream in = url.openStream();

						File file = new File(folder, fileName);
						Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
						file.setLastModified(a.getTime().getTime());
						in.close();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				folder.setLastModified(a.getTime().getTime());
			});
		}
	}

	@Override
	public boolean canConnect() {
		try {
			new URL("https://www.nzx.com/").openConnection().getContent();
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValidSecutiry(String securityCode) {
		Document doc;
		try {
			doc = Jsoup.connect(instrumentSite + securityCode).get();
		}
		catch (IOException e) {
			return false;
		}

		return doc.select("div[class=small-12.medium-9.columns.content]").isEmpty();
	}
}
