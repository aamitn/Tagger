package com.tagger;

import java.util.Vector;
import javax.swing.SwingUtilities;

public class TagReplacementToolApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			TagReplacementToolConfig tc = new TagReplacementToolConfig();
			Vector<String> vec = new Vector<>();
			tc.getClass().getDeclaredFields();
			for (var field : tc.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				try { vec.add((String) field.get(tc));} 
				catch (IllegalAccessException e) { e.printStackTrace();}
			}
			TagReplacementToolModel model = new TagReplacementToolModel(
					vec.get(1), vec.get(2), vec.get(3), vec.get(4), vec.get(5));
			TagReplacementToolView view = new TagReplacementToolView();
			new TagReplacementToolController(model, view);
		});
	}
}