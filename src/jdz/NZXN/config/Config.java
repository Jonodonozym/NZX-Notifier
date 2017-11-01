/**
 * Config.java
 *
 * Created by Jaiden Baker on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 18, 2017 11:21:22 AM
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
import java.util.List;
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
	private Properties props;
	
	// setters
	public void setLastCheck(LocalDateTime time){ props.setProperty("LastCheck", time.toString()); }
	public void setInterval(int interval){ props.setProperty("CheckIntervalMinutes", ""+interval); }
	public void setAnnEnabled(boolean isEnabled){ props.setProperty("AnnouncementEnabled", ""+isEnabled); }
	public void setAnnSaveEnabled(boolean isEnabled){ props.setProperty("AnnouncementSaving", ""+isEnabled); }
	public void setSecWhitelist(List<String> securities){ props.setProperty("SecWhitelist", mergeList(securities).toUpperCase()); }
	public void setTypeWhitelist(List<String> securities){ props.setProperty("TypeWhitelist", mergeList(securities).toUpperCase()); }
	public void setDescWhitelist(List<String> securities){ props.setProperty("DescWhitelist", mergeList(securities).toLowerCase()); }
	public void setSecBlacklist(List<String> securities){ props.setProperty("SecBlacklist", mergeList(securities).toUpperCase()); }
	public void setTypeBlacklist(List<String> securities){ props.setProperty("TypeBlacklist", mergeList(securities).toUpperCase()); }
	public void setDescBlacklist(List<String> securities){ props.setProperty("DescBlacklist", mergeList(securities).toLowerCase()); }
	public void setPriceEnabled(boolean isEnabled){ props.setProperty("PricesEnabled", ""+isEnabled); }
	public void setPriceAlerts(List<String> priceAlerts){ props.setProperty("PriceAlerts", mergeList(priceAlerts)); }
	public void setMuted(boolean isMuted){ props.setProperty("isMuted", isMuted+""); NotificationManager.setAlwaysOnTop(!isMuted);}
	
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
	public static Config loadConfig(){
		Config config = new Config();
		try {
			config.props = new Properties();
			config.props.load(new FileInputStream(getConfigFile()));
		} catch (IOException e) {
			try {
				getConfigFile().delete();
				config.props = new Properties();
				config.props.load(new FileInputStream(getConfigFile()));
				JOptionPane.showMessageDialog(new JFrame(), "The config file has been incorrectly modified, default config has been restored");
			} catch (IOException e1) {
				e.printStackTrace();
				e1.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "Error loading the default config. Please contact support if the problem persists.");
			}
		}
		
		return config;
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
	private static File getConfigFile(){
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
