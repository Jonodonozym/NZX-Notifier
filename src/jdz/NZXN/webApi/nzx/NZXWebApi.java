
package jdz.NZXN.webApi.nzx;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import jdz.NZXN.dataStructs.Announcement;
import lombok.Getter;

public abstract class NZXWebApi {
	@Getter private static final NZXWebApi instance = new NZXWebApiImp();

	public abstract LocalDateTime getDateTime();

	public List<Announcement> getAllMarketAnnouncements() {
		return getMarketAnnouncements(0);
	}

	public abstract List<Announcement> getMarketAnnouncements(long after);

	public abstract float getSecurityDollarValue(String securityCode);

	public abstract void downloadAttatchments(List<Announcement> announcement, File directory);

	public abstract boolean canConnect();

	public abstract boolean isValidSecutiry(String securityCode);
}
