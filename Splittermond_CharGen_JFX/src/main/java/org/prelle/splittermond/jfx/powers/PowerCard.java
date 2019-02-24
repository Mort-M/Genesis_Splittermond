/**
 * 
 */
package org.prelle.splittermond.jfx.powers;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class PowerCard extends VBox implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacter     model;

	private Label heading;

	//-------------------------------------------------------------------
	/**
	 */
	public PowerCard() {
		getStyleClass().addAll("table","chardata-tile");
		
		initComponents();
		initLayout();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		heading   = new Label(uiResources.getString("label.power"));
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
		case POWER_ADDED:
		case POWER_CHANGED:
		case POWER_REMOVED:
			logger.debug("PowerCard received "+event);
			updateContent();
			break;
		default:
		}		
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		getChildren().retainAll(heading);
		
		int y=0;
		for (PowerReference data : model.getPowers()) {
			y++;
			String lineStyle = ((y%2)==0)?"even":"odd";

			Label name = null;
			switch (data.getPower().getSelectable()) {
			case ALWAYS:
			case GENERATION:
				name = new Label(data.getPower().getName());
				break;
			case LEVEL:
			case MAX3:
			case MULTIPLE:
				name = new Label(data.getPower().getName()+" "+data.getCount());
				break;
			}
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
