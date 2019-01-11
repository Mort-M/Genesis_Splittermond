/**
 * 
 */
package org.prelle.splittermond.jfx.equip.input;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.items.Armor;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.requirements.AttributeRequirement;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class ArmorTemplateDataPane extends ShieldTemplateDataPane {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private Armor model;
	
	protected TextField tfDamageR;
	private ChoiceBox<AttributeRequirement> cbMinStr;

	//-------------------------------------------------------------------
	public ArmorTemplateDataPane(	) {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		super.initComponents();
		tfDamageR   = new TextField();
		cbMinStr    = new ChoiceBox<AttributeRequirement>();
		for (int i=0; i<=4; i++)
			cbMinStr.getItems().add(new AttributeRequirement(Attribute.STRENGTH, i));
		cbMinStr.setConverter(new StringConverter<AttributeRequirement>() {
			public String toString(AttributeRequirement object) { return ""+object.getValue(); }
			public AttributeRequirement fromString(String string) { return null; }
		});
	}

	//-------------------------------------------------------------------
	protected void initLayout() {
		super.initLayout();
		Label heaDamageR   = new Label(ItemAttribute.DAMAGE_REDUCTION.getShortName());
		
		add(heaDamageR   , 0,1);
		add(tfDamageR    , 1,1);
		
		/*
		 * Styles
		 */
		heaDamageR  .getStyleClass().add("text-small-subheader");

		tfDamageR.setStyle("-fx-max-width: 2.5em");
	}

	//-------------------------------------------------------------------
	protected void initInteractivity() {
		super.initInteractivity();
		
		cbMinStr.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			model.getRequirements().clear();
			if (n!=null) {
				model.addRequirement(n);			
				logger.debug("Added requirement "+n+"    --> "+model);
			} else {
				logger.debug("Clear requirements");
			}
			});
		tfDamageR.textProperty().addListener( (ov,o,n)-> model.setDamageReduction(Integer.parseInt(tfDamageR.getText())));
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		logger.debug("refresh "+model);
		super.refresh();
		tfDamageR.setText(String.valueOf(model.getDamageReduction()));
	}

	//-------------------------------------------------------------------
	public void setData(Armor weapon) {
		if (weapon==null)
			throw new NullPointerException();
		logger.debug("overwrite with = "+weapon);
		model = weapon;
		super.setData(weapon);
		refresh();
	}

	//-------------------------------------------------------------------
	Armor getModel() {
		return model;
	}

}
