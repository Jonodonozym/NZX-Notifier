
package jdz.NZXN.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import jdz.NZXN.utils.debugging.FileLogger;

public final class StringEncryptor {
	private static final byte[] XORKey = "a7^ndj*oOhn%@l".getBytes();
	private static final SecretKeySpec cipherKey = createSecretKey("aoid0oiw832dasf1".toCharArray(),
			"i^%98q]E".getBytes(), 40000, 128);

	private static final SecretKeySpec createSecretKey(char[] keyPartA, byte[] keyPartB, int iterationCount,
			int keyLength) {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec keySpec = new PBEKeySpec(keyPartA, keyPartB, iterationCount, keyLength);
			SecretKey keyTmp = keyFactory.generateSecret(keySpec);
			return new SecretKeySpec(keyTmp.getEncoded(), "AES");
		}
		catch (Exception e) {
			FileLogger.createErrorLog(e);
			System.exit(1);
		}
		return null;
	}

	public static final String encrypt(String string) throws GeneralSecurityException, UnsupportedEncodingException {
		Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		pbeCipher.init(Cipher.ENCRYPT_MODE, cipherKey);
		AlgorithmParameters parameters = pbeCipher.getParameters();
		IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
		byte[] cryptoText = pbeCipher.doFinal(string.getBytes("UTF-8"));
		byte[] iv = ivParameterSpec.getIV();
		String chiperedString = base64Encode(iv) + ":" + base64Encode(cryptoText);

		return new String(xorWithKey(chiperedString.getBytes(), XORKey));
	}

	public static final String decrypt(String string) throws GeneralSecurityException, IOException {
		String deXORed = new String(xorWithKey(string.getBytes(), XORKey));

		String iv = deXORed.split(":")[0];
		String property = deXORed.split(":")[1];
		Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		pbeCipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(base64Decode(iv)));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}


	private static final String base64Encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	private static final byte[] base64Decode(String property) throws IOException {
		return Base64.getDecoder().decode(property);
	}

	private static final byte[] xorWithKey(byte[] in, byte[] key) {
		Random r = new Random(13487139487L);
		byte[] out = new byte[in.length];
		for (int i = 0; i < in.length; i++)
			out[i] = r.nextDouble() < 0.75 ? (byte) (in[i] ^ key[i % key.length]) : in[i];
		return out;
	}
}
