/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.creature.Creature;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class AttributeEditPane extends GridPane {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);

	private Creature model;
	private AttributeViewPane viewPane;
	private WeaponViewPane weaponView;
	private WeaponEditPane weaponEdit;
	private SkillViewPane skillView;
	private SkillEditPane skillEdit;
	
	private Map<Attribute,TextField> tfInput;

	//-------------------------------------------------------------------
	/**
	 */
	public AttributeEditPane(AttributeViewPane view, WeaponViewPane weaponView, WeaponEditPane weaponEdit, SkillViewPane skillView, SkillEditPane skillEdit) {
		this.viewPane = view;
		this.weaponEdit = weaponEdit;
		this.weaponView = weaponView;
		this.skillEdit = skillEdit;
		this.skillView = skillView;
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		tfInput = new HashMap<>();
		for (Attribute attr : Attribute.values()) {
			TextField tf = new TextField();
			tf.setUserData(attr);
			tf.setStyle("-fx-min-width: 2.5em");
//			tf.setStyle("-fx-max-width: 3.5em");
			tfInput.put(attr, tf);
		}
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		
		// Primary attributes
		int index = 0;
		for (Attribute key : Attribute.primaryValues()) {
			Label lbl = new Label(key.getShortName());
			lbl.getStyleClass().add("table-head");
			lbl.setMaxWidth(Double.MAX_VALUE);
			
			TextField tf = tfInput.get(key);
			GridPane.setMargin(tf, new Insets(1,2,5,2));
			
			this.add(lbl, index, 0);
			this.add(tf , index, 1);
			index++;
		}
		
		// Secondary attributes
		index = 0;
		for (Attribute key : Attribute.secondaryValues()) {
			if (key==Attribute.INITIATIVE)
				continue;
			
			Label lbl = new Label(key.getShortName());
			lbl.getStyleClass().add("table-head");
			lbl.setMaxWidth(Double.MAX_VALUE);
			
			TextField tf = tfInput.get(key);
			GridPane.setMargin(tf, new Insets(1,2,5,2));
			this.add(lbl, index, 2);
			this.add(tf , index, 3);
			index++;
			
//			if (key==Attribute.DEFENSE) {
//				key = Attribute.DAMAGE_REDUCTION;
//				lbl = new Label(key.getShortName());
//				lbl.getStyleClass().add("table-head");
//				lbl.setMaxWidth(Double.MAX_VALUE);
//				
//				tf = tfInput.get(key);
//				GridPane.setMargin(tf, new Insets(1,2,5,2));
//				this.add(lbl, index, 2);
//				this.add(tf , index, 3);
//				index++;
//			}
		}
		
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		for (TextField input : tfInput.values()) {
			Attribute attr = (Attribute)input.getUserData();
			input.textProperty().addListener( (ov,o,n) -> {
				try {
					int val = Integer.parseInt(n);
					logger.debug("Set "+attr+" to "+val);
					model.getAttribute(attr).setDistributed(val);
					viewPane.refresh();
					skillEdit.refresh();
					skillView.refresh();
					weaponView.refresh();
					weaponEdit.refresh();
				} catch (Exception nfe) {
					
				}
			});
		}
	}

	//--------------------------------------------------------------------
	private void refresh() {
		for (Attribute key: Attribute.values()) {
			TextField tf = tfInput.get(key);
			if (tf==null) {
				logger.info("No field for "+key);
				continue;
			}
			AttributeValue val = model.getAttribute(key);
			if (val==null) {
				logger.error("Missing attribute value for "+key);
				continue;
			}
			tf.setText(String.valueOf(val.getValue()));
		}
	}

	//--------------------------------------------------------------------
	public void setData(Creature model) {
		this.model = model;
//		logger.info("setData "+model.dump());
		refresh();
		initInteractivity();
	}

}
