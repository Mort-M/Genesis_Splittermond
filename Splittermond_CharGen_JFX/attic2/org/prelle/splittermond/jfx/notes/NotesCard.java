/**
 * 
 */
package org.prelle.splittermond.jfx.notes;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class NotesCard extends VBox implements GenerationEventListener {

	private SpliMoCharacter     model;

//	private Label heading;

	//-------------------------------------------------------------------
	/**
	 */
	public NotesCard() {
		getStyleClass().addAll("table","chardata-tile");
		
		initComponents();
		initLayout();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");
		this.setStyle("-fx-pref-width: 8em; -fx-pref-height: 8em;");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case NOTES_CHANGED:
			updateContent();
			break;
		default:
		}		
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		getChildren().clear();
		
		Label name = new Label(model.getNotes());
		name.setWrapText(true);
		name.setMaxWidth(Double.MAX_VALUE);
		getChildren().add(name);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		updateContent();
	}

}
