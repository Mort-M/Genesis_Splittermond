/**
 * 
 */
package org.prelle.splittermond.chargen.fluent;

import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

/**
 * @author prelle
 *
 */
public class LanguageReferenceListControl extends Control implements GenerationEventListener {
	
	private final static String DEFAULT_STYLE_CLASS = "language-list-control";
	
	private ObservableList<LanguageReference> items;
	private ObjectProperty<LanguageReference> selectedItemProperty;
	private CharacterController charGen;
	
	//-------------------------------------------------------------------
	public LanguageReferenceListControl(CharacterController ctrl) {		
		this.charGen = ctrl;
		items = FXCollections.observableArrayList(ctrl.getModel().getLanguages());
		selectedItemProperty = new SimpleObjectProperty<>();
		
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		setSkin(new LanguageReferenceListViewSkin(this));
		GenerationEventDispatcher.addListener(this);
	}
	
	//-------------------------------------------------------------------
	CharacterController getController() {
		return charGen;
	}

	//-------------------------------------------------------------------
	public ObservableList<LanguageReference> getItems() {
		return items;
	}

	//-------------------------------------------------------------------
	public ObjectProperty<LanguageReference> selectedItemProperty() { return selectedItemProperty; }
	public LanguageReference getSelectedItem() { return selectedItemProperty.get(); }
	public void setSelectedItem(LanguageReference value) { selectedItemProperty.set(value); }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case LANGUAGE_ADDED:
		case LANGUAGE_REMOVED:
			items.clear();
			items.addAll(charGen.getModel().getLanguages());
			break;
		default:
		}
	}

}
