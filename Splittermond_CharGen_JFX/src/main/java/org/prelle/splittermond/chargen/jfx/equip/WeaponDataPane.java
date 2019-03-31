package org.prelle.splittermond.chargen.jfx.equip;

import java.util.Iterator;

import org.prelle.splimo.SplitterTools;
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
public class WeaponDataPane extends GridPane {
	
	private final static String STYLE_EMPHASIZED = "emphasized";
	
	private CarriedItem model;
	
	private Label lblDamage;
	private Label lblSpeed;
	private Label lblAttribute;
	private Label lblRequired;
	private Label lblFeatures;

	//-------------------------------------------------------------------
	public WeaponDataPane() {
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblDamage    = new Label();
		lblSpeed     = new Label();
		lblAttribute = new Label();
		lblRequired  = new Label();
		lblFeatures  = new Label();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label heaDamage    = new Label(ItemAttribute.DAMAGE.getShortName());
		Label heaSpeed     = new Label(ItemAttribute.SPEED.getShortName());
		Label heaAttribute = new Label(ItemAttribute.ATTRIBUTES.getShortName());
		Label heaRequired  = new Label(ItemAttribute.MIN_ATTRIBUTES.getShortName());
		Label heaFeatures  = new Label(ItemAttribute.FEATURES.getShortName());
		
		add(heaDamage     , 0,0);
		add(lblDamage     , 1,0);
		add(heaSpeed      , 0,1);
		add(lblSpeed      , 1,1);
		add(heaAttribute  , 0,2);
		add(lblAttribute  , 1,2);
		add(heaRequired   , 0,3);
		add(lblRequired   , 1,3);
		add(heaFeatures   , 0,4);
		add(lblFeatures   , 1,4);
		
		heaFeatures.setAlignment(Pos.TOP_LEFT);
		heaFeatures.setMaxHeight(Double.MAX_VALUE);
		
		setVgap(2);
		setHgap(5);
		
		/*
		 * Styles
		 */
		this.getStyleClass().addAll("content","text-body","bordered");
		heaDamage.getStyleClass().add("base");
		heaSpeed.getStyleClass().add("base");
		heaAttribute.getStyleClass().add("base");
		heaRequired.getStyleClass().add("base");
		heaFeatures.getStyleClass().add("base");
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (!model.isType(ItemType.WEAPON))
			return; 
		
		// Damage
		lblDamage.setText(SplitterTools.getWeaponDamageString(model.getDamage(ItemType.WEAPON)));
		if (model.getDamage(ItemType.WEAPON)<0)
			lblDamage.setText("?");
		if (model.isModified(ItemAttribute.DAMAGE)) {
			if (!lblDamage.getStyleClass().contains(STYLE_EMPHASIZED))
				lblDamage.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblDamage.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Speed
		lblSpeed.setText(String.valueOf(model.getSpeed(ItemType.WEAPON)));
		if (model.getSpeed(ItemType.WEAPON)<0)
			lblSpeed.setText("?");
		if (model.isModified(ItemAttribute.SPEED)) {
			if (!lblSpeed.getStyleClass().contains(STYLE_EMPHASIZED))
				lblSpeed.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblSpeed.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Min Attributes
		if (model.getAttribute1(ItemType.WEAPON)!=null && model.getAttribute2(ItemType.WEAPON)!=null )
			lblAttribute.setText(model.getAttribute1(ItemType.WEAPON).getShortName()+"+"+model.getAttribute2(ItemType.WEAPON).getShortName());
		if (model.isModified(ItemAttribute.ATTRIBUTES)) {
			if (!lblAttribute.getStyleClass().contains(STYLE_EMPHASIZED))
				lblAttribute.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblAttribute.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Min. Attributes
		StringBuffer buf = new StringBuffer();
		for (Iterator<Requirement> it =model.getRequirements(ItemType.WEAPON).iterator(); it.hasNext() ; ) {
			Requirement req = it.next();
			if (req instanceof AttributeRequirement) {
				AttributeRequirement aReq = (AttributeRequirement)req;
				buf.append(aReq.getAttribute().getShortName()+" "+aReq.getValue());
			}
			if (it.hasNext())
				buf.append(", ");
		}
		lblRequired.setText(buf.toString());
		if (model.isModified(ItemAttribute.MIN_ATTRIBUTES)) {
			if (!lblRequired.getStyleClass().contains(STYLE_EMPHASIZED))
				lblRequired.getStyleClass().add(STYLE_EMPHASIZED);
		} else 
			lblRequired.getStyleClass().remove(STYLE_EMPHASIZED);
		
		// Features
		buf = new StringBuffer();
		for (Iterator<Feature> it =model.getFeatures(ItemType.WEAPON).iterator(); it.hasNext() ; ) {
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
