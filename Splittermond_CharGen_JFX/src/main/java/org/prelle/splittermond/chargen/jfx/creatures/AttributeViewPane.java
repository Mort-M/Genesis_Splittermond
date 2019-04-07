package org.prelle.splittermond.chargen.jfx.creatures;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.creature.Lifeform;

/**
 * @author prelle
 *
 */
public class AttributeViewPane extends GridPane {

	private Lifeform model;
	
	private Map<Attribute,Label> tfInput;

	//-------------------------------------------------------------------
	/**
	 */
	public AttributeViewPane() {
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		tfInput = new HashMap<>();
		for (Attribute attr : Attribute.values()) {
			Label tf = new Label();
			tf.setStyle("-fx-min-width: 2.5em");
			tf.setUserData(attr);
			tfInput.put(attr, tf);
		}
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		
		// Primary attributes
		int index = 0;
		for (Attribute key : Attribute.primaryValues()) {
			Label lbl = new Label(key.getShortName());
			lbl.setAlignment(Pos.CENTER);
			lbl.getStyleClass().add("table-head");
			lbl.setMaxWidth(Double.MAX_VALUE);
			GridPane.setMargin(lbl, new Insets(1,0,5,0));
			GridPane.setHgrow(lbl, Priority.SOMETIMES);
			GridPane.setHalignment(lbl, HPos.CENTER);
			
			Label tf = tfInput.get(key);
			tf.setAlignment(Pos.CENTER);
			GridPane.setMargin(tf, new Insets(1,2,5,2));
			GridPane.setHgrow(tf, Priority.SOMETIMES);
			GridPane.setHalignment(tf, HPos.CENTER);
//			logger.debug("Add "+tf+" to "+index+",1  "+key);
			this.add(lbl, index, 0);
			this.add(tf , index, 1);
			index++;
		}
		
		// Secondary attributes
		index = 0;
		Attribute[] order = new Attribute[]{Attribute.SIZE,Attribute.SPEED,Attribute.INITIATIVE,Attribute.LIFE,Attribute.FOCUS,Attribute.DEFENSE,Attribute.DAMAGE_REDUCTION,Attribute.BODYRESIST,Attribute.MINDRESIST};
		for (Attribute key : order) {
			if (key==Attribute.INITIATIVE)
				continue;
			
			Label lbl = new Label(key.getShortName());
			lbl.setAlignment(Pos.CENTER);
			lbl.getStyleClass().add("table-head");
			lbl.setMaxWidth(Double.MAX_VALUE);
			GridPane.setHgrow(lbl, Priority.SOMETIMES);
			GridPane.setHalignment(lbl, HPos.CENTER);
			
			Label tf = tfInput.get(key);
			tf.setAlignment(Pos.CENTER);
			GridPane.setHalignment(tf, HPos.CENTER);
//			logger.debug("Add "+tf+" to "+index+",3  "+key);
			this.add(lbl, index, 2);
			this.add(tf , index, 3);
			index++;
		}
		
	}

	//--------------------------------------------------------------------
	void refresh() {
		for (Attribute key: Attribute.values()) {
			Label tf = tfInput.get(key);
			if (tf==null) {
				continue;
			}
			if (key==Attribute.SPLINTER)
				continue;
			try {
				AttributeValue val = model.getAttribute(key);
				if (val==null) {
					continue;
				}
				tf.setText(String.valueOf(val.getValue()));
			} catch (NullPointerException e) {
			}
		}
	}

	//--------------------------------------------------------------------
	public void setData(Lifeform model) {
		this.model = model;
		refresh();
	}

}
