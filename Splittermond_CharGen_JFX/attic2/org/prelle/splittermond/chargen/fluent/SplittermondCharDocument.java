/**
 *
 */
package org.prelle.splittermond.chargen.fluent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.fluent.CommandBar;
import org.prelle.javafx.fluent.NodeWithTitle;
import org.prelle.rpgframework.jfx.CharacterDocumentView;
import org.prelle.rpgframework.jfx.CharacterDocumentView.Section;
import org.prelle.rpgframework.jfx.SettingsAndCommandBar;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.common.jfx.SpellPane;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.ViewMode;
import org.prelle.splittermond.jfx.attributes.AttributePanePrimary;
import org.prelle.splittermond.jfx.attributes.AttributePaneSecondary;
import org.prelle.splittermond.jfx.master.MastershipScreen;
import org.prelle.splittermond.jfx.resources.ResourcePane;
import org.prelle.splittermond.jfx.skills.SkillPane;
import org.prelle.splittermond.jfx.skills.SkillPaneCallback;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.ToDoElement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SplittermondCharDocument implements GenerationEventListener, NodeWithTitle {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private static Preferences CONFIG = Preferences.userRoot().node("/org/rpgframework/genesis/splittermond");

	private StringProperty titleProperty = new SimpleStringProperty();
	private ObjectProperty<Node> contentProperty = new SimpleObjectProperty<>();


	private SpliMoCharacter model;
	private CharacterHandle handle;
	private ScreenManagerProvider provider;
	private CharacterController control;

	private CharacterDocumentView content;

	private Label lbExpTotal;
	private Label lbExpInvested;
	private Label lbLevel;
	private CommandBar commands;
	private MenuItem cmdPrint;
	private MenuItem cmdDelete;

	private VBox layout;
	private BaseDataGrid baseData;
	private AttributePanePrimary attrPrimary;
	private AttributePaneSecondary attrSecondary;
	private PowersAndWeaknessesPane powerweakPane;
	private CultureAndLanguagePane cultLangPane;
	private SkillPane skillsCommon;
	private SkillPane skillsCombat;
	private SkillPane skillsMagic;
	private SpellPane spellsPane;
	private ResourcePane resrcPane;

	private Map<Node, Section> sections = new HashMap<>();

	//-------------------------------------------------------------------
	public SplittermondCharDocument(CharacterController control, CharacterHandle handle, ScreenManagerProvider manager) {
		this.control = control;
		this.handle  = handle;
		this.provider= manager;
		initComponents();
		initInteractivity();

		content.setPointsNameProperty(uiResources.getString("label.ep.free"));

		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initBaseData() {
		baseData = new BaseDataGrid(control, handle);
		baseData.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		Section sec = new Section();
		sec.setContent(baseData);
		content.getSectionList().add(sec);
		sections.put(baseData, sec);
	}

	//-------------------------------------------------------------------
	private void initPowers() {
		powerweakPane = new PowersAndWeaknessesPane(control);

		powerweakPane.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		Section sec = new Section();
		sec.setTitle(uiResources.getString("powerscreen.title"));
		sec.setContent(powerweakPane);
		sec.getToDoList().addAll(convert(control.getPowerController().getToDos()));
		content.getSectionList().add(sec);
		sections.put(powerweakPane, sec);

		// Description text
		powerweakPane.selectedItemProperty().addListener( (ov,o,n) -> {
			logger.info("Selected "+n);
			VBox desc = new VBox(5);
			desc.getStyleClass().add("description-text");

			if (n!=null) {
				Label lbName = new Label(n.getPower().getName());
				lbName.getStyleClass().add("subtitle");
				Label lbPage = new Label(n.getPower().getProductNameShort()+" "+n.getPower().getPage()+"");
				lbPage.setStyle("-fx-font-weight: bold");
				Label lbText = new Label(n.getPower().getHelpText());
				lbText.setWrapText(true);
				desc.setUserData(n);
				desc.getChildren().addAll(lbName, lbPage, lbText);
			}
				content.setDescriptionNode(desc);
		});
		powerweakPane.selectedAvailProperty().addListener( (ov,o,n) -> {
			logger.info("Selected "+n);
			VBox desc = new VBox(5);
			desc.getStyleClass().add("description-text");

			if (n!=null) {
				Label lbName = new Label(n.getName());
				lbName.getStyleClass().add("subtitle");
				Label lbPage = new Label(n.getProductNameShort()+" "+n.getPage()+"");
				lbPage.setStyle("-fx-font-weight: bold");
				Label lbText = new Label(n.getDescription());
				lbText.setWrapText(true);
				desc.setUserData(n);
				desc.getChildren().addAll(lbName, lbPage, lbText);
			}
				content.setDescriptionNode(desc);
		});

	}

	//-------------------------------------------------------------------
	private void initCulturesAndLanguages() {
		cultLangPane = new CultureAndLanguagePane(control);

		cultLangPane.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		Section sec = new Section();
		sec.setTitle(uiResources.getString("section.cultlang"));
		sec.setContent(cultLangPane);
		content.getSectionList().add(sec);
		sections.put(cultLangPane, sec);

		// Description text
		cultLangPane.selectedCultureItemProperty().addListener( (ov,o,n) -> {
			logger.info("Selected "+n);
			VBox desc = new VBox(5);
			desc.getStyleClass().add("description-text");

			if (n!=null) {
				Label lbName = new Label(n.getCultureLore().getName());
				lbName.getStyleClass().add("subtitle");
				Label lbPage = new Label(n.getCultureLore().getProductNameShort()+" "+n.getCultureLore().getPage()+"");
				lbPage.setStyle("-fx-font-weight: bold");
				Label lbText = new Label(n.getCultureLore().getHelpText());
				lbText.setWrapText(true);
				desc.setUserData(n);
				desc.getChildren().addAll(lbName, lbPage, lbText);
			}
				content.setDescriptionNode(desc);
		});
		cultLangPane.selectedAvailProperty().addListener( (ov,o,n) -> {
			logger.info("Selected "+n);
			VBox desc = new VBox(5);
			desc.getStyleClass().add("description-text");

			if (n!=null) {
				Label lbName = new Label(n.getName());
				lbName.getStyleClass().add("subtitle");
				Label lbPage = new Label(n.getProductNameShort()+" "+n.getPage()+"");
				lbPage.setStyle("-fx-font-weight: bold");
				Label lbText = new Label(n.getHelpText());
				lbText.setWrapText(true);
				desc.setUserData(n);
				desc.getChildren().addAll(lbName, lbPage, lbText);
			}
				content.setDescriptionNode(desc);
		});
	}

	//-------------------------------------------------------------------
	private void initAttributes() {
		attrPrimary = new AttributePanePrimary(control.getAttributeController(), ViewMode.MODIFICATION);
		attrSecondary = new AttributePaneSecondary(control.getAttributeController(), ViewMode.MODIFICATION);

		HBox bxAttributesInner = new HBox(attrPrimary, attrSecondary);
		bxAttributesInner.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
//		VBox bxAttributes = new VBox(lbAttributes, bxAttributesInner);
//		bxAttributes.setStyle("-fx-spacing: 0.3em;");
//		content.getChildren().add(bxAttributes);
		Section secAttr = new Section();
		secAttr.setTitle(uiResources.getString("label.attributes"));
		secAttr.setContent(bxAttributesInner);
		secAttr.getToDoList().addAll(convert(control.getAttributeController().getToDos()));
		content.getSectionList().add(secAttr);
		sections.put(attrPrimary, secAttr);

		// Description text
		EventHandler<MouseEvent> attrHandler = (event) -> {
			EventTarget target = event.getTarget();
			if ((target instanceof Label) && (((Label)target).getUserData() instanceof Attribute)) {
				Attribute newAttr = (Attribute) ((Label)target).getUserData();
				Object old = (content.getDescriptionNode()!=null)?content.getDescriptionNode().getUserData():null;
				if (newAttr==old)
					return;
				Label lbName = new Label(newAttr.getName());
				lbName.getStyleClass().add("subtitle");
				Label lbPage = new Label(SplitterMondCore.getSkill("athletics").getProductNameShort()+" "+uiResources.getString("descr.attribute."+newAttr.name().toLowerCase()+".page"));
				lbPage.setStyle("-fx-font-weight: bold");
				Label lbText = new Label(uiResources.getString("descr.attribute."+newAttr.name().toLowerCase()));
				lbText.setWrapText(true);
				VBox desc = new VBox(5);
				desc.getStyleClass().add("description-text");
				desc.setUserData(newAttr);
				desc.getChildren().addAll(lbName, lbPage, lbText);
				content.setDescriptionNode(desc);
			}
		};
		attrPrimary.setOnMouseEntered(attrHandler);
		attrPrimary.setOnMouseMoved(attrHandler);
		attrSecondary.setOnMouseEntered(attrHandler);
		attrSecondary.setOnMouseMoved(attrHandler);

	}

	//-------------------------------------------------------------------
	private void initResources() {
		resrcPane = new ResourcePane(control.getResourceController(), true, false);
		resrcPane.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px; -fx-pref-height: 20em;");
		Section section = new Section();
		section.setTitle(uiResources.getString("label.resources"));
		section.setContent(resrcPane);
		section.getToDoList().addAll(convert(control.getResourceController().getToDos()));
		content.getSectionList().add(section);
		sections.put(resrcPane, section);

		// Selections
//		resrcPane.selectedSpellProperty().addListener( (ov,o,n) -> {
//			VBox desc = new VBox(5);
//			if (n!=null) {
//				Spell spell = n.getSpell();
//				Label lbName = new Label(spell.getName());
//				lbName.getStyleClass().add("subtitle");
//				Label lbPage = new Label(spell.getProductNameShort()+" "+n.getSkill().getPage());
//				lbPage.setStyle("-fx-font-weight: bold");
//				Label lbText = new Label(spell.getHelpText());
//				lbText.setWrapText(true);
//				desc.getChildren().addAll(lbName, lbPage, lbText);
//			}
//			content.setDescriptionNode(desc);
//		});

		section.getToDoList().addAll(convert(control.getResourceController().getToDos()));
	}

	//-------------------------------------------------------------------
	private void initSkills() {
		/*
		 * Common skills
		 */
		skillsCommon = new SkillPane(new SkillPaneCallback() {
			public void showAndWaitMasterships(Skill skill) {
				logger.info("showMasterships for "+skill);

				MastershipScreen screen = new MastershipScreen(control);
				screen.setData(model, model.getSkillValue(skill));
				provider.getScreenManager().show(screen);
			}
		}, control.getSkillController(), control.getMastershipController(), true, SkillType.NORMAL);
		skillsCommon.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
//		VBox bxSkills = new VBox(lineSkills, skillsCommon);
//		bxSkills.setStyle("-fx-spacing: 0.3em;");
//		content.getChildren().add(bxSkills);
		Section secSkills = new Section();
		secSkills.setTitle(uiResources.getString("label.skills"));
		secSkills.setContent(skillsCommon);
		content.getSectionList().add(secSkills);
		sections.put(skillsCommon, secSkills);

		// Selections
		skillsCommon.selectedSkillProperty().addListener( (ov,o,n) -> {
			VBox desc = new VBox(5);
			if (n!=null) {
				Label lbName = new Label(n.getName());
				lbName.getStyleClass().add("subtitle");
				Label lbPage = new Label(n.getSkill().getProductNameShort()+" "+n.getSkill().getPage());
				lbPage.setStyle("-fx-font-weight: bold");
				Label lbText = new Label(n.getSkill().getHelpText());
				lbText.setWrapText(true);
				desc.getChildren().addAll(lbName, lbPage, lbText);
			}
			content.setDescriptionNode(desc);
		});

		secSkills.getToDoList().addAll(convert(control.getSkillController().getToDos(SkillType.NORMAL)));

	}

	//-------------------------------------------------------------------
	private void initCombatSkills() {
		skillsCombat = new SkillPane(new SkillPaneCallback() {
			@Override
			public void showAndWaitMasterships(Skill skill) {
				logger.info("showMasterships for "+skill);

				MastershipScreen screen = new MastershipScreen(control);
				screen.setData(model, model.getSkillValue(skill));
				provider.getScreenManager().show(screen);
			}
		}, control.getSkillController(), control.getMastershipController(), true, SkillType.COMBAT);
		skillsCombat.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		Section secCSkills = new Section();
		secCSkills.setTitle(uiResources.getString("label.combatskills"));
		secCSkills.setContent(skillsCombat);
		content.getSectionList().add(secCSkills);
		sections.put(skillsCombat, secCSkills);

		secCSkills.getToDoList().addAll(convert(control.getSkillController().getToDos(SkillType.COMBAT)));
	}

	//-------------------------------------------------------------------
	private void initMagicSkills() {
		skillsMagic = new SkillPane(new SkillPaneCallback() {
			@Override
			public void showAndWaitMasterships(Skill skill) {
				logger.info("showMasterships for "+skill);

				MastershipScreen screen = new MastershipScreen(control);
				screen.setData(model, model.getSkillValue(skill));
				provider.getScreenManager().show(screen);
			}
		}, control.getSkillController(), control.getMastershipController(), true, SkillType.MAGIC);
		skillsMagic.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		Section secMSkills = new Section();
		secMSkills.setTitle(uiResources.getString("label.magicskills"));
		secMSkills.setContent(skillsMagic);
		secMSkills.getToDoList().addAll(convert(control.getSkillController().getToDos(SkillType.MAGIC)));
		content.getSectionList().add(secMSkills);
		sections.put(skillsMagic, secMSkills);

	}

	//-------------------------------------------------------------------
	private void initSpells() {
		spellsPane = new SpellPane(control.getSpellController(), false);
		spellsPane.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		Section section = new Section();
		section.setTitle(uiResources.getString("label.spells"));
		section.setContent(spellsPane);
		section.getToDoList().addAll(convert(control.getSpellController().getToDos()));
		content.getSectionList().add(section);
		sections.put(spellsPane, section);

		// Selections
		spellsPane.selectedSpellProperty().addListener( (ov,o,n) -> {
			VBox desc = new VBox(5);
			if (n!=null) {
				Spell spell = n.getSpell();
				Label lbName = new Label(spell.getName());
				lbName.getStyleClass().add("subtitle");
				Label lbPage = new Label(spell.getProductNameShort()+" "+n.getSkill().getPage());
				lbPage.setStyle("-fx-font-weight: bold");
				Label lbText = new Label(spell.getHelpText());
				lbText.setWrapText(true);
				desc.getChildren().addAll(lbName, lbPage, lbText);
			}
			content.setDescriptionNode(desc);
		});

		section.getToDoList().addAll(convert(control.getSpellController().getToDos()));
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		Label hdExpTotal    = new Label(SpliMoCharGenConstants.RES.getString("label.ep.total")+": ");
		Label hdExpInvested = new Label(SpliMoCharGenConstants.RES.getString("label.ep.used")+": ");
		Label hdLevel       = new Label(SpliMoCharGenConstants.RES.getString("label.level")+": ");
		lbExpTotal    = new Label("?");
		lbExpInvested = new Label("?");
		lbLevel       = new Label("?");
		lbExpTotal.getStyleClass().add("base");
		lbExpInvested.getStyleClass().add("base");
		lbLevel.getStyleClass().add("base");

		/*
		 * Command bar
		 */
		cmdPrint = new MenuItem(uiResources.getString("label.print"), new Label("\uE749"));
		cmdDelete = new MenuItem(uiResources.getString("label.delete"), new Label("\uE74D"));
		commands = new CommandBar();
		if (handle!=null) {
			commands.getItems().add(cmdPrint);
		}

		HBox expLine = new HBox(5);
		expLine.getChildren().addAll(hdExpTotal, lbExpTotal, hdExpInvested, lbExpInvested, hdLevel, lbLevel);
		HBox.setMargin(hdLevel, new Insets(0,0,0,20));
		expLine.getStyleClass().add("character-document-view-firstline");

		SettingsAndCommandBar firstLine = new SettingsAndCommandBar();
		firstLine.setSettings(expLine);
		firstLine.setCommandBar(commands);

		content = new CharacterDocumentView();
		content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		content.setStyle("-fx-spacing: 2em;");

		initBaseData();
		initAttributes();
		initPowers();
		initCulturesAndLanguages();
		initResources();
		initSkills();
		initCombatSkills();
		initMagicSkills();
		initSpells();

		layout = new VBox(firstLine, content);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, control.getModel());});
//		cmdDelete.setOnAction( ev -> BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, control.getCharacter()));
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model, CharacterHandle handle) {
		logger.debug("Show character "+model);
		this.model = model;
		this.handle= handle;

		lbExpTotal.setText((model.getExperienceInvested()+model.getExperienceFree())+"");
		lbExpInvested.setText(model.getExperienceInvested()+"");
		lbLevel.setText(model.getLevel()+"");

//		Label header = new Label(model.getName());
//		header.setStyle("-fx-font-size: 300%; -fx-background-image: url(images/background.jpg); -fx-background-repeat: no-repeat; -fx-background-size: cover;");
//		header.setMaxWidth(Double.MAX_VALUE);
//		setHeader(header);
		setTitle(model.getName());
		content.setPointsFree(model.getExperienceFree());

		baseData.refresh();
		attrPrimary.setData(model);
		attrSecondary.setData(model);
		skillsCommon.setContent(model);
		skillsCombat.setContent(model);
		skillsMagic.setContent(model);
		spellsPane.setData(model);
		resrcPane.setData(model);
	}

	//-------------------------------------------------------------------
	public void refresh() {
		lbExpTotal.setText((model.getExperienceInvested()+model.getExperienceFree())+"");
		lbExpInvested.setText(model.getExperienceInvested()+"");
		lbLevel.setText(model.getLevel()+"");
		setTitle(model.getName());
		content.setPointsFree(model.getExperienceFree());
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case BASE_DATA_CHANGED:
			logger.debug("RCV "+event.getType());
			setTitle(model.getName());
			break;
		case POINTS_LEFT_ATTRIBUTES:
		case POINTS_LEFT_MASTERSHIPS:
		case POINTS_LEFT_POWERS:
		case POINTS_LEFT_RESOURCES:
		case SKILL_CHANGED:
		case SPELL_FREESELECTION_CHANGED:
		case CHARACTER_CHANGED:
			logger.debug("RCV "+event.getType());
			updateAttentionFlags();
			break;
		case EXPERIENCE_CHANGED:
			logger.debug("RCV "+event);
			lbExpTotal.setText((model.getExperienceInvested()+model.getExperienceFree())+"");
			lbExpInvested.setText(model.getExperienceInvested()+"");
			lbLevel.setText(model.getLevel()+"");
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
	private void updateAttentionFlags() {
		sections.get(attrPrimary).getToDoList().clear();
		sections.get(attrPrimary).getToDoList().addAll(convert(control.getAttributeController().getToDos()));
		sections.get(spellsPane).getToDoList().clear();
		sections.get(spellsPane).getToDoList().addAll(convert(control.getSpellController().getToDos()));
		sections.get(powerweakPane).getToDoList().clear();
		sections.get(powerweakPane).getToDoList().addAll(convert(control.getPowerController().getToDos()));
		sections.get(skillsCommon).getToDoList().clear();
		sections.get(skillsCommon).getToDoList().addAll(convert(control.getSkillController().getToDos(SkillType.NORMAL)));
		sections.get(skillsCombat).getToDoList().clear();
		sections.get(skillsCombat).getToDoList().addAll(convert(control.getSkillController().getToDos(SkillType.COMBAT)));
		sections.get(skillsMagic).getToDoList().clear();
		sections.get(skillsMagic).getToDoList().addAll(convert(control.getSkillController().getToDos(SkillType.MAGIC)));
		sections.get(resrcPane).getToDoList().clear();
		sections.get(resrcPane).getToDoList().addAll(convert(control.getResourceController().getToDos()));
		sections.get(spellsPane).getToDoList().clear();
		System.err.println("SplittermondCharDocument.updateAttentionFlags: "+control.getSpellController().getToDos());
		sections.get(spellsPane).getToDoList().addAll(convert(control.getSpellController().getToDos()));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.fluent.NodeWithTitle#getContent()
	 */
	@Override
	public Node getContent() {
		return contentProperty.get();
	}
	private void setContent(Node value) {
		contentProperty.set(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.fluent.NodeWithTitle#contentProperty()
	 */
	@Override
	public ReadOnlyObjectProperty<Node> contentProperty() {
		return contentProperty;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.fluent.NodeWithTitle#getTitle()
	 */
	@Override
	public String getTitle() {
		return titleProperty.get();
	}
	private void setTitle(String value) {
		titleProperty.set(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.fluent.NodeWithTitle#titleProperty()
	 */
	@Override
	public ReadOnlyStringProperty titleProperty() {
		return titleProperty;
	}

	//-------------------------------------------------------------------
	private static List<ToDoElement> convert(List<String> todos) {
		List<ToDoElement> ret = new ArrayList<>();
		for (String todo : todos) {
			ToDoElement l = new ToDoElement(ToDoElement.Severity.STOPPER, todo);
			ret.add(l);
		}
		return ret;
	}

}
