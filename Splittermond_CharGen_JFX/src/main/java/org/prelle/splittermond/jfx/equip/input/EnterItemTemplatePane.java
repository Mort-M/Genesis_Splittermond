/**
 * 
 */
package org.prelle.splittermond.jfx.equip.input;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.rpgframework.jfx.DataInputPane;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Armor;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Shield;
import org.prelle.splimo.items.Weapon;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class EnterItemTemplatePane extends VBox implements DataInputPane<ItemTemplate> {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private final static ResourceBundle RES = ResourceBundle.getBundle("i18n/splimo-chargen");

	private ItemTemplate model;
	private TextField tfName;
	private TextField tfPage;
	private TextArea  taDesc;
	private ChoiceBox<ItemType> cbType;
	private ChoiceBox<Skill> cbSkill;
	private ChoiceBox<SkillSpecialization> cbSkillSpec;
	private BasicItemTemplateDataPane basicPane;
	private WeaponTemplateDataPane weaponPane;
	private RangeWeaponTemplateDataPane rangedPane;
	private ShieldTemplateDataPane shieldPane;
	private ArmorTemplateDataPane armorPane;
	private HBox dataLine;
	private boolean isUpdating;
	
	//-------------------------------------------------------------------
	public EnterItemTemplatePane(ItemTemplate model) {
		this.model = model;
		initComponents();
		initLayout();
		initInteractivity();
		
		basicPane.setData(model);
		
		refresh();
	}
	
	//-------------------------------------------------------------------
	public EnterItemTemplatePane() {
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		tfName = new TextField();
		tfPage = new TextField();
		taDesc = new TextArea();
		taDesc.setWrapText(true);
		cbType = new ChoiceBox<>();
		cbType.getItems().addAll(ItemType.values());
		cbType.setConverter(new StringConverter<ItemType>() {
			public String toString(ItemType object) { return object.getName(); }
			public ItemType fromString(String string) {return null; }
		});
		
		cbSkill         = new ChoiceBox<>();
		cbSkill.getItems().addAll(SplitterMondCore.getSkills());
		cbSkill.setConverter(new StringConverter<Skill>() {
			public String toString(Skill object) { return object.getName(); }
			public Skill fromString(String string) { return null; }
		});
		
		cbSkillSpec         = new ChoiceBox<>();
		cbSkillSpec.setConverter(new StringConverter<SkillSpecialization>() {
			public String toString(SkillSpecialization object) { return object.getName(); }
			public SkillSpecialization fromString(String string) { return null; }
		});
		
		basicPane  = new BasicItemTemplateDataPane();
		weaponPane = new WeaponTemplateDataPane();
		rangedPane = new RangeWeaponTemplateDataPane();
		shieldPane = new ShieldTemplateDataPane();
		armorPane  = new ArmorTemplateDataPane();
		
		dataLine = new HBox(20);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		taDesc.setStyle("-fx-pref-height: 5em; -fx-pref-width: 25em");
		
		Label heaName = new Label(RES.getString("label.name"));
		Label heaPage = new Label(RES.getString("label.page"));
		Label heaDesc = new Label(RES.getString("label.desc"));
		Label heaType = new Label(RES.getString("label.type"));
		Label heaSkill= new Label(RES.getString("label.skill"));
		Label heaSpec = new Label(RES.getString("label.specialization"));
		heaName.getStyleClass().add("text-small-subheader");
		heaPage.getStyleClass().add("text-small-subheader");
		heaDesc.getStyleClass().add("text-small-subheader");
		heaType.getStyleClass().add("text-small-subheader");
		heaSkill.getStyleClass().add("text-small-subheader");
		heaSpec.getStyleClass().add("text-small-subheader");
		
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		grid.add(heaName, 0, 0);
		grid.add(tfName , 1, 0);
		grid.add(heaPage, 0, 1);
		grid.add(tfPage , 1, 1);		
		grid.add(heaDesc, 2, 0);
		grid.add(taDesc , 2, 1, 1,4);
		grid.add(heaType, 0, 2);
		grid.add(cbType , 1, 2);
		grid.add(heaSkill,0, 3);
		grid.add(cbSkill, 1, 3);
		grid.add(heaSpec, 0, 4);
		grid.add(cbSkillSpec , 1, 4);
		
		dataLine.getChildren().add(basicPane);
		
		setSpacing(20);
		getChildren().addAll(grid, dataLine);
	}
	
	//-------------------------------------------------------------------
	private void refresh() {
		logger.debug("Refresh from "+model);
		isUpdating = true;
		tfName.setText(model.getName());
		if (model.getPage()!=0)
			tfPage.setText(String.valueOf(model.getPage()));
		else
			tfPage.setText(null);
		taDesc.setText(model.getHelpText());
		cbType.getSelectionModel().select(model.getFirstItemType());
		logger.debug("Refresh from "+model);
		
		if (model.getSkill()!=null)
			cbSkill.getSelectionModel().select(model.getSkill());
		else
			cbSkill.getSelectionModel().clearSelection();
		
		if (model.getSpecialization()!=null)
			cbSkillSpec.getSelectionModel().select(model.getSpecialization());
		else
			cbSkillSpec.getSelectionModel().clearSelection();
		
		basicPane.setData(model);
		dataLine.getChildren().retainAll(basicPane);
		if (model.getType(Weapon.class)!=null) {
			weaponPane.setData(model.getType(Weapon.class));
			dataLine.getChildren().add(weaponPane);
		} else
		if (model.getType(LongRangeWeapon.class)!=null) {
			logger.debug("  taking RangeWeapon from "+model+" results in "+model.getType(LongRangeWeapon.class));
			logger.debug("  set "+model.getType(ItemType.LONG_RANGE_WEAPON));
			rangedPane.setData(model.getType(LongRangeWeapon.class));
			dataLine.getChildren().add(rangedPane);
		} else
		if (model.getType(Shield.class)!=null) {
			shieldPane.setData(model.getType(Shield.class));
			dataLine.getChildren().add(shieldPane);
		} else
		if (model.getType(Armor.class)!=null) {
			armorPane.setData(model.getType(Armor.class));
			dataLine.getChildren().add(armorPane);
		}
		
		
		isUpdating = false;
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tfName.textProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); tfName.setText(n); }
				if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); tfName.setText(n); }
				if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); tfName.setText(n); }
				if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); tfName.setText(n); }
			}
		});
		tfPage.textProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				try {
					Integer.parseInt(n);
				} catch (NumberFormatException nfe) {
					tfPage.setText(String.valueOf(model.getPage()));
				}
			}
		});

		cbType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			ItemType type = n;
			logger.debug("Change to "+n);
			if (!isUpdating)
				model.replaceItemType(n);
			switch (type) {
			case WEAPON:
				Weapon weapon = new Weapon();
				if (!isUpdating)
					model.overwriteItemTypeData(weapon);
				weaponPane.setData(weapon);
				weaponPane.refresh();
				dataLine.getChildren().retainAll(basicPane);
				dataLine.getChildren().add(weaponPane);
				cbSkill.getSelectionModel().clearSelection();
				cbSkill.setDisable(true);
				cbSkillSpec.setDisable(true);
				break;
			case LONG_RANGE_WEAPON:
				LongRangeWeapon ranged = new LongRangeWeapon();
				if (!isUpdating)
					model.overwriteItemTypeData(ranged);
				rangedPane.setData(ranged);
				rangedPane.refresh();
				dataLine.getChildren().retainAll(basicPane);
				dataLine.getChildren().add(rangedPane);
				cbSkill.getSelectionModel().clearSelection();
				cbSkill.setDisable(true);
				cbSkillSpec.setDisable(true);
				break;
			case SHIELD:
				Shield shield = new Shield();
				if (!isUpdating)
					model.overwriteItemTypeData(shield);
				shieldPane.setData(shield);
				shieldPane.refresh();
				dataLine.getChildren().retainAll(basicPane);
				dataLine.getChildren().add(shieldPane);
				cbSkill.getSelectionModel().clearSelection();
				cbSkill.setDisable(true);
				cbSkillSpec.setDisable(true);
				break;
			case ARMOR:
				Armor armor = new Armor();
				if (!isUpdating)
					model.overwriteItemTypeData(armor);
				armorPane.setData(armor);
				armorPane.refresh();
				dataLine.getChildren().retainAll(basicPane);
				dataLine.getChildren().add(armorPane);
				cbSkill.getSelectionModel().clearSelection();
				cbSkillSpec.setDisable(true);
				cbSkill.setDisable(true);
				break;
			default:
				dataLine.getChildren().retainAll(basicPane);
				cbSkill.setDisable(false);
				cbSkillSpec.setDisable(false);
			}
		});
		
		cbSkill.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.debug("Selected skill "+n);
			cbSkillSpec.getItems().clear();
			if (n!=null) {
				List<SkillSpecialization> list = new ArrayList<>(n.getSpecializations());
				Collections.sort(list, new Comparator<SkillSpecialization>() {
					public int compare(SkillSpecialization o1, SkillSpecialization o2) {
						return Collator.getInstance().compare(o1.getName(), o2.getName());
					}
				});
				cbSkillSpec.getItems().addAll(list);
			}
			model.setSkill(n);
		});
		
		cbSkillSpec.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setSpecialization(n));
//		refresh();
	}

	//-------------------------------------------------------------------
	public String getName() {
		return tfName.getText();
	}

	//-------------------------------------------------------------------
	public int getPage() {
		return Integer.parseInt(tfPage.getText());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.DataInputPane#setData(java.lang.Object)
	 */
	@Override
	public void setData(ItemTemplate model) {
		this.model = model;
		basicPane.setData(model);
		refresh();
		if (model.getType(Armor.class)!=null)
			armorPane.setData(model.getType(Armor.class));
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.DataInputPane#writeIntoData(java.lang.Object)
	 */
	@Override
	public void writeIntoData(ItemTemplate data) {
//		model.setID(tf);
		model.setHelpText(taDesc.getText());
		model.setPage(Integer.parseInt(tfPage.getText()));
		// TODO
	}

}
