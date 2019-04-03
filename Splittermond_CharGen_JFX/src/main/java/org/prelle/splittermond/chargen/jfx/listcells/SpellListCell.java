package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.TriStateCheckBox;
import org.prelle.javafx.TriStateCheckBox.State;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.charctrl.SpellController.FreeSelection;
import org.prelle.splittermond.chargen.jfx.sections.SpellListSection;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan Prelle
 *
 */
public class SpellListCell extends ListCell<Spell> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SpellListSection.class.getName());

	private CharacterController ctrl;
	private SpellController spellCtrl;
	private SpliMoCharacter model;
	private Label lblName;
	private Label lblLevel;
	private TriStateCheckBox slider;
	private Label lblValue;
	private HBox layout;
	private ReadOnlyObjectProperty<Skill> schoolProperty;
	
	//-------------------------------------------------------------------
	public SpellListCell(CharacterController ctrl, ReadOnlyObjectProperty<Skill> schoolProperty) {
		this.ctrl = ctrl;
		this.model = ctrl.getModel();
		this.schoolProperty = schoolProperty;
		this.spellCtrl = ctrl.getSpellController();
		
		lblName = new Label();
		lblName.setMaxWidth(Double.MAX_VALUE);
		lblLevel= new Label();
		slider = new TriStateCheckBox();
		slider.setStateFormatter(new StringConverter<TriStateCheckBox.State>(){
			public String toString(State state) {
				switch (state) {
				case SELECTION1: return RES.getString("spellslider.state1");
				case SELECTION2: return RES.getString("spellslider.state2");
				case SELECTION3: return RES.getString("spellslider.state3");
				}
				return "??";
			}

			@Override
			public State fromString(String string) { return null;}
		});
		lblValue = new Label();
		lblLevel.setStyle("-fx-padding: 0 5 0 3");
		lblName .setStyle("-fx-padding: 0 5 0 5");
		lblValue.setStyle("-fx-padding: 0 5 0 5");
		layout = new HBox(lblLevel, lblName, slider, lblValue);
		HBox.setHgrow(lblName, Priority.ALWAYS);
	}

	//-------------------------------------------------------------------
	private SpellValue createSpellValue(Spell spell, Skill school) {
		// Replace with value used in model
		for (SpellValue chk : model.getSpells()) {
			if (chk.getSkill()==school && chk.getSpell()==spell)
				return chk;
		}
		return new SpellValue(spell, school);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(Spell item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			this.setGraphic(null);
			this.setText(null);
		} else {
			setGraphic(layout);
			
			lblName.setText(item.getName());
			lblLevel.setText(String.valueOf(item.getLevelInSchool(schoolProperty.get())));
			
			SpellValue spellVal = createSpellValue(item, schoolProperty.get());
			boolean isFreeSelected = spellVal.wasFree();
			boolean isExpSelected  = !isFreeSelected && model.getSpells().contains(spellVal);
			int intVal = model.getSpellValueFor(spellVal);
			lblValue.setText(String.valueOf(intVal));
			
			// Check state SELECTION1	
			if (spellCtrl.canBeDeSelected(spellVal) || !(isFreeSelected || isExpSelected)) {
				if (!slider.getAllowedStates().contains(State.SELECTION1))
					slider.getAllowedStates().add(State.SELECTION1);
			} else {
				slider.getAllowedStates().remove(State.SELECTION1);
			}
			
			// Check state SELECTION2	
			if (spellCtrl.canBeFreeSelected(spellVal)!=null || isFreeSelected) {
				if (!slider.getAllowedStates().contains(State.SELECTION2))
					slider.getAllowedStates().add(State.SELECTION2);
			} else if (slider.getAllowedStates().contains(State.SELECTION2)) {
				slider.getAllowedStates().remove(State.SELECTION2);
			}
			
			// Check state SELECTION3	
			if (spellCtrl.canBeSelected(spellVal) || isExpSelected) {
				if (!slider.getAllowedStates().contains(State.SELECTION3))
					slider.getAllowedStates().add(State.SELECTION3);
			} else {
				slider.getAllowedStates().remove(State.SELECTION3);
			}
			
			slider.setDisable(slider.getAllowedStates().size()<=1);
			

			// Value
//			Label lblVal = labelsBySpell.get(spell);
//			lblVal.setText(String.valueOf(model.getSpellValueFor(spellVal)));
			
			// If spell has been bought with free selections
			for (FreeSelection free : spellCtrl.getFreeSelections()) {
				if (free.getSchool()!=schoolProperty.get())
					continue;
				if (free.getUsedFor()==null || !free.getUsedFor().equals(spellVal))
					continue;
				// Yes, it has
//				if (logger.isTraceEnabled())
//					logger.trace(spellVal+" was free selected");
				slider.select(State.SELECTION2);
				return;
			}
			
			// If spell has been bought with exp
			if (isExpSelected) {
				slider.select(State.SELECTION3);
				return;
			}
		}
	}
}
