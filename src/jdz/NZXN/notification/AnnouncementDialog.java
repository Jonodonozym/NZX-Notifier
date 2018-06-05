/**
 * Notification.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 2:25:33 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 1, 2017 11:56:11 AM
 */

package jdz.NZXN.notification;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jdz.NZXN.checker.AnnouncementEvent;
import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.dataStructs.Announcement.AnnouncementFlag;
import jdz.NZXN.utils.swing.JHyperlink;

/**
 * Notification class that displays announcements on the screen
 *
 * @author Jaiden Baker
 */
@SuppressWarnings("serial")
public class AnnouncementDialog extends NotificationDialog {
	public static final Font mainFont = new Font("Calibri", Font.PLAIN, 12);
	private static int maxAnnouncements = 10;
	private List<Announcement> announcements;

	public AnnouncementDialog(AnnouncementEvent event) {
		super();
		this.announcements = event.getAnnouncements();

		// calculating height
		double lines = 5 + (announcements.size() > maxAnnouncements ? maxAnnouncements + 1.5 : announcements.size());
		setMinimumSize(new Dimension(width, (int) (16 * lines)));

		displayContents();
	}

	@Override
	protected JPanel getNotificationPanel() {

		// initializing content panel
		JPanel contentPanel = new JPanel();
		contentPanel.setName(announcements.size() + " New Announcements");
		contentPanel.setLayout(new GridBagLayout());

		// creating new container for the announcements to go in
		Container labelC = new Container();
		labelC.setLayout(new BorderLayout(16, 0));
		
		GridBagConstraints constraints = getConstraints();
		int rows = Math.min(maxAnnouncements, announcements.size());
		JPanel[] columns = getColumnPanels(3, rows);

		// adding each announcement to the columns
		int i = maxAnnouncements;
		for (Announcement a : announcements) {
			if (i-- == 0)
				break;

			String l2Text = a.getNotification();
			if (a.getFlag() == AnnouncementFlag.PRICE_SENSITIVE)
				l2Text = "<html><font color=#DE2700>(P) </font>" + a.getNotification() + "</html>";
			if (a.getFlag() == AnnouncementFlag.THIRD_PARTY)
				l2Text = "<html>(3) " + a.getNotification() + "</html>";

			columns[0].add(new JHyperlink(a.getCompany(), a.getCompanyURL()));
			columns[1].add(new JHyperlink(l2Text, a.getUrl()));
			columns[2].add(new JLabel(a.getTimeString()));
		}

		// adding columns to the container, then to the content panel
		labelC.add(columns[0], BorderLayout.LINE_START);
		labelC.add(columns[1], BorderLayout.CENTER);
		labelC.add(columns[2], BorderLayout.LINE_END);

		contentPanel.add(labelC, constraints);

		// adds 'and x more announcements' label if there are more than maxAnnouncements
		if (announcements.size() > maxAnnouncements) {
			constraints.gridy = 2;
			final JHyperlink l = new JHyperlink("And " + (announcements.size() - maxAnnouncements) + " more...",
					"https://www.nzx.com/markets/NZSX/announcements");
			contentPanel.add(l, constraints);
		}

		// finally, launch all global nukes into the sun with the next line
		return contentPanel;
	}

	private GridBagConstraints getConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0f;
		constraints.weighty = 1.0f;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 12, 12, 12);
		return constraints;
	}

	private JPanel[] getColumnPanels(int numColumns, int numRows) {
		JPanel[] panels = new JPanel[numColumns];
		for (int i = 0; i < numColumns; i++) {
			panels[i] = new JPanel();
			panels[i].setLayout(new GridLayout(numRows, 1));
		}
		return panels;
	}
}