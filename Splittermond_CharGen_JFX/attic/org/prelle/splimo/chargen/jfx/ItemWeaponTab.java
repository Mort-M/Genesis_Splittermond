package org.prelle.splimo.chargen.jfx;

import static org.prelle.splimo.items.ItemType.WEAPON;

import org.prelle.splimo.Utils;
import org.prelle.splimo.charctrl.ItemController;
import org.prelle.splimo.chargen.lvl.jfx.SkillField;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.Weapon;
import org.prelle.splittermond.jfx.equip.ItemUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

//-------------------------------------------------------------------
class ItemWeaponTab extends GridPane {

	private CarriedItem model;
	private ItemController control;
	
	private Node damage;
	private Node speed;
	private Label attributes;
	private Label minAttr;
	private ListView<Feature> features;
	
	//-------------------------------------------------------------------
	public ItemWeaponTab() {
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			damage = new SkillField("    ");
			speed = new SkillField();
		} else {
			damage = new Label();
			speed = new Label();
		}
		
		attributes = new Label();
		minAttr    = new Label();
		features   = new ListView<Feature>();
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		this.setVgap(5);
		this.setHgap(15);
		this.setPadding(new Insets(3));
		
		Label lblDamage = new Label(ItemAttribute.DAMAGE.getName());
		Label lblSpeed  = new Label(ItemAttribute.SPEED.getName());
		Label lblAttr   = new Label(ItemAttribute.ATTRIBUTES.getName());
		Label lblMinAttr = new Label(ItemAttribute.MIN_ATTRIBUTES.getName());
		Label lblFeatures= new Label(ItemAttribute.FEATURES.getName());
		lblDamage.getStyleClass().add("text-small-subheader");
		lblSpeed.getStyleClass().add("text-small-subheader");
		lblAttr.getStyleClass().add("text-small-subheader");
		lblMinAttr.getStyleClass().add("text-small-subheader");
		lblFeatures.getStyleClass().add("text-small-subheader");

		features.setPrefHeight(100);

		add(lblDamage   , 0, 1);
		add(damage      , 1, 1);
		add(lblSpeed    , 0, 2);
		add(speed       , 1, 2);
		add(lblAttr     , 0, 3);
		add(attributes  , 1, 3);
		add(lblMinAttr  , 0, 4);
		add(minAttr     , 1, 4);
		add(lblFeatures , 0, 5, 2,1);
		add(features    , 0, 6, 2,1);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)damage).inc.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.increase(ItemAttribute.DAMAGE);}});
			((SkillField)damage).dec.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.decrease(ItemAttribute.DAMAGE);}});
			((SkillField)speed).inc.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.increase(ItemAttribute.SPEED);}});
			((SkillField)speed).dec.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.decrease(ItemAttribute.SPEED);}});
		}
	}
	
	//-------------------------------------------------------------------
	public void setData(CarriedItem data, ItemController control) {
		this.model = data;
		this.control = control;
		
		refresh();
	}

	//-------------------------------------------------------------------
	void refresh() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)damage).dec.setDisable(!control.canBeDecreased(ItemAttribute.DAMAGE));
			((SkillField)damage).inc.setDisable(!control.canBeIncreased(ItemAttribute.DAMAGE));
			((SkillField)speed).dec.setDisable(!control.canBeDecreased(ItemAttribute.SPEED));
			((SkillField)speed).inc.setDisable(!control.canBeIncreased(ItemAttribute.SPEED));
		}
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)damage).setText(ItemUtils.getWeaponDamageString(model.getDamage(ItemType.WEAPON)));
			((SkillField)speed).setText(String.valueOf(model.getSpeed(ItemType.WEAPON)));
		} else {
			((Label)damage).setText(ItemUtils.getWeaponDamageString(model.getDamage(ItemType.WEAPON)));
			((Label)speed).setText(String.valueOf(model.getSpeed(ItemType.WEAPON)));
		}
		
		Weapon weapon = model.getItem().getType(Weapon.class);
		attributes.setText(ItemUtils.getAttributeString(weapon.getAttribute1(), weapon.getAttribute2()));
		minAttr.setText(ItemUtils.getMindestAttribute(weapon.getRequirements()));
		ItemUtils.fillFeatureListView(model, features, WEAPON);
	}
	
}