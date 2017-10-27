package org.prelle.splimo.chargen.gen.jfx;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

class Wizard {
	
	private static final int UNDEFINED = -1;
	protected ObservableList<WizardPage> pages = FXCollections.observableArrayList();
	private Stack<Integer> history = new Stack<Integer>();
	private int curPageIdx = UNDEFINED;

	private Map<WizardPage, Scene> scenes;
	private Stage stage;
	
	//-------------------------------------------------------------------
	Wizard(Stage owner, WizardPage... nodes) {
		this.stage = owner;
		stage.setResizable(false);
		pages.addAll(nodes);		
		scenes = new HashMap<WizardPage, Scene>();
		for (WizardPage page : nodes) {
			page.setWizard(this);

			Scene scene = new Scene(page);
			scene.getStylesheets().add("css/default.css");
			scenes.put(page, scene);
		}
		
		navTo(0);
		
	}

	//-------------------------------------------------------------------
	void nextPage() {
		if (hasNextPage()) {
			navTo(curPageIdx + 1);
		}
	}

	//-------------------------------------------------------------------
	void priorPage() {
		if (hasPriorPage()) {
			navTo(history.pop(), false);
		}
	}

	//-------------------------------------------------------------------
	boolean hasNextPage() {
		return (curPageIdx < pages.size() - 1);
	}

	//-------------------------------------------------------------------
	boolean hasPriorPage() {
		return !history.isEmpty();
	}

	//-------------------------------------------------------------------
	void navTo(int nextPageIdx, boolean pushHistory) {
		if (nextPageIdx < 0 || nextPageIdx >= pages.size())
			return;
		if (curPageIdx != UNDEFINED) {
//			pages.get(curPageIdx).setVisible(false);
			if (pushHistory) {
				history.push(curPageIdx);
			}
		}

		WizardPage nextPage = pages.get(nextPageIdx);
		curPageIdx = nextPageIdx;
		
		stage.setScene(scenes.get(nextPage));
		
		nextPage.manageButtons();
//		nextPage.requestLayout();
	}

	//-------------------------------------------------------------------
	void navTo(int nextPageIdx) {
		navTo(nextPageIdx, true);
	}

//	//-------------------------------------------------------------------
//	void navTo(String id) {
//		Node page = lookup("#" + id);
//		if (page != null) {
//			int nextPageIdx = pages.indexOf(page);
//			if (nextPageIdx != UNDEFINED) {
//				navTo(nextPageIdx);
//			}
//		}
//	}

	//-------------------------------------------------------------------
	public void finish() {
	}

	//-------------------------------------------------------------------
	public void cancel() {
	}
	
	//-------------------------------------------------------------------
	protected void close() {
		stage.hide();
	}

}