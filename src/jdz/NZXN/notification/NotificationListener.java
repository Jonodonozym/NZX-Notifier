
package jdz.NZXN.notification;

import com.google.common.eventbus.Subscribe;

import jdz.NZXN.checker.AnnouncementEvent;
import jdz.NZXN.checker.PriceChangeEvent;

public class NotificationListener {
	@Subscribe public void onAnnouncementEvent(AnnouncementEvent event) {
		NotificationManager.add(new AnnouncementDialog(event));
	}
	
	@Subscribe public void onPriceChangeEvent(PriceChangeEvent event) {
		NotificationManager.add(new PriceDialog(event));
	}
}
