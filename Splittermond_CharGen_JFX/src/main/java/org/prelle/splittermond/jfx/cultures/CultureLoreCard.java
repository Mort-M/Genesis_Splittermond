/**
 * 
 */
package org.prelle.splittermond.jfx.cultures;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author prelle
 *
 */
public class CultureLoreCard extends VBox implements GenerationEventListener {
	
	private final static Logger logger = Logger.getLogger("splimo.jfx");
	
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private SpliMoCharacter     model;

	private Label heading;

	//-------------------------------------------------------------------
	/**
	 */
	public CultureLoreCard() {
		getStyleClass().addAll("table","chardata-tile");
		
		initComponents();
		initLayout();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		heading   = new Label(uiResources.getString("label.culture"));
		heading.getStyleClass().add("table-head");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");
		getChildren().add(heading);
		heading.setMaxWidth(Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CULTURELORE_ADDED:
		case CULTURELORE_REMOVED:
			logger.debug("CultureCard received "+event);
			updateContent();
			break;
		default:
		}		
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		getChildren().retainAll(heading);
		
		int y=0;
		for (CultureLoreReference data : model.getCultureLores()) {
			y++;
			String lineStyle = ((y%2)==0)?"even":"odd";

			Label name = new Label(data.getCultureLore().getName());
			name.setMaxWidth(Double.MAX_VALUE);
			name.getStyleClass().add(lineStyle);
			getChildren().add(name);
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		updateContent();
	}

}
