/**
 * 
 */
package org.prelle.splittermond.jfx.skills;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

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
public class SkillCard extends GridPane implements GenerationEventListener {
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	private SpliMoCharacter     model;

	private List<Skill> skills;
	private Label headAttr, headValue;
	private Map<Skill, Label> finalValue;

	//-------------------------------------------------------------------
	/**
	 */
	public SkillCard(SkillType type) {
		skills = SplitterMondCore.getSkills(type);
		finalValue = new HashMap<Skill, Label>();
		
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
		
		for (Skill skill : skills) {
			Label finVal= new Label();
			finalValue  .put(skill, finVal);
		}
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		this.add(headAttr  , 0,0);
		this.add(headValue , 1,0);
		
		int y=0;
		for (Skill attr : skills) {
			y++;
			Label longName  = new Label(attr.getName());
			Label finVal    = finalValue.get(attr);
			
			String lineStyle = ((y%2)==0)?"even":"odd";
			longName.getStyleClass().addAll(lineStyle);
			finVal.getStyleClass().add(lineStyle);

			longName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			finVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.add(longName , 0, y);
			this.add(  finalValue.get(attr), 1, y);
		}
		
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
		case SKILL_CHANGED:
			updateContent();
			break;
		default:
		}
		
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		for (Skill attr : skills) {
			Label final_l = finalValue.get(attr);
			SkillValue data = model.getSkillValue(attr);
			final_l.setText(String.valueOf(data.getValue()));
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		updateContent();
	}

}
