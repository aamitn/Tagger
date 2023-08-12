package com.tagger;

import javax.swing.*;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class TagReplacementToolView {
	JFrame mainFrame;
	private JComboBox<String> moNameComboBox;
	JTextArea outputArea;

	public TagReplacementToolView() {
		createAndShowGUI();
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

		JButton openButton = new JButton("Open URL");
		JButton saveButton = new JButton("Save");
		JButton loadButton = new JButton("Load");
		JButton refreshButton = new JButton("Refresh");
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(loadButton);
		toolBar.add(refreshButton);

		JButton helpButton = new JButton("Help");
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(helpButton);

		JButton aboutButton = new JButton("About");
		toolBar.add(aboutButton);

		// Add action listeners for the buttons here if needed

		mainFrame.add(toolBar, BorderLayout.NORTH);

		helpButton.addActionListener(e -> showHelpDialog()); // Help button action
		aboutButton.addActionListener(e -> showAboutDialog());// About button action
		openButton.addActionListener(e -> openWebsite("https://google.com"));  //Open button action
		saveButton.addActionListener(e -> saveData()); 	// Add ActionListener to the "Save" button
		loadButton.addActionListener(e -> loadData()); 	// Add ActionListener to the "Load" button		
		refreshButton.addActionListener(e -> refreshData());
	}

	private void refreshData() {
		// Update the JComboBox with fresh data from the database
		moNameComboBox.removeAllItems();
		String[] moNames = TagReplacementToolModel.getMoNames();
		for (String moName : moNames) {
			outputArea.setText("Refreshing..");
			moNameComboBox.addItem(moName);
		}
		// Clear the output area
		outputArea.setText("");
	}

	private void saveData() {
		try {
			// Get the current state of the application data
			String moName = Objects.requireNonNull(moNameComboBox.getSelectedItem()).toString();
			String moContent = outputArea.getText();

			// Choose the save file location using JFileChooser with custom file filter
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new TRTFileFilter());

			int returnValue = fileChooser.showSaveDialog(mainFrame);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				if (!selectedFile.getAbsolutePath().toLowerCase().endsWith(".trt")) {
					selectedFile = new File(selectedFile.getAbsolutePath() + ".trt");
				}

				if (selectedFile.exists()) {
					// File already exists, ask for confirmation to overwrite
					int overwriteConfirmation = JOptionPane.showConfirmDialog(mainFrame,
							"The selected file already exists. Do you want to overwrite it?",
							"File Exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (overwriteConfirmation == JOptionPane.NO_OPTION) {
						// User chose not to overwrite, return without saving
						return;
					}
				}

				// Write the data to the selected file in custom save file format
				try (PrintWriter writer = new PrintWriter(selectedFile)) {
					writer.println("MO_NAME:" + moName);
					writer.println("MO_CONTENT:");
					writer.println(moContent);
					writer.flush();
				}

				JOptionPane.showMessageDialog(mainFrame, "Data saved successfully!",
						"Save", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainFrame, "An error occurred while saving the data.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadData() {
		try {
			// Choose the file to load data from using JFileChooser with custom file filter
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new TRTFileFilter());

			int returnValue = fileChooser.showOpenDialog(mainFrame);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();

				// Read the data from the selected file in custom save file format
				try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
					StringBuilder moContentBuilder = new StringBuilder();
					String line;
					boolean readingMoContent = false;

					while ((line = reader.readLine()) != null) {
						if (line.startsWith("MO_NAME:")) {
							moNameComboBox.setSelectedItem(line.substring(8));
						} else if (line.startsWith("MO_CONTENT:")) {
							readingMoContent = true;
						} else if (readingMoContent) {
							moContentBuilder.append(line).append("\n");
						}
					}

					// Set the loaded MO content to the output area
					outputArea.setText(moContentBuilder.toString());

					JOptionPane.showMessageDialog(mainFrame, "Data loaded successfully!",
							"Load", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainFrame, "An error occurred while loading the data.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// Custom file filter for ".trt" files
	private static class TRTFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".trt");
		}

		public String getDescription() {
			return "Tag Replacement Tool Files (*.trt)";
		}
	}

	private void openWebsite(String url) {
		try {
			// Use Desktop to open the default web browser
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				JOptionPane.showMessageDialog(mainFrame, "Opening the default web browser is not supported on this platform.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainFrame, "An error occurred while trying to open the website.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void showHelpDialog() {
		JOptionPane.showMessageDialog(null,
				"Help content goes here.",
				"Help",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private static void showAboutDialog() {
		JOptionPane.showMessageDialog(null,
				"Tag Replacement Tool\nVersion 1.0\nAuthor: Amit Kumar Nandi",
				"About",
				JOptionPane.INFORMATION_MESSAGE);
	}
	 private void createInputPanel() {
	        JPanel inputPanel = new JPanel(new FlowLayout());
	        JLabel moNameLabel = new JLabel("Select mo_name:");
	        moNameComboBox = new JComboBox<>(TagReplacementToolModel.getMoNames());
	        moNameComboBox.setEditable(true);
	        JButton processButton = new JButton("Process");
	        inputPanel.add(moNameLabel);
	        inputPanel.add(moNameComboBox);
	        inputPanel.add(processButton);

	        // Add action listener for the "Process" button
	        processButton.addActionListener(e -> processMoContent());

	        mainFrame.add(inputPanel, BorderLayout.CENTER);
	    }

	private void processMoContent() {
		String moName = getSelectedMoName();    
		StringBuilder resultBuilder = new StringBuilder();

		// Check if the selected mo name exists in the database
		if (!TagReplacementToolModel.isMoNameExists(moName)) {
			JOptionPane.showMessageDialog(mainFrame, "The selected mo_name does not exist in the database.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}


		try (Connection conn = DriverManager.getConnection(TagReplacementToolModel.DB_URL, TagReplacementToolModel.USER, TagReplacementToolModel.PASS)) {
			TagReplacementToolModel.createTables(conn);
			String moContent = TagReplacementToolModel.getMoContent(conn, moName);
			List<String> tags = TagReplacementToolModel.extractTags(moContent);

			for (String tag : tags) {
				String tagName = TagReplacementToolModel.getTagName(tag);
				String tagComment = null;
				try {
					tagComment = TagReplacementToolModel.getTagComment(conn, tagName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

    private void createOutputPanel() {
        outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        mainFrame.add(outputScrollPane, BorderLayout.SOUTH);
    }


	public void setMoNames(String[] moNames) {
		moNameComboBox.setModel(new DefaultComboBoxModel<>(moNames));
	}

	public String getSelectedMoName() {
		return Objects.requireNonNull(moNameComboBox.getSelectedItem()).toString();
	}

	// Implement the custom dialog logic here
	static String showInputDialogWithInfo(String message, String moName, String tagName, String tagComment) {
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
	static void showFinalContentWindow(String moContent) {
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

	static void showSQLErrorMessage(Exception e)
	{
		JOptionPane.showMessageDialog(null,
				"Error connecting to the database: " + e.getMessage(),
				"Database Error", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	// Other methods for handling GUI components, events, and dialogs
}