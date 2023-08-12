package com.tagger;

public class TagReplacementToolController {
	@SuppressWarnings("unused")
	private TagReplacementToolModel model;
	private TagReplacementToolView view;

	public TagReplacementToolController(TagReplacementToolModel model, TagReplacementToolView view) {
		this.model = model;
		this.view = view;
		initController();
	}
	private void initController() {
		// Attach event listeners and handle user interactions
		loadMoNames();
	}
	private void loadMoNames() {
		String[] moNames = TagReplacementToolModel.getMoNames();
		view.setMoNames(moNames);
	}
	// Other methods for handling events and updating the model and view
}