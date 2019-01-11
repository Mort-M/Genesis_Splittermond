/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.npc.NPCAttributeGenerator;
import org.prelle.splimo.npc.NPCGenerator;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class CreatureAttributeEditPane extends GridPane implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private Creature model;
	private NPCAttributeGenerator control;
	
	
	private Map<Attribute,TextField> tfInput;

	//-------------------------------------------------------------------
	/**
	 */
	public CreatureAttributeEditPane(NPCGenerator ctrl) {
		this.control = ctrl.getAttributeController();
		initComponents();
		initLayout();
		
		GenerationEventDispatcher.addListener(this);
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		tfInput = new HashMap<>();
		for (Attribute attr : Attribute.values()) {
			TextField tf = new TextField();
			tf.setUserData(attr);
			tf.setStyle("-fx-min-width: 2.5em");
			tf.setStyle("-fx-max-width: 3.5em");
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
			logger.info("tfInput("+key+")="+tf);
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
					control.calculateDerived();
				} catch (Exception nfe) {
					
				}
			});
		}
	}

	//--------------------------------------------------------------------
	private void refresh() {
		logger.debug("START refresh");
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
		logger.debug("STOP  refresh");
	}

	//--------------------------------------------------------------------
	public void setData(Creature model) {
		this.model = model;
//		logger.info("setData "+model.dump());
		refresh();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.ATTRIBUTE_CHANGED) {
			logger.debug("RCV "+event);
//			if (Arrays.asList(Attribute.primaryValues()).contains(event.getValue()))
				refresh();
		}
	}

}
