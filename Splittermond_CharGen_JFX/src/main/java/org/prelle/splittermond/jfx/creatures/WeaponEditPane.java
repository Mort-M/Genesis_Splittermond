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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureWeapon;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Weapon;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class WeaponEditPane extends VBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private static StringConverter<Skill> SKILLCONV = new StringConverter<Skill>() {
		public String toString(Skill object) {return object.getName();}
		public Skill fromString(String string) { return null;}
	};
	private static StringConverter<ItemTemplate> ITEMCONV = new StringConverter<ItemTemplate>() {
		public String toString(ItemTemplate object) {
			if (object==null) return "";
			return object.getName();
		}
		public ItemTemplate fromString(String string) { return null;}
	};
	private static StringConverter<Number> DAMAGECONV = new StringConverter<Number>() {
		public String toString(Number data) {
			return SplitterTools.getWeaponDamageString((Integer)data);
		}
		public Number fromString(String string) { return SplitterTools.parseWeaponDamageString(string);}
	};
	private static StringConverter<Number> NUMBERCONV = new StringConverter<Number>() {
		public String toString(Number data) {return String.valueOf(data);}
		public Number fromString(String string) { return Integer.parseInt(string);}
	};
	
	private Creature model;
	private WeaponViewPane viewPane;
	
	private Button btnAdd;
	private GridPane grid;
	private List<Label> headings;
	private Map<CreatureWeapon, WeaponLine> pointsMap;
	
	private TableView<CreatureWeapon> table;
	private TableColumn<CreatureWeapon, Skill> colSkill;
	private TableColumn<CreatureWeapon, ItemTemplate> colItem;
	private TableColumn<CreatureWeapon, String> colName;
	private TableColumn<CreatureWeapon, Number> colAttr;
	private TableColumn<CreatureWeapon, Number> colPoints;
	private TableColumn<CreatureWeapon, Number> colValue;
	private TableColumn<CreatureWeapon, Number> colDamage;
	private TableColumn<CreatureWeapon, Number> colSpeed;
	private TableColumn<CreatureWeapon, Number> colIni;
	private TableColumn<CreatureWeapon, String> colFeature;
	
	private ObservableList<ItemTemplate> possibleItems;

	//--------------------------------------------------------------------
	private static int getAttributePoints(Creature model, ItemTemplate n) {
		Attribute att1 = Attribute.AGILITY;
		Attribute att2 = Attribute.STRENGTH;
		if (n!=null && n.isType(ItemType.WEAPON)) {
			att1 = n.getType(Weapon.class).getAttribute1();
			att2 = n.getType(Weapon.class).getAttribute2();
		} else if (n!=null && n.isType(ItemType.LONG_RANGE_WEAPON)) {
			att1 = n.getType(LongRangeWeapon.class).getAttribute1();
			att2 = n.getType(LongRangeWeapon.class).getAttribute2();
		}
		
		return model.getAttribute(att1).getValue() + model.getAttribute(att2).getValue();
	}
	
	//--------------------------------------------------------------------
	public WeaponEditPane(WeaponViewPane view) {
		this.viewPane = view;
		initComponents();
		initLayout();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		possibleItems = FXCollections.observableArrayList();
		
		pointsMap = new HashMap<>();
		btnAdd = new Button("+");
		
		grid   = new GridPane();
		headings   = new ArrayList<>();
		
		colSkill = new TableColumn<>(UI.getString("label.skill"));
		colItem  = new TableColumn<>(UI.getString("creature.weapon.item"));
		colName  = new TableColumn<>(UI.getString("label.name"));
		colAttr  = new TableColumn<>(UI.getString("label.attributes"));
		colPoints= new TableColumn<>(UI.getString("label.points"));
		colValue = new TableColumn<>(UI.getString("label.value"));
		colDamage= new TableColumn<>(ItemAttribute.DAMAGE.getName());
		colSpeed = new TableColumn<>(ItemAttribute.SPEED.getShortName());
		colIni   = new TableColumn<>(Attribute.INITIATIVE.getShortName());
		colFeature= new TableColumn<>(ItemAttribute.FEATURES.getName());
		colSkill.setStyle("-fx-pref-width: 12em");
		colItem.setStyle("-fx-pref-width: 12em");
		table = new TableView<>();
		table.getColumns().addAll(colSkill, colItem, colName, colAttr, colPoints, colValue, colDamage, colSpeed, colIni, colFeature);
		
		colSkill.setCellValueFactory(new PropertyValueFactory("skill"));
		colItem.setCellValueFactory(new PropertyValueFactory("item"));
		colName.setCellValueFactory(new PropertyValueFactory("name"));
		colDamage.setCellValueFactory(new PropertyValueFactory("damage"));
		colValue.setCellValueFactory(new PropertyValueFactory("value"));
		colSpeed.setCellValueFactory(new PropertyValueFactory("speed"));
		colIni.setCellValueFactory(new PropertyValueFactory("initiative"));
		colFeature.setCellValueFactory(new PropertyValueFactory("features"));
		colAttr.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CreatureWeapon,Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CreatureWeapon, Number> param) {
				CreatureWeapon weapon = param.getValue();
				ItemTemplate n = weapon.getItem();
				
				Attribute att1 = Attribute.AGILITY;
				Attribute att2 = Attribute.STRENGTH;
				if (n!=null && n.isType(ItemType.WEAPON)) {
					att1 = n.getType(Weapon.class).getAttribute1();
					att2 = n.getType(Weapon.class).getAttribute2();
				} else if (n!=null && n.isType(ItemType.LONG_RANGE_WEAPON)) {
					att1 = n.getType(LongRangeWeapon.class).getAttribute1();
					att2 = n.getType(LongRangeWeapon.class).getAttribute2();
				}
				
				return new SimpleIntegerProperty(model.getAttribute(att1).getValue() + model.getAttribute(att2).getValue());
			}
		});
		colPoints.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CreatureWeapon,Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CreatureWeapon, Number> param) {
				CreatureWeapon weapon = param.getValue();
				ItemTemplate n = weapon.getItem();
				
				Attribute att1 = Attribute.AGILITY;
				Attribute att2 = Attribute.STRENGTH;
				if (n!=null && n.isType(ItemType.WEAPON)) {
					att1 = n.getType(Weapon.class).getAttribute1();
					att2 = n.getType(Weapon.class).getAttribute2();
				} else if (n!=null && n.isType(ItemType.LONG_RANGE_WEAPON)) {
					att1 = n.getType(LongRangeWeapon.class).getAttribute1();
					att2 = n.getType(LongRangeWeapon.class).getAttribute2();
				}
				
				return new SimpleIntegerProperty(weapon.getValue() - (model.getAttribute(att1).getValue() + model.getAttribute(att2).getValue()));
			}
		});
		
		colSkill.setCellFactory(ChoiceBoxTableCell.forTableColumn(SKILLCONV, FXCollections.observableArrayList(SplitterMondCore.getSkills(SkillType.COMBAT))));
		colItem.setCellFactory(ChoiceBoxTableCell.forTableColumn(ITEMCONV, possibleItems));
		colName.setCellFactory(TextFieldTableCell.forTableColumn());
		colDamage.setCellFactory(TextFieldTableCell.forTableColumn(DAMAGECONV));
		colSpeed.setCellFactory(TextFieldTableCell.forTableColumn(NUMBERCONV));
		colIni.setCellFactory(TextFieldTableCell.forTableColumn(NUMBERCONV));
		colValue.setCellFactory(TextFieldTableCell.forTableColumn(NUMBERCONV));
//		colDamage.setCellFactory(new Callback<TableColumn<CreatureWeapon,Number>, TableCell<CreatureWeapon,Number>>() {
//			public TableCell<CreatureWeapon, Number> call(TableColumn<CreatureWeapon, String> param) {
//				TextFieldTableCell<CreatureWeapon, Number> cell = new TextFieldTableCell<>(DAMAGECONV)
//				return cell;
//			}
//		});;
//		colSpeed.setCellFactory(TextFieldTableCell.forTableColumn());
		
		colSkill.setEditable(true);
		colItem.setEditable(true);
		colName.setEditable(true);
		colDamage.setEditable(true);
		colSpeed.setEditable(true);
		colIni.setEditable(true);
		colValue.setEditable(true);
		colAttr.setEditable(false);
		table.setEditable(true);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label headSkill = new Label(UI.getString("label.skill"));
		Label headItem  = new Label(UI.getString("creature.weapon.item"));
		Label headName  = new Label(UI.getString("label.name"));
		Label headAttr  = new Label(UI.getString("label.attributes"));
		Label headPoints= new Label(UI.getString("label.points"));
		Label headValue = new Label(UI.getString("label.value"));
		Label headDamage= new Label(ItemAttribute.DAMAGE.getName());
		Label headSpeed = new Label(ItemAttribute.SPEED.getShortName());
		Label headINI   = new Label(Attribute.INITIATIVE.getShortName());
		Label headFeature= new Label(ItemAttribute.FEATURES.getName());
		Label headAction= new Label("-");
		
		headSkill .getStyleClass().add("table-head");
		headItem  .getStyleClass().add("table-head");
		headName  .getStyleClass().add("table-head");
		headAttr  .getStyleClass().add("table-head");
		headPoints.getStyleClass().add("table-head");
		headValue .getStyleClass().add("table-head");
		headDamage.getStyleClass().add("table-head");
		headSpeed .getStyleClass().add("table-head");
		headINI   .getStyleClass().add("table-head");
		headFeature.getStyleClass().add("table-head");
		headAction.getStyleClass().add("table-head");
		
		headSkill .setMaxWidth(Double.MAX_VALUE);
		headItem  .setMaxWidth(Double.MAX_VALUE);
		headName  .setMaxWidth(Double.MAX_VALUE);
		headAttr  .setMaxWidth(Double.MAX_VALUE);
		headPoints.setMaxWidth(Double.MAX_VALUE);
		headValue .setMaxWidth(Double.MAX_VALUE);
		headDamage.setMaxWidth(Double.MAX_VALUE);
		headSpeed .setMaxWidth(Double.MAX_VALUE);
		headINI   .setMaxWidth(Double.MAX_VALUE);
		headFeature.setMaxWidth(Double.MAX_VALUE);
		headAction.setMaxWidth(Double.MAX_VALUE);
		
		grid.add(headSkill , 0, 0);
		grid.add(headItem  , 1, 0);
		grid.add(headName  , 2, 0);
		grid.add(headAttr  , 3, 0);
		grid.add(headPoints, 4, 0);
		grid.add(headValue , 5, 0);
		grid.add(headDamage, 6, 0);
		grid.add(headSpeed , 7, 0);
		grid.add(headINI   , 8, 0);
		grid.add(headFeature, 9, 0);
		grid.add(headAction,10, 0);
		headings.addAll(Arrays.asList(headSkill, headItem, headName, headAttr, headPoints, headValue, headDamage, headSpeed, headINI, headFeature, headAction));
		grid.setVgap(2);
		
		ScrollPane scroll = new ScrollPane(grid);
		VBox content = new VBox();
		content.getChildren().addAll(scroll, btnAdd);
		content.getStyleClass().add("content");
		
		Label heading = new Label(UI.getString("label.weapons"));
		heading.getStyleClass().add("text-subheader");
		
		getChildren().addAll(heading, content, table);
		setSpacing(10);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
//		cbSkills.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnAdd.setDisable(n==null));
		btnAdd.setOnAction(event -> addWeapon());
		
		colSkill.setOnEditCommit(event -> {
			Skill skill = event.getNewValue();
			possibleItems.clear();
			if (skill!=null)
				possibleItems.addAll(SplitterMondCore.getWeaponItems(skill));
			event.getRowValue().setSkill(skill);
		});
		
		colItem.setOnEditCommit(event -> {
			ItemTemplate item = event.getNewValue();
			CreatureWeapon cweapon = event.getRowValue();
			if (item!=null) {
				cweapon.setCustomName(null);
				if (item.isType(ItemType.WEAPON)) {
					cweapon.setDamage(item.getType(Weapon.class).getDamage());
					cweapon.setSpeed(item.getType(Weapon.class).getSpeed());
				} else if (item.isType(ItemType.LONG_RANGE_WEAPON)) {
					cweapon.setDamage(item.getType(LongRangeWeapon.class).getDamage());
					cweapon.setSpeed(item.getType(LongRangeWeapon.class).getSpeed());
				}
			}
			cweapon.setItem(item);
			table.getColumns().get(0).setVisible(false);
			table.getColumns().get(0).setVisible(true);
		});
		
		colDamage.setOnEditCommit(event -> event.getRowValue().setDamage( (Integer)event.getNewValue()));
		colSpeed .setOnEditCommit(event -> event.getRowValue().setSpeed ( (Integer)event.getNewValue()));
		colIni   .setOnEditCommit(event -> event.getRowValue().setInitiative( (Integer)event.getNewValue()));
		colValue .setOnEditCommit(event -> {
			event.getRowValue().setValue ( (Integer)event.getNewValue());
			table.getColumns().get(0).setVisible(false);
			table.getColumns().get(0).setVisible(true);
		});
	}

	//--------------------------------------------------------------------
	private void addWeapon() {
		CreatureWeapon weapon = new CreatureWeapon();
		weapon.setSkill(SplitterMondCore.getSkill("melee"));
		weapon.setDamage(600);
		weapon.setSpeed(8);
		model.addWeapon(weapon);
		refresh();
		viewPane.refresh();
	}

	//--------------------------------------------------------------------
	private void removeWeapon(CreatureWeapon weapon) {
		pointsMap.remove(weapon);
		model.removeWeapon(weapon);
		refresh();
		viewPane.refresh();
	}

	//--------------------------------------------------------------------
	private void addWeapon(CreatureWeapon data, int y) {
		
		WeaponLine line = new WeaponLine(model, data);
		pointsMap.put(data, line);
		line.layoutToGrid(grid, y);
	}

	//--------------------------------------------------------------------
	void refresh() {
		grid.getChildren().retainAll(headings);
		
		int y=0;
		for (CreatureWeapon value : model.getCreatureWeapons()) {
			y++;
			addWeapon(value, y);
		}
		
		table.getItems().clear();
		table.getItems().addAll(model.getCreatureWeapons());
	}

	//--------------------------------------------------------------------
	public void setData(Creature model) {
		this.model = model;
		refresh();
	}

}

class WeaponLine {
	
	private Creature       model;
	private CreatureWeapon weapon;
	
	private ChoiceBox<Skill> cbSkills;
	private ChoiceBox<ItemTemplate> cbItems;
	private TextField        tfName;
	private Label            lblAttr;
	private Label            tfPoints;
	private TextField        tfValue;
	private TextField        tfDamage;
	private TextField        tfSpeed;
	private TextField        tfINI;
	
	//-------------------------------------------------------------------
	public WeaponLine(Creature model, CreatureWeapon weapon) {
		this.model = model;
		this.weapon= weapon;
		
		initComponents();
		initInteractivity();
		initData();
	}
	
	//--------------------------------------------------------------------
	private void initComponents() {
		// Skills
		cbSkills = new ChoiceBox<>();
		cbSkills.setConverter(new StringConverter<Skill>() {
			public String toString(Skill object) {return object.getName();}
			public Skill fromString(String string) { return null;}
		});
		cbSkills.getItems().addAll(SplitterMondCore.getSkills(SkillType.COMBAT));

		// Items
		cbItems = new ChoiceBox<>();
		cbItems.setConverter(new StringConverter<ItemTemplate>() {
			public String toString(ItemTemplate object) {return object.getName();}
			public ItemTemplate fromString(String string) { return null;}
		});
		
		tfName = new TextField();
		tfName.setStyle("-fx-min-width: 4em");

		lblAttr = new Label();
		lblAttr.setMaxWidth(Double.MAX_VALUE);
		lblAttr.setAlignment(Pos.CENTER);
		
		tfPoints = new Label();
		tfPoints.setMaxWidth(Double.MAX_VALUE);
		tfPoints.setAlignment(Pos.CENTER);

		tfValue  = new TextField();
//		tfValue.setStyle("-fx-pref-width: 3em");

		tfDamage = new TextField();
//		tfDamage.setStyle("-fx-pref-width: 4em");

		tfSpeed = new TextField();
		tfSpeed.setStyle("-fx-max-width: 1em");

		tfINI = new TextField();
//		tfINI.setStyle("-fx-pref-width: 3em");
	}
	
	//--------------------------------------------------------------------
	private void initInteractivity() {
		// Skills
		cbSkills.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			cbItems.getItems().clear();
			if (n!=null)
				cbItems.getItems().addAll(SplitterMondCore.getWeaponItems(n));
			weapon.setSkill(n);
		});
		
		// Items
		cbItems.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			weapon.setItem(n);
			if (n!=null) {
				int attrVal = getAttributePoints();
				lblAttr.setText(String.valueOf(attrVal));
				// Skill points value
				int points = model.getSkillPoints(cbSkills.getValue());
				tfPoints.setText(String.valueOf(points));
				tfValue.setText(String.valueOf(attrVal + points));
				
				tfName.setText(n.getName());
				weapon.setCustomName(null);
				tfSpeed.setText(String.valueOf(weapon.getSpeed()));
			}
		});
		
		tfValue.textProperty().addListener( (ov,o,n) -> textChanged(tfValue,o,n));
		tfValue.setOnAction(event -> textFinalized(tfValue, tfValue.getText()));
		tfValue.focusedProperty().addListener( (ov,o,n) -> {
			if (!n) 
				textFinalized(tfValue, tfValue.getText());
			});
		tfName.textProperty().addListener( (ov,o,n) -> textChanged(tfName,o,n));
		tfName.setOnAction(event -> textFinalized(tfName, tfName.getText()));
		tfName.focusedProperty().addListener( (ov,o,n) -> {
			if (!n) 
				textFinalized(tfName, tfName.getText());
			});
		tfSpeed.textProperty().addListener( (ov,o,n) -> textChanged(tfSpeed,o,n));
		tfSpeed.setOnAction(event -> textFinalized(tfSpeed, tfSpeed.getText()));
		tfSpeed.focusedProperty().addListener( (ov,o,n) -> {
			if (!n) 
				textFinalized(tfSpeed, tfSpeed.getText());
			});
		tfDamage.textProperty().addListener( (ov,o,n) -> textChanged(tfDamage,o,n));
		tfDamage.setOnAction(event -> textFinalized(tfDamage, tfDamage.getText()));
		tfDamage.focusedProperty().addListener( (ov,o,n) -> {
			if (!n) 
				textFinalized(tfDamage, tfDamage.getText());
			});
		tfINI.textProperty().addListener( (ov,o,n) -> textChanged(tfINI,o,n));
		tfINI.setOnAction(event -> textFinalized(tfINI, tfINI.getText()));
		tfINI.focusedProperty().addListener( (ov,o,n) -> {
			if (!n) 
				textFinalized(tfINI, tfINI.getText());
			});
	}
	
	//--------------------------------------------------------------------
	private void initData() {
		if (weapon.getSkill()!=null)
			cbSkills.getSelectionModel().select(weapon.getSkill());
		if (weapon.getItem()!=null)
			cbItems.getSelectionModel().select(weapon.getItem());

		if (weapon.getCustomName()!=null)
			tfName.setText(weapon.getCustomName());
		
		tfDamage.setText(SplitterTools.getWeaponDamageString(weapon.getDamage()));
		tfSpeed.setText(String.valueOf(weapon.getSpeed()));
		tfINI.setText(String.valueOf(weapon.getInitiative()));
	}
	
	//--------------------------------------------------------------------
	public void layoutToGrid(GridPane grid, int y) {
		grid.add(cbSkills, 0, y);
		grid.add(cbItems , 1, y);
		grid.add(tfName  , 2, y);
		grid.add(lblAttr , 3, y);
		grid.add(tfPoints, 4, y);
		grid.add(tfValue , 5, y);
		grid.add(tfDamage, 6, y);
		grid.add(tfSpeed , 7, y);
		grid.add(tfINI   , 8, y);
		
		GridPane.setHgrow(cbSkills, Priority.SOMETIMES);
		GridPane.setHgrow(cbItems , Priority.SOMETIMES);
		GridPane.setHgrow(tfName  , Priority.SOMETIMES);
		GridPane.setHgrow(lblAttr , Priority.NEVER);
		GridPane.setHgrow(tfPoints, Priority.NEVER);
		GridPane.setHgrow(tfValue , Priority.NEVER);
		GridPane.setHgrow(tfDamage, Priority.NEVER);
		GridPane.setHgrow(tfSpeed , Priority.NEVER);
		GridPane.setHgrow(tfINI   , Priority.NEVER);
	}

	//--------------------------------------------------------------------
	private int getAttributePoints() {
		ItemTemplate n = weapon.getItem();
		
		Attribute att1 = Attribute.AGILITY;
		Attribute att2 = Attribute.STRENGTH;
		if (n!=null && n.isType(ItemType.WEAPON)) {
			att1 = n.getType(Weapon.class).getAttribute1();
			att2 = n.getType(Weapon.class).getAttribute2();
		} else if (n!=null && n.isType(ItemType.LONG_RANGE_WEAPON)) {
			att1 = n.getType(LongRangeWeapon.class).getAttribute1();
			att2 = n.getType(LongRangeWeapon.class).getAttribute2();
		}
		
		return model.getAttribute(att1).getValue() + model.getAttribute(att2).getValue();
	}

	//--------------------------------------------------------------------
	public void textChanged(TextField input, String oldValue, String newValue) {
		try {
			if (input==tfValue) {
				int attrVal = getAttributePoints();
				int finVal = Integer.parseInt(newValue);
				weapon.setValue(finVal);
				int points  = finVal - attrVal;
				tfPoints.setText(String.valueOf(points));
			} else if (input==tfName) {
				if (weapon.getItem()!=null) {
					if (weapon.getItem().getName().equals(newValue))
						weapon.setCustomName(null);
					else
						weapon.setCustomName(newValue);
				} else
					weapon.setCustomName(newValue);
			} else if (input==tfSpeed) {
				weapon.setSpeed(Integer.parseInt(newValue));
			} else if (input==tfDamage) {
				weapon.setDamage(SplitterTools.parseWeaponDamageString(newValue));
			}
			input.setStyle("-fx-border-width: 0px;"); 
		} catch (NumberFormatException e) {
			input.setStyle("-fx-border-color: red; -fx-border-width: 1px;"); 
		}
	}

	//--------------------------------------------------------------------
	public void textFinalized(TextField input, String newValue) {
		textChanged(input, null, newValue);
	}

}
