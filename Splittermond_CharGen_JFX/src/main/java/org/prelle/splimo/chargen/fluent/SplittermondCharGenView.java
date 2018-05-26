/**
 * 
 */
package org.prelle.splimo.chargen.fluent;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.fluent.NavigationPane;
import org.prelle.javafx.fluent.NavigationView;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.ViewMode;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.jfx.attributes.AttributeCard;
import org.prelle.splittermond.jfx.skills.SkillPane;
import org.prelle.splittermond.jfx.skills.SkillPaneCallback;

import de.rpgframework.character.CharacterHandle;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SplittermondCharGenView extends NavigationView implements GenerationEventListener, ScreenManagerProvider, ResponsiveControl {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static Preferences CONFIG = Preferences.userRoot().node("/org/rpgframework/genesis/splittermond");
	
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private SpliMoCharacter model;
	private CharacterHandle handle;
	private CharacterController control;
	private ViewMode mode;
	private VBox content;

	private MenuItem menuOverview;
	private MenuItem menuAttrib;
	private MenuItem menuStrengthWeakness;
	private MenuItem menuCultLang;
	private MenuItem menuSkillNormal;
	private MenuItem menuSkillCombat;
	private MenuItem menuSkillMagic;
	private MenuItem menuSettings;

	private AttributeCard attrPrimary;
	private AttributeCard attrSecondary;
	private SkillPane skills;

	//-------------------------------------------------------------------
	public SplittermondCharGenView(CharacterController control) {
		this.control = control;
		initComponents();
		initNavigation();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		content = new VBox();
		content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		content.setStyle("-fx-spacing: 2em;");
		
		attrPrimary = new AttributeCard(Attribute.primaryValues());
		attrSecondary = new AttributeCard(Attribute.secondaryValuesWithoutDR());
		Label lbAttributes = new Label("Attribute");
		lbAttributes.setStyle("-fx-font-size: 150%; -fx-font-weight: bold");
		HBox bxAttributesInner = new HBox(attrPrimary, attrSecondary);
		bxAttributesInner.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		VBox bxAttributes = new VBox(lbAttributes, bxAttributesInner);
		bxAttributes.setStyle("-fx-spacing: 0.3em;");
		content.getChildren().add(bxAttributes);
		
		Label lbSkills = new Label("Allgemeine Fertigkeiten");
		lbSkills.setStyle("-fx-font-size: 150%; -fx-font-weight: bold");
		Label lbSkillsArrow = new Label("\uE111");
		lbSkillsArrow.setStyle("-fx-font-size: 100%; -fx-font-family: 'Segoe UI Symbol'");
		Region bufSkills = new Region();
		bufSkills.setMaxWidth(Double.MAX_VALUE);
		HBox lineSkills = new HBox(lbSkills,bufSkills,lbSkillsArrow);
		HBox.setHgrow(bufSkills, Priority.ALWAYS);
		skills = new SkillPane(new SkillPaneCallback() {
			
			@Override
			public void showAndWaitMasterships(Skill skill) {
			}
		}, control.getSkillController(), control.getMastershipController(), true, SkillType.NORMAL);
		skills.setStyle("-fx-spacing: 2em; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, black, 5, 0.5, 2, 2); -fx-padding: 1em; -fx-border-width: 2px;");
		VBox bxSkills = new VBox(lineSkills, skills);
		bxSkills.setStyle("-fx-spacing: 0.3em;");
		content.getChildren().add(bxSkills);
		
		Region left = new Region();
		Region right = new Region();
		left.setMaxWidth(Double.MAX_VALUE);
//		left.setStyle("-fx-min-width: 5em;");
		right.setMaxWidth(Double.MAX_VALUE);
//		right.setStyle("-fx-min-width: 5em;");
		
		HBox layout = new HBox();
//		layout.setStyle("-fx-background-color: rgba(0,255,255, 0.3)");
		layout.getChildren().addAll(left, content, right);
		ScrollPane scroll = new ScrollPane(layout);
		scroll.setFitToWidth(true);
		scroll.setStyle("-fx-background-color: cyan; -fx-background-image: url(images/background.jpg); -fx-background-repeat: no-repeat; -fx-background-size: cover;");
//		BorderPane layout = new BorderPane();
		layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		layout.setCenter(content);
//		layout.setLeft(left);
//		layout.setRight(right);
		HBox.setHgrow(left, Priority.ALWAYS);
		HBox.setHgrow(right, Priority.ALWAYS);
		
		setContent(scroll);
	}

	//-------------------------------------------------------------------
	private void initNavigation() {
		menuOverview = new MenuItem("Übersicht", new Label(""));
		menuAttrib   = new MenuItem("Attribute", new Label(""));
		menuStrengthWeakness = new MenuItem("Stärken & Schwächen", new Label(""));
		menuCultLang = new MenuItem("Kultur & Sprache", new Label(""));
		menuSkillNormal = new MenuItem("Allgemeine Fertigkeiten", new Label(""));
		menuSkillCombat = new MenuItem("Kampffertigkeiten", new Label(""));
		menuSkillMagic = new MenuItem("Zauberschulen", new Label(""));
		menuSettings = new MenuItem("Einstellungen", new Label("\u2699"));
		getItems().addAll(menuOverview, menuAttrib, menuStrengthWeakness, menuCultLang, menuSkillNormal);
		getItems().add(menuSkillCombat);
		getItems().add(menuSkillMagic);
		getItems().add(new NavigationPane.SpacingMenuItem());
		getItems().add(new MenuItem("Drucken", new Label("\uD83D\uDDB6")));
		getItems().add(new NavigationPane.SpacingMenuItem());
		getItems().add(menuSettings);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model, CharacterHandle handle) {
		logger.debug("Show character "+model);
		this.model = model;
		this.handle= handle;
		
		Label header = new Label(model.getName());
		header.setStyle("-fx-font-size: 300%; -fx-background-image: url(images/background.jpg); -fx-background-repeat: no-repeat; -fx-background-size: cover;");
		header.setMaxWidth(Double.MAX_VALUE);
		setHeader(header);
		setTitle(model.getName());

//		baseBlock.setData(model);
		/*
		 * Attributes
		 */
		attrPrimary.setData(model);
		attrSecondary.setData(model);
		skills.setContent(model);
		
//		powers.setData(model);
//		resources.setData(model);
//		cultures.setData(model);
//		languages.setData(model);
//		skillNormal.setData(model);
//		skillCombat.setData(model);
//		skillMagic.setData(model);
//		spells.setData(model);
//		notes.setData(model);
//		
//		updateAttentionFlags();
//		
//		charDocPane.setData(model);
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
//			updateAttentionFlags();
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
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		super.setResponsiveMode(value);
		
	}

}
