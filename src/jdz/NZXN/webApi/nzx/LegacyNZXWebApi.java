/**
 * NZXWebAPI.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 8:43:41 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 9:18:01 AM
 */

package jdz.NZXN.webApi.nzx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jdz.NZXN.config.Config;
import jdz.NZXN.dataStructs.Announcement;

@Deprecated
public class LegacyNZXWebApi {
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy, h:mma", Locale.ENGLISH);
	public static DateTimeFormatter formatter2 = new DateTimeFormatterBuilder().appendPattern("eee dd MMMM HH:mm:ss")
			.parseDefaulting(ChronoField.YEAR, Year.now().getValue()).toFormatter(Locale.ENGLISH);
	public static String site = "https://www.nzx.com";
	public static String securityURL = "https://www.nzx.com/markets/NZSX/securities/";
	public static String announcementsURL = "https://www.nzx.com/markets/NZSX/announcements";
	
	public static String announcementsTable = "table#announcements-table";

	public static LocalDateTime getNZXTime() {

		Document doc;
		try {
			doc = Jsoup.connect(announcementsURL).get();
		} catch (IOException e) {
			return null;
		}

		String time = "";
		for (Element e : doc.getElementsByClass("current_time").select("span")) {
			time = (time.equals("")) ? e.ownText() : time + " " + e.ownText();
		}
		return LocalDateTime.parse(time, formatter2);

	}

	public static List<Announcement> getAnnouncements(Config config) {		
		Document doc;
		try {
			doc = Jsoup.connect(announcementsURL).get();
		} catch (IOException e) {
			return new ArrayList<Announcement>();
		}

		Element table = doc.select(announcementsTable).select("tbody").get(0);

		List<Announcement> announcements = new ArrayList<Announcement>();

		List<String> secWhitelist = config.getSecWhitelist();
		List<String> typeWhitelist = config.getTypeWhitelist();
		List<String> descWhitelist = config.getDescWhitelist();
		List<String> secBlacklist = config.getSecBlacklist();
		List<String> typeBlacklist = config.getTypeBlacklist();
		List<String> descBlacklist = config.getDescBlacklist();

		for (Element row : table.select("tr")) {
			Elements cells = row.select("td");

			if (cells.isEmpty())
				continue;

			// gets the announcement time
			// stops processing if the time has already been checked
			String dateText = cells.get(2).ownText();
			if (config.getLastCheck() != null) {
				LocalDateTime date = LocalDateTime.parse(dateText.replaceAll("pm", "PM").replaceAll("am", "AM"),
						formatter);
				if (date.isBefore(config.getLastCheck()))
					break;
			}

			// Get the security
			// Ensures that the url is a security
			String companyURL = site+cells.get(0).select("a").attr("href");
			String company = companyURL.substring(companyURL.lastIndexOf("/")+1);
			String type = cells.get(3).text();
			String notification = cells.get(1).select("a").text();
			String url = site+cells.get(1).select("a").attr("href");
			boolean isPriceSensitive = cells.get(1).select("a").hasClass("announcement-flag price-sensitive");
			boolean isThirdParty = cells.get(1).select("a").hasClass("announcement-flag third-party");

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

			announcements.add(new Announcement(company, companyURL, notification, url, type, dateText, isPriceSensitive, isThirdParty));
		}

		return announcements;
	}

	public static double getValue(String security) throws IOException, NumberFormatException {
		Document doc = Jsoup.connect(securityURL + security).get();
		return Double.parseDouble(doc.select("span.value").text().replace("$", ""))*100;
	}

	public static void downloadAttatchments(List<Announcement> announcements) {
		for (Announcement a : announcements) {
			Document doc;
			try {
				doc = Jsoup.connect(a.url).get();
			} catch (IOException e) {
				return;
			}

			Elements attatchments = doc.select("footer#attachments").get(0).select("ul").select("li");
			for (Element e : attatchments) {
				String fileURL = e.select("a").attr("href");
				String fileName = e.select("a").text() + fileURL.substring(fileURL.lastIndexOf("."));
				try {
					String dirPath = new File(
							new LegacyNZXWebApi().getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
							+ File.separator + "Past Announcements" + File.separator + a.notification + ", " + a.time;
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

	public static void addToCSV(List<Announcement> announcements) {
		try {
			File csv = getCSVFile();
			if (csv == null)
				return;
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
			for (Announcement a : announcements) {
				bw.write(a.company + "," + "\"=HYPERLINK(\"\"" + a.url + "\"\",\"\"" + a.notification.replace(")", "").replace("(", "") + "\"\")\"" + ","
						+ a.type + "," + a.time);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getCSVFile() {
		try {
			String dirPath = Paths.get(LegacyNZXWebApi.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.toFile() + File.separator + "Past Announcements";
			String pastAnnPath = dirPath + File.separator + "Past Announcements.csv";

			new File(dirPath).mkdir();

			File pastAnn = new File(pastAnnPath);
			if (!pastAnn.exists()) {
				pastAnn.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(pastAnn, true));
				bw.write("Security,Notification,Type,Date,Time");
				bw.newLine();
				bw.close();
			}
			return pastAnn;
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean checkConnection(){
		try {
			new URL("https://www.nzx.com/").openConnection().getContent();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
