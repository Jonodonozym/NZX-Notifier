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
import lombok.Getter;

public class PasswordVault {
	@Getter private static LoginDetails ANZDetails = LoginDetails.getEmpty();
	@Getter private static boolean passwordRemembered = false;
	
	static {
		reload();
	}

	public static void setANZDetails(String username, String password) {
		ANZDetails = new LoginDetails(username,password);
	}
	
	public static void save() {
		try {
			File passFile = getPasswordFile();
			passFile.delete();
			passFile.createNewFile();
			
			StringWriter sw = new StringWriter();
			sw.append("RememberPasswords == "+passwordRemembered+"\n");
			if (passwordRemembered) {
				sw.append("ANZ == "+ANZDetails.getUsername()+" || "+ANZDetails.getPassword());
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
	
	public static void reload() {
		File passFile = getPasswordFile();
		if (passFile.exists()){
			try {
				Scanner scanner = new Scanner(passFile);
				String content = StringEncryptor.decrypt(scanner.useDelimiter("\\Z").next());
				scanner.close();
				
				String[] lines = content.split("\n");

				passwordRemembered = Boolean.parseBoolean(lines[0].replaceFirst("RememberPasswords == ", ""));
				if (passwordRemembered) {
					String[] ANZArgs = lines[1].split(" == ")[1].split(" || ");
					ANZDetails = new LoginDetails(ANZArgs[0],  ANZArgs[1]);
				}
			}
			catch(IOException | GeneralSecurityException e) {
				FileLogger.createErrorLog(e);
			}
		}
	}
	
	private static File getPasswordFile() {
		try {
			File configFile = FileConfiguration.getDefaultConfigFile();
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
