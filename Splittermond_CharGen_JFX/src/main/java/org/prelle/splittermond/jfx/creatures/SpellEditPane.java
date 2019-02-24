/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

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
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.Creature;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class SpellEditPane extends VBox {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	class SpellValueTreeItem extends TreeItem<Object> {
		private SpellValue spellValue;

		public SpellValueTreeItem(SpellValue value) {
			super(value.getSpellLevel()+" "+value.getSpell().getName());
			this.spellValue = value;
		}
		//-------------------------------------------------------------------
		public SpellValue getSpellValue() {return spellValue;}
	}

	private Creature model;
	private SpellViewPane viewPane;

	private ChoiceBox<Skill> cbSchools;
	private ChoiceBox<SpellValue> cbSpells;
	private Button btnAdd;
	private TreeItem<Object> root;
	private TreeView<Object> table;

	//--------------------------------------------------------------------
	public SpellEditPane(SpellViewPane view) {
		this.viewPane = view;
		initComponents();
		initLayout();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		cbSchools = new ChoiceBox<>();
		cbSchools.setConverter(new StringConverter<Skill>() {
			public String toString(Skill object) {return object.getName();}
			public Skill fromString(String string) { return null;}
		});
		cbSpells  = new ChoiceBox<>();
		cbSpells.setConverter(new StringConverter<SpellValue>() {
			public String toString(SpellValue object) {return object.getSpellLevel()+" "+object.getSpell().getName();}
			public SpellValue fromString(String string) { return null;}
		});
		btnAdd = new Button("+");
		btnAdd.setStyle("-fx-pref-width: 4em");
		cbSchools.setStyle("-fx-pref-width: 12em");
		cbSpells.setStyle("-fx-pref-width: 12em");

		root   = new TreeItem<Object>(null);
		table  = new TreeView<Object>(root);
		table.setShowRoot(false);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		cbSchools.setMaxWidth(Double.MAX_VALUE);
		cbSpells.setMaxWidth(Double.MAX_VALUE);
		btnAdd.setMaxWidth(Double.MAX_VALUE);
		TilePane tile = new TilePane(Orientation.HORIZONTAL, 5,5);
		tile.getChildren().addAll(cbSchools, cbSpells);
		tile.setPrefColumns(2);
		HBox addLine = new HBox(10);
		addLine.getChildren().addAll(tile, btnAdd);
//		HBox.setHgrow(tile, Priority.ALWAYS);
//		HBox.setHgrow(btnAdd, Priority.SOMETIMES);

		VBox content = new VBox(5);
		content.getChildren().addAll(table, addLine);
		content.getStyleClass().add("content");

		Label heading = new Label(UI.getString("label.spells"));
		heading.getStyleClass().add("text-subheader");

		getChildren().addAll(heading, content);
		setSpacing(0);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		cbSchools.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> updateSpells(n));
		cbSpells.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnAdd.setDisable(n==null));

		btnAdd.setOnAction(event -> addSpell());
	}

	//--------------------------------------------------------------------
	private void updateSpells(Skill school) {
		cbSpells.getItems().clear();
		if (school==null)
			return;

		for (Spell spell : SplitterMondCore.getSpells(school)) {
			SpellValue val = new SpellValue(spell, school);
			if (!model.getSpells().contains(val)) {
				cbSpells.getItems().add(val);
			}
		}
	}

	//--------------------------------------------------------------------
	private void addSpell() {
		SpellValue value = cbSpells.getSelectionModel().getSelectedItem();
		model.addSpell(value);
		refresh();
		viewPane.refresh();
	}

	//--------------------------------------------------------------------
	private void refresh() {
		root.getChildren().clear();
		cbSchools.getItems().clear();


		Map<Skill, List<SpellValue>> data = new HashMap<>();
		/*
		 * Collect data
		 */
		for (SkillValue sVal : model.getSkills(SkillType.MAGIC)) {
			if (sVal.getValue()<1)
				continue;
			Skill skill = sVal.getSkill();
			cbSchools.getItems().add(skill);
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

			TreeItem<Object> item = new TreeItem<>(skill.getName());
			root.getChildren().add(item);

			List<SpellValue> spells = data.get(skill);
			for (Iterator<SpellValue> it2=spells.iterator(); it2.hasNext(); ) {
				SpellValue spell = it2.next();
				SpellValueTreeItem spellItem = new SpellValueTreeItem(spell);
				item.getChildren().add(spellItem);
			}
		}
	}

	//--------------------------------------------------------------------
	public void setData(Creature model) {
		this.model = model;
		refresh();
	}

}
