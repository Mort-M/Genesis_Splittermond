package org.prelle.splimo.chargen.jfx;

import java.util.ResourceBundle;

import org.prelle.splimo.Utils;
import org.prelle.splimo.charctrl.ItemController;
import org.prelle.splimo.chargen.lvl.jfx.SkillField;
import org.prelle.splimo.equipment.QualityEffectPane;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemAttribute;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

//-------------------------------------------------------------------
class ItemCommonTab extends GridPane {

	private final static ResourceBundle res = SpliMoCharGenJFXConstants.UI;

	private CarriedItem model;
	private ItemController control;

	private TextField customName;
	private Node load;
	private Node robust;
	private Label complex;
	private Label price;
	private Label avail;
	private QualityEffectPane quality;

	//-------------------------------------------------------------------
	public ItemCommonTab() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			customName = new TextField();
			load = new SkillField();
			robust = new SkillField();
		} else {
			load = new Label();
			robust = new Label();
		}

		complex = new Label();
		price   = new Label();
		avail   = new Label();
		quality = new QualityEffectPane();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		this.setVgap(5);
		this.setHgap(15);
		this.setPadding(new Insets(3));

		Label lblName   = new Label(res.getString("label.name"));
		Label lblLoad   = new Label(ItemAttribute.LOAD.getName());
		Label lblRobust = new Label(ItemAttribute.RIGIDITY.getName());
		Label lblComplex= new Label(ItemAttribute.COMPLEXITY.getName());
		Label lblPrice  = new Label(ItemAttribute.PRICE.getName());
		Label lblAvail  = new Label(ItemAttribute.AVAILABILITY.getName());
		lblName.getStyleClass().add("text-small-subheader");
		lblLoad.getStyleClass().add("text-small-subheader");
		lblRobust.getStyleClass().add("text-small-subheader");
		lblComplex.getStyleClass().add("text-small-subheader");
		lblPrice.getStyleClass().add("text-small-subheader");
		lblAvail.getStyleClass().add("text-small-subheader");

		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			add(lblName   , 0, 0);
			add(customName, 1, 0);
		}
		add(lblLoad   , 0, 1);
		add(load      , 1, 1);
		add(lblRobust , 0, 2);
		add(robust    , 1, 2);
		add(lblComplex, 0, 3);
		add(complex   , 1, 3);
		add(lblPrice  , 0, 4);
		add(price     , 1, 4);
		add(lblAvail  , 0, 5);
		add(avail     , 1, 5);
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT))
			add(quality   , 0, 6, 2,1);

	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			((SkillField)load).inc.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.increase(ItemAttribute.LOAD);}});
			((SkillField)load).dec.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.decrease(ItemAttribute.LOAD);}});
			((SkillField)robust).inc.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.increase(ItemAttribute.RIGIDITY);}});
			((SkillField)robust).dec.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent ev) {control.decrease(ItemAttribute.RIGIDITY);}});
		}
	}

	//-------------------------------------------------------------------
	public void setData(CarriedItem data, ItemController control) {
		this.model = data;
		this.control = control;

		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			customName.setText(data.getName());
			((SkillField)load).setText(String.valueOf(model.getLoad()));
			((SkillField)robust).setText(String.valueOf(model.getRigidity()));
		} else {
			((Label)load).setText(String.valueOf(model.getLoad()));
			((Label)robust).setText(String.valueOf(model.getRigidity()));
		}

		complex.setText(data.getItem().getComplexity() != null ? data.getItem().getComplexity().getName() : "keine");
		avail.setText(data.getAvailability().getName());
		price.setText(String.valueOf(data.getPrice()) + " T");

		if (load instanceof SkillField) {
			((SkillField)load).dec.setDisable(!control.canBeDecreased(ItemAttribute.LOAD));
			((SkillField)load).inc.setDisable(!control.canBeIncreased(ItemAttribute.LOAD));
		}
		if (robust instanceof SkillField) {
			((SkillField)robust).dec.setDisable(!control.canBeDecreased(ItemAttribute.RIGIDITY));
			((SkillField)robust).inc.setDisable(!control.canBeIncreased(ItemAttribute.RIGIDITY));
		}
		quality.setController(control);
		quality.setData(model);
	}

	//--------------------------------------------------------------------
	void refresh() {
		if (model==null)
			return;
		if (Utils.supports(org.prelle.splimo.Feature.MODIFY_EQUIPMENT)) {
			customName.setText(model.getName());
			((SkillField)load).setText(String.valueOf(model.getLoad()));
			((SkillField)robust).setText(String.valueOf(model.getRigidity()));
		} else {
			((Label)load).setText(String.valueOf(model.getLoad()));
			((Label)robust).setText(String.valueOf(model.getRigidity()));
		}

		complex.setText(model.getItem().getComplexity() != null ? model.getItem().getComplexity().getName() : "keine");
		avail.setText(model.getAvailability().getName());
		price.setText(String.valueOf(model.getPrice()) + " T");

		if (load instanceof SkillField) {
			((SkillField)load).dec.setDisable(!control.canBeDecreased(ItemAttribute.LOAD));
			((SkillField)load).inc.setDisable(!control.canBeIncreased(ItemAttribute.LOAD));
		}
		if (robust instanceof SkillField) {
			((SkillField)robust).dec.setDisable(!control.canBeDecreased(ItemAttribute.RIGIDITY));
			((SkillField)robust).inc.setDisable(!control.canBeIncreased(ItemAttribute.RIGIDITY));
		}
		quality.updateContent();
	}

}