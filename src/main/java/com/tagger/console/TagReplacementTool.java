package com.tagger.console;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagReplacementTool {
	
	static final String DB_NAME = "mydb";
	
	// JDBC URL, database credentials, and table names
	static final String DB_URL = "jdbc:mysql://localhost/"+DB_NAME;
    private static final String USER = "root";
    private static final String PASS = "";
    private static final String TABLE1_NAME = "table1";
    private static final String TABLE2_NAME = "table2";

    private JFrame mainFrame;
    private JComboBox<String> moNameComboBox;
    private JTextArea outputArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagReplacementTool tool = new TagReplacementTool();
            tool.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        mainFrame = new JFrame("Tag Replacement Tool");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        createToolBar();
        createInputPanel();
        createOutputPanel();

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null); // Center the window
        mainFrame.setVisible(true);
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton openButton = new JButton("Open");
        JButton saveButton = new JButton("Save");
        toolBar.add(openButton);
        toolBar.add(saveButton);

        JButton helpButton = new JButton("Help");
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(helpButton);

        JButton aboutButton = new JButton("About");
        toolBar.add(aboutButton);

        // Add action listeners for the buttons here if needed

        mainFrame.add(toolBar, BorderLayout.NORTH);
        
        // Help button action
        helpButton.addActionListener(e -> showHelpDialog());

        // About button action
        aboutButton.addActionListener(e -> showAboutDialog());
    }
    
    private static void showHelpDialog() {
        JOptionPane.showMessageDialog(null,
                "Help content goes here.",
                "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showAboutDialog() {
        JOptionPane.showMessageDialog(null,
                "Tag Replacement Tool\nVersion 1.0\nAuthor: Your Name",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel moNameLabel = new JLabel("Select mo_name:");
        moNameComboBox = new JComboBox<>(getMoNames());
        moNameComboBox.setEditable(true);
        JButton processButton = new JButton("Process");
        inputPanel.add(moNameLabel);
        inputPanel.add(moNameComboBox);
        inputPanel.add(processButton);

        // Add action listener for the "Process" button
        processButton.addActionListener(e -> processMoContent());

        mainFrame.add(inputPanel, BorderLayout.CENTER);
    }

    private void createOutputPanel() {
        outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        mainFrame.add(outputScrollPane, BorderLayout.SOUTH);
    }

    private String[] getMoNames() {
        List<String> moNameList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
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

    private void processMoContent() {
        String moName = moNameComboBox.getSelectedItem().toString();
        StringBuilder resultBuilder = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            createTables(conn);
            String moContent = getMoContent(conn, moName);
            List<String> tags = extractTags(moContent);

            for (String tag : tags) {
                String tagName = getTagName(tag);
                String tagComment = getTagComment(conn, tagName);
                resultBuilder.append("Mo Name: ").append(moName).append("\n");
                resultBuilder.append("Tag Name: ").append(tagName).append("\n");
                resultBuilder.append("Tag Comment: ").append(tagComment).append("\n");

                String replacementText = showInputDialogWithInfo("Enter replacement text for the tag '" + tagName + "':",
                        moName, tagName, tagComment);

                if (replacementText.equals("__ABORT__")) {
                    break; // Abort tag replacement for this mo_name
                }

                moContent = moContent.replace(tag, replacementText);
            }

            showFinalContentWindow(moContent);
            outputArea.setText(resultBuilder.toString());

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // Implement the custom dialog logic here
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

        JButton abortButton = new JButton("Abort");
        panel.add(abortButton);

        int result = JOptionPane.showOptionDialog(null, panel, message, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (result == JOptionPane.OK_OPTION) {
            return textField.getText();
        } else if (result == JOptionPane.CANCEL_OPTION) {
            // Return a special value to indicate "Abort" button was clicked
            return "__ABORT__";
        } else {
            return "";
        }
    }

    // Implement the final content window logic here
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