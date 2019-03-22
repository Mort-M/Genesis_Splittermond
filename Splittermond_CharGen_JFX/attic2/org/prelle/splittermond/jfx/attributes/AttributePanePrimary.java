/**
 * 
 */
package org.prelle.splittermond.jfx.attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.WindowMode;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.chargen.jfx.AttributeField;
import org.prelle.splittermond.chargen.jfx.ViewMode;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * @author prelle
 *
 */
public class AttributePanePrimary extends GridPane implements GenerationEventListener, ResponsiveControl {

	private final static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private AttributeController control;
	private SpliMoCharacter     model;
	private ViewMode         	mode;

	private Label headAttr, headPoints, headMod, headStart, headValue, headCost;
	private Map<Attribute, Label> modification;
	private Map<Attribute, AttributeField> distributed;
	private Map<Attribute, Label> finalValue;
	private Map<Attribute, Label> startValue;
	private Map<Attribute, Label> costValue;

	//--------------------------------------------------------------------
	public AttributePanePrimary(AttributeController cntrl, ViewMode mode) {
		this.control = cntrl;
		this.mode    = mode;
		assert control!=null;
		assert mode   !=null;

		modification = new HashMap<Attribute, Label>();
		distributed  = new HashMap<Attribute, AttributeField>();
		finalValue   = new HashMap<Attribute, Label>();
		startValue   = new HashMap<Attribute, Label>();
		costValue    = new HashMap<Attribute, Label>();
		
		doInit();
		doLayout();
		doInteractivity();
	}

	//-------------------------------------------------------------------
	private void doInit() {
		super.setVgap(2);
		super.setHgap(0);
		super.setMaxWidth(Double.MAX_VALUE);
		super.getStyleClass().add("chardata-tile");
		
		headAttr   = new Label(UI.getString("label.attributes"));
		headPoints = new Label(UI.getString("label.points"));
		headMod    = new Label(UI.getString("label.modified.short"));
		headStart  = new Label(UI.getString("label.start"));
		headValue  = new Label(UI.getString("label.value"));
		headCost   = new Label(UI.getString("label.cost"));
		headAttr.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headPoints.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headMod.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headStart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headValue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headCost.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		headAttr.getStyleClass().add("table-head");
		headPoints.getStyleClass().add("table-head");
		headMod.getStyleClass().add("table-head");
		headStart.getStyleClass().add("table-head");
		headValue.getStyleClass().add("table-head");
		headCost.getStyleClass().add("table-head");
		
		headPoints.setAlignment(Pos.CENTER);
		headValue.setAlignment(Pos.CENTER);
		GridPane.setVgrow(headAttr, Priority.NEVER);
		
		for (final Attribute attr : Attribute.primaryValues()) {
			AttributeField value = new AttributeField();
			Label modVal= new Label();
			Label finVal= new Label();
			Label startVal= new Label();
			
			finalValue  .put(attr, finVal);
			distributed .put(attr, value);
			modification.put(attr, modVal);
			startValue  .put(attr, startVal);
			costValue   .put(attr, new Label());
		}
	}

	//-------------------------------------------------------------------
	private void doLayout() {
		switch (mode) {
		case GENERATION:
			this.add(headAttr  , 0,0, 2,1);
			this.add(headPoints, 2,0);
			this.add(headMod   , 3,0);
			this.add(headValue , 4,0);
			break;
		case MODIFICATION:
			this.add(headAttr  , 0,0, 2,1);
			this.add(headStart , 2,0);
			this.add(headValue , 3,0);
			this.add(headCost  , 4,0);
			break;
		}
		
		int y=0;
		for (final Attribute attr : Attribute.primaryValues()) {
			y++;
			Label longName  = new Label(attr.getName());
			Label shortName = new Label(attr.getShortName());
			shortName.setId("short");
			Label modVal    = modification.get(attr);
			AttributeField points = distributed.get(attr);
			Label finVal    = finalValue.get(attr);
			Label startVal  = startValue.get(attr);
			Label temp      = costValue.get(attr);
			
			String lineStyle = ((y%2)==1)?"even":"odd";
			GridPane.setVgrow(longName, Priority.ALWAYS);
			shortName.getStyleClass().add(lineStyle);
			modVal   .getStyleClass().add(lineStyle);
			points   .getStyleClass().add(lineStyle);
			finVal   .getStyleClass().add(lineStyle);
			startVal .getStyleClass().add(lineStyle);
			
			temp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			temp.getStyleClass().add(lineStyle);
			longName .getStyleClass().add(lineStyle);
			longName.setUserData(attr);

			longName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			shortName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			modVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			points.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			finVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			startVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.add(longName , 0, y);
			this.add(shortName, 1, y);
			switch (mode) {
			case GENERATION:
				this.add( distributed.get(attr), 2, y);
				this.add(modification.get(attr), 3, y);
				this.add(  finalValue.get(attr), 4, y);
				break;
			case MODIFICATION:
				this.add( startValue.get(attr), 2, y);
				this.add(distributed.get(attr), 3, y);
				this.add(  costValue.get(attr), 4, y);
				break;
			}
		}
		
		
		// Column alignment
		ColumnConstraints nameCol = new ColumnConstraints();
		nameCol.setHgrow(Priority.ALWAYS);
		ColumnConstraints rightAlign = new ColumnConstraints();
		rightAlign.setHalignment(HPos.RIGHT);
		ColumnConstraints centerAlign = new ColumnConstraints();
		centerAlign.setHalignment(HPos.CENTER);

		ObservableList<ColumnConstraints> constraints = getColumnConstraints();
		constraints.add(nameCol);
		constraints.add(new ColumnConstraints());
		switch (mode) {
		case GENERATION:
			constraints.add(centerAlign);  // Spinner
			constraints.add(rightAlign);  // Modifier
			constraints.add(rightAlign);  // Final value
			break;
		case MODIFICATION:
			constraints.add(rightAlign);  // Start value
			constraints.add(new ColumnConstraints());  // Spinner
			constraints.add(rightAlign); // Cost
			break;
		}
	}

	//-------------------------------------------------------------------
	private void doInteractivity() {
		for (final Attribute attr : Attribute.primaryValues()) {
			AttributeField field = distributed.get(attr);
			field.inc.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					control.increase(attr);
				}
			});
			field.dec.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					control.decrease(attr);
				}
			});
		}
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		for (Attribute attr : Attribute.primaryValues()) {
			Label start_l = startValue.get(attr);
			Label modif_l = modification.get(attr);
			AttributeField field = distributed.get(attr);
			Label cost_l  = costValue.get(attr);
			Label final_l = finalValue.get(attr);
			
			AttributeValue data = model.getAttribute(attr);
			
			String modString = (data.getModifier()==0)?"":String.valueOf(data.getModifier());
					
			switch (mode) {
			case GENERATION:
				field.setText(Integer.toString(data.getDistributed()));
				final_l.setText(String.valueOf(data.getValue()));
				break;
			case MODIFICATION:
				field.setText(Integer.toString(data.getValue()));
				break;
			}
			start_l.setText(Integer.toString(data.getStart()));
			modif_l.setText(modString);
			cost_l .setText(Integer.toString(control.getIncreaseCost(attr)));
			field.inc.setDisable(!control.canBeIncreased(attr));
			field.dec.setDisable(!control.canBeDecreased(attr));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.ATTRIBUTE_CHANGED) {
			Attribute      attribute = (Attribute) event.getKey();
			if (attribute==Attribute.SPLINTER)
				return;
			if (!attribute.isPrimary())
				return;
			logger.debug("handle "+event);
			AttributeValue set       = (AttributeValue) event.getValue();
			Label start  = startValue.get(attribute);
			Label modVal = this.modification.get(attribute);
			Label finV_l = finalValue.get(attribute);
			
			if (attribute.isPrimary()) {
				// Update increase and decrease buttons
				AttributeField field     = distributed.get(attribute);
				field.inc.setDisable(!control.canBeIncreased(attribute));
				field.dec.setDisable(!control.canBeDecreased(attribute));
				if (field !=null) field .setText(Integer.toString(set.getValue()));
			}
			// Update values
			if (start !=null) start .setText(Integer.toString(set.getStart()));
			if (modVal!=null) { 
				modVal.setText(Integer.toString(set.getModifier()));
			} else {
				logger.warn("No label for modifier "+attribute+" in "+modification.keySet());
			}
			if (finV_l!=null) finV_l.setText(Integer.toString(set.getValue()));
			updateContent();
		} else if (event.getType()==GenerationEventType.POINTS_LEFT_ATTRIBUTES) {
			logger.debug("handle "+event);
			updateContent();
		} else if (event.getType()==GenerationEventType.EXPERIENCE_CHANGED) {
			logger.debug("handle "+event);
			updateContent();
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		updateContent();
		doInteractivity();
		
		GenerationEventDispatcher.removeListener(this);	
		GenerationEventDispatcher.addListener(this);	
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		headCost.setVisible(value==WindowMode.EXPANDED);
		headCost.setManaged(value==WindowMode.EXPANDED);
		for (Node node : getChildren()) {
			if (costValue.values().contains(node)) {
				node.setVisible(value==WindowMode.EXPANDED);
				node.setManaged(value==WindowMode.EXPANDED);
			}
			if (node.getId()!=null && node.getId().equals("short")) {
				node.setVisible(value==WindowMode.EXPANDED);
				node.setManaged(value==WindowMode.EXPANDED);
			}
		}
	}

}
