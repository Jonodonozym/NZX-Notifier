
package jdz.NZXN.checker;

import java.util.List;

import jdz.NZXN.dataStructs.Announcement;
import lombok.Data;

@Data
public class AnnouncementEvent {
	private final List<Announcement> announcements;
}
