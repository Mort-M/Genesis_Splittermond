package org.prelle.splimo.chargen.jfx;

import static org.prelle.splimo.items.ItemType.ARMOR;

import org.prelle.splimo.Utils;
import org.prelle.splimo.charctrl.ItemController;
import org.prelle.splimo.chargen.lvl.jfx.SkillField;
import org.prelle.splimo.items.Armor;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemType;
import org.prelle.splittermond.jfx.equip.ItemUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

//-------------------------------------------------------------------
class ItemArmorTab extends GridPane {

	private CarriedItem model;
	private ItemController control;
	
	private Label defense;
	private Node damageRed;
	private Node handicap;
	private Node tickMalus;
	private Label minAttr;
	private ListView<Feature> features;
	
	//-------------------------------------------------------------------
	public ItemArmorTab() {
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			damageRed = new SkillField();
			handicap  = new SkillField();
			tickMalus = new SkillField();
		} else {
			damageRed = new Label();
			handicap  = new Label();
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
		Label lblDamage  = new Label(ItemAttribute.DAMAGE_REDUCTION.getName());
		lblDefense.getStyleClass().add("text-small-subheader");
		lblDamage.getStyleClass().add("text-small-subheader");
		lblHandi.getStyleClass().add("text-small-subheader");
		lblTickM.getStyleClass().add("text-small-subheader");
		lblMinAttr.getStyleClass().add("text-small-subheader");
		lblFeatures.getStyleClass().add("text-small-subheader");

		features.setPrefHeight(100);

		add(lblDefense  , 0, 1);
		add(defense     , 1, 1);
		add(lblDamage   , 0, 2);
		add(damageRed   , 1, 2);
		add(lblHandi    , 0, 3);
		add(handicap    , 1, 3);
		add(lblTickM    , 0, 4);
		add(tickMalus   , 1, 4);
		add(lblMinAttr  , 0, 5);
		add(minAttr     , 1, 5);
		add(lblFeatures , 0, 6, 2,1);
		add(features    , 0, 7, 2,1);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)damageRed).inc.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.increase(ItemAttribute.DAMAGE_REDUCTION);}});
			((SkillField)damageRed).dec.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.decrease(ItemAttribute.DAMAGE_REDUCTION);}});
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
		if (model==null)
			return;
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)damageRed).dec.setDisable(!control.canBeDecreased(ItemAttribute.DAMAGE_REDUCTION));
			((SkillField)damageRed).inc.setDisable(!control.canBeIncreased(ItemAttribute.DAMAGE_REDUCTION));
			((SkillField)handicap).dec.setDisable(!control.canBeDecreased(ItemAttribute.HANDICAP));
			((SkillField)handicap).inc.setDisable(!control.canBeIncreased(ItemAttribute.HANDICAP));
			((SkillField)tickMalus).dec.setDisable(!control.canBeDecreased(ItemAttribute.TICK_MALUS));
			((SkillField)tickMalus).inc.setDisable(!control.canBeIncreased(ItemAttribute.TICK_MALUS));
		}
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)damageRed).setText(String.valueOf(model.getDamageReduction(ItemType.ARMOR)));
			((SkillField)handicap) .setText(String.valueOf(model.getHandicap       (ItemType.ARMOR)));
			((SkillField)tickMalus).setText(String.valueOf(model.getTickMalus      (ItemType.ARMOR)));
		} else {
			((Label)damageRed).setText(String.valueOf(model.getDamageReduction(ItemType.ARMOR)));
			((Label)handicap) .setText(String.valueOf(model.getHandicap       (ItemType.ARMOR)));
			((Label)tickMalus).setText(String.valueOf(model.getTickMalus      (ItemType.ARMOR)));
		}
		
		Armor armor = model.getItem().getType(Armor.class);
		defense.setText(String.valueOf(armor.getDefense()));
		minAttr.setText(ItemUtils.getMindestAttribute(armor.getRequirements()));
		ItemUtils.fillFeatureListView(model, features, ARMOR);
	}
	
}