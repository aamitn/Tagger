package com.tagger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONObject;
import org.json.JSONTokener;

public class TagReplacementToolConfig {

	public TagReplacementToolConfig()
	{
		loadConfig();
	}

	static final String CONFIG_FILE_PATH = "C:\\trtconfig.json";

	// Configuration variables (from trtconfig.json) - Replace with your own values

	private   String DB_URL = "";
	private   String USER = "";
	private   String PASS = "";
	private   String TABLE1_NAME = "";
	private   String TABLE2_NAME = "";

	private  void loadConfig() {
		try {
			String configData = readFile(CONFIG_FILE_PATH);
			JSONObject configJson = new JSONObject(new JSONTokener(configData));

			setDB_URL(configJson.getString("DB_URL"));
			setUSER(configJson.getString("USER"));
			setPASS(configJson.getString("PASS"));
			//DB_NAME = configJson.getString("DB_NAME");
			setTABLE1_NAME(configJson.getString("TABLE1_NAME"));
			setTABLE2_NAME(configJson.getString("TABLE2_NAME"));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error loading configuration from " + CONFIG_FILE_PATH,
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private static String readFile(String filePath) throws IOException {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
		}
		return content.toString();
	}

	/**
	 * @return the dB_URL
	 */
	public String getDB_URL() {
		return DB_URL;
	}

	/**
	 * @param dB_URL the dB_URL to set
	 */
	public void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}

	/**
	 * @return the uSER
	 */
	public String getUSER() {
		return USER;
	}

	/**
	 * @param uSER the uSER to set
	 */
	public void setUSER(String uSER) {
		USER = uSER;
	}

	/**
	 * @return the pASS
	 */
	public String getPASS() {
		return PASS;
	}

	/**
	 * @param pASS the pASS to set
	 */
	public void setPASS(String pASS) {
		PASS = pASS;
	}

	/**
	 * @return the tABLE1_NAME
	 */
	public String getTABLE1_NAME() {
		return TABLE1_NAME;
	}

	/**
	 * @param tABLE1_NAME the tABLE1_NAME to set
	 */
	public void setTABLE1_NAME(String tABLE1_NAME) {
		TABLE1_NAME = tABLE1_NAME;
	}

	/**
	 * @return the tABLE2_NAME
	 */
	public String getTABLE2_NAME() {
		return TABLE2_NAME;
	}

	/**
	 * @param tABLE2_NAME the tABLE2_NAME to set
	 */
	public void setTABLE2_NAME(String tABLE2_NAME) {
		TABLE2_NAME = tABLE2_NAME;
	}
}
