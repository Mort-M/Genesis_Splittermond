/**
 * 
 */
package org.prelle.splittermond.jfx.languages;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author prelle
 *
 */
public class LanguageCard extends VBox implements GenerationEventListener {
	
	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private SpliMoCharacter     model;

	private Label heading;

	//-------------------------------------------------------------------
	/**
	 */
	public LanguageCard() {
		getStyleClass().addAll("table","chardata-tile");
		
		initComponents();
		initLayout();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		heading   = new Label(UI.getString("label.language"));
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
		case LANGUAGE_ADDED:
		case LANGUAGE_REMOVED:
			logger.debug("LanguageCard received "+event);
			updateContent();
			break;
		default:
		}		
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		getChildren().retainAll(heading);
		
		int y=0;
		for (LanguageReference data : model.getLanguages()) {
			y++;
			String lineStyle = ((y%2)==0)?"even":"odd";

			Label name = new Label(data.getLanguage().getName());
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
