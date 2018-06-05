/**
 * CheckAnnouncementsTask.java
 *
 * Created by Jaiden Baker on Jul 9, 2017 3:47:44 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 1:28:49 PM
 */

package jdz.NZXN.checker;

import jdz.NZXN.config.FileConfiguration;
import jdz.NZXN.NZXN;
import jdz.NZXN.config.ConfigChangeListener;
import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.gui.ConfigWindow;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdz.NZXN.utils.ComparePrice;
import jdz.NZXN.utils.SleepModeDetector;
import jdz.NZXN.utils.SleepModeListener;
import jdz.NZXN.utils.StringUtils;
import jdz.NZXN.webApi.nzx.NZXWebApi;
import lombok.Getter;

public class AnnouncementChecker implements SleepModeListener {
	@Getter private static final AnnouncementChecker instance = new AnnouncementChecker();

	private TimerTask runningTask = null;
	private Thread checkThread = null;
	private List<Runnable> runBeforeCheck = new ArrayList<Runnable>();
	private List<Runnable> runAfterCheck = new ArrayList<Runnable>();
	private List<Runnable> runEachSecond = new ArrayList<Runnable>();
	private int intervalSeconds = 300;
	@Getter private int secondsSinceCheck = 0;
	@Getter private long lastCheck = System.currentTimeMillis();

	public AnnouncementChecker() {
		ConfigChangeListener.register(ConfigProperty.CHECK_INTERVAL_MINUTES, (newValue) -> {
			setIntervalMinutes(newValue);
		});

		SleepModeDetector.addListener(this);
	}

	public void start() {
		if (runningTask != null)
			throw new RuntimeException("Error: only 1 CheckAnnouncementsTask can exist at a time");

		runningTask = new TimerTask() {
			@Override
			public void run() {
				if (++secondsSinceCheck >= intervalSeconds)
					check();
				for (Runnable r : runEachSecond)
					r.run();
			}
		};

		intervalSeconds = ConfigProperty.CHECK_INTERVAL_MINUTES.get() * 60;
		lastCheck = ConfigProperty.LAST_CHECK.get();

		new Timer().schedule(runningTask, 1000, 1000);
	}

	/**
	 * Runs the check on a separate thread and makes sure only 1 check can be done
	 * at a time
	 */
	public void check() {
		if (checkThread == null) {
			checkThread = new Thread() {
				@Override
				public void run() {
					doCheck();
					checkThread = null;
				}
			};
			checkThread.run();
		}
	}

	private void doCheck() {
		for (Runnable r : runBeforeCheck)
			r.run();

		secondsSinceCheck = 0;
		lastCheck = System.currentTimeMillis();
		if (NZXWebApi.getInstance().canConnect()) {
			LocalDateTime lastCheckDateTime = NZXWebApi.getInstance().getDateTime();
			if (lastCheckDateTime == null)
				lastCheck = System.currentTimeMillis();
			else
				lastCheck = lastCheckDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

			if (ConfigProperty.ANNOUNCEMENT_ALERTS_ENABLED.get()) {
				List<Announcement> announcements = NZXWebApi.getInstance()
						.getMarketAnnouncements(ConfigProperty.LAST_CHECK.get());
				if (!announcements.isEmpty()) {
					NZXN.getEventBus().post(new AnnouncementEvent(announcements));
					if (ConfigProperty.ANNOUNCEMENT_SAVING_ENABLED.get())
						NZXWebApi.getInstance().downloadAttatchments(announcements,
								ConfigProperty.ANNOUNCEMENT_SAVING_FOLDER.get());
				}
			}

			if (ConfigProperty.PRICE_ALERTS_ENABLED.get()) {
				List<String> prices = ConfigProperty.PRICE_ALERTS.get();
				List<String> toRemove = new ArrayList<String>();
				List<String> toAdd = new ArrayList<String>();
				for (String s : prices) {
					List<String> args = StringUtils.parseList(s, ":");
					if (args.get(0).length() != 3)
						continue;

					try {
						float newValue = NZXWebApi.getInstance().getSecurityDollarValue(args.get(0)) * 100;
						float oldValue = Float.parseFloat(args.get(2));
						if (ComparePrice.checkPrice(oldValue, newValue, args.get(1))) {
							toRemove.add(s);
							if (args.get(1).equals("Any change"))
								toAdd.add(args.get(0) + ":" + args.get(1) + ":" + newValue);
							NZXN.getEventBus().post(new PriceChangeEvent(args.get(0), newValue, oldValue));
						}
					}
					catch (NumberFormatException e) {}

				}
				prices.removeAll(toRemove);
				prices.addAll(toAdd);
				ConfigProperty.PRICE_ALERTS.set(prices);
			}
			ConfigProperty.LAST_CHECK.set(lastCheck);
		}

		FileConfiguration.save();
		if (ConfigWindow.currentWindow != null)
			ConfigWindow.currentWindow.reloadConfig();
		for (Runnable r : runAfterCheck)
			r.run();
	}

	public void setIntervalMinutes(int minutes) {
		intervalSeconds = minutes * 60;
	}

	public void addTaskBeforeCheck(Runnable r) {
		runBeforeCheck.add(r);
	}

	public void addTaskAfterCheck(Runnable r) {
		runAfterCheck.add(r);
	}

	public void addTaskEachSecond(Runnable r) {
		runEachSecond.add(r);
	}

	public long getCurrentTime() {
		return lastCheck + 1000 * secondsSinceCheck;
	}

	public long getNextCheck() {
		return lastCheck + 1000 * intervalSeconds;
	}

	@Override
	public void onDeviceWake() {
		// TODO Auto-generated method stub

	}
}