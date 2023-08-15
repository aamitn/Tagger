package com.tagger;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagReplacementToolModel {
	static String DB_URL;
	static String USER;
	static String PASS;
	static String TABLE1_NAME;
	static String TABLE2_NAME;


	public TagReplacementToolModel(String dbUrl, String user, String pass, String table1, String table2) {	
		TagReplacementToolModel.DB_URL = dbUrl;
		TagReplacementToolModel.USER = user;
		TagReplacementToolModel.PASS = pass;
		TagReplacementToolModel.TABLE1_NAME = table1;
		TagReplacementToolModel.TABLE2_NAME = table2;   
	}

	// Create the tables if they don't exist
	static void createTables(Connection conn) throws SQLException {
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
	}

	public static String @NotNull [] getMoNames() {
		List<String> moNameList = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT mo_name FROM " + TABLE1_NAME)) {

			while (rs.next()) {
				moNameList.add(rs.getString("mo_name"));
			}
		} catch (SQLException e) {
			TagReplacementToolView.showSQLErrorMessage(e);
			e.printStackTrace();
		}
		return moNameList.toArray(new String[0]);
	}

	public static boolean isMoNameExists(String moName) {
		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
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

	// Retrieve mo_content based on mo_name
	static String getMoContent(Connection conn, String moName) throws SQLException {
		String moContent = null;
		String query = "SELECT mo_content FROM " + TABLE1_NAME + " WHERE mo_name = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, moName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					moContent = rs.getString("mo_content");
				}
			}
		}
		return moContent;
	}


	// Get the tag name from the tag
	public static String getTagName(String tag) {
		// Check if the tag contains any content (anything after the space)
		int spaceIndex = tag.indexOf(' ');
		if (spaceIndex != -1) {
			// If the tag has content, extract the tag name
			return tag.substring(2, spaceIndex);
		} else {
			// If the tag has no content, return the entire tag without the {# and } characters
			return tag.substring(2, tag.length() - 1);
		}
	}

	static String getTagComment(Connection conn, String tagName) throws SQLException {
		String tagComment = null;
		String query = "SELECT tag_comment FROM " + TABLE2_NAME + " WHERE mo_tags = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, tagName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					tagComment = rs.getString("tag_comment");
				}
			}
		}
		return tagComment;
	}

	public static List<String> extractTags(String moContent) {
		List<String> tags = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\{#(.*?)}");
		Matcher matcher = pattern.matcher(moContent);
		while (matcher.find()) {
			tags.add(matcher.group());
		}
		return tags;
	}
	// Other methods for database operations, e.g., retrieving mo_content, tags, tag comments, etc.
}