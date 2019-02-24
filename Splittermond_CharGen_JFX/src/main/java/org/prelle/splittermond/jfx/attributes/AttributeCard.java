/**
 *
 */
package org.prelle.splittermond.jfx.attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * @author prelle
 *
 */
public class AttributeCard extends GridPane implements GenerationEventListener {

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacter     model;

	private Attribute[] attributes;
	private Label headAttr, headValue;
	private Map<Attribute, Label> finalValue;

	//-------------------------------------------------------------------
	/**
	 */
	public AttributeCard(Attribute[] attribs) {
		attributes = attribs;
		finalValue = new HashMap<Attribute, Label>();

		getStyleClass().addAll("table","chardata-tile");

		initComponents();
		initLayout();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		headAttr   = new Label(uiResources.getString("label.name"));
		headValue  = new Label(uiResources.getString("label.value"));
		headAttr.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headValue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		headAttr.getStyleClass().add("table-head");
		headValue.getStyleClass().add("table-head");

		headValue.setAlignment(Pos.CENTER);

		for (final Attribute attr : attributes) {
			Label finVal= new Label();
			finalValue  .put(attr, finVal);
		}
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		this.getStyleClass().add("text-body");

		this.add(headAttr  , 0,0, 2,1);
		this.add(headValue , 2,0);

		int y=0;
		for (final Attribute attr : attributes) {
			y++;
			Label longName  = new Label(attr.getName());
			Label shortName = new Label(attr.getShortName());
			Label finVal    = finalValue.get(attr);

			String lineStyle = ((y%2)==0)?"even":"odd";
			longName.getStyleClass().addAll(lineStyle);
			shortName.getStyleClass().add(lineStyle);
			finVal.getStyleClass().add(lineStyle);

			longName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			shortName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			finVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

			this.add(longName , 0, y);
			this.add(shortName, 1, y);
			this.add(  finalValue.get(attr), 2, y);
		}

		// Column alignment
		ObservableList<ColumnConstraints> constraints = getColumnConstraints();
		ColumnConstraints maxGrow = new ColumnConstraints();
		maxGrow.setMaxWidth(Double.MAX_VALUE);
		maxGrow.setHgrow(Priority.ALWAYS);
		ColumnConstraints rightAlign = new ColumnConstraints();
		rightAlign.setHalignment(HPos.RIGHT);
		constraints.add(maxGrow);
		constraints.add(new ColumnConstraints());
		constraints.add(rightAlign);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		// TODO Auto-generated method stub
		if (event.getType()!=GenerationEventType.ATTRIBUTE_CHANGED)
			return;

		updateContent();
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		for (Attribute attr : attributes) {
			Label final_l = finalValue.get(attr);
			AttributeValue data = model.getAttribute(attr);
			final_l.setText(String.valueOf(data.getValue()));
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		updateContent();
	}

}
