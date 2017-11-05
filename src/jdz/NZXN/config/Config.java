/**
 * Config.java
 *
 * Created by Jaiden Baker on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 4, 2017 2:51:33 PM
 */

package jdz.NZXN.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jdz.NZXN.notification.NotificationManager;
import jdz.NZXN.utils.ExportFile;

/**
 * Configuration class that acts as an interface saving, loading, reading and writing the config across program
 * sessions
 * 
 * Really just a ton of getters and setters with save / load methods and a few helper methods for List IO
 *
 * @author Jaiden Baker
 */
public class Config {
	private static final Config instance = new Config();
	
	private Properties props;
	private Map<String, List<ConfigChangeListener>> listeners = new HashMap<String, List<ConfigChangeListener>>();

	private Config() {
		reload();
	}
	
	public static Config getInstance(){
		return instance;
	}

	public void addListener(String property, ConfigChangeListener l){
		if (!listeners.containsKey(property))
			listeners.put(property, new ArrayList<ConfigChangeListener>());
		listeners.get(property).add(l);
	}

	// setters
	public void setProperty(String property, Object value){
		String newValue = value.toString();
		
		ConfigChangeEvent e = new ConfigChangeEvent(property, props.getProperty(property), newValue);
		props.setProperty(property, newValue);
		
		if (listeners.containsKey(property))
			for (ConfigChangeListener l: listeners.get(property))
				l.onConfigChange(e);
	}
	
	public void setLastCheck(LocalDateTime time){ setProperty("LastCheck", time); }
	public void setInterval(int interval){ setProperty("CheckIntervalMinutes", ""+interval); }
	public void setAnnEnabled(boolean isEnabled){ setProperty("AnnouncementEnabled", ""+isEnabled); }
	public void setAnnSaveEnabled(boolean isEnabled){ setProperty("AnnouncementSaving", ""+isEnabled); }
	public void setSecWhitelist(List<String> securities){ setProperty("SecWhitelist", mergeList(securities).toUpperCase()); }
	public void setTypeWhitelist(List<String> securities){ setProperty("TypeWhitelist", mergeList(securities).toUpperCase()); }
	public void setDescWhitelist(List<String> securities){ setProperty("DescWhitelist", mergeList(securities).toLowerCase()); }
	public void setSecBlacklist(List<String> securities){ setProperty("SecBlacklist", mergeList(securities).toUpperCase()); }
	public void setTypeBlacklist(List<String> securities){ setProperty("TypeBlacklist", mergeList(securities).toUpperCase()); }
	public void setDescBlacklist(List<String> securities){ setProperty("DescBlacklist", mergeList(securities).toLowerCase()); }
	public void setPriceEnabled(boolean isEnabled){ setProperty("PricesEnabled", ""+isEnabled); }
	public void setPriceAlerts(List<String> priceAlerts){ setProperty("PriceAlerts", mergeList(priceAlerts)); }
	public void setMuted(boolean isMuted){ setProperty("isMuted", isMuted+""); NotificationManager.setAlwaysOnTop(!isMuted);}
	
	// getters
	public LocalDateTime getLastCheck(){
		String str = props.getProperty("LastCheck");
		try{ return LocalDateTime.parse(str); }
		catch (DateTimeParseException e){ return null; }
	}
	
	public int getInterval(){ return Integer.parseInt(props.getProperty("CheckIntervalMinutes")); }
	public boolean getAnnEnabled(){ return Boolean.parseBoolean(props.getProperty("AnnouncementEnabled")); }
	public boolean getAnnSaveEnabled(){ return Boolean.parseBoolean(props.getProperty("AnnouncementSaving")); }
	public List<String> getSecWhitelist(){ return parseList(props.getProperty("SecWhitelist")); }
	public List<String> getTypeWhitelist(){ return parseList(props.getProperty("TypeWhitelist")); }
	public List<String> getDescWhitelist(){ return parseList(props.getProperty("DescWhitelist")); }
	public List<String> getSecBlacklist(){ return parseList(props.getProperty("SecBlacklist")); }
	public List<String> getTypeBlacklist(){ return parseList(props.getProperty("TypeBlacklist")); }
	public List<String> getDescBlacklist(){ return parseList(props.getProperty("DescBlacklist")); }
	public boolean getPriceEnabled(){ return Boolean.parseBoolean(props.getProperty("PricesEnabled")); }
	public List<String> getPriceAlerts(){ return parseList(props.getProperty("PriceAlerts")); }
	public boolean getMuted(){ return Boolean.parseBoolean(props.getProperty("isMuted")); }
	
	// helper method to merge a list into a single string
	public String mergeList(List<String> list){ return mergeList(list, ","); }
	
	// merge a list into a single string with a specific delimiter
	public String mergeList(List<String> list, String separator){
		StringBuilder sb = new StringBuilder(list.size()*32);
		for(String s: list){
			if (sb.length() != 0)
				sb.append(separator);
			sb.append(s);
		}
		return sb.toString();
	}

	// split a string into a list
	private List<String> parseList(String list){ return parseList(list, ","); }
	
	// split a string into a list with a specific delimiter
	public static List<String> parseList(String list, String seperator){
		if (list.equals("")) return new ArrayList<String>();
		List<String> retList = new ArrayList<String>(Arrays.asList(list.split(seperator)));
		for (String s: retList){
			s = s.trim();
		}
		return new ArrayList<String>(retList);
	}
	
	// static method which creates a new Config instance from the config file in appdata
	public void reload(){
		try {
			props = new Properties();
			props.load(new FileInputStream(getConfigFile()));
		} catch (IOException e) {
			try {
				getConfigFile().delete();
				props = new Properties();
				props.load(new FileInputStream(getConfigFile()));
				JOptionPane.showMessageDialog(new JFrame(), "The config file has been incorrectly modified, default config has been restored");
			} catch (IOException e1) {
				e.printStackTrace();
				e1.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "Error loading the default config. Please contact support if the problem persists.");
			}
		}
	}
	
	// saves this config to the config file
	public void save(){ 
		try {
			File file = getConfigFile();
			file.delete();
			file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			props.store(os, "Config for NZX notifier");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error saving config. Please contact support if the problem persists.");
		}
	}
	
	// get the config file from the appdata folder, creating a default config file if one doesn't exist
	File getConfigFile(){
		try {
			String dirPath = System.getenv("APPDATA")+File.separator+"NZX Notifier";
			String filePath = dirPath+File.separator+"Config.cfg";
			
			File dir = new File(dirPath);
				dir.mkdir();
				
			File file = new File(filePath);
			if (!file.exists())
				ExportFile.ExportResource("/jdz/NZXN/res/Config.cfg", dirPath);
			
			return file;
		}
		catch (Exception e) { } // wonder what would happen if this screws up
		return null;
	}
}
