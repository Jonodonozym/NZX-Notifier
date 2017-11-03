/**
 * CheckAnnouncementsTask.java
 *
 * Created by Jaiden Baker on Jul 9, 2017 3:47:44 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 1:28:49 PM
 */

package jdz.NZXN.main;

import jdz.NZXN.config.Config;
import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.gui.ConfigWindow;
import jdz.NZXN.io.AnnouncementIO;
import jdz.NZXN.notification.AnnouncementNotification;
import jdz.NZXN.notification.Notification;
import jdz.NZXN.notification.NotificationManager;
import jdz.NZXN.notification.PriceNotification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdz.NZXN.utils.ComparePrice;
import jdz.NZXN.utils.SleepModeDetector;
import jdz.NZXN.utils.SleepModeListener;
import jdz.NZXN.webApi.nzx.NZXWebApi;
import lombok.Getter;

public class CheckAnnouncementsTask implements SleepModeListener{
	@Getter private static final CheckAnnouncementsTask instance = new CheckAnnouncementsTask();
	
	private TimerTask runningTask = null;
	private Thread checkThread = null;
	private List<Runnable> runBeforeCheck = new ArrayList<Runnable>();
	private List<Runnable> runAfterCheck = new ArrayList<Runnable>();
	private List<Runnable> runEachSecond = new ArrayList<Runnable>();
	private int intervalSeconds = 300;
	@Getter private int secondsSinceCheck = 0;
	@Getter private LocalDateTime lastCheck = LocalDateTime.now();
	
	public CheckAnnouncementsTask() {
		Config.getInstance().addListener("CheckIntervalMinutes", (e)->{
			setIntervalMinutes(Integer.parseInt(e.getNewValue()));
			});
		SleepModeDetector.addListener(this);
	}
	
	public void start(){
		if (runningTask != null)
			throw new RuntimeException("Error: only 1 CheckAnnouncementsTask can exist at a time");
		runningTask = new TimerTask(){
			@Override
			public void run() {
				if (++secondsSinceCheck >= intervalSeconds)
					check();
				for (Runnable r: runEachSecond)
					r.run();
			}
		};
		Config config = Config.getInstance();
		intervalSeconds = config.getInterval()*60;
		lastCheck = config.getLastCheck();
		
		new Timer().schedule(runningTask, 1000, 1000);
	}
	
	/**
	 * Runs the check on a separate thread and makes sure only 1 check can be done at a time
	 */
	public void check(){
		if (checkThread == null){
			checkThread = new Thread(){
				@Override
				public void run(){
					doCheck();
					checkThread = null;
				}
			};
			checkThread.run();
		}
	}
	
	private void doCheck(){
		for (Runnable r: runBeforeCheck)
			r.run();

		Config config = Config.getInstance();
		secondsSinceCheck = 0;
		lastCheck = LocalDateTime.now();
		if (NZXWebApi.instance.canConnect())
		{
			lastCheck = NZXWebApi.instance.getDateTime();
			if (lastCheck == null)
				lastCheck = LocalDateTime.now();
			
			List<Notification> notifications = new ArrayList<Notification>();
			
			if (config.getAnnEnabled()){
				List<Announcement> a = NZXWebApi.instance.getMarketAnnouncements(config);
				if (!a.isEmpty()){
					if (config.getAnnSaveEnabled())
							NZXWebApi.instance.downloadAttatchments(a);
					AnnouncementIO.addToCSV(a);
					notifications.add(new AnnouncementNotification(a));
				}
			}
			
			if (config.getPriceEnabled()){
				List<String> prices = config.getPriceAlerts();
				List<String> toRemove = new ArrayList<String>();
				List<String> toAdd = new ArrayList<String>();
				for (String s: prices){
					List<String> args = Config.parseList(s, ":");
					if (args.get(0).length() != 3)
						continue;
					
					try {
						float value = NZXWebApi.instance.getSecurityValue(args.get(0));
						float currentValue = Float.parseFloat(args.get(2));
						if (ComparePrice.checkPrice(currentValue, value, args.get(1))){
							toRemove.add(s);
							if (args.get(1).equals("Any change"))
								toAdd.add(args.get(0)+":"+args.get(1)+":"+value);
							notifications.add(new PriceNotification(args.get(0), value, currentValue));
						}
					} catch (NumberFormatException e) { }
					
				}
				prices.removeAll(toRemove);
				prices.addAll(toAdd);
				config.setPriceAlerts(prices);
			}
			NotificationManager.add(notifications);
			config.setLastCheck(lastCheck);
		}
		
		config.save();
		if (ConfigWindow.currentWindow != null)
			ConfigWindow.currentWindow.reloadConfig();
		for (Runnable r: runAfterCheck)
			r.run();
	}
	
	public void setIntervalMinutes(int minutes){ intervalSeconds = minutes*60; }
	public void addTaskBeforeCheck(Runnable r){ runBeforeCheck.add(r); }
	public void addTaskAfterCheck(Runnable r){ runAfterCheck.add(r); }
	public void addTaskEachSecond(Runnable r){ runEachSecond.add(r); }

	public LocalDateTime getCurrentTime() {
		return lastCheck.plusSeconds(secondsSinceCheck);
	}

	public LocalDateTime getNextCheck() {
		return lastCheck.plusSeconds(intervalSeconds);
	}

	@Override
	public void onDeviceWake() {
		// TODO Auto-generated method stub
		
	}
}