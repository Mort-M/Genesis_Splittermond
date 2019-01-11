/**
 * 
 */
package org.prelle.splittermond.jfx.resources;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class ResourceCard extends VBox implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacter     model;

	private Label heading;

	//-------------------------------------------------------------------
	/**
	 */
	public ResourceCard() {
		getStyleClass().addAll("table","chardata-tile");
		
		initComponents();
		initLayout();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		heading   = new Label(uiResources.getString("label.resources"));
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
		case RESOURCE_ADDED:
		case RESOURCE_CHANGED:
		case RESOURCE_REMOVED:
			logger.debug("ResourceCard received "+event);
			updateContent();
			break;
		default:
		}		
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		getChildren().retainAll(heading);
		
		logger.info("\n\nRES = "+model.getResources());
		int y=0;
		for (ResourceReference data : model.getResources()) {
			if (data.getValue()==0)
				continue;
			y++;
			String lineStyle = ((y%2)==0)?"even":"odd";

			Label name = new Label(data.getResource().getName()+" "+data.getValue());
			name.setMaxWidth(Double.MAX_VALUE);
			name.getStyleClass().add(lineStyle);
			getChildren().add(name);
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		if (model==null)
			throw new NullPointerException("May not be null");
		this.model = model;
		updateContent();
	}

}
