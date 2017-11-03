
package jdz.NZXN.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTests {

	@Test
	public void stringEncryptor() {
		String password = "Jonodonozym";
		
		try {
			String encryptedPassword = StringEncryptor.encrypt(password);
			String decryptedPassword = StringEncryptor.decrypt(encryptedPassword);
			assertEquals(password, decryptedPassword);
			System.out.println("Encrypted password: "+encryptedPassword);
			System.out.println("Decrypted password: "+decryptedPassword);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
