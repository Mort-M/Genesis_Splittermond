package org.prelle.splittermond.chargen.lvl.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.common.jfx.SpellPane;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.ViewMode;
import org.prelle.splittermond.jfx.attributes.AttributePane;
import org.prelle.splittermond.jfx.skills.SkillPane;
import org.prelle.splittermond.jfx.skills.SkillPaneCallback;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CharacterLevelingPane extends HBox implements EventHandler<ActionEvent>, SkillPaneCallback {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;
	
	private CharacterController charCtrl;
	
	private TopBlock       top;
	private AttributePane  attrib;
	private PowerResourceBlock powRes;
	private SkillPane      skill;
	private SkillPane	   combat;
	private WeaponBlock    weapons;
	private ArmorBlock     armor;
	private ShieldBlock    shield;
	private SkillPane	   magic;
	private SpellPane      spells;
	
	private CharacterEditCallback callback;
	
	//-------------------------------------------------------------------
	public CharacterLevelingPane(ScreenManagerProvider provider, CharacterController control, CharacterEditCallback callback) {
		super(5);
		this.charCtrl = control;
		this.callback = callback;
		// Prepare Character generator
//		charGen = new CharacterGenerator(CharGenMode.LEVELING);
		
		VBox box = new VBox(10);
		box.setFillWidth(true);
		box.setId("character-document");
		
		top    = new TopBlock(control.getCultureLoreController(), control.getLanguageController());
		attrib = new AttributePane(control.getAttributeController(), ViewMode.MODIFICATION);
		powRes = new PowerResourceBlock();
		skill  = new SkillPane(this, control.getSkillController(), control.getMastershipController(), true, SkillType.NORMAL);
		combat = new SkillPane(this, control.getSkillController(), control.getMastershipController(), true, SkillType.COMBAT);
		weapons= new WeaponBlock();
		armor  = new ArmorBlock();
		shield = new ShieldBlock();
		magic  = new SkillPane(this, control.getSkillController(), control.getMastershipController(), true, SkillType.MAGIC);
		spells = new SpellPane(charCtrl.getSpellController(), true);
		top.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(top, Priority.ALWAYS);
		
		box.getChildren().add(top);
		box.getChildren().add(attrib);
		box.getChildren().add(powRes);
		box.getChildren().add(skill);
		box.getChildren().add(combat);
		box.getChildren().add(weapons);
		box.getChildren().add(armor);
		box.getChildren().add(shield);
		box.getChildren().add(magic);
		box.getChildren().add(spells);
		
		ScrollPane scroll = new ScrollPane();
		scroll.setPadding(new Insets(3));
		scroll.setContent(box);
		scroll.setFitToWidth(true);
//		scroll.setPrefViewportWidth(1000);
//		scroll.setPrefViewportHeight(700);
		
		this.getChildren().addAll(scroll);
		
		HBox.setHgrow(scroll, Priority.ALWAYS);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter data) {
		top.setContent(data, charCtrl.getMastershipController());
		attrib.setData(data);
		powRes.setContent(data, charCtrl);
		skill.setContent(data);
		magic.setContent(data);
		combat.setContent(data);
		armor.setContent(data);
		shield.setContent(data);
		weapons.setContent(data);
//		spells.setContent(data, control);
		spells.setData(data);
	}

	//--------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		logger.debug("'Apply' clicked");
		try {
//			charCtrl.updateHistory();
			CharacterLevelingPane.this.callback.dialogClosed(false);
		} catch (Exception e) {
			logger.error("Saving character failed",e);
			System.err.println("Saving character failed: "+e);
		}
		
		// Close dialog
		this.getScene().getWindow().hide();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.jfx.skills.SkillPaneCallback#showAndWaitMasterships(org.prelle.splimo.Skill)
	 */
	@Override
	public void showAndWaitMasterships(Skill skill) {
		// TODO Auto-generated method stub
		logger.warn("TODO: open mastership dialog");
	}

}
