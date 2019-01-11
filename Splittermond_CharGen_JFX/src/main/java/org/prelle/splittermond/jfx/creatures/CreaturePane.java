/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import org.prelle.javafx.FlipControl;
import org.prelle.splimo.chargen.jfx.ViewMode;
import org.prelle.splimo.creature.Creature;

import javafx.geometry.Orientation;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class CreaturePane extends VBox {

	private ViewMode mode;
	
	private Creature model;
	
	private CreatureTypeViewPane  typeView;
	private AttributeViewPane attribView;
	private AttributeEditPane attribEdit;
	private WeaponViewPane  weaponView;
	private WeaponEditPane  weaponEdit;
	private SkillViewPane  skillView;
	private SkillEditPane  skillEdit;
	private SpellViewPane  spellView;
	private SpellEditPane  spellEdit;
	private CreatureFeatureViewPane  featView;

	//-------------------------------------------------------------------
	/**
	 */
	public CreaturePane(ViewMode mode) {
		this.mode = mode;
		initComponents();
		initStyle();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		typeView   = new CreatureTypeViewPane();
		attribView = new AttributeViewPane();
		weaponView = new WeaponViewPane();
		skillView  = new SkillViewPane(true);
		spellView  = new SpellViewPane();
		featView   = new CreatureFeatureViewPane();
		
		if (mode==ViewMode.MODIFICATION) {
			skillEdit  = new SkillEditPane(skillView);
			weaponEdit = new WeaponEditPane(weaponView);
			attribEdit = new AttributeEditPane(attribView, weaponView, weaponEdit, skillView, skillEdit);
			spellEdit  = new SpellEditPane(spellView);
		}
	}

	//--------------------------------------------------------------------
	private void initStyle() {
		typeView.getStyleClass().add("content");
		attribView.getStyleClass().add("content");
		weaponView.getStyleClass().add("content");
		skillView.getStyleClass().add("content");
		spellView.getStyleClass().add("content");
		featView.getStyleClass().add("content");
		
		if (mode==ViewMode.MODIFICATION) {
			attribEdit.getStyleClass().add("content");
			weaponEdit.getStyleClass().add("content");
			skillEdit.getStyleClass().add("content");
			spellEdit.getStyleClass().add("content");
		}
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setSpacing(4);
		attribView.setMaxWidth(Double.MAX_VALUE);
		weaponView.setMaxWidth(Double.MAX_VALUE);
		if (mode==ViewMode.MODIFICATION) {
			FlipControl flip = new FlipControl(Orientation.HORIZONTAL, true);
			flip.getItems().addAll(attribView, attribEdit);			
			getChildren().addAll(flip);
			
			flip = new FlipControl(Orientation.HORIZONTAL, true);
			flip.getItems().addAll(weaponView, weaponEdit);			
			getChildren().addAll(flip);
			
			flip = new FlipControl(Orientation.HORIZONTAL, true);
			flip.getItems().addAll(skillView, skillEdit);			
			getChildren().addAll(flip);
			
			flip = new FlipControl(Orientation.HORIZONTAL, true);
			flip.getItems().addAll(spellView, spellEdit);			
			getChildren().addAll(flip);
		} else {
			getChildren().addAll(typeView, attribView, weaponView, skillView, spellView, featView);
		}
	}

	//--------------------------------------------------------------------
	public void setData(Creature model) {
		this.model = model;
		
		typeView.setData(model);
		attribView.setData(model);
		weaponView.setData(model);
		skillView.setData(model);
		spellView.setData(model);
		featView.setData(model);
		if (mode==ViewMode.MODIFICATION) {
			attribEdit.setData(model);
			weaponEdit.setData(model);
			skillEdit.setData(model);
			spellEdit.setData(model);
		}
	}

}
