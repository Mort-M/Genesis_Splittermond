package org.prelle.splittermond.chargen.jfx.equip;

import java.util.Iterator;

import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splimo.requirements.Requirement;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class ShieldDataPane extends GridPane {
	
	private final static String STYLE_EMPHASIZED = "emphasized";
	
	private CarriedItem model;
	
	private Label lblDefense;
	private Label lblHandicap;
	private Label lblTickMalus;
	private Label lblMinStr;
	private Label lblFeatures;

	//-------------------------------------------------------------------
	public ShieldDataPane() {
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblDefense   = new Label();
		lblHandicap  = new Label();
		lblTickMalus = new Label();
		lblMinStr    = new Label();
		lblFeatures  = new Label();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label heaDefense   = new Label(ItemAttribute.DEFENSE.getShortName());
		Label heaHandicap  = new Label(ItemAttribute.HANDICAP.getShortName());
		Label heaTickMalus = new Label(ItemAttribute.TICK_MALUS.getShortName());
		Label heaMinStr    = new Label(ItemAttribute.MIN_ATTRIBUTES.getShortName());
		Label heaFeatures  = new Label(ItemAttribute.FEATURES.getShortName());
		
		add(heaDefense   , 0,0);
		add(lblDefense   , 1,0);
		add(heaHandicap  , 0,1);
		add(lblHandicap  , 1,1);
		add(heaTickMalus , 0,2);
		add(lblTickMalus , 1,2);
		add(heaMinStr    , 0,3);
		add(lblMinStr    , 1,3);
		add(heaFeatures  , 0,4);
		add(lblFeatures  , 1,4);
		
		heaFeatures.setAlignment(Pos.TOP_LEFT);
		heaFeatures.setMaxHeight(Double.MAX_VALUE);
		
		setVgap(2);
		setHgap(5);
		
		/*
		 * Styles
		 */
		this.getStyleClass().addAll("content","text-body","bordered");
		heaDefense  .getStyleClass().add("base");
		heaHandicap .getStyleClass().add("base");
		heaTickMalus.getStyleClass().add("base");
		heaMinStr   .getStyleClass().add("base");
		heaFeatures .getStyleClass().add("base");
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (!model.isType(ItemType.SHIELD))
			return; 
		
		// Defense
		lblDefense.setText(String.valueOf(model.getDefense(ItemType.SHIELD)));
		if (model.isModified(ItemAttribute.DEFENSE)) {
			if (!lblDefense.getStyleClass().contains(STYLE_EMPHASIZED))
				lblDefense.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblDefense.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Handicap
		lblHandicap.setText(String.valueOf(model.getHandicap(ItemType.SHIELD)));
		if (model.isModified(ItemAttribute.HANDICAP)) {
			if (!lblHandicap.getStyleClass().contains(STYLE_EMPHASIZED))
				lblHandicap.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblHandicap.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Tick Malus
		lblTickMalus.setText(String.valueOf(model.getTickMalus(ItemType.SHIELD)));
		if (model.isModified(ItemAttribute.TICK_MALUS)) {
			if (!lblTickMalus.getStyleClass().contains(STYLE_EMPHASIZED))
				lblTickMalus.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblTickMalus.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Min. Attributes
		StringBuffer buf = new StringBuffer();
		for (Iterator<Requirement> it =model.getRequirements(ItemType.SHIELD).iterator(); it.hasNext() ; ) {
			Requirement req = it.next();
			if (req instanceof AttributeRequirement) {
				AttributeRequirement aReq = (AttributeRequirement)req;
				buf.append(aReq.getAttribute().getShortName()+" "+aReq.getValue());
			}
			if (it.hasNext())
				buf.append(", ");
		}
		lblMinStr.setText(buf.toString());
		if (model.isModified(ItemAttribute.MIN_ATTRIBUTES)) {
			if (!lblMinStr.getStyleClass().contains(STYLE_EMPHASIZED))
				lblMinStr.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblMinStr.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Features
		buf = new StringBuffer();
		for (Iterator<Feature> it =model.getFeatures(ItemType.SHIELD).iterator(); it.hasNext() ; ) {
			Feature feat = it.next();
			buf.append(feat.getName());
			if (it.hasNext())
				buf.append("\n");
		}
		lblFeatures.setText(buf.toString());
		if (model.isModified(ItemAttribute.FEATURES)) {
			if (!lblFeatures.getStyleClass().contains(STYLE_EMPHASIZED))
				lblFeatures.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblFeatures.getStyleClass().remove(STYLE_EMPHASIZED);
		
	}

	//-------------------------------------------------------------------
	public void setData(CarriedItem model) {
		this.model = model;
		refresh();
	}

}
