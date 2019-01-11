/**
 * 
 */
package org.prelle.splittermond.jfx.skills;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class AddGroupedSkillPane extends VBox {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;
	
	private ChoiceBox<Skill> cbSkills;
	private ChoiceBox<SkillSpecialization> cbFocus;

	//-------------------------------------------------------------------
	public AddGroupedSkillPane() {
		initComponents();
		initLayout();
		initInteractivity();
		
		cbSkills.getSelectionModel().select(0);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		ObservableList<Skill> options = FXCollections.observableArrayList();
		for (Skill skill : SplitterMondCore.getSkills()) {
			if (skill.isGrouped())
				options.add(skill);
		}
		cbSkills = new ChoiceBox<>(options);
		cbSkills.setConverter(new StringConverter<Skill>() {
			public String toString(Skill value) {return value.getName();}
			public Skill fromString(String string) {return null;}
		});
		cbFocus = new ChoiceBox<>();
		cbFocus.setConverter(new StringConverter<SkillSpecialization>() {
			public String toString(SkillSpecialization value) {return value.getName();}
			public SkillSpecialization fromString(String string) {return null;}
		});
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setSpacing(5);
		Label lbSkills = new Label(UI.getString("label.skill"));
		Label lbFocus = new Label(UI.getString("label.specialization"));
		
		VBox.setMargin(lbFocus, new Insets(20, 0, 0, 0));
		getChildren().addAll(lbSkills, cbSkills, lbFocus, cbFocus);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbSkills.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			cbFocus.getItems().clear();
			if (n!=null) {
				cbFocus.getItems().addAll(n.getSpecializations());
				if (n.getSpecializations().size()>0)
					cbFocus.getSelectionModel().select(0);

			}
		});
	}

	//-------------------------------------------------------------------
	public Skill getSkill() { return cbSkills.getSelectionModel().getSelectedItem(); }
	public SkillSpecialization getFocus() { return cbFocus.getSelectionModel().getSelectedItem(); }
	
}
