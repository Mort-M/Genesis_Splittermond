/**
 * 
 */
package org.prelle.splimo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlipControl;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.rpgframework.api.BabylonEventBus;
import org.prelle.rpgframework.api.BabylonEventType;
import org.prelle.rpgframework.jfx.AttentionPane;
import org.prelle.splimo.BaseDataBlockSpliMo.View;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.chargen.lvl.jfx.CharacterEditCallback;
import org.prelle.splimo.chargen.lvl.jfx.CharacterLevelingPane;
import org.prelle.splimo.levelling.CharacterLeveller;
import org.prelle.splittermond.jfx.attributes.AttributeCard;
import org.prelle.splittermond.jfx.attributes.AttributeScreen;
import org.prelle.splittermond.jfx.creatures.CreatureScreen;
import org.prelle.splittermond.jfx.cultures.CultureLoreCard;
import org.prelle.splittermond.jfx.cultures.CultureLoreScreen;
import org.prelle.splittermond.jfx.equip.EquipmentScreen;
import org.prelle.splittermond.jfx.languages.LanguageCard;
import org.prelle.splittermond.jfx.languages.LanguageScreen;
import org.prelle.splittermond.jfx.notes.NotesCard;
import org.prelle.splittermond.jfx.powers.PowerCard;
import org.prelle.splittermond.jfx.powers.PowerScreen;
import org.prelle.splittermond.jfx.resources.ResourceCard;
import org.prelle.splittermond.jfx.resources.ResourceScreen;
import org.prelle.splittermond.jfx.skills.SkillCard;
import org.prelle.splittermond.jfx.skills.SkillScreen2;
import org.prelle.splittermond.jfx.spells.SpellCard;
import org.prelle.splittermond.jfx.spells.SpellScreen;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.Attachment;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterHandle.Format;
import de.rpgframework.character.CharacterHandle.Type;
import de.rpgframework.character.CharacterProvider;
import de.rpgframework.core.CommandBus;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class CharacterViewScreenSpliMo extends ManagedScreen implements GenerationEventListener, ScreenManagerProvider {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static Preferences CONFIG = Preferences.userRoot().node("/org/rpgframework/genesis/splittermond");
	
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private SpliMoCharacter model;
	private CharacterHandle handle;
	private CharacterController control;
	private ViewMode mode;

	private HBox content;
	private CharacterLevelingPane charDocPane;

	private BaseDataBlockSpliMo baseBlock;
	
	private FlipControl flipCtrl;
	private FlowPane flow;
	/*
	 * Attribute shortview
	 */
	private AttributeCard attrPrimary;
	private AttributeCard attrSecondary;
	/* Powers */
	private PowerCard powers;
	/* Resources */
	private ResourceCard resources;
	/* Culture Lores */
	private CultureLoreCard cultures;
	/* Languages */
	private LanguageCard languages;
	/* Skills */
	private SkillCard skillNormal;
	private SkillCard skillCombat;
	private SkillCard skillMagic;
	/* Spells */
	private SpellCard spells;
	private NotesCard notes;
	
//	private AttentionPane stackBtnAttributes;
//	private AttentionPane stackCrdAttributes;
	/** Nodes to show in flow */
	private List<Node> availableElements;
	private FlipControl flipAttr;
	private FlipControl flipPowers;
	private FlipControl flipResources;
	private FlipControl flipCultures;
	private FlipControl flipLanguages;
	private FlipControl flipNormalSkills;
	private FlipControl flipCombat;
	private FlipControl flipMagic;
	private FlipControl flipSpells;
	private FlipControl flipEquipment;
	private FlipControl flipCompanions;
	private FlipControl flipDevelopment;
	private FlipControl flipNotes;
	
	private AttentionPane paneAttr;
	private AttentionPane panePowers;
	private AttentionPane paneResources;
	private AttentionPane paneNormalSkills;
	private AttentionPane paneCombat;
	private AttentionPane paneMagic;
	private AttentionPane paneSpells;
	
	
	private transient CharGenWizardSpliMo    wizard;

	//-------------------------------------------------------------------
	public CharacterViewScreenSpliMo(CharacterController control, ViewMode mode) {
		this.control = control;
		this.mode    = mode;
//		this.invisible = FXCollections.observableArrayList(new ArrayList<Node>());
		setSkin(new ManagedScreenStructuredSkin(this));
		setTitle("Undefined");
		
		initComponents();
		initLayout();
		initInteractivity();
	
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().addAll(CloseType.APPLY, CloseType.CANCEL);
//		heading = new Label();
		content = new HBox();
		
		baseBlock = new BaseDataBlockSpliMo(mode, control);
		
		attrPrimary = new AttributeCard(Attribute.primaryValues());
		attrSecondary = new AttributeCard(Attribute.secondaryValues());
		powers      = new PowerCard();
		resources   = new ResourceCard();
		cultures    = new CultureLoreCard();
		languages   = new LanguageCard();
		skillNormal = new SkillCard(SkillType.NORMAL);
		skillCombat = new SkillCard(SkillType.COMBAT);
		skillMagic  = new SkillCard(SkillType.MAGIC);
		spells      = new SpellCard();
		notes       = new NotesCard();
		
		CharacterEditCallback callback = new CharacterEditCallback() {
			
			@Override
			public void dialogClosed(boolean wasCancelled) {
				// TODO Auto-generated method stub
				
			}
		};
		charDocPane = new CharacterLevelingPane(this, control, callback);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		content.setSpacing(40);
		
		flow = new FlowPane(Orientation.VERTICAL);
		flow.setVgap(20);
		flow.setHgap(20);
		
		flipCtrl = new FlipControl(Orientation.HORIZONTAL);
		
		availableElements = new ArrayList<>();
		initBaseBlockLayout();
		initAttributeBlocks();
		initPowerCard();
		initResourceCard();
		initCultureLores();
		initLanguages();
		initSkillsNormal();
		initSkillsCombat();
		initSkillsMagic();
		initSpells();
		initEquipment();
		initCompanions();
//		initBackground();
		initDevelopment();
//		initNotes();

		restoreState();
		
		ScrollPane scroll = new ScrollPane(flow);
		scroll.setFitToHeight(true);
		
		flipCtrl.getItems().addAll(scroll, charDocPane);
		content.getChildren().add(flipCtrl);
		
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initBaseBlockLayout() {
		HBox.setMargin(baseBlock, new Insets(40,0,20,0));
		
		content.getChildren().add(baseBlock);
	}

	//-------------------------------------------------------------------
	private void initAttributeBlocks() {
		/*
		 * Simple button
		 */
		Button btnAttributes = new Button(uiResources.getString("label.attributes"));
		btnAttributes.setAlignment(Pos.TOP_LEFT);
		btnAttributes.getStyleClass().addAll("text-subheader");
//		btnAttributes.setPrefSize(200, 200);
		btnAttributes.setStyle("-fx-background-color: dark; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label title = new Label(uiResources.getString("label.attributecards"));
		title.getStyleClass().add("section-head");
		
		Region spacing = new Region();
		spacing.setPrefHeight(20);
		VBox crdAttributes = new VBox();
//		crdAttributes.setPrefWidth(300);
		crdAttributes.getChildren().addAll(title, attrPrimary, spacing,attrSecondary);
		
		/*
		 * FlipPanel
		 */
		flipAttr = new FlipControl(Orientation.VERTICAL, true);
		flipAttr.getItems().addAll( btnAttributes, crdAttributes);
		flipAttr.setId("Attributes");
		
		paneAttr = new AttentionPane(flipAttr);
		paneAttr.setId("Attributes");
		availableElements.add(paneAttr);
	}

	//-------------------------------------------------------------------
	private void initPowerCard() {
		/*
		 * Simple button
		 */
		Button btnPowers = new Button(uiResources.getString("label.powers")+" & "+uiResources.getString("label.weaknesses"));
		btnPowers.setWrapText(true);
		btnPowers.setAlignment(Pos.TOP_LEFT);
		btnPowers.getStyleClass().add("text-subheader");
//		btnPowersNResources.setPrefSize(200, 200);
		btnPowers.setStyle("-fx-background-color: darker; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label title = new Label(uiResources.getString("label.powers"));
		title.getStyleClass().add("section-head");
		
		VBox crdPowers = new VBox();
		crdPowers.getChildren().addAll(title, powers);
		
		/*
		 * FlipPanel
		 */
		flipPowers = new FlipControl(Orientation.VERTICAL, true);
		flipPowers.getItems().addAll( btnPowers, crdPowers);
		flipPowers.setId("PowersNResources");
//		availableElements.add(flipPowersNResources);
		panePowers = new AttentionPane(flipPowers);
		panePowers.setId("PowersNResources");
		availableElements.add(panePowers);
	}

	//-------------------------------------------------------------------
	private void initResourceCard() {
		/*
		 * Simple button
		 */
		Button btnResources = new Button(uiResources.getString("label.resources"));
		btnResources.setWrapText(true);
		btnResources.setAlignment(Pos.TOP_LEFT);
		btnResources.getStyleClass().add("text-subheader");
//		btnPowersNResources.setPrefSize(200, 200);
		btnResources.setStyle("-fx-background-color: darker; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label title = new Label(uiResources.getString("label.resources"));
		title.getStyleClass().add("section-head");
		
		VBox crdResources = new VBox();
//		crdPowersNResources.setPrefWidth(300);
		crdResources.getChildren().addAll(title, resources);
		
		/*
		 * FlipPanel
		 */
		flipResources = new FlipControl(Orientation.VERTICAL, true);
		flipResources.getItems().addAll( btnResources, crdResources);
		flipResources.setId("Resources");
//		availableElements.add(flipPowersNResources);
		paneResources = new AttentionPane(flipResources);
		paneResources.setId("Resources");
		availableElements.add(paneResources);
	}

	//-------------------------------------------------------------------
	private void initCultureLores() {
		/*
		 * Simple button
		 */
		Button button = new Button(uiResources.getString("label.culturelores"));
		button.setWrapText(true);
		button.setAlignment(Pos.TOP_LEFT);
		button.getStyleClass().add("text-subheader");
//		btnPowersNResources.setPrefSize(200, 200);
		button.setStyle("-fx-background-color: darker; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label title = new Label(uiResources.getString("label.culturelores"));
		title.getStyleClass().add("section-head");
		
		VBox card = new VBox();
//		crdPowersNResources.setPrefWidth(300);
		card.getChildren().addAll(title, cultures);
		
		/*
		 * FlipPanel
		 */
		flipCultures = new FlipControl(Orientation.VERTICAL, true);
		flipCultures.getItems().addAll( button, card);
		flipCultures.setId("CultureLores");
//		availableElements.add(flipCultures);
		AttentionPane attPane = new AttentionPane(flipCultures);
		attPane.setId("CultureLores");
		availableElements.add(attPane);
	}

	//-------------------------------------------------------------------
	private void initLanguages() {
		/*
		 * Simple button
		 */
		Button button = new Button(uiResources.getString("label.languages"));
		button.setWrapText(true);
		button.setAlignment(Pos.TOP_LEFT);
		button.getStyleClass().add("text-subheader");
//		btnPowersNResources.setPrefSize(200, 200);
		button.setStyle("-fx-background-color: darker; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label title = new Label(uiResources.getString("label.languages"));
		title.getStyleClass().add("section-head");
		
		VBox card = new VBox();
//		crdPowersNResources.setPrefWidth(300);
		card.getChildren().addAll(title, languages);
		
		/*
		 * FlipPanel
		 */
		flipLanguages = new FlipControl(Orientation.VERTICAL, true);
		flipLanguages.getItems().addAll( button, card);
		flipLanguages.setId("Languages");
//		availableElements.add(flipLanguages);
		AttentionPane attPane = new AttentionPane(flipLanguages);
		attPane.setId("Languages");
		availableElements.add(attPane);
	}

	//-------------------------------------------------------------------
	private void initSkillsNormal() {
		/*
		 * Simple button
		 */
		Button btnNormalSkills = new Button(uiResources.getString("label.skills"));
		btnNormalSkills.setWrapText(true);
		btnNormalSkills.setAlignment(Pos.TOP_LEFT);
		btnNormalSkills.getStyleClass().add("text-subheader");
//		btnNormalSkills.setPrefSize(200, 200);
		btnNormalSkills.setStyle("-fx-background-color: lighter; -fx-text-fill: dark; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label tNormal = new Label(uiResources.getString("label.skills"));
		tNormal.getStyleClass().add("section-head");
		VBox crdNormalSkills = new VBox();
//		crdNormalSkills.setPrefWidth(300);
//		crdNormalSkills.setPrefHeight(400);
		crdNormalSkills.getChildren().addAll(tNormal, skillNormal);
		
		/*
		 * FlipPanel
		 */
		flipNormalSkills = new FlipControl(Orientation.VERTICAL, true);
		flipNormalSkills.getItems().addAll( btnNormalSkills, crdNormalSkills);
		flipNormalSkills.setId("NormalSkills");
//		availableElements.add(flipNormalSkills);
		paneNormalSkills = new AttentionPane(flipNormalSkills);
		paneNormalSkills.setId("NormalSkills");
		availableElements.add(paneNormalSkills);
	}

	//-------------------------------------------------------------------
	private void initSkillsCombat() {
		/*
		 * Simple button
		 */
		Button btnCombat = new Button(uiResources.getString("label.combat"));
		btnCombat.setWrapText(true);
		btnCombat.setAlignment(Pos.TOP_LEFT);
		btnCombat.getStyleClass().add("text-subheader");
//		btnCombat.setPrefSize(200, 200);
		btnCombat.setStyle("-fx-background-color: light; -fx-text-fill: dark; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label tCombat = new Label(uiResources.getString("label.combatskill.short"));
		tCombat.getStyleClass().add("section-head");
		VBox crdCombat = new VBox();
//		crdCombat.setPrefWidth(300);
		crdCombat.getChildren().addAll(tCombat, skillCombat);
		
		/*
		 * FlipPanel
		 */
		flipCombat = new FlipControl(Orientation.VERTICAL, true);
		flipCombat.getItems().addAll( btnCombat, crdCombat);
		flipCombat.setId("Combat");
//		availableElements.add(flipCombat);
		paneCombat = new AttentionPane(flipCombat);
		paneCombat.setId("Combat");
		availableElements.add(paneCombat);
	}

	//-------------------------------------------------------------------
	private void initSkillsMagic() {
		/*
		 * Simple button
		 */
		Button btnMagic = new Button(uiResources.getString("label.magic"));
		btnMagic.setWrapText(true);
		btnMagic.setAlignment(Pos.TOP_LEFT);
		btnMagic.getStyleClass().add("text-subheader");
//		btnMagic.setPrefSize(200, 200);
		btnMagic.setStyle("-fx-background-color: dark; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label tMagic = new Label(uiResources.getString("label.magicskills"));
		tMagic.getStyleClass().add("section-head");
		VBox crdMagic = new VBox();
		crdMagic.getChildren().addAll(tMagic, skillMagic);
		
		/*
		 * FlipPanel
		 */
		flipMagic = new FlipControl(Orientation.VERTICAL, true);
		flipMagic.getItems().addAll( btnMagic, crdMagic);
		flipMagic.setId("Magic");
//		availableElements.add(flipMagic);
		paneMagic = new AttentionPane(flipMagic);
		paneMagic.setId("Magic");
		availableElements.add(paneMagic);
	}

	//-------------------------------------------------------------------
	private void initSpells() {
		/*
		 * Simple button
		 */
		Button btnSpells = new Button(uiResources.getString("label.spells"));
		btnSpells.setWrapText(true);
		btnSpells.setAlignment(Pos.TOP_LEFT);
		btnSpells.getStyleClass().add("text-subheader");
		btnSpells.setStyle("-fx-background-color: dark; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label tSpells = new Label(uiResources.getString("label.spells"));
		tSpells.getStyleClass().add("section-head");
		VBox crdSpell = new VBox();
		crdSpell.getChildren().addAll(tSpells, spells);
		
		/*
		 * FlipPanel
		 */
		flipSpells = new FlipControl(Orientation.VERTICAL, true);
		flipSpells.getItems().addAll( btnSpells, crdSpell);
		flipSpells.setId("Spells");
//		availableElements.add(flipSpells);
		paneSpells = new AttentionPane(flipSpells);
		paneSpells.setId("Spells");
		availableElements.add(paneSpells);
	}

	//-------------------------------------------------------------------
	private void initEquipment() {
		/*
		 * Simple button
		 */
		Button button = new Button(uiResources.getString("label.equipment"));
		button.setWrapText(true);
		button.setAlignment(Pos.TOP_LEFT);
		button.getStyleClass().add("text-subheader");
//		button.setPrefSize(200, 200);
		button.setStyle("-fx-background-color: darker; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label label = new Label(uiResources.getString("label.equipment"));
		label.getStyleClass().add("section-head");
		VBox card = new VBox();
		card.getChildren().addAll(label);
		
		/*
		 * FlipPanel
		 */
		flipEquipment = new FlipControl(Orientation.VERTICAL, true);
		flipEquipment.getItems().addAll( button, card);
		flipEquipment.setId("Equipment");
//		availableElements.add(flipEquipment);
		AttentionPane attPane = new AttentionPane(flipEquipment);
		attPane.setId("Equipment");
		availableElements.add(attPane);
	}

	//-------------------------------------------------------------------
	private void initCompanions() {
		/*
		 * Simple button
		 */
		Button button = new Button(uiResources.getString("label.companions"));
		button.setWrapText(true);
		button.setAlignment(Pos.TOP_LEFT);
		button.getStyleClass().add("text-subheader");
//		button.setPrefSize(200, 200);
		button.setStyle("-fx-background-color: lighter; -fx-text-fill: dark; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label label = new Label(uiResources.getString("label.companions"));
		label.getStyleClass().add("section-head");
		VBox card = new VBox();
		card.getChildren().addAll(label);
		
		/*
		 * FlipPanel
		 */
		flipCompanions = new FlipControl(Orientation.VERTICAL, true);
		flipCompanions.getItems().addAll( button, card);
		flipCompanions.setId("Companions");
//		availableElements.add(flipCompanions);
		AttentionPane attPane = new AttentionPane(flipCompanions);
		attPane.setId("Companions");
		availableElements.add(attPane);
	}

	//-------------------------------------------------------------------
	private void initDevelopment() {
		/*
		 * Simple button
		 */
		Button button = new Button(uiResources.getString("label.development"));
		button.setWrapText(true);
		button.setAlignment(Pos.TOP_LEFT);
		button.getStyleClass().add("text-subheader");
//		button.setPrefSize(200, 200);
		button.setStyle("-fx-background-color: dark; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label label = new Label(uiResources.getString("label.development"));
		label.getStyleClass().add("section-head");
		VBox card = new VBox();
		card.getChildren().addAll(label);
		
		/*
		 * FlipPanel
		 */
		flipDevelopment = new FlipControl(Orientation.VERTICAL, true);
		flipDevelopment.getItems().addAll( button, card);
		flipDevelopment.setId("Development");
//		availableElements.add(flipDevelopment);
		AttentionPane attPane = new AttentionPane(flipDevelopment);
		attPane.setId("Development");
		availableElements.add(attPane);
	}

	//-------------------------------------------------------------------
	private void initNotes() {
		/*
		 * Simple button
		 */
		Button button = new Button(uiResources.getString("label.notes"));
		button.setWrapText(true);
		button.setAlignment(Pos.TOP_LEFT);
		button.getStyleClass().add("text-subheader");
//		button.setPrefSize(200, 200);
		button.setStyle("-fx-background-color: darker; -fx-text-fill: light; -fx-pref-width: 8em; -fx-pref-height: 8em;");

		/*
		 * Detail card
		 */
		Label label = new Label(uiResources.getString("label.notes"));
		label.getStyleClass().add("section-head");
		VBox card = new VBox();
		card.getChildren().addAll(label, notes);
		
		/*
		 * FlipPanel
		 */
		flipNotes = new FlipControl(Orientation.VERTICAL, true);
		flipNotes.getItems().addAll( button, card);
		flipNotes.setId("Development");
		availableElements.add(flipNotes);
	}

	//-------------------------------------------------------------------
	private void openAttributes() {
		AttributeScreen toShow = new AttributeScreen(control, mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openSkills(SkillType type) {
		SkillScreen2 toShow = new SkillScreen2(control, mode, type);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openPowers() {
		PowerScreen toShow = new PowerScreen(control, mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openResources() {
		ResourceScreen toShow = new ResourceScreen(control, mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openCultureLores() {
		CultureLoreScreen toShow = new CultureLoreScreen(control.getCultureLoreController(), mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openLanguages() {
		LanguageScreen toShow = new LanguageScreen(control.getLanguageController(), mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openDevelopment() {
		logger.debug("openDevelopment");
		DevelopmentScreenSpliMo toShow = new DevelopmentScreenSpliMo();
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openSpells() {
		SpellScreen toShow = new SpellScreen(control, mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openEquipment() {
		EquipmentScreen toShow = new EquipmentScreen(control, mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openCompanions() {
		CreatureScreen toShow = new CreatureScreen(control);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void openNotes() {
		logger.warn("TODO: open Notes dialog");
//		CreatureScreen toShow = new CreatureScreen(control);
//		toShow.setData(model);
//		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.debug("Drag started "+event.getSource());
		Node source = (Node) event.getSource();
		logger.debug("Drag started2 "+source.getParent());
		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(source.getId());
        logger.debug("Drag "+source.getId());
        db.setContent(content);
        
        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        logger.debug("Snapshot is "+snapshot);
        db.setDragView(snapshot);
        
        event.consume();	
    }

	//-------------------------------------------------------------------
	private void dragEntered(DragEvent event) {
		Node target = (Node) event.getSource();
		/* the drag-and-drop gesture entered the target */
	    /* show to the user that it is an actual gesture target */
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
			target.getStyleClass().add("drop-target");
		}
         event.consume();
	}

	//-------------------------------------------------------------------
	private void dragExited(DragEvent event) {
		Node target = (Node) event.getSource();
		/* the drag-and-drop gesture entered the target */
	    /* show to the user that it is an actual gesture target */
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
			target.getStyleClass().remove("drop-target");
		}
         event.consume();
	}

	//-------------------------------------------------------------------
	private void dragOver(DragEvent event) {
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
	}

	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
		Node target = (Node) event.getSource();
		if (target.getId()==null) {
			logger.warn("Cannot drop before target without ID: "+target+"      source was "+event.getSource());
			return;
		}
	       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String idToMove = db.getString();
        	logger.debug("Move "+idToMove+" before "+target);
        	// Find component for source ID
        	Node toMove = null;
        	for (Node node : flow.getChildrenUnmodifiable()) {
        		if (node.getId().equals(idToMove)) {
        			toMove = node;
        			// Remove
        			flow.getChildren().remove(toMove);
        			break;
        		}
        	}
        	
        	// Re-insert
        	int index = flow.getChildren().indexOf(target.getParent());
        	flow.getChildren().add(index, toMove);
        	saveState();
           success = true;
        }
        /* let the source know whether the string was successfully 
         * transferred and used */
        event.setDropCompleted(success);
        
        event.consume();
	}

	//-------------------------------------------------------------------
	private void saveState() {
		StringBuffer buf = new StringBuffer();
		for (Node node : flow.getChildren()) {
			String id = node.getId();
			if (node instanceof FlipControl) {
				id = ( ( ((FlipControl)node).getVisibleIndex()==0)?"btn":"crd") + id;
			}
			buf.append(id+" ");
		}
		CONFIG.put("charview.order", buf.toString());
		logger.trace("memorized "+buf);
	}

	//-------------------------------------------------------------------
	private void restoreState() {
		String value = CONFIG.get("charview.order", null);		
		logger.warn("TODO: restore "+value);
		
		List<Node> toShow = new ArrayList<Node>(availableElements);
		
		if (value!=null) {
		StringTokenizer tok = new StringTokenizer(value);
		while (tok.hasMoreTokens()) {
			String id = tok.nextToken();
			String baseId = id;
			if (id.startsWith("btn") || id.startsWith("crd"))
				baseId = id.substring(3);
        	for (Node node : toShow) {
        		if (node.getId()==null)
        			continue;
        		if (node.getId().equals(baseId)) {
        			// Remove
        			toShow.remove(node);
        			// TODO flip if required
        			if (id.startsWith("crd")) {
        				logger.debug("  flip "+baseId);
//        				((FlipControl)node).flip();
        				((FlipControl)((AttentionPane)node).getChild()).flip();
//        				((FlipControl)node).flip();
        			}
            		flow.getChildren().add(node);
        			break;
        		}
        	}
		}
		}
		
		// Append all elements not previously added
		for (Node node : toShow) {
			flow.getChildren().add(node);
		}
		
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		// Attributes
		flipAttr.setOnMouseClicked(event -> openAttributes());
		flipCultures.setOnMouseClicked(event -> openCultureLores());
		flipLanguages.setOnMouseClicked(event -> openLanguages());
		flipPowers.setOnMouseClicked(event -> openPowers());
		flipResources.setOnMouseClicked(event -> openResources());
		flipNormalSkills.setOnMouseClicked(event -> openSkills(SkillType.NORMAL));
		flipCombat.setOnMouseClicked(event -> openSkills(SkillType.COMBAT));
		flipMagic.setOnMouseClicked(event -> openSkills(SkillType.MAGIC));
		flipSpells.setOnMouseClicked(event -> openSpells());
		flipDevelopment.setOnMouseClicked(event -> {event.consume(); openDevelopment(); } );
		flipEquipment.setOnMouseClicked(event -> openEquipment());
		flipCompanions.setOnMouseClicked(event -> openCompanions());
//		flipNotes.setOnMouseClicked(event -> openNotes());
		
		/*
		 * Drag and drop
		 */
		flipAttr.setOnDragDetected(event -> dragStarted(event));
		flipAttr.setOnDragEntered (event -> dragEntered(event));
		flipAttr.setOnDragOver    (event -> dragOver(event));
		flipAttr.setOnDragExited  (event -> dragExited(event));
		flipAttr.setOnDragDropped (event -> dragDropped(event));

		flipPowers.setOnDragDetected(event -> dragStarted(event));
		flipPowers.setOnDragEntered (event -> dragEntered(event));
		flipPowers.setOnDragOver    (event -> dragOver(event));
		flipPowers.setOnDragExited  (event -> dragExited(event));
		flipPowers.setOnDragDropped (event -> dragDropped(event));

		flipResources.setOnDragDetected(event -> dragStarted(event));
		flipResources.setOnDragEntered (event -> dragEntered(event));
		flipResources.setOnDragOver    (event -> dragOver(event));
		flipResources.setOnDragExited  (event -> dragExited(event));
		flipResources.setOnDragDropped (event -> dragDropped(event));

		flipCultures.setOnDragDetected(event -> dragStarted(event));
		flipCultures.setOnDragEntered (event -> dragEntered(event));
		flipCultures.setOnDragOver    (event -> dragOver(event));
		flipCultures.setOnDragExited  (event -> dragExited(event));
		flipCultures.setOnDragDropped (event -> dragDropped(event));

		flipLanguages.setOnDragDetected(event -> dragStarted(event));
		flipLanguages.setOnDragEntered (event -> dragEntered(event));
		flipLanguages.setOnDragOver    (event -> dragOver(event));
		flipLanguages.setOnDragExited  (event -> dragExited(event));
		flipLanguages.setOnDragDropped (event -> dragDropped(event));

		flipNormalSkills.setOnDragDetected(event -> dragStarted(event));
		flipNormalSkills.setOnDragEntered (event -> dragEntered(event));
		flipNormalSkills.setOnDragOver    (event -> dragOver(event));
		flipNormalSkills.setOnDragExited  (event -> dragExited(event));
		flipNormalSkills.setOnDragDropped (event -> dragDropped(event));

		flipMagic.setOnDragDetected(event -> dragStarted(event));
		flipMagic.setOnDragEntered (event -> dragEntered(event));
		flipMagic.setOnDragOver    (event -> dragOver(event));
		flipMagic.setOnDragExited  (event -> dragExited(event));
		flipMagic.setOnDragDropped (event -> dragDropped(event));

		flipCombat.setOnDragDetected(event -> dragStarted(event));
		flipCombat.setOnDragEntered (event -> dragEntered(event));
		flipCombat.setOnDragOver    (event -> dragOver(event));
		flipCombat.setOnDragExited  (event -> dragExited(event));
		flipCombat.setOnDragDropped (event -> dragDropped(event));

		flipSpells.setOnDragDetected(event -> dragStarted(event));
		flipSpells.setOnDragEntered (event -> dragEntered(event));
		flipSpells.setOnDragOver    (event -> dragOver(event));
		flipSpells.setOnDragExited  (event -> dragExited(event));
		flipSpells.setOnDragDropped (event -> dragDropped(event));

		flipEquipment.setOnDragDetected(event -> dragStarted(event));
		flipEquipment.setOnDragEntered (event -> dragEntered(event));
		flipEquipment.setOnDragOver    (event -> dragOver(event));
		flipEquipment.setOnDragExited  (event -> dragExited(event));
		flipEquipment.setOnDragDropped (event -> dragDropped(event));

		flipCompanions.setOnDragDetected(event -> dragStarted(event));
		flipCompanions.setOnDragEntered (event -> dragEntered(event));
		flipCompanions.setOnDragOver    (event -> dragOver(event));
		flipCompanions.setOnDragExited  (event -> dragExited(event));
		flipCompanions.setOnDragDropped (event -> dragDropped(event));

//		flipBackground.setOnDragDetected(event -> dragStarted(event));
//		flipBackground.setOnDragEntered (event -> dragEntered(event));
//		flipBackground.setOnDragOver    (event -> dragOver(event));
//		flipBackground.setOnDragExited  (event -> dragExited(event));
//		flipBackground.setOnDragDropped (event -> dragDropped(event));

		flipDevelopment.setOnDragDetected(event -> dragStarted(event));
		flipDevelopment.setOnDragEntered (event -> dragEntered(event));
		flipDevelopment.setOnDragOver    (event -> dragOver(event));
		flipDevelopment.setOnDragExited  (event -> dragExited(event));
		flipDevelopment.setOnDragDropped (event -> dragDropped(event));
		
		baseBlock.viewProperty().addListener( (ov,o,n) -> {
			if (n==View.DOCUMENT && flipCtrl.visibleNodeProperty().get()!=charDocPane)
				flipCtrl.flip();
			if (n==View.TILES && flipCtrl.visibleNodeProperty().get()==charDocPane)
				flipCtrl.flip();
		});
		flipCtrl.visibleNodeProperty().addListener( (ov,o,n) -> {
			if (n==charDocPane)
				baseBlock.viewProperty().set(View.DOCUMENT);
			else
				baseBlock.viewProperty().set(View.TILES);
		});
	}


	//-------------------------------------------------------------------
	private void updateAttentionFlags() {
		logger.debug("updateAttentionFlags");
		paneAttr.setAttentionFlag(control.getAttributeController().getToDos().size()>0);
		panePowers.setAttentionFlag(control.getPowerController().getToDos().size()>0 );
		paneResources.setAttentionFlag(control.getResourceController().getToDos().size()>0);
		paneNormalSkills.setAttentionFlag(control.getSkillController().getToDos(SkillType.NORMAL).size()>0);
		paneCombat.setAttentionFlag(control.getSkillController().getToDos(SkillType.COMBAT).size()>0);
		paneMagic.setAttentionFlag(control.getSkillController().getToDos(SkillType.MAGIC).size()>0);
		paneSpells.setAttentionFlag(!control.getSpellController().getToDos().isEmpty());
		
		paneAttr.setAttentionToolTip(control.getAttributeController().getToDos());
		panePowers.setAttentionToolTip(control.getPowerController().getToDos());
		paneResources.setAttentionToolTip(control.getResourceController().getToDos());
		paneNormalSkills.setAttentionToolTip(control.getSkillController().getToDos(SkillType.NORMAL));
		paneCombat.setAttentionToolTip(control.getSkillController().getToDos(SkillType.COMBAT));
		paneMagic.setAttentionToolTip(control.getSkillController().getToDos(SkillType.MAGIC));
		paneSpells.setAttentionToolTip(control.getSpellController().getToDos());
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model, CharacterHandle handle) {
		logger.debug("Show character "+model);
		this.model = model;
		this.handle= handle;
		
		setTitle(model.getName());

		baseBlock.setData(model);
		/*
		 * Attributes
		 */
		attrPrimary.setData(model);
		attrSecondary.setData(model);
		
		powers.setData(model);
		resources.setData(model);
		cultures.setData(model);
		languages.setData(model);
		skillNormal.setData(model);
		skillCombat.setData(model);
		skillMagic.setData(model);
		spells.setData(model);
		
		updateAttentionFlags();
		
		charDocPane.setData(model);
	}

	//--------------------------------------------------------------------
	public SpliMoCharacter getCharacter() {
		return model;
	}

	//-------------------------------------------------------------------
	public void startGeneration(SpliMoCharacter model) {
		logger.debug("startGeneration "+model);
		this.model = model;
		
		/*
		 * Attributes
		 */
		attrPrimary.setData(model);
		attrSecondary.setData(model);
		
		baseBlock.setData(model);
		powers.setData(model);
		resources.setData(model);
		cultures.setData(model);
		languages.setData(model);
		skillNormal.setData(model);
		skillCombat.setData(model);
		skillMagic.setData(model);
		spells.setData(model);
		
		charDocPane.setData(model);
		
		wizard = new CharGenWizardSpliMo(model, (SpliMoCharacterGenerator)control);
		manager.show(wizard);
		updateAttentionFlags();
	}
	
	//-------------------------------------------------------------------
	@Override
	public boolean close(CloseType type) {
		logger.info("close("+type+")");
		saveState();	
		if (type==CloseType.CANCEL) {
			if (mode==ViewMode.GENERATION) {
				if (CloseType.NO == manager.showAlertAndCall(AlertType.CONFIRMATION, 
								uiResources.getString("check.chargen.creation.abort.title"), 
								uiResources.getString("check.chargen.creation.abort.text"))) 
				{
					return false;
				}
			} else if (((CharacterLeveller)control).isAltered()) {
				if (CloseType.NO == manager.showAlertAndCall(AlertType.CONFIRMATION, 
								uiResources.getString("check.chargen.editing.abort.title"), 
								uiResources.getString("check.chargen.editing.abort.text"))) 
				{
					return false;
				}
			}
		} else if (type==CloseType.APPLY) {
			if (mode==ViewMode.GENERATION) {
				SpliMoCharacterGenerator generator = (SpliMoCharacterGenerator)control;
				if (!generator.hasEnoughData()) {
					manager.showAlertAndCall(AlertType.ERROR, 
							uiResources.getString("error.chargen.missingdata.title"), 
							uiResources.getString("error.chargen.missingdata.text"));
					return false;
				}
				((SpliMoCharacterGenerator)control).generate();
			} else {
				/*
				 * Write all made modifications to character
				 */
				logger.debug("Add modifications to character log");
				((CharacterLeveller)control).updateHistory();
				
				/*
				 * 1. Call plugin to encode character
				 * 2. Use character service to save character
				 */
				logger.debug("encode character "+model.getName());				
				CommandResult result = CommandBus.fireCommand(this, CommandType.ENCODE, 
						handle.getRuleIdentifier(),
						model
						);
				if (!result.wasProcessed()) {
					logger.error("Cannot save character, since encoding failed");
					manager.showAlertAndCall(
							AlertType.ERROR, 
							"Das hätte nicht passieren dürfen", 
							"Es hat sich kein Plugin gefunden, welches das Kodieren von Charakteren dieses Systems erlaubt."
					);
				} else {
					byte[] encoded = (byte[]) result.getReturnValue();
					try {
						logger.info("Save character "+model.getName());
						RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, null, encoded);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			/*
			 * Update portrait
			 */
			logger.debug("Update portrait");
			CharacterProvider charServ = RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService();
			try {
				if (model.getImage()!=null && handle!=null) {
					Attachment attach = handle.getFirstAttachment(Type.CHARACTER, Format.IMAGE);
					if (attach!=null) {
						logger.info("Update character image");
						attach.setData(model.getImage());
						charServ.modifyAttachment(handle, attach);
					} else {
						charServ.addAttachment(handle, Type.CHARACTER, Format.IMAGE, null, model.getImage());
					}
				} else if (handle!=null) {
					Attachment attach = handle.getFirstAttachment(Type.CHARACTER, Format.IMAGE);
					if (attach!=null) {
						logger.info("Delete old character image");
						charServ.removeAttachment(handle, attach);
					}
				}
			} catch (IOException e) {
				logger.error("Failed modifying portrait attachment",e);
			}
		}
		
		// Remove all listeners
		logger.debug("Remove generation listener");
		GenerationEventDispatcher.clear();
		logger.info("-------------Closing "+getClass().getSimpleName()+"---------------------------------");
		
		return true;
	}
	
	//-------------------------------------------------------------------
	public void childClosed(ManagedScreen child, CloseType type) {
		logger.debug("childClosed("+child+", "+type+") not overwritten");
		
		if (child instanceof GenerationEventListener) {
			GenerationEventDispatcher.removeListener((GenerationEventListener) child);
		}
		
		if (child==wizard) {
			if (type==CloseType.FINISH) {
				// Update dialog content
				updateAttentionFlags();
			} else if (type==CloseType.CANCEL) {
				logger.debug("Generation cancelled");
				wizard  = null;
				manager.close(this, CloseType.CANCEL);
			}
		} else if (child.getClass()==AppearanceScreen.class) {
			if (!model.getName().equals(handle.getName())) {
				logger.info("Rename "+handle.getName()+" to "+model.getName());
				try {
					RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService().renameCharacter(handle, model.getName());
					BabylonEventBus.fireEvent(BabylonEventType.CHAR_RENAMED, handle);
				} catch (IOException e) {
					logger.error("Failed renaming",e);
				}
			}
		} else {
			updateAttentionFlags();
			logger.warn("childClosed("+child+", "+type+") not processed");
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case BASE_DATA_CHANGED:
			setTitle(model.getName());
			break;
		case POINTS_LEFT_ATTRIBUTES:
		case POINTS_LEFT_MASTERSHIPS:
		case POINTS_LEFT_POWERS:
		case POINTS_LEFT_RESOURCES:
			updateAttentionFlags();
			break;
		case ATTRIBUTE_CHANGED:
			// update items after attribute change in case min Requirements are now met.
			logger.info("attribute changed, updating items..");
			boolean mightHaveChanged = EquipmentTools.updateAllItems(model);
			if (mightHaveChanged) {
				GenerationEventDispatcher.fireEvent(
						new GenerationEvent(GenerationEventType.ITEM_CHANGED, model)
				);
			}
			break;
		default:
		}
		
	}

	//-------------------------------------------------------------------
	public void setScreenManager(ScreenManager manager) {
		this.manager = manager;
		baseBlock.setManager(manager);
	}

}
