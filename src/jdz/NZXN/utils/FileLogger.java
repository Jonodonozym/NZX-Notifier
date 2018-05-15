/**
 * FileLogger.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.NZXN.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
/**
 * Lets you log plugin messages in a file
 * Also lets you log errors in the file instead of displaying a big ugly message on the console
 *
 * @author Jonodonozym
 */
public final class FileLogger {
	@Getter private static final FileLogger instance = new FileLogger();
	
	private BufferedWriter defaultLogWriter = null;
	private final String logName;
	private final String logDirectory;

	public FileLogger() {
		this("Log");
	}
	
	public FileLogger(String logName) {
		this.logDirectory = new File("").getPath();
		this.logName = logName;
	}
	
	/**
	 * Starts a new log file
	 * 
	 * you probably never need to do this, I just use it for a few methods myself and thought I should share.
	 * Aren't I a wonderful developer?
	 */
	private void startNewLog(){
		try{
			if (defaultLogWriter != null)
				defaultLogWriter.close();

			File file = new File(logDirectory + File.separator + logName+" "+getTimestamp()+".txt");
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (!file.exists())
				file.createNewFile();
			
			defaultLogWriter = new BufferedWriter(new FileWriter(file));
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
	}
	
	/**
	 * Logs a message to the current log file
	 * creates a new log file if one isn't already in use
	 * @param message
	 */
	public void log(String message){
		try{
			if (defaultLogWriter == null)
				startNewLog();
			defaultLogWriter.write(getTimestampShort()+": "+message);
			defaultLogWriter.newLine();
			defaultLogWriter.flush();
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
	}
	
	/**
	 * Writes an exception's stack trace to an error log file, given an exception and extra information you might want to tack on to help debugging
	 * 
	 * @param exception
	 */
	public void createErrorLog(Exception exception, String... extraData) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		if (extraData.length > 0) {
			pw.println();
			pw.println("Extra data:");
			for (String s : extraData)
				pw.println('\t' + s);
		}
		pw.flush();
		String exceptionAsString = sw.toString();
		createErrorLog(logDirectory + File.separator + "Errors" + File.separator+exception.getClass().getSimpleName()
				+ getTimestamp() + ".txt", exceptionAsString);
	}

	/**
	 * Writes an error message to an error log file
	 * 
	 * @param exception
	 */
	public void createErrorLog(String error) {
		createErrorLog(logDirectory + File.separator + "Errors" + File.separator+"Error "
				+ getTimestamp() + ".txt", error);
		}

	/**
	 * Writes an error message to an error log file
	 * 
	 * @param exception
	 */
	public void createErrorLog(String fileDir, String error) {
		log("An error occurred. Check the Error log file for details.");

		File file = new File(fileDir);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		
		String header = "An error occurred in the program. If you can't work out the issue from this file,\n"
				+ "send this file to the developer with a description of the failure\n";
	
		writeFile(header,error,file);
	}
	
	public String getTimestamp(){
		return new SimpleDateFormat("yyyy-MM-dd  HH-mm-ss-SSS").format(new Date());
	}
	
	public String getTimestampShort(){
		return "["+new SimpleDateFormat("HH-mm-ss.SSS").format(new Date())+"]";
	}
	
	private void writeFile(String header, String contents, File file){
		try {
			if (!file.exists())
				file.createNewFile();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			if (header != ""){
				String[] lines = header.split("\n");
				for (String line: lines) {
					bfw.write(line);
					bfw.newLine();
				}
				bfw.newLine();
			}
			String[] lines = contents.split("\n");
			for (String line: lines) {
				bfw.newLine();
				bfw.write(line);
			}
			bfw.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}


}
