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
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXUtil;
import org.prelle.splittermond.chargen.jfx.ViewMode;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * @author prelle
 *
 */
public class AttributePaneSecondary extends GridPane implements GenerationEventListener, ResponsiveControl {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private AttributeController control;
	private SpliMoCharacter     model;
	private ViewMode         mode;

	private Label headDeri, headDPoints, headDMod, headDEqMod, headDValue;
	private Map<Attribute, Label> modification;
	private Map<Attribute, Label> eqModification;
	private Map<Attribute, Label> derived;
	private Map<Attribute, Label> finalValue;
	private Map<Attribute, Label> startValue;

	//--------------------------------------------------------------------
	public AttributePaneSecondary(AttributeController cntrl, ViewMode mode) {
		this.control = cntrl;
		this.mode    = mode;
		assert control!=null;
		assert mode   !=null;

		modification = new HashMap<Attribute, Label>();
		eqModification = new HashMap<Attribute, Label>();
		finalValue   = new HashMap<Attribute, Label>();
		startValue   = new HashMap<Attribute, Label>();
		derived      = new HashMap<Attribute, Label>();
		
		doInit();
		doLayout();
	}

	//-------------------------------------------------------------------
	private void doInit() {
		super.setVgap(0);
		super.setHgap(3);
		super.setMaxWidth(Double.MAX_VALUE);
		super.getStyleClass().add("chardata-tile");
		
		headDeri   = new Label(UI.getString("label.derived_values"));
		headDPoints= new Label(UI.getString("label.points"));
		headDMod   = new Label(UI.getString("label.modified.short"));
		headDEqMod = new Label(UI.getString("label.eqmodified.short"));
		headDValue = new Label(UI.getString("label.value"));
		headDeri.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headDPoints.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headDMod.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headDEqMod.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headDValue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		headDeri.getStyleClass().add("table-head");
		headDPoints.getStyleClass().add("table-head");
		headDMod.getStyleClass().add("table-head");
		headDEqMod.getStyleClass().add("table-head");
		headDValue.getStyleClass().add("table-head");
		
		for (final Attribute attr : Attribute.secondaryValues()) {
			Label value = new Label();
			Label modVal= new Label();
			Label eqModVal= new Label();
			Label finVal= new Label();
			Label startVal= new Label();
			
			finalValue  .put(attr, finVal);
			derived     .put(attr, value);
			modification.put(attr, modVal);
			eqModification.put(attr, eqModVal);
			startValue  .put(attr, startVal);
		}
		
	}

	//-------------------------------------------------------------------
	private void doLayout() {
		super.setVgap(2);
		super.setHgap(0);
		super.setMaxWidth(Double.MAX_VALUE);
		
		switch (mode) {
		case GENERATION:
			this.add(headDeri  , 0,0, 2,1);
			this.add(headDPoints, 2,0);
			this.add(headDMod   , 3,0);
			this.add(headDValue , 4,0);
			break;
		case MODIFICATION:
			this.add(headDeri   , 0,0, 2,1);
			this.add(headDPoints, 2,0);
			this.add(headDMod   , 3,0);
			this.add(headDEqMod , 4,0);
			this.add(headDValue , 5,0);
			break;
		}
		
		/*
		 * Derived attributes
		 */
		int y=0;
		for (final Attribute attr : Attribute.secondaryValues()) {
			if (attr==Attribute.DAMAGE_REDUCTION)
				continue;
			y++;
			Label longName  = new Label(attr.getName());
			Label shortName = new Label(attr.getShortName());
			shortName.setId("short");
			Label modVal    = modification.get(attr);
			Label eqModVal  = eqModification.get(attr);
			Label points    = derived.get(attr);
			Label finVal    = finalValue.get(attr);
			
			String lineStyle = ((y%2)==1)?"even":"odd";
			GridPane.setVgrow(longName, Priority.ALWAYS);
			shortName.getStyleClass().add(lineStyle);
			modVal.getStyleClass().add(lineStyle);
			eqModVal.getStyleClass().add(lineStyle);
			points.getStyleClass().add(lineStyle);
			finVal.getStyleClass().addAll(lineStyle);

			longName .getStyleClass().add(lineStyle);
			longName.setUserData(attr);

			longName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			GridPane.setVgrow(longName, Priority.ALWAYS);
			shortName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			points.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			modVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			eqModVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			finVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.add(longName , 0, y);
			this.add(shortName, 1, y);
			this.add(points   , 2, y);
			this.add(modVal   , 3, y);
			switch (mode) {
			case GENERATION:
				this.add(finVal   , 4, y);
				break;
			case MODIFICATION:
				this.add(eqModVal , 4, y);
				this.add(finVal   , 5, y);
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
			constraints.add(rightAlign);  // Final value
			break;
		case MODIFICATION:
			constraints.add(centerAlign);  // Equipment modifier
			constraints.add(rightAlign);  // Final value
			break;
		}
	}

	//-------------------------------------------------------------------
	private void doInteractivity() {
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		for (final Attribute attr : Attribute.secondaryValues()) {
			Label modif_l = modification.get(attr);
			Label deriv_l = derived.get(attr);
			Label final_l = finalValue.get(attr);

			AttributeValue data = model.getAttribute(attr);
			deriv_l.setText(Integer.toString(data.getDistributed()));
			if (data.getModifier()!=0) {
				modif_l.setText(Integer.toString(data.getModifier()));
				modif_l.setTooltip(new Tooltip(SpliMoCharGenJFXUtil.getModificationTooltip(data)));
			} else {
				modif_l.setText(null);
				modif_l.setTooltip(null);
			}

			final_l.setText(Integer.toString(data.getValue()));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case ATTRIBUTE_CHANGED:
			logger.debug("RCV "+event);
			Attribute      attribute = (Attribute) event.getKey();
			if (attribute==Attribute.SPLINTER)
				return;
			if (attribute.isPrimary()) 
				return;
			logger.debug("handle "+event);
			AttributeValue set       = (AttributeValue) event.getValue();
			Label start  = startValue.get(attribute);
			Label modVal = this.modification.get(attribute);
			Label finV_l = finalValue.get(attribute);
			
				Label derive = derived.get(attribute);
				derive.setText(Integer.toString(set.getDistributed()));
			// Update values
			if (start !=null) start .setText(Integer.toString(set.getStart()));
			if (modVal!=null) { 
				modVal.setText(Integer.toString(set.getModifier()));
			} else {
				logger.warn("No label for modifier "+attribute+" in "+modification.keySet());
			}
			if (finV_l!=null) finV_l.setText(Integer.toString(set.getValue()));
			updateContent();
			break;
		case POINTS_LEFT_ATTRIBUTES: 
		case EXPERIENCE_CHANGED: 
		case ITEM_CHANGED:
			logger.debug("handle "+event);
			updateContent();
			break;
		default:
			break;
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
		headDMod.setVisible(value==WindowMode.EXPANDED);
		headDMod.setManaged(value==WindowMode.EXPANDED);
		for (Node node : getChildren()) {
			if (modification.values().contains(node)) {
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
