
package jdz.NZXN;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.dataStructs.Announcement.AnnouncementFlag;
import jdz.NZXN.webApi.nzx.NZXWebApi;

public class NZXWebAPITest {
	private final NZXWebApi api = NZXWebApi.getInstance();

	@Test
	public void printSecurityValue() {
		System.out.println(api.getSecurityDollarValue("ATM"));
	}

	@Test
	public void checkConnection() {
		assertTrue(api.canConnect());
	}

	@Test
	public void checkDateTime() {
		int minuteRange = 30;
		LocalDateTime localTime = LocalDateTime.now();
		LocalDateTime time = api.getDateTime();
		assertTrue(time.isAfter(localTime.minusMinutes(minuteRange / 2))
				&& time.isBefore(localTime.plusMinutes(minuteRange / 2)));
	}

	@Test
	public void checkAnnouncements() {
		List<Announcement> announcements = api.getMarketAnnouncements(System.currentTimeMillis() - 864000000L);
		assertTrue(!announcements.isEmpty());
	}

	@Test
	public void checkSaveAnnouncement() {
		String url = "https://www.nzx.com/announcements/318046";
		Announcement announcement = new Announcement("TEST", url, "haha", url, "normal", new Date(), "0", AnnouncementFlag.NONE);

		File rootDir = new File("asdfdir");
		rootDir.mkdirs();
		File announcementFolder = new File(rootDir, announcement.getNotification() + " - " + announcement.getTime());
		File announcementFile = new File(announcementFolder, "Investor Presentation.pdf");

		api.downloadAttatchments(Arrays.asList(announcement), rootDir);
		
		boolean exists = announcementFile.exists();

		announcementFile.delete();
		announcementFolder.delete();
		rootDir.delete();
		
		assertTrue(exists);
	}

	@Test
	public void testValidSecurity() {
		long time = System.currentTimeMillis();

		String[] validSecurities = { "ATM", "NTL" };
		String[] invalidSecurities = { "ASDDD", "IIO" };

		for (String security : validSecurities)
			assertTrue(api.isValidSecutiry(security));
		for (String security : invalidSecurities)
			assertFalse(api.isValidSecutiry(security));

		int numSecurities = validSecurities.length + invalidSecurities.length;
		System.out.println("Time to check validity of " + numSecurities + " securities: "
				+ (System.currentTimeMillis() - time) + "ms");
	}
}
