package com.tagger.console;

import java.security.MessageDigest;
import java.util.Date;

public class hasher {

	public String createHash() throws Exception {
		// Get the current timestamp
		Date timestamp = new Date();

		// Create a MessageDigest instance
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		// Convert the timestamp to a byte array
		byte[] bytes = timestamp.toString().getBytes();

		// Calculate the hash of the timestamp
		byte[] hash = md.digest(bytes);

		// Convert the hash to a hexadecimal string
		String hashString = bytesToHexString(hash);

		return hashString;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			int value = bytes[i] & 0xFF;
			sb.append(Integer.toHexString(value));
		}
		return sb.toString();
	}


}