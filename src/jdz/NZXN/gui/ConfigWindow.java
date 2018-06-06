/**
 * ConfigWindow.java
 *
 * Created by Jaiden Baker on Jul 4, 2017 4:05:50 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 24, 2017 1:24:16 PM
 */

package jdz.NZXN.gui;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import jdz.NZXN.checker.AnnouncementChecker;
import jdz.NZXN.config.ConfigChangeListener;
import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.resources.Resources;
import jdz.NZXN.utils.swing.JSysTrayFrame;

/**
 * A Window that allows the user to change the configuration for the NZX
 * Notifier program
 * offloads pretty much everything to the Panes
 *
 * @author Jaiden Baker
 */
@SuppressWarnings("serial")
public class ConfigWindow extends JSysTrayFrame {
	public static ConfigWindow currentWindow = null;
	private MainConfigPane mainConfig;
	private PriceConfigPane priceConfig;
	private AnnounceConfigPane announceConfig;
	private ANZLoginPanel ANZLogin;

	/**
	 * Creates and displays a config window
	 */
	public ConfigWindow() {
		this(true);
	}

	/**
	 * Creates a config window, optionally displaying it
	 * 
	 * @param isVisible whether or not the window starts off as visible
	 */
	public ConfigWindow(boolean isVisible) {
		super("NZX Notifier",
				"The NZX Notifier is still running. " + "Double click the tray icon to re-open the config window.",
				new JPanel(), Resources.appIcon);

		MenuItem configureItem = new MenuItem("Configure");
		configureItem.addActionListener((a) -> {
			setVisible(true);
			setExtendedState(Frame.NORMAL);
		});
		addSysTrayMenuItem(configureItem);


		setVisible(false);
		setBackground(Color.white);
		setResizable(false);
		currentWindow = this;

		mainConfig = new MainConfigPane(this);
		priceConfig = new PriceConfigPane(this);
		announceConfig = new AnnounceConfigPane(this);
		ANZLogin = new ANZLoginPanel(this);

		CheckboxMenuItem muteItem = new CheckboxMenuItem("Mute", ConfigProperty.IS_MUTED.get());
		muteItem.addItemListener((l) -> {
			ConfigProperty.IS_MUTED.set(l.getStateChange() == ItemEvent.SELECTED);
		});
		ConfigChangeListener.register(ConfigProperty.IS_MUTED, (newValue) -> {
			muteItem.setState(newValue);
		});
		addSysTrayMenuItem(muteItem);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Main Config", mainConfig);
		tabbedPane.add("Announcement Filters", announceConfig);
		tabbedPane.add("Price Alerts", priceConfig);
		tabbedPane.add("ANZ", ANZLogin);

		setContentPane(tabbedPane);

		AnnouncementChecker.getInstance().addTaskBeforeCheck(() -> {
			saveConfig();
		});

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			saveConfig();
		}));

		SwingUtilities.invokeLater(() -> {
			pack();
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);
			setVisible(isVisible);
			toFront();
		});
	}

	/**
	 * Takes the config from this window and saves it externally
	 */
	public void saveConfig() {
		mainConfig.saveConfig();
		announceConfig.saveConfig();
		priceConfig.saveConfig();
	}

	/**
	 * Loads the external config and update the window's contents to match
	 */
	public void reloadConfig() {
		mainConfig.reloadConfig();
		announceConfig.reloadConfig();
		priceConfig.reloadConfig();
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
