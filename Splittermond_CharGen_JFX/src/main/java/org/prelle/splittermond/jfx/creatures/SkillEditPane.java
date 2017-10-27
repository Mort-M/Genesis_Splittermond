/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.Creature;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class SkillEditPane extends VBox {
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private Creature model;
	private SkillViewPane viewPane;
	
	private ChoiceBox<Skill> cbSkills;
	private Button btnAdd;
	private GridPane grid;
	private List<Label> headings;
	private Map<SkillValue, Label> pointsMap;
	
	//--------------------------------------------------------------------
	public SkillEditPane(SkillViewPane view) {
		this.viewPane = view;
		initComponents();
		initLayout();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		pointsMap = new HashMap<>();
		cbSkills = new ChoiceBox<>();
		cbSkills.setConverter(new StringConverter<Skill>() {
			public String toString(Skill object) {return object.getName();}
			public Skill fromString(String string) { return null;}
		});
		btnAdd = new Button("+");
		
		grid   = new GridPane();
		headings   = new ArrayList<>();
		
		
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		HBox addLine = new HBox(10);
		addLine.getChildren().addAll(cbSkills, btnAdd);

		Label headSkill = new Label(UI.getString("label.skill"));
		Label headAttr  = new Label(UI.getString("label.attributes"));
		Label headPoints= new Label(UI.getString("label.points"));
		Label headValue = new Label(UI.getString("label.value"));
		Label headAction= new Label(" ");
		
		headSkill .getStyleClass().add("table-head");
		headAttr  .getStyleClass().add("table-head");
		headPoints.getStyleClass().add("table-head");
		headValue .getStyleClass().add("table-head");
		headAction.getStyleClass().add("table-head");
		
		headSkill .setMaxWidth(Double.MAX_VALUE);
		headAttr  .setMaxWidth(Double.MAX_VALUE);
		headPoints.setMaxWidth(Double.MAX_VALUE);
		headValue .setMaxWidth(Double.MAX_VALUE);
		headAction.setMaxWidth(Double.MAX_VALUE);
		
		grid.add(headSkill , 0, 0);
		grid.add(headAttr  , 1, 0);
		grid.add(headPoints, 2, 0);
		grid.add(headValue , 3, 0);
		grid.add(headAction, 4, 0);
		headings.addAll(Arrays.asList(headSkill, headAttr, headPoints, headValue, headAction));
		grid.setVgap(2);
		
		ScrollPane scroll = new ScrollPane(grid);
		VBox content = new VBox();
		content.getChildren().addAll(scroll, addLine);
		content.getStyleClass().add("content");
		
		Label heading = new Label(UI.getString("label.skills"));
		heading.getStyleClass().add("text-subheader");
		
		getChildren().addAll(heading, content);
		setSpacing(10);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		cbSkills.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnAdd.setDisable(n==null));
		btnAdd.setOnAction(event -> addSkill());
	}

	//--------------------------------------------------------------------
	private void addSkill() {
		Skill skill = cbSkills.getSelectionModel().getSelectedItem();
		int attr = model.getAttribute(skill.getAttribute1()).getValue() + model.getAttribute(skill.getAttribute2()).getValue();
		SkillValue value = new SkillValue(skill, attr);
		model.addSkill(value);
		refresh();
		viewPane.refresh();
	}

	//--------------------------------------------------------------------
	private void removeSkill(SkillValue sVal) {
		model.removeSkill(sVal);
		refresh();
		viewPane.refresh();
	}

	//--------------------------------------------------------------------
	private void addSkill(SkillValue sVal, int y) {
		Skill skill = sVal.getSkill();
		int attrVal = 0;
		if (skill.getType()!=SkillType.COMBAT) {
			attrVal = model.getAttribute(skill.getAttribute1()).getValue() + model.getAttribute(skill.getAttribute2()).getValue();
		}
		
		Label lblName = new Label(skill.getName());
		Label lblAttr = new Label(String.valueOf(attrVal));
		lblAttr.setMaxWidth(Double.MAX_VALUE);
		lblAttr.setAlignment(Pos.CENTER);

		int points = sVal.getValue();
		int value   = points+attrVal;
		Label tfPoints = new Label(String.valueOf(points));
		tfPoints.setMaxWidth(Double.MAX_VALUE);
		tfPoints.setAlignment(Pos.CENTER);
		pointsMap.put(sVal, tfPoints);

		TextField tfValue  = new TextField(String.valueOf(value));
		tfValue.setUserData(sVal);
		tfValue.setStyle("-fx-pref-width: 4em");
		tfValue.textProperty().addListener( (ov,o,n) -> textChanged(tfValue,o,n));
		tfValue.setOnAction(event -> textFinalized(tfValue, tfValue.getText()));
		tfValue.focusedProperty().addListener( (ov,o,n) -> {
			if (!n) 
				textFinalized(tfValue, tfValue.getText());
			});
		
		grid.add(lblName , 0, y);
		grid.add(lblAttr , 1, y);
		grid.add(tfPoints, 2, y);
		grid.add(tfValue , 3, y);
		
		if (!Creature.DEFAULT_SKILL_NAMES.contains(skill.getId())) {
			Button btnRem = new Button(UI.getString("button.delete"));
			btnRem.setUserData(sVal);
			grid.add(btnRem, 4, y);
			btnRem.setOnAction(event -> removeSkill(sVal));
		}
	}

	//--------------------------------------------------------------------
	public void textChanged(TextField input, String oldValue, String newValue) {
		SkillValue sVal = (SkillValue)input.getUserData();
		Skill skill = sVal.getSkill();
		
		try {
			int finVal = Integer.parseInt(newValue);
			int attrVal = model.getAttribute(skill.getAttribute1()).getValue() + model.getAttribute(skill.getAttribute2()).getValue();
			int points  = finVal - attrVal;
			if (points>0) {
				Label label = pointsMap.get(sVal);
				label.setText(String.valueOf(points));
				sVal.setValue(points);
			}
		} catch (NumberFormatException e) {
//			input.setText(oldValue);
		}
	}

	//--------------------------------------------------------------------
	public void textFinalized(TextField input, String newValue) {
		SkillValue sVal = (SkillValue)input.getUserData();
		Skill skill = sVal.getSkill();
		int attrVal = model.getAttribute(skill.getAttribute1()).getValue() + model.getAttribute(skill.getAttribute2()).getValue();
		
		try {
			int finVal = Integer.parseInt(newValue);
			int points  = finVal - attrVal;
			if (points>0) {
				finVal = attrVal;
				input.setText(String.valueOf(finVal));
			}
			Label label = pointsMap.get(sVal);
			label.setText(String.valueOf(points));
		} catch (NumberFormatException e) {
			input.setText(String.valueOf(attrVal + sVal.getValue()));
			Label label = pointsMap.get(sVal);
			label.setText(String.valueOf(sVal.getValue()));
		}
	}

	//--------------------------------------------------------------------
	void refresh() {
		grid.getChildren().retainAll(headings);
		
		int y=0;
		for (SkillValue value : model.getSkills()) {
			y++;
			addSkill(value, y);
		}
		
		/*
		 * All non-combat skills to choose from
		 */
		for (Skill skill : SplitterMondCore.getSkills()) {
			if (skill.getType()==SkillType.COMBAT)
				continue;
			cbSkills.getItems().add(skill);
		}
	}

	//--------------------------------------------------------------------
	public void setData(Creature model) {
		this.model = model;
		refresh();
	}

}
