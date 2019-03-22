/**
 * 
 */
package org.prelle.splittermond.jfx.cultures;

import java.util.Arrays;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ManagedScreenPage;
import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.charctrl.LanguageController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.SpliMoManagedScreenPage;
import org.prelle.splittermond.jfx.languages.LanguagePane;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan
 *
 */
public class CultureLoreScreen extends SpliMoManagedScreenPage implements GenerationEventListener {

	private CultureLoreController controlCult;
	private LanguageController control;

	private AvailableCultureLorePane available;
	private CultureLorePane cultures;
	private LanguagePane languages;
	
	private VBox descLayout;
	private Label    lblDescription;
	private CheckBox chkIncludeUnavailable;
	private Label    lblIncludeUnavailable;
	
	//--------------------------------------------------------------------
	/**
	 */
	public CultureLoreScreen(CharacterController charGen, ChararcterHandle handle) {
		super(UI.getString("culturelorescreen.title"), charGen, handle);
		this.controlCult = charGen.getCultureLoreController();
		this.control = charGen.getLanguageController();
		if (controlCult==null)
			throw new NullPointerException("Controller is null");
		
		initComponents();
		initLayout();
		initInteractivity();
		GenerationEventDispatcher.addListener(this);
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		
		available = new AvailableCultureLorePane(controlCult, AvailableCultureLorePane.DisplayMode.AVAILABLE);
		available.getStyleClass().add("content");
		
		cultures  = new CultureLorePane(controlCult);
		cultures.getStyleClass().add("content");
		languages = new LanguagePane(control);
//		languages.getStyleClass().add("content");

		lblDescription = new Label(UI.getString("culturelorescreen.lblDesc"));
		lblDescription.setWrapText(true);
		lblDescription.getStyleClass().add("body");
		chkIncludeUnavailable = new CheckBox(UI.getString("culturelorescreen.chkIncl"));
		chkIncludeUnavailable.setWrapText(true);
		chkIncludeUnavailable.getStyleClass().add("text-small-subheader");
		lblIncludeUnavailable = new Label(UI.getString("culturelorescreen.lblIncl"));
		lblIncludeUnavailable.setWrapText(true);
		lblIncludeUnavailable.getStyleClass().add("body");

		
//		/*
//		 * Exp & Co.
//		 */
//		freePoints = new FreePointsNode();
//		freePoints.setStyle("-fx-max-height: 3em; -fx-max-width: 3em");
//		freePoints.setPoints(charGen.getModel().getExperienceFree());
//		freePoints.setName(UI.getString("label.ep.free"));
//		Label hdExpTotal    = new Label(SpliMoCharGenConstants.RES.getString("label.ep.total")+": ");
//		Label hdExpInvested = new Label(SpliMoCharGenConstants.RES.getString("label.ep.used")+": ");
//		Label hdLevel       = new Label(SpliMoCharGenConstants.RES.getString("label.level")+": ");
//		lbExpTotal    = new Label("?");
//		lbExpInvested = new Label("?");
//		lbLevel       = new Label("?");
//		lbExpTotal.getStyleClass().add("base");
//		lbExpInvested.getStyleClass().add("base");
//		lbLevel.getStyleClass().add("base");
//		lbExpTotal.setText(String.valueOf(charGen.getModel().getExperienceInvested()+charGen.getModel().getExperienceFree()));
//		lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
//		lbLevel.setText(charGen.getModel().getLevel()+"");
//		
//		cmdPrint = new MenuItem(UI.getString("command.primary.print"), new Label("\uE749"));
//		cmdDelete = new MenuItem(UI.getString("command.primary.delete"), new Label("\uE74D"));
//		if (handle!=null)
//			getCommandBar().getPrimaryCommands().addAll(cmdPrint);
//		commands = new CommandBar();
//		commands.getItems().add(new MenuItem("Drucken", new Label("\uD83D\uDDB6")));
//		
//		HBox expLine = new HBox(5);
//		expLine.getChildren().addAll(hdExpTotal, lbExpTotal, hdExpInvested, lbExpInvested, hdLevel, lbLevel);
//		HBox.setMargin(hdLevel, new Insets(0,0,0,20));
//		expLine.getStyleClass().add("character-document-view-firstline");
//		
//		firstLine = new SettingsAndCommandBar();
//		firstLine.setSettings(expLine);
////		firstLine.setCommandBar(commands);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		// Available
		Label lblAvailable = new Label(UI.getString("label.available"));
		available.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		VBox.setVgrow(available, Priority.ALWAYS);
		lblAvailable.getStyleClass().add("text-subheader");
		VBox boxAvailable = new VBox(20);
		boxAvailable.getChildren().addAll(lblAvailable, available);
		
		// CultureLores
		Label lblSelected = new Label(UI.getString("label.selected"));
		cultures.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		VBox.setVgrow(cultures, Priority.ALWAYS);
		lblSelected.getStyleClass().add("text-subheader");
		VBox boxCultLores = new VBox(20);
		boxCultLores.getChildren().addAll(lblSelected, cultures);

		// Description, controls
		Label lblDesc = new Label("Erläuterung");
		lblDesc.getStyleClass().add("text-subheader");
		descLayout = new VBox(20);
		descLayout.setStyle("-fx-pref-width: 20em");
		descLayout.getChildren().addAll(lblDesc, lblDescription, chkIncludeUnavailable, lblIncludeUnavailable);

		// Languages
		Label lblLanguages = new Label(UI.getString("label.languages"));
		languages.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		HBox.setHgrow(languages, Priority.ALWAYS);
		lblLanguages.getStyleClass().add("text-subheader");
		VBox boxLanguages = new VBox(20);
		boxLanguages.getChildren().addAll(lblLanguages, languages);

		// Flow; Resources and Powers
		HBox flow = new HBox(40);
		flow.getChildren().addAll(freePoints, boxLanguages, boxAvailable, boxCultLores, descLayout);
		HBox.setMargin(boxLanguages, new Insets(0, 0, 0, -20));

		VBox content = new VBox();
		content.setSpacing(20);
		content.getChildren().addAll(firstLine, flow);
		content.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(flow, Priority.ALWAYS);
		VBox.setMargin(flow  , new Insets(0,0,20,0));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		chkIncludeUnavailable.selectedProperty().addListener( (ov,o,n) -> {
			logger.debug("---switch "+n);
			controlCult.showCultureLoresWithUnmetRequirements(n);
		});
		
		available.getList().setOnSwipeRight(event -> {
			CultureLore selected = available.selectedItemProperty().get();
			logger.debug("Swiped right: "+selected);
			if (selected!=null && controlCult.canBeSelected(selected)) {
				controlCult.select(selected);
			}
		});
		available.setOnAction(event -> {
			logger.debug("Action "+event.getSource());
			CultureLore selected = (CultureLore)event.getSource();
			if (selected!=null && controlCult.canBeSelected(selected)) {
				logger.debug("Select "+selected);
				controlCult.select(selected);
			}
		});
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		setTitle(charGen.getModel().getName()+" "+UI.getString("culturelorescreen.title"));
		super.refresh();
//		lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
//		lbLevel.setText(charGen.getModel().getLevel()+"");
		available.setData(model);
		cultures.setData(model);
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
			super.refresh();
//			lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
//			lbLevel.setText(charGen.getModel().getLevel()+"");
			break;
		case LANGUAGE_ADDED:
		case LANGUAGE_REMOVED:
			super.refresh();
//			lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
//			lbLevel.setText(charGen.getModel().getLevel()+"");
			break;
		case CULTURELORE_ADDED:
		case CULTURELORE_REMOVED:
			super.refresh();
//			lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
//			lbLevel.setText(charGen.getModel().getLevel()+"");
			break;
		default:
			break;
		}		
	}

}
