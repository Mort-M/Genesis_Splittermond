/**
 *
 */
package org.prelle.splittermond.jfx.spells;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.collections.ObservableList;
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
public class SpellCard extends GridPane implements GenerationEventListener {

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacter     model;

	private Label headAttr, headValue;

	//-------------------------------------------------------------------
	/**
	 */
	public SpellCard() {
		getStyleClass().addAll("table","chardata-tile");

		initComponents();
		initLayout();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		headAttr   = new Label(uiResources.getString("label.name"));
		headValue  = new Label(uiResources.getString("label.school.short"));
		headAttr.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		headValue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		headAttr.getStyleClass().add("table-head");
		headValue.getStyleClass().add("table-head");

		headValue.setAlignment(Pos.CENTER);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		this.add(headAttr  , 0,0);
		this.add(headValue , 1,0);

//		int y=0;
//		for (Skill attr : skills) {
//			y++;
//			Label longName  = new Label(attr.getName());
//			Label finVal    = finalValue.get(attr);
//
//			String lineStyle = ((y%2)==0)?"even":"odd";
//			longName.getStyleClass().addAll(lineStyle);
//			finVal.getStyleClass().add(lineStyle);
//
//			longName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//			finVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//
//			this.add(longName , 0, y);
//			this.add(  finalValue.get(attr), 1, y);
//		}

		// Column alignment
		ObservableList<ColumnConstraints> constraints = getColumnConstraints();
		ColumnConstraints maxGrow = new ColumnConstraints();
		maxGrow.setMaxWidth(Double.MAX_VALUE);
		maxGrow.setHgrow(Priority.ALWAYS);
		ColumnConstraints rightAlign = new ColumnConstraints();
		rightAlign.setHalignment(HPos.RIGHT);
		constraints.add(maxGrow);
		constraints.add(rightAlign);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case SPELL_ADDED:
		case SPELL_REMOVED:
		case SPELL_CHANGED:
			updateContent();
			break;
		default:
		}

	}

	//-------------------------------------------------------------------
	private void clear() {
		List<Node> toRemove = new ArrayList<Node>();
		for (Node child : this.getChildren()) {
			if (child!=headAttr && child!=headValue)
				toRemove.add(child);
		}

		getChildren().removeAll(toRemove);
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		clear();

		List<SpellValue> spells = model.getSpells();
		int y = 0;
		for (SpellValue data : spells) {
			Label name = new Label(data.getSpell().getName());
			Label sch  = new Label(data.getSkill().getName());
			y++;
			add(name, 0, y);
			add(sch , 1, y);
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		updateContent();
	}

}
