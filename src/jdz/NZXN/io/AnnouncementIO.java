
package jdz.NZXN.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import jdz.NZXN.checker.AnnouncementEvent;
import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.dataStructs.Announcement;

public class AnnouncementIO {
	@Subscribe public void onAnnouncement(AnnouncementEvent event) {
		addToCSV(event.getAnnouncements());
	}
	
	public static void addToCSV(List<Announcement> announcements) {
		try {
			File csv = getCSVFile();
			if (csv == null)
				return;
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
			for (Announcement a : announcements) {
				bw.write(a.getCompany() + "," + "\"=HYPERLINK(\"\"" + a.getUrl() + "\"\",\"\""
						+ a.getNotification().replace(")", "").replace("(", "") + "\"\")\"" + "," + a.getType() + ","
						+ a.getTime() + "," + a.getFlag());
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getCSVFile() {
		try {
			File AnnouncementFolder = ConfigProperty.ANNOUNCEMENT_SAVING_FOLDER.get();
			if (!AnnouncementFolder.exists())
				AnnouncementFolder.mkdirs();

			File CSVFile = new File(AnnouncementFolder, "Past Announcements.csv");
			if (!CSVFile.exists()) {
				CSVFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(CSVFile, true));
				bw.write("Security,Notification,Type,Date,Time,Flag");
				bw.newLine();
				bw.close();
			}
			return CSVFile;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
