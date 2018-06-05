
package jdz.NZXN;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import jdz.NZXN.checker.AnnouncementEvent;
import jdz.NZXN.checker.PriceChangeEvent;
import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.dataStructs.Announcement.AnnouncementFlag;
import jdz.NZXN.gui.UIManager;
import jdz.NZXN.notification.NotificationListener;

public class NotificationTest {
	private final NotificationListener listener = new NotificationListener();

	@Test
	public void testStacking() {
		UIManager.useCleanStyle();
		listener.onPriceChangeEvent(new PriceChangeEvent("NTL", 0.15, 0.14));
		Announcement a = new Announcement("NTL", "https://www.nzx.com/instruments/NTL", "test", "", "", new Date(), "now",
				AnnouncementFlag.NONE);
		listener.onAnnouncementEvent(new AnnouncementEvent(Arrays.asList(a)));
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
