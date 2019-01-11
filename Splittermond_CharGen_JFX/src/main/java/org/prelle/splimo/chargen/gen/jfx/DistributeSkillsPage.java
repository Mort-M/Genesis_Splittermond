/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.GeneratingSkillController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.jfx.skills.SkillPane;
import org.prelle.splittermond.jfx.skills.SkillPaneCallback;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class DistributeSkillsPage extends WizardPage implements GenerationEventListener, SkillPaneCallback {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	private SpliMoCharacterGenerator charGen;
	private GeneratingSkillController control;

	private Label pointsLeft;
//	private DistributeSkillsTableView tableCombat, tableNormal, tableMagic;
	private SkillPane tableCombat, tableNormal, tableMagic;
	private TabPane tabs;
	private VBox context;

	//-------------------------------------------------------------------
	public DistributeSkillsPage(ScreenManagerProvider provider, SpliMoCharacter model, SpliMoCharacterGenerator charGen) {
		this.control = charGen.getSkillGenerator();
		this.charGen = charGen;
		pageInit(uiResources.getString("wizard.distrSkill.title"), 
				new Image(DistributeSkillsPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));

		GenerationEventDispatcher.addListener(this);

		nextButton.setDisable( control.getPointsLeft()>0 );
		finishButton.setDisable( !charGen.hasEnoughData() );
		
		// Points to distribute
		pointsLeft.setText(control.getPointsLeft()+"");

		/*
		 * Define the table
		 */
		// Combat skills
//		tableCombat = new DistributeSkillsTableView(charGen, SkillType.COMBAT);
		tableCombat = new SkillPane(this, charGen.getSkillController(), charGen.getMastershipController(), false, SkillType.COMBAT);
		tableCombat.setContent(model);
		Tab combat = new Tab();
		combat.setText(SkillType.COMBAT.getName());
		combat.setContent(tableCombat);
		combat.setClosable(false);

		// Normal skills
		tableNormal = new SkillPane(this, charGen.getSkillController(), charGen.getMastershipController(), false, SkillType.NORMAL);
		tableNormal.setContent(model);
		ScrollPane spNormal = new ScrollPane(tableNormal);
		spNormal.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		spNormal.setFitToWidth(false);
		Tab normal = new Tab();
		normal.setText(SkillType.NORMAL.getName());
		normal.setContent(spNormal);
		normal.setClosable(false);

		// Magic skills
		tableMagic = new SkillPane(this, charGen.getSkillController(), charGen.getMastershipController(), false, SkillType.MAGIC);
		tableMagic.setContent(model);
		ScrollPane spMagic = new ScrollPane(tableMagic);
		spMagic.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		spMagic.setFitToWidth(false);
		Tab magic = new Tab();
		magic.setText(SkillType.MAGIC.getName());
		magic.setContent(spMagic);
		magic.setClosable(false);
		
		// Bring it together
		tabs.getTabs().addAll(combat, normal, magic);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		tabs = new TabPane();
		tabs.setMinHeight(300);
		tabs.setPrefHeight(500);
		
//		ScrollPane pane = new ScrollPane(tabs);
//		pane.setFitToHeight(true);
//		pane.setFitToWidth(true);
		return tabs;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContextMenu()
	 */
	@Override
	public Parent getContextMenu() {
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");

		// Line that reflects remaining points to distribute
		pointsLeft = new Label(String.valueOf(control.getPointsLeft()));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		Label pointsLeft_f = new Label(uiResources.getString("wizard.distrSkill.pointsLeft")+" ");
		pointsLeft_f.getStyleClass().add("wizard-context");


		context = new VBox(15);
		context.setAlignment(Pos.TOP_CENTER);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, pointsLeft, pointsLeft_f);
		return context;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		super.nextPage();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()!=GenerationEventType.SKILL_CHANGED)
			return;

		pointsLeft.setText(""+control.getPointsLeft());
		logger.debug("process "+event+"   left="+control.getPointsLeft());
		
		// Disable next button if there are unspent points
		nextButton.setDisable( control.getPointsLeft()>0 );
		finishButton.setDisable( !charGen.hasEnoughData() );

	}

	@Override
	public void showAndWaitMasterships(Skill skill) {
		// TODO Auto-generated method stub
		logger.error("Not implemented");
//		MastershipScreen screen = new MastershipScreen(control.getMastershipController(), mode);
////		screen.setC
//		CloseType closed = (CloseType)manager.showAndWait(screen);
//		logger.warn("Closed with "+closed);
	}

}


