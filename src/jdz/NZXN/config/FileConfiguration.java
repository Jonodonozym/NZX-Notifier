/**
 * Config.java
 *
 * Created by Jaiden Baker on Jul 2, 2017 4:05:40 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 4, 2017 2:51:33 PM
 */

package jdz.NZXN.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdz.NZXN.config.ConfigChangeListener.Listener;
import jdz.NZXN.utils.ExportFile;
import jdz.NZXN.utils.debugging.FileLogger;

/**
 * Configuration class that acts as an interface for saving, loading, reading
 * and
 * writing the config across program sessions
 *
 * @author Jaiden Baker
 */
public class FileConfiguration {
	private static final Map<ConfigProperty<?>, Object> properties = new HashMap<ConfigProperty<?>, Object>();
	private static final File file = getDefaultConfigFile();

	static {
		reloadDefaults();
		reload();
	}

	static <T> void set(ConfigProperty<T> property, T value) {
		List<Listener<T>> listeners = ConfigChangeListener.getListeners(property);
		if (!listeners.isEmpty()) {
			T oldValue = get(property);
			for (Listener<T> l : listeners)
				l.onConfigChange(oldValue, value);
		}

		properties.put(property, value);
	}

	@SuppressWarnings("unchecked")
	static <T> T get(ConfigProperty<T> property) {
		if (!properties.containsKey(property))
			properties.put(property, property.getDefaultValue());
		return (T) properties.get(property);
	}

	/**
	 * saves this config to the config file
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void save() {
		try {
			file.delete();
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("# Config for NZX notifier");
			bw.newLine();
			bw.write("# " + FileLogger.getTimestamp());
			bw.newLine();

			for (ConfigProperty p : ConfigProperty.getAll()) {
				bw.write(p.getName() + " = " + p.toString(properties.get(p)));
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			FileLogger.createErrorLog(e);
		}
	}

	/**
	 * Attempts to load the config from the config file, loading defaults if one
	 * does not exist
	 */
	public static void reload() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null && !line.equals("")) {
				if (line.startsWith("#")) {
					line = br.readLine();
					continue;
				}

				int equalsIndex = line.indexOf("=");
				int commentIndex = line.indexOf("#");

				String name = line.substring(0, equalsIndex).trim();
				String value = commentIndex == -1 ? line.substring(equalsIndex + 1)
						: line.substring(equalsIndex + 1, commentIndex);
				value = value.trim();

				for (ConfigProperty<?> property : ConfigProperty.getAll())
					if (property.getName().equalsIgnoreCase(name)) {
						properties.put(property, property.parse(value));
						break;
					}

				line = br.readLine();
			}
			br.close();
		}
		catch (IOException e) {
			FileLogger.createErrorLog(e);
			reloadDefaults();
		}
	}

	private static void reloadDefaults() {
		properties.clear();
		for (ConfigProperty<?> p : ConfigProperty.getAll())
			properties.put(p, p.getDefaultValue());
	}

	/**
	 * Get the config file from the appdata folder, creating a default config file
	 * if one doesn't exist
	 * 
	 * @return
	 */
	static File getDefaultConfigFile() {
		try {
			String dirPath = System.getenv("APPDATA") + File.separator + "NZX Notifier";
			String filePath = dirPath + File.separator + "Config.cfg";

			File dir = new File(dirPath);
			dir.mkdir();

			File file = new File(filePath);
			if (!file.exists())
				ExportFile.ExportResource("/jdz/NZXN/res/Config.cfg", dirPath);

			return file;
		}
		catch (Exception e) {
			FileLogger.createErrorLog(e);
		} // wonder what would happen if this screws up
		return null;
	}
}
