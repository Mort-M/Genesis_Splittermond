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
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureWeapon;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.npc.NPCWeaponController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class CreatureWeaponEditPane extends VBox {

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
	private NPCWeaponController ctrl;

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
	public CreatureWeaponEditPane(NPCWeaponController ctrl) {
		this.ctrl = ctrl;
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
		scroll.setStyle("-fx-min-height: 10em");
		VBox content = new VBox();
		content.getChildren().addAll(scroll, btnAdd);
		content.getStyleClass().add("content");

		Label heading = new Label(UI.getString("label.weapons"));
		heading.getStyleClass().add("text-subheader");

		table.setStyle("-fx-pref-height: 10em");
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
		ctrl.addWeapon(weapon);
		model.addWeapon(weapon);
		refresh();
	}

	//--------------------------------------------------------------------
	private void removeWeapon(CreatureWeapon weapon) {
		pointsMap.remove(weapon);
		model.removeWeapon(weapon);
		refresh();
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
