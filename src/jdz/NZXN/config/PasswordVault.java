/**
 * PasswordVault.java
 *
 * Created by Jaiden Baker on Nov 3, 2017 10:29:14 AM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 3, 2017 11:30:00 AM
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

import jdz.NZXN.utils.FileLogger;
import jdz.NZXN.utils.StringEncryptor;

public class PasswordVault {
	private static final PasswordVault instance = new PasswordVault();
	
	private String ANZUsername = "", ANZPassword = "";
	
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
			sw.append("ANZ == "+ANZUsername+" || "+ANZPassword);
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
				String content = scanner.useDelimiter("\\Z").next();
				scanner.close();
				String[] args = content.split(" == ")[1].split(" || ");
				ANZUsername = args[0];
				ANZPassword = args[1];
			}
			catch(IOException e) {
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
