/**
 * CheckAnnouncementsTask.java
 *
 * Created by Jaiden Baker on Jul 9, 2017 3:47:44 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 1:28:49 PM
 */

package jdz.NZXN.main;

import jdz.NZXN.Notification.PriceNotification;
import jdz.NZXN.WebApi.NZXWebApi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdz.NZXN.Config.Config;
import jdz.NZXN.GUI.ConfigWindow;
import jdz.NZXN.IO.AnnouncementIO;
import jdz.NZXN.Notification.AnnouncementNotification;
import jdz.NZXN.Notification.Notification;
import jdz.NZXN.Notification.NotificationManager;
import jdz.NZXN.structs.Announcement;
import jdz.NZXN.utils.ComparePrice;

public class CheckAnnouncementsTask{
	private static ActualTask runningTask = null;
	private static Thread checkThread = null;
	private static List<Runnable> runBeforeCheck = new ArrayList<Runnable>();
	private static List<Runnable> runAfterCheck = new ArrayList<Runnable>();
	private static List<Runnable> runEachSecond = new ArrayList<Runnable>();
	private static int secondsSinceCheck = 0;
	private static int intervalSeconds = 300;
	private static LocalDateTime lastCheck = LocalDateTime.now();
	
	public static void start(){
		if (runningTask != null)
			throw new RuntimeException("Error: only 1 CheckAnnouncementsTask can exist at a time");
		runningTask = new ActualTask();
		Config config = Config.loadConfig();
		intervalSeconds = config.getInterval()*60;
		lastCheck = config.getLastCheck();
		
		new Timer().schedule(runningTask, 1000, 1000);
	}
	
	private static class ActualTask extends TimerTask{
		@Override
		public void run() {
			if (++secondsSinceCheck >= intervalSeconds)
				check();
			for (Runnable r: runEachSecond)
				r.run();
		}
	}
	
	/**
	 * Runs the check on a separate thread and makes sure only 1 check can be done at a time
	 */
	public static void check(){
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
	
	private static void doCheck(){
		for (Runnable r: runBeforeCheck)
			r.run();

		Config config = Config.loadConfig();
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
	
	public static void setIntervalMinutes(int minutes){ intervalSeconds = minutes*60; }
	public static LocalDateTime getLastCheck(){ return lastCheck; }
	public static void addTaskBeforeCheck(Runnable r){ runBeforeCheck.add(r); }
	public static void addTaskAfterCheck(Runnable r){ runAfterCheck.add(r); }
	public static void addTaskEachSecond(Runnable r){ runEachSecond.add(r); }

	public static LocalDateTime getCurrentTime() {
		return lastCheck.plusSeconds(secondsSinceCheck);
	}

	public static LocalDateTime getNextCheck() {
		return lastCheck.plusSeconds(intervalSeconds);
	}
}