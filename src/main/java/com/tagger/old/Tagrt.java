package com.tagger.old;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Tagrt {
	
	private static final String CONFIG_FILE_PATH = "C:\\Users\\amit\\eclipse-workspace\\Tagger\\src\\trtconfig.json";

	private static String DB_URL;
	private static String USER;
	private static String PASS;
	private static String TABLE1_NAME;
	private static String TABLE2_NAME;

    // Create the tables if they don't exist
    public void createTables() {
        try (Connection conn = DriverManager.getConnection(getDB_URL(), getUSER(), getPASS())) {
            try (Statement stmt = conn.createStatement()) {
                String createTable1Query = "CREATE TABLE IF NOT EXISTS " + TABLE1_NAME + " (" +
                        "mo_name TEXT PRIMARY KEY, " +
                        "mo_content TEXT, " +
                        "mo_tags TEXT)";
                stmt.execute(createTable1Query);

                String createTable2Query = "CREATE TABLE IF NOT EXISTS " + TABLE2_NAME + " (" +
                        "mo_tags TEXT PRIMARY KEY, " +
                        "tag_comment TEXT)";
                stmt.execute(createTable2Query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve mo_content based on mo_name
    public String getMoContent(String moName) {
        String moContent = null;
        String query = "SELECT mo_content FROM " + TABLE1_NAME + " WHERE mo_name = ?";
        try (Connection conn = DriverManager.getConnection(getDB_URL(), getUSER(), getPASS());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, moName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    moContent = rs.getString("mo_content");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moContent;
    }

    // Extract tags from mo_content
    public List<String> extractTags(String moContent) {
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{#(.*?)\\}");
        Matcher matcher = pattern.matcher(moContent);
        while (matcher.find()) {
            tags.add(matcher.group());
        }
        return tags;
    }

    // Get the tag name from the tag
    public String getTagName(String tag) {
        // Assuming the tag format is {#tagname this is a tag}
        int start = tag.indexOf('#') + 1;
        int end = tag.indexOf(' ', start);
        return tag.substring(start, end);
    }

    public String getTagComment(String tagName) {
        String tagComment = null;
        String query = "SELECT tag_comment FROM " + TABLE2_NAME + " WHERE mo_tags = ?";
        try (Connection conn = DriverManager.getConnection(getDB_URL(), getUSER(), getPASS());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tagName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tagComment = rs.getString("tag_comment");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagComment;
    }

    public boolean isMoNameExists(String moName) {
        try (Connection conn = DriverManager.getConnection(getDB_URL(), getUSER(), getPASS());
             PreparedStatement pstmt = conn.prepareStatement("SELECT mo_name FROM " + TABLE1_NAME + " WHERE mo_name = ?")) {
            pstmt.setString(1, moName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Return true if a row with the given mo_name exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    String[] getMoNames() {
        List<String> moNameList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(getDB_URL(), getUSER(), getPASS());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT mo_name FROM " + TABLE1_NAME)) {

            while (rs.next()) {
                moNameList.add(rs.getString("mo_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moNameList.toArray(new String[0]);
    }
    
    void loadConfig() {
        try {
            String configData = readFile(CONFIG_FILE_PATH);
            JSONObject configJson = new JSONObject(new JSONTokener(configData));

            setDB_URL(configJson.getString("DB_URL"));
            setUSER(configJson.getString("USER"));
            setPASS(configJson.getString("PASS"));
            TABLE1_NAME = configJson.getString("TABLE1_NAME");
            TABLE2_NAME = configJson.getString("TABLE2_NAME");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading configuration from " + CONFIG_FILE_PATH,
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }


	public static String getUSER() {
		return USER;
	}

	public static void setUSER(String uSER) {
		USER = uSER;
	}

	public static String getPASS() {
		return PASS;
	}

	public static void setPASS(String pASS) {
		PASS = pASS;
	}

	public static String getDB_URL() {
		return DB_URL;
	}

	public static void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}
}