/**
 * ConfigWindow.java
 *
 * Created by Jaiden Baker on Jul 4, 2017 4:05:50 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 1:24:16 PM
 */

package jdz.NZXN.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import jdz.NZXN.Config.Config;
import jdz.NZXN.main.CheckAnnouncementsTask;
import jdz.NZXN.res.Resources;
import jdz.NZXN.utils.SysTrayFrame;

/**
 * A Window that allows the user to change the configuration for the NZX Notifier program
 * offloads pretty much everything to the Panes
 *
 * @author Jaiden Baker
 */
@SuppressWarnings("serial")
public class ConfigWindow extends SysTrayFrame {
	public static ConfigWindow currentWindow = null;
	private MainConfigPane mainConfig;
	private PriceConfigPane priceConfig;
	private AnnounceConfigPane announceConfig;

	/**
	 * Creates and displays a config window
	 */
	public ConfigWindow(){ this(true); }
	
	/**
	 * Creates a config window, optionally displaying it
	 * @param isVisible whether or not the window starts off as visible
	 */
	public ConfigWindow(boolean isVisible) {
		super("NZX Notifier",
				"The NZX Notifier is still running. " + "Double click this icon to re-open the config window.",
				new JPanel(), Resources.appIcon);
		
		setVisible(false);
		setBackground(Color.white);
		setResizable(false);
		currentWindow = this;

		Config config = Config.loadConfig();

		mainConfig = new MainConfigPane(this, config);
		priceConfig = new PriceConfigPane(this, config);
		announceConfig = new AnnounceConfigPane(this, config);

		// adding each panel to a tabbed frame
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Main Config", mainConfig);
		tabbedPane.add("Announcement Filters", announceConfig);
		tabbedPane.add("Price Alerts", priceConfig);

		setContentPane(tabbedPane);

		// auto-save the config before running a check
		CheckAnnouncementsTask.addTaskBeforeCheck(new Runnable() {
			@Override
			public void run() {
				saveConfig();
			}
		});

		// auto-save when the program is closed
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				saveConfig();
			}
		}));

		// center the window and display if isVisible is true
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pack();
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				setLocation(dim.width/2-getWidth()/2, dim.height/2-getHeight()/2);
				setVisible(isVisible);
				toFront();
			}
		});
	}

	/**
	 * Takes the config from this window and saves it externally
	 */
	public void saveConfig() {
		Config config = Config.loadConfig();
		mainConfig.saveConfig(config);
		announceConfig.saveConfig(config);
		priceConfig.saveConfig(config);
		config.save();
	}
	
	/**
	 * Loads the external config and update the window's contents to match
	 */
	public void reloadConfig(){
		Config config = Config.loadConfig();
		mainConfig.reloadConfig(config);
		announceConfig.reloadConfig(config);
		priceConfig.reloadConfig(config);
	}

	/**
	 * Adds a warning before closing about notifications no longer showing
	 */
	@Override
	public void exit() {
		int dialogResult = JOptionPane.showConfirmDialog(new JFrame(),
				"Are you sure you want to exit? This will stop all notifications from displaying.", "Warning",
				JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION) {
			saveConfig();
			System.exit(0);
		}
	}
}
