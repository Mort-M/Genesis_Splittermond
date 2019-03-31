package org.prelle.splittermond.chargen.jfx.equip;

import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemAttribute;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class BasicItemDataPane extends GridPane {
	
	private final static String STYLE_EMPHASIZED = "emphasized";
	
	private CarriedItem model;
	
	private Label lblAvailability;
	private Label lblPrice;
	private Label lblLoad;
	private Label lblRigidity;
	private Label lblComplexity;

	//-------------------------------------------------------------------
	public BasicItemDataPane() {
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblAvailability = new Label();
		lblPrice        = new Label();
		lblLoad         = new Label();
		lblRigidity     = new Label();
		lblComplexity   = new Label();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label heaAvailability = new Label(ItemAttribute.AVAILABILITY.getShortName());
		Label heaPrice        = new Label(ItemAttribute.PRICE.getShortName());
		Label heaLoad         = new Label(ItemAttribute.LOAD.getShortName());
		Label heaRigidity     = new Label(ItemAttribute.RIGIDITY.getShortName());
		Label heaComplexity   = new Label(ItemAttribute.COMPLEXITY.getShortName());
		
		add(heaAvailability, 0,0);
		add(lblAvailability, 1,0);
		add(heaPrice       , 0,1);
		add(lblPrice       , 1,1);
		add(heaLoad        , 0,2);
		add(lblLoad        , 1,2);
		add(heaRigidity    , 0,3);
		add(lblRigidity    , 1,3);
		add(heaComplexity  , 0,4);
		add(lblComplexity  , 1,4);
		
		setVgap(2);
		setHgap(5);
		
		/*
		 * Styles
		 */
		this.getStyleClass().addAll("content","text-body","bordered");
		heaAvailability.getStyleClass().add("base");
		heaPrice.getStyleClass().add("base");
		heaLoad.getStyleClass().add("baser");
		heaRigidity.getStyleClass().add("base");
		heaComplexity.getStyleClass().add("base");
	}

	//-------------------------------------------------------------------
	public void refresh() {
		// Availability
		if (model.getAvailability()!=null)
			lblAvailability.setText(model.getAvailability().getName());
		if (model.isModified(ItemAttribute.AVAILABILITY)) {
			if (!lblAvailability.getStyleClass().contains(STYLE_EMPHASIZED))
				lblAvailability.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblAvailability.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Price
		lblPrice.setText(SplitterTools.telareAsCurrencyString(model.getPrice()));
		if (model.isModified(ItemAttribute.PRICE)) {
			if (lblPrice.getStyleClass().contains(STYLE_EMPHASIZED))
				lblPrice.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblPrice.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Load
		lblLoad.setText(String.valueOf(model.getLoad()));
		if (model.isModified(ItemAttribute.LOAD)) {
			if (!lblLoad.getStyleClass().contains(STYLE_EMPHASIZED))
				lblLoad.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblLoad.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Rigidity
		lblRigidity.setText(String.valueOf(model.getRigidity()));
		if (model.isModified(ItemAttribute.RIGIDITY)) {
			if (!lblRigidity.getStyleClass().contains(STYLE_EMPHASIZED))
				lblRigidity.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblRigidity.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Complexity
		if (model.getItem().getComplexity()==null)
			lblComplexity.setText("");
		else
			lblComplexity.setText(model.getItem().getComplexity().getName());
		if (model.isModified(ItemAttribute.COMPLEXITY)) {
			if (!lblComplexity.getStyleClass().contains(STYLE_EMPHASIZED))
				lblComplexity.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblComplexity.getStyleClass().remove(STYLE_EMPHASIZED);
	}

	//-------------------------------------------------------------------
	public void setData(CarriedItem model) {
		this.model = model;
		refresh();
	}

}
