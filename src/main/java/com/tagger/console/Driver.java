package com.tagger.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Driver {

	// JDBC URL, database credentials, and table names
	static final String DB_URL = "jdbc:mysql://localhost/mydb";
	static final String USER = "root";
	static final String PASS = "";
	static final String TABLE1_NAME = "table1";
	static final String TABLE2_NAME = "table2";

    public static void main(String[] args) {
        createGUI();
    }

    private static void createGUI() {
        JFrame frame = new JFrame("Tag Replacement Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel moNameLabel = new JLabel("Enter mo_name:");
        JTextField moNameField = new JTextField(15);
        JButton processButton = new JButton("Process");
        inputPanel.add(moNameLabel);
        inputPanel.add(moNameField);
        inputPanel.add(processButton);

        // Output panel
        JTextArea outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(outputScrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // Button action listener
        processButton.addActionListener(e -> {
            String moName = moNameField.getText();
            String result = processMoContent(moName);
            outputArea.setText(result);
        });
    }

    private static String processMoContent(String moName) {
        StringBuilder resultBuilder = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Create the tables if they don't exist
            createTables(conn);

            // Retrieve mo_content based on mo_name
            String moContent = getMoContent(conn, moName);

            // Get the tags for the given mo_name and store them in a collection
            List<String> tags = extractTags(moContent);

            // Process and replace tags in mo_content
            for (String tag : tags) {
                String tagName = getTagName(tag);
                String tagComment = getTagComment(conn, tagName);
                resultBuilder.append("Mo Name: ").append(moName).append("\n");
                resultBuilder.append("Tag Name: ").append(tagName).append("\n");
                resultBuilder.append("Tag Comment: ").append(tagComment).append("\n");

                // Get user input for the replacement text using a custom dialog
                String replacementText = showInputDialogWithInfo("Enter replacement text for the tag '" + tagName + "':",
                        moName, tagName, tagComment);

                // Replace the tag with the user's input
                moContent = moContent.replace(tag, replacementText);
            }

            // Display the final content in a separate window
            showFinalContentWindow(moContent);

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return resultBuilder.toString();
    }

    // Custom dialog to show tag information along with input field
    private static String showInputDialogWithInfo(String message, String moName, String tagName, String tagComment) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Mo Name: " + moName));
        panel.add(new JLabel("Tag Name: " + tagName));
        panel.add(new JLabel("\nTag Comments: "));

        JTextArea commentArea = new JTextArea(tagComment);
        commentArea.setEditable(false);
        JScrollPane commentScrollPane = new JScrollPane(commentArea);
        panel.add(commentScrollPane);
        panel.add(new JLabel("\n\nReplacement: "));
        JTextField textField = new JTextField(20);
        panel.add(textField);

        int result = JOptionPane.showOptionDialog(null, panel, message, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null);

        return (result == JOptionPane.OK_OPTION) ? textField.getText() : "";
    }

    private static void showFinalContentWindow(String moContent) {
        JFrame finalContentFrame = new JFrame("Final Content");
        finalContentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        finalContentFrame.setLayout(new BorderLayout());

        // Final content panel
        JTextArea finalContentArea = new JTextArea(moContent);
        finalContentArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        finalContentArea.setEditable(true);
        JScrollPane finalContentScrollPane = new JScrollPane(finalContentArea);

        // Add a popup menu to the final content area
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem editItem = new JMenuItem("Edit");
        popupMenu.add(copyItem);
        popupMenu.add(editItem);

        finalContentArea.setComponentPopupMenu(popupMenu);

        // "Copy" action to copy the content to the clipboard
        copyItem.addActionListener(e -> {
            StringSelection selection = new StringSelection(finalContentArea.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        });

        // "Edit" action to enable editing of the content
        editItem.addActionListener(e -> finalContentArea.setEditable(true));

        // Disable editing when the popup menu is closed
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // Do nothing
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                finalContentArea.setEditable(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Do nothing
            }
        });

        finalContentFrame.add(finalContentScrollPane, BorderLayout.CENTER);
        finalContentFrame.setPreferredSize(new Dimension(800, 600));
        finalContentFrame.pack();
        // Center the final content window on the screen
        finalContentFrame.setLocationRelativeTo(null);
        finalContentFrame.setVisible(true);
    }


	    // Rest of the code remains unchanged
	    // ...
	    // ...
    
    
    
	// Create the tables if they don't exist
	private static void createTables(Connection conn) throws SQLException {
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

	// Retrieve mo_content based on mo_name
	private static String getMoContent(Connection conn, String moName) throws SQLException {
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

	// Extract tags from mo_content
	private static List<String> extractTags(String moContent) {
		List<String> tags = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\{#(.*?)\\}");
		Matcher matcher = pattern.matcher(moContent);
		while (matcher.find()) {
			tags.add(matcher.group());
		}
		return tags;
	}

	// Get the tag name from the tag
	private static String getTagName(String tag) {
		// Assuming the tag format is {#tagname this is a tag}
		int start = tag.indexOf('#') + 1;
		int end = tag.indexOf(' ', start);
		return tag.substring(start, end);
	}

	// Get tag comment from table2
	private static String getTagComment(Connection conn, String tagName) throws SQLException {
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
}