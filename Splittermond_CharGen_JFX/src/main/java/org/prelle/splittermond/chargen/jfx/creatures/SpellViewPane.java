package org.prelle.splittermond.chargen.jfx.creatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.creature.Lifeform;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

/**
 * @author prelle
 *
 */
public class SpellViewPane extends FlowPane {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(CompanionSection.class.getName());

	private Lifeform model;
	
	private Label lblHeading;

	//-------------------------------------------------------------------
	/**
	 */
	public SpellViewPane() {
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		lblHeading = new Label(UI.getString("label.spells")+":");
		lblHeading.getStyleClass().add("base");
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setHgap(3);
		setVgap(3);
		getChildren().add(lblHeading);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
	}

	//--------------------------------------------------------------------
	void refresh() {
		getChildren().retainAll(lblHeading);
		
		Map<Skill, List<SpellValue>> data = new HashMap<>();
		/*
		 * Collect data
		 */
		for (SkillValue sVal : model.getSkills(SkillType.MAGIC)) {
			// Ignore all skills with no points
			if (sVal.getValue()<1 && sVal.getModifier()<1)
				continue;
			Skill skill = sVal.getSkill();
			List<SpellValue> spells = new ArrayList<SpellValue>();
			data.put(skill, spells);
			for (SpellValue spVal : model.getSpells()) {
				if (spVal.getSkill()!=skill)
					continue;
				spells.add(spVal);
			}
		}
		
		/*
		 * Format data
		 */
		List<Skill> skills = new ArrayList<>(data.keySet());
		Collections.sort(skills);
		for (Iterator<Skill> it=skills.iterator(); it.hasNext(); ) {
			Skill skill = it.next();
			Text textNode = new Text(skill.getName()+" ");
			getChildren().add(textNode);
			
			List<SpellValue> spells = data.get(skill);
			int lastLevel = -1;
			for (Iterator<SpellValue> it2=spells.iterator(); it2.hasNext(); ) {
				SpellValue spell = it2.next();
				if (spell.getSpellLevel()>lastLevel) {
					textNode = new Text(spell.getSpellLevel()+": ");
					getChildren().add(textNode);					
				}
				
				String text = spell.getSpell().getName();
				if (it2.hasNext())
					text +=",";
				textNode = new Text(text);
				getChildren().add(textNode);
			}
			
			if (it.hasNext()) {
				textNode = new Text(", ");
				getChildren().add(textNode);					
			}
		}
	}

	//--------------------------------------------------------------------
	public void setData(Lifeform model) {
		this.model = model;
//		logger.info("setData "+model.dump());
		refresh();
		initInteractivity();
	}

}
