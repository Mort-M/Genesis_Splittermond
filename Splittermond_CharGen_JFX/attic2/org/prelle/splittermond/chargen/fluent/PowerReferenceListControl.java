/**
 * 
 */
package org.prelle.splittermond.chargen.fluent;

import org.prelle.splimo.PowerReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.PowerController;
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
public class PowerReferenceListControl extends Control implements GenerationEventListener {
	
	private final static String DEFAULT_STYLE_CLASS = "power-list-control";
	
	private ObservableList<PowerReference> items;
	private ObjectProperty<PowerReference> selectedItemProperty;
	private CharacterController charGen;
	
	//-------------------------------------------------------------------
	public PowerReferenceListControl(CharacterController ctrl) {		
		this.charGen = ctrl;
		items = FXCollections.observableArrayList(ctrl.getModel().getPowers());
		selectedItemProperty = new SimpleObjectProperty<>();
		
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		setSkin(new PowerReferenceListViewSkin(this));
		GenerationEventDispatcher.addListener(this);
	}
	
	//-------------------------------------------------------------------
	CharacterController getController() {
		return charGen;
	}

	//-------------------------------------------------------------------
	public ObservableList<PowerReference> getItems() {
		return items;
	}

	//-------------------------------------------------------------------
	public ObjectProperty<PowerReference> selectedItemProperty() { return selectedItemProperty; }
	public PowerReference getSelectedItem() { return selectedItemProperty.get(); }
	public void setSelectedItem(PowerReference value) { selectedItemProperty.set(value); }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case POWER_ADDED:
		case POWER_CHANGED:
		case POWER_REMOVED:
			items.clear();
			items.addAll(charGen.getModel().getPowers());
			break;
		default:
		}
	}

}
