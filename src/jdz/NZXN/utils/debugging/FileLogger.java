package jdz.NZXN.utils.debugging;

import java.awt.Color;

/**
 * FileLogger.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 22, 2017 3:29:47 PM
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

/**
 * Lets you log messages in a file Also lets you log errors in the file instead
 * of displaying a big ugly message on the console
 *
 * @author Jonodonozym
 */
public final class FileLogger {
	private static BufferedWriter defaultLogWriter = null;
	private static final String defaultErrorLogMessage = "An error occurred in the application. If you can't work out the issue from this file, send this file to the developer with a description of the failure";

	/**
	 * Starts a new log file
	 * 
	 * you probably never need to do this, I just use it for a few methods
	 * myself and thought I should share. Aren't I a wonderful developer?
	 */
	private static void startNewLog() {
		try {
			if (defaultLogWriter != null)
				defaultLogWriter.close();

			createDefaultDirectory(getLogsDirectory());
			File file = new File(getLogsDirectory() + File.separator + "Log " + getTimestamp() + ".txt");
			if (!file.exists())
				file.createNewFile();

			defaultLogWriter = new BufferedWriter(new FileWriter(file));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Logs a message to the current log file creates a new log file if one
	 * isn't already in use
	 * 
	 * @param message
	 */
	public static void log(String message) {
		try {
			if (defaultLogWriter == null)
				startNewLog();
			defaultLogWriter.write(getTimestampShort() + ": " + message);
			defaultLogWriter.newLine();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Writes an exception's stack trace to an error log file, given an
	 * exception and extra information you might want to tack on to help
	 * debugging
	 * 
	 * @param exception
	 */
	public static void createErrorLog(Exception exception, String... extraData) {
		PrintWriter pw = new PrintWriter(new StringWriter());
		exception.printStackTrace(pw);
		pw.println();
		pw.println("Extra data:");
		for (String s : extraData)
			pw.println('\t' + s);
		String exceptionAsString = pw.toString();
		createErrorLog(getLogsDirectory() + File.separator + "Errors" + File.separator + exception.getClass().getName()
				+ getTimestamp() + ".txt", exceptionAsString);
	}

	/**
	 * Writes an exception's stack trace to an error log file, given an
	 * exception
	 * 
	 * @param exception
	 */
	public static void createErrorLog(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = "\n" + sw.toString();
		createErrorLog(getLogsDirectory() + File.separator + "Errors" + File.separator + exception.getClass().getName()
				+ getTimestamp() + ".txt", exceptionAsString);
	}

	/**
	 * Writes an error message to an error log file
	 * 
	 * @param exception
	 */
	public static void createErrorLog(String fileDir, String error) {
		createErrorFrame();

		createDefaultDirectory(getLogsDirectory());
		createDefaultDirectory(getLogsDirectory() + File.separator + "Errors");

		File file = new File(fileDir);

		writeFile(
				defaultErrorLogMessage,
				error, file);
	}

	/**
	 * Writes an error message to an error log file
	 * 
	 * @param exception
	 */
	public static void createErrorLog(String error) {
		createErrorFrame();

		createDefaultDirectory(getLogsDirectory());
		createDefaultDirectory(getLogsDirectory() + File.separator + "Errors");

		File file = new File(getLogsDirectory() + File.separator + "Errors" + File.separator + "Error " + getTimestamp() + ".txt");

		writeFile(
				defaultErrorLogMessage,
				error, file);
	}

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Panel.background", Color.WHITE);
			UIManager.put("OptionPane.background", Color.WHITE);

			UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			UIManager.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			UIManager.put("TabbedPane.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			UIManager.put("ComboBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		} catch (Exception e) {
			createErrorLog(e);
		}
	}

	private static void createErrorFrame() {
		JOptionPane.showMessageDialog(null,
				"A fatal error occurred while running the program.\n"
						+ "Check the latest error log under {installLocation}/Logs/Errors for more details.\n"
						+ "If you cannot resolve the issue, please send the error log file to the developers.",
				"Fatal Error", JOptionPane.ERROR_MESSAGE);
	}

	private static String getLogsDirectory() {
		return "Logs";
	}

	private static void createDefaultDirectory(String directory) {
		File file = new File(directory);
		if (!file.exists())
			file.mkdirs();
	}

	private static String getTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd  HH-mm-ss").format(new Date());
	}

	private static String getTimestampShort() {
		return "[" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + "]";
	}

	private static void writeFile(String header, String contents, File file) {
		try {
			if (!file.exists())
				file.createNewFile();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			if (header != "") {
				bfw.write(header);
				bfw.newLine();
				bfw.newLine();
			}
			bfw.write(contents);
			bfw.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void main(String[] args) {
		createErrorFrame();
	}
}
