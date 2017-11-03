
package jdz.NZXN.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.webApi.nzx.NZXWebApiImp;

public class AnnouncementIO {
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
			String dirPath = Paths.get(NZXWebApiImp.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.toFile() + File.separator + "Past Announcements";
			String pastAnnPath = dirPath + File.separator + "Past Announcements.csv";

			new File(dirPath).mkdirs();

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
}
