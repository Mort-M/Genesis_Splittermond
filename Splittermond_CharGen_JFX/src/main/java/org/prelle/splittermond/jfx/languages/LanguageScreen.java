/**
 * 
 */
package org.prelle.splittermond.jfx.languages;

import java.util.Arrays;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.charctrl.LanguageController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.PointsPane;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.chargen.jfx.ViewMode;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan
 *
 */
public class LanguageScreen extends ManagedScreen implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private LanguageController control;
	private ViewMode mode;

	private PointsPane points;
	private LanguagePane languages;
	private VBox descLayout;
	
	//--------------------------------------------------------------------
	/**
	 */
	public LanguageScreen(LanguageController control, ViewMode mode) {
		this.control = control;
		this.mode    = mode;
		if (control==null)
			throw new NullPointerException("Controller is null");
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("languagescreen.title"));
		
		languages = new LanguagePane(control);
		languages.getStyleClass().add("content");

		/*
		 * Exp & Co.
		 */
		points = new PointsPane(mode);
		if (control instanceof Generator)
			points.setGenerator((Generator) control);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");

		// Languages
		Label lblLanguages = new Label(UI.getString("label.languages"));
		languages.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		HBox.setHgrow(languages, Priority.ALWAYS);
		lblLanguages.getStyleClass().add("text-subheader");
		VBox boxLanguages = new VBox(20);
		boxLanguages.getChildren().addAll(lblLanguages, languages);
		
		// Flow; Resources and Powers
		HBox flow = new HBox(40);
		flow.getChildren().addAll(boxLanguages);

		HBox content = new HBox();
		content.setSpacing(20);
		content.getChildren().addAll(points, flow);
		content.setMaxHeight(Double.MAX_VALUE);
		HBox.setHgrow(flow, Priority.ALWAYS);
		HBox.setMargin(points, new Insets(0,0,20,0));
		HBox.setMargin(flow  , new Insets(0,0,20,0));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
			
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		setTitle(model.getName()+"/"+UI.getString("languagescreen.title"));
		points.setData(model);
		languages.setData(model);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			points.refresh();
			break;
		case LANGUAGE_ADDED:
		case LANGUAGE_REMOVED:
			points.refresh();
			break;
		case CULTURELORE_ADDED:
		case CULTURELORE_REMOVED:
			points.refresh();
			break;
//		case SKILL_CHANGED:
//			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
//			logger.debug(" changed: "+event.getKey()+"  selected="+selectedSkill);
//			if (((Skill)event.getKey())==selectedSkill) {
//				refreshMasteries(selectedSkill);
//				refreshSpecializations(selectedSkill);
//			}
//			points.refresh();
//			break;
//		case MASTERSHIP_ADDED:
//		case MASTERSHIP_CHANGED:
//		case MASTERSHIP_REMOVED:
//			logger.debug("rcv "+event);
//			logger.debug(" changed: "+event.getKey()+"  selected="+selectedSkill);
//			if (((Skill)event.getKey())==selectedSkill) {
//				refreshMasteries(selectedSkill);
//				refreshSpecializations(selectedSkill);
//			}			
//			break;
//		case LEVEL_CHANGED:
//			refreshMasteries(selectedSkill);
//			refreshSpecializations(selectedSkill);
		default:
			break;
		}		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.CloseType)
	 */
	@Override
	public boolean close(CloseType closeType) {
		GenerationEventDispatcher.removeListener(this);
		return true;
	}

}
