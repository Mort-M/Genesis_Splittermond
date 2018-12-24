/**
 * 
 */
package org.prelle.splimo.chargen.fluent;

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
public class WeaknessListControl extends Control implements GenerationEventListener {
	
	private final static String DEFAULT_STYLE_CLASS = "weakness-list-control";
	
	private ObservableList<String> items;
	private ObjectProperty<String> selectedItemProperty;
	private CharacterController charGen;
	
	//-------------------------------------------------------------------
	public WeaknessListControl(CharacterController ctrl) {		
		this.charGen = ctrl;
		items = FXCollections.observableArrayList(ctrl.getModel().getWeaknesses());
		selectedItemProperty = new SimpleObjectProperty<>();
		
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		setSkin(new WeaknessListViewSkin(this));
		GenerationEventDispatcher.addListener(this);
	}
	
	//-------------------------------------------------------------------
	CharacterController getController() {
		return charGen;
	}

	//-------------------------------------------------------------------
	public ObservableList<String> getItems() {
		return items;
	}

	//-------------------------------------------------------------------
	public ObjectProperty<String> selectedItemProperty() { return selectedItemProperty; }
	public String getSelectedItem() { return selectedItemProperty.get(); }
	public void setSelectedItem(String value) { selectedItemProperty.set(value); }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case WEAKNESS_ADDED:
		case WEAKNESS_REMOVED:
			items.clear();
			items.addAll(charGen.getModel().getWeaknesses());
			break;
		default:
		}
	}

}
