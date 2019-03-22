/**
 * 
 */
package org.prelle.splittermond.chargen.fluent;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

/**
 * @author prelle
 *
 */
public class CultureLoreReferenceListControl extends Control implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;
	
	private final static String DEFAULT_STYLE_CLASS = "cultlore-list-control";
	
	private ObservableList<CultureLoreReference> items;
	private ObjectProperty<CultureLoreReference> selectedItemProperty;
	private CharacterController charGen;
	
	//-------------------------------------------------------------------
	public CultureLoreReferenceListControl(CharacterController ctrl) {		
		this.charGen = ctrl;
		items = FXCollections.observableArrayList(ctrl.getModel().getCultureLores());
		selectedItemProperty = new SimpleObjectProperty<>();
		
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		setSkin(new CultureLoreReferenceListViewSkin(this));
		GenerationEventDispatcher.addListener(this);
	}
	
	//-------------------------------------------------------------------
	CharacterController getController() {
		return charGen;
	}

	//-------------------------------------------------------------------
	public ObservableList<CultureLoreReference> getItems() {
		return items;
	}

	//-------------------------------------------------------------------
	public ObjectProperty<CultureLoreReference> selectedItemProperty() { return selectedItemProperty; }
	public CultureLoreReference getSelectedItem() { return selectedItemProperty.get(); }
	public void setSelectedItem(CultureLoreReference value) { selectedItemProperty.set(value); }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CULTURELORE_ADDED:
		case CULTURELORE_REMOVED:
			logger.info("RCV "+event);
			items.clear();
			items.addAll(charGen.getModel().getCultureLores());
			break;
		default:
		}
	}

}
