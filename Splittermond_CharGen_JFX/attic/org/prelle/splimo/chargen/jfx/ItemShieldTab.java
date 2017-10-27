package org.prelle.splimo.chargen.jfx;

import static org.prelle.splimo.items.ItemType.SHIELD;

import org.prelle.splimo.Utils;
import org.prelle.splimo.charctrl.ItemController;
import org.prelle.splimo.chargen.lvl.jfx.SkillField;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.Shield;
import org.prelle.splittermond.jfx.equip.ItemUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

//-------------------------------------------------------------------
class ItemShieldTab extends GridPane {

	private CarriedItem model;
	private ItemController control;
	
	private Label defense;
	private Node handicap;
	private Node tickMalus;
	private Label minAttr;
	private ListView<Feature> features;
	
	//-------------------------------------------------------------------
	public ItemShieldTab() {
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			handicap = new SkillField();
			tickMalus = new SkillField();
		} else {
			handicap = new Label();
			tickMalus = new Label();
		}
		
		defense    = new Label();
		minAttr    = new Label();
		features   = new ListView<Feature>();
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		this.setVgap(5);
		this.setHgap(15);
		this.setPadding(new Insets(3));
		
		Label lblDefense = new Label(ItemAttribute.DEFENSE.getName());
		Label lblHandi   = new Label(ItemAttribute.HANDICAP.getName());
		Label lblTickM   = new Label(ItemAttribute.TICK_MALUS.getName());
		Label lblMinAttr = new Label(ItemAttribute.MIN_ATTRIBUTES.getName());
		Label lblFeatures= new Label(ItemAttribute.FEATURES.getName());
		lblDefense.getStyleClass().add("text-small-subheader");
		lblHandi.getStyleClass().add("text-small-subheader");
		lblTickM.getStyleClass().add("text-small-subheader");
		lblMinAttr.getStyleClass().add("text-small-subheader");
		lblFeatures.getStyleClass().add("text-small-subheader");

		features.setPrefHeight(100);

		add(lblDefense  , 0, 1);
		add(defense     , 1, 1);
		add(lblHandi    , 0, 2);
		add(handicap    , 1, 2);
		add(lblTickM    , 0, 3);
		add(tickMalus   , 1, 3);
		add(lblMinAttr  , 0, 4);
		add(minAttr     , 1, 4);
		add(lblFeatures , 0, 5, 2,1);
		add(features    , 0, 6, 2,1);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)handicap).inc.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.increase(ItemAttribute.HANDICAP);}});
			((SkillField)handicap).dec.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.decrease(ItemAttribute.HANDICAP);}});
			((SkillField)tickMalus).inc.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.increase(ItemAttribute.TICK_MALUS);}});
			((SkillField)tickMalus).dec.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.decrease(ItemAttribute.TICK_MALUS);}});
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
			((SkillField)handicap).dec.setDisable(!control.canBeDecreased(ItemAttribute.HANDICAP));
			((SkillField)handicap).inc.setDisable(!control.canBeIncreased(ItemAttribute.HANDICAP));
			((SkillField)tickMalus).dec.setDisable(!control.canBeDecreased(ItemAttribute.TICK_MALUS));
			((SkillField)tickMalus).inc.setDisable(!control.canBeIncreased(ItemAttribute.TICK_MALUS));
		}
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)handicap).setText(String.valueOf(model.getHandicap(ItemType.SHIELD)));
			((SkillField)tickMalus).setText(String.valueOf(model.getTickMalus(ItemType.SHIELD)));
		} else {
			((Label)handicap).setText(ItemUtils.getWeaponDamageString(model.getHandicap(ItemType.SHIELD)));
			((Label)tickMalus).setText(String.valueOf(model.getTickMalus(ItemType.SHIELD)));
		}
		
		Shield shield = model.getItem().getType(Shield.class);
		defense.setText(String.valueOf(shield.getDefense()));
		minAttr.setText(ItemUtils.getMindestAttribute(shield.getRequirements()));
		ItemUtils.fillFeatureListView(model, features, SHIELD);
	}
	
}