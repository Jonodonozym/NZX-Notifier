/**
 * PasswordVault.java
 *
 * Created by Jaiden Baker on Nov 3, 2017 10:29:14 AM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 4, 2017 2:51:24 PM
 */

package jdz.NZXN.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import jdz.NZXN.utils.StringEncryptor;
import jdz.NZXN.utils.debugging.FileLogger;

public class PasswordVault {
	private static final PasswordVault instance = new PasswordVault();
	
	private String ANZUsername = "", ANZPassword = "";
	private boolean rememberPasswords = false;
	
	private PasswordVault() {
		reload();
	}
	
	public static PasswordVault getInstance() {
		return instance;
	}
	
	public void setANZDetails(String username, String password) {
		ANZUsername = username;
		ANZPassword = password;
	}

	public String getANZUsername() {
		return ANZUsername;
	}
	
	public String getANZPassword() {
		return ANZPassword;
	}
	
	public void save() {
		try {
			File passFile = getPasswordFile();
			passFile.delete();
			passFile.createNewFile();
			
			StringWriter sw = new StringWriter();
			sw.append("RememberPasswords == "+rememberPasswords+"\n");
			if (rememberPasswords) {
				sw.append("ANZ == "+ANZUsername+" || "+ANZPassword);
			}
			String encryptedData = StringEncryptor.encrypt(sw.toString());
			sw.close();
			
			FileWriter fw = new FileWriter(passFile);
			fw.write(encryptedData);
			fw.close();
		}
		catch (IOException | GeneralSecurityException e) {
			FileLogger.createErrorLog(e);
		}
	}
	
	public void reload() {
		File passFile = getPasswordFile();
		if (passFile.exists()){
			try {
				Scanner scanner = new Scanner(passFile);
				String content = StringEncryptor.decrypt(scanner.useDelimiter("\\Z").next());
				scanner.close();
				
				String[] lines = content.split("\n");

				rememberPasswords = Boolean.parseBoolean(lines[0].replaceFirst("RememberPasswords == ", ""));
				if (rememberPasswords) {
					String[] ANZArgs = lines[1].split(" == ")[1].split(" || ");
					ANZUsername = ANZArgs[0];
					ANZPassword = ANZArgs[1];
				}
			}
			catch(IOException | GeneralSecurityException e) {
				FileLogger.createErrorLog(e);
			}
		}
	}
	
	private File getPasswordFile() {
		try {
			File configFile = Config.getInstance().getConfigFile();
			File passwordFolder = new File(configFile.getParentFile(),".temp");
			
			if (!passwordFolder.exists()) {
				passwordFolder.mkdir();
				Path path = Paths.get(passwordFolder.toURI());
				Files.setAttribute(path, "dos:hidden", true);
			}
			
			return new File(passwordFolder, "vst");
		}
		catch(IOException e) {
			FileLogger.createErrorLog(e);
			return null;
		}
	}
}
