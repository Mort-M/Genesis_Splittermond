/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.FontIcon;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.items.Armor;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.PersonalizationReference;
import org.prelle.splimo.items.Shield;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.persist.WeaponDamageConverter;
import org.prelle.splimo.requirements.AttributeRequirement;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class EquipmentListBox extends VBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private ItemLocationType location;
	private SpliMoCharacter model;

	private Label heading;
	private Button btnAdd;
	private Button btnDel;
	private ListView<CarriedItem> list;
	private Consumer<CarriedItem> onEdit;
	private Consumer<CarriedItem> onRemove;
	private Consumer<ItemLocationType> onAdd;
	private BiConsumer<CarriedItem,ItemLocationType> onMove;

	//-------------------------------------------------------------------
	public EquipmentListBox(ItemLocationType location, SpliMoCharacter model) {
		super(20);
		this.location = location;
		this.model    = model;
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		heading = new Label();
		heading.getStyleClass().add("text-subheader");


		list = new ListView<CarriedItem>();
		btnAdd = new Button(null, new FontIcon("\uE17E\uE109"));
		btnDel = new Button(null, new FontIcon("\uE17E\uE107"));
		btnDel.setDisable(true);
		btnAdd.setStyle("-fx-background-color: transparent");
		btnDel.setStyle("-fx-background-color: transparent");
		list.getStyleClass().add("bordered");
		list.setCellFactory(new Callback<ListView<CarriedItem>, ListCell<CarriedItem>>() {
			public ListCell<CarriedItem> call(ListView<CarriedItem> param) {
				CarriedItemCell cell = new CarriedItemCell();
				cell.setOnDragDetected(event -> dragDetected(event, list, cell));
				cell.setOnMouseClicked(event -> {if (event.getClickCount()==2) edit(cell.getItem());});
				/*
				 * Context menu
				 */
				ContextMenu contextMenu = new ContextMenu();
//	            MenuItem editItem = new MenuItem(UI.getString("label.edit"));
//	            editItem.setOnAction(event -> {
//	            	CarriedItem item = cell.getItem();
//	                // code to edit item...
//	            });
	            MenuItem deleteItem = new MenuItem(UI.getString("label.remove"));
	            deleteItem.setOnAction(event -> remove(cell.getItem()));
	            contextMenu.getItems().addAll(deleteItem);

	            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
	                if (isNowEmpty) {
	                    cell.setContextMenu(null);
	                } else {
	                    cell.setContextMenu(contextMenu);
	                }
	            });

	            return cell;
			}
		});

		switch (location) {
		case BODY:
			heading.setText(UI.getString("screen.equipment.column.body"));
			list.setStyle("-fx-background-image: url('images/icon_body_large.png'); -fx-background-position: bottom center; -fx-background-repeat: no-repeat;");
			break;
		case BEASTOFBURDEN:
			heading.setText(UI.getString("screen.equipment.column.beast"));
			list.setStyle("-fx-background-image: url('images/icon_beast_large.png'); -fx-background-position: bottom center; -fx-background-repeat: no-repeat;");
			break;
		case CONTAINER:
			heading.setText(UI.getString("screen.equipment.column.inventory"));
			list.setStyle("-fx-background-image: url('images/icon_inventory_large.png'); -fx-background-position: bottom center; -fx-background-repeat: no-repeat;");
			break;
		case SOMEWHEREELSE: heading.setText("?"); break;
		}
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		list.setMinWidth(350);
		list.setMaxHeight(Double.MAX_VALUE);

		heading.setMaxWidth(Double.MAX_VALUE);
		HBox lineInvent = new HBox(heading,btnDel, btnAdd);
		lineInvent.setSpacing(10);
		HBox.setHgrow(heading, Priority.ALWAYS);

		this.getChildren().addAll(lineInvent, list);
		VBox.setVgrow(list, Priority.ALWAYS);
		VBox.setMargin(list, new Insets(0, 0, 20, 0));
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnAdd.setOnAction(event -> add());
		btnDel.setOnAction(event -> remove(list.getSelectionModel().getSelectedItem()));

		list.setOnDragOver    (event -> dragOver(event));
		list.setOnDragDropped (event -> dragDropped(event));
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnDel.setDisable(n==null));
	}

	//-------------------------------------------------------------------
	private void add() {
		logger.debug("Add");
		if (onAdd!=null) {
			onAdd.accept(location);
		}
	}

	//-------------------------------------------------------------------
	private void edit(CarriedItem item) {
		logger.debug("Edit "+item);
		if (onEdit!=null) {
			onEdit.accept(item);
		}
	}

	//-------------------------------------------------------------------
	private void remove(CarriedItem item) {
		logger.debug("Remove "+item);
		if (onRemove!=null && item!=null) {
			onRemove.accept(item);
		}
	}

	//-------------------------------------------------------------------
	private void move(CarriedItem item, ItemLocationType newLocation) {
		logger.debug("Mve "+item+" to "+newLocation);
		if (onMove!=null && item!=null) {
			onMove.accept(item, newLocation);
		}
	}

	//-------------------------------------------------------------------
	public void setOnEdit(Consumer<CarriedItem> value) {
		this.onEdit = value;
	}

	//-------------------------------------------------------------------
	public void setOnRemove(Consumer<CarriedItem> value) {
		this.onRemove = value;
	}

	//-------------------------------------------------------------------
	public void setOnAdd(Consumer<ItemLocationType> value) {
		this.onAdd = value;
	}

	//-------------------------------------------------------------------
	public void setOnMove(BiConsumer<CarriedItem,ItemLocationType> value) {
		this.onMove = value;
	}

	//-------------------------------------------------------------------
	protected void dragDetected(MouseEvent event, ListView<CarriedItem> list, CarriedItemCell cell) {
		logger.debug("Drag started "+event.getSource());
		Node source = (Node) event.getSource();

		CarriedItem item = cell.getItem();
		String dragID = "";
		if (location==ItemLocationType.BODY)
			dragID="body";
		else if (location==ItemLocationType.BEASTOFBURDEN)
			dragID="beast";
		else if (location==ItemLocationType.CONTAINER)
			dragID="inventory";
		dragID += ":"+model.getItems().indexOf(item);

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(dragID);
         logger.debug("Drag "+dragID);
        db.setContent(content);

        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
         db.setDragView(snapshot);

        event.consume();
    }

	//-------------------------------------------------------------------
	private void dragOver(DragEvent event) {
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
	}

	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String idToMove = db.getString();
            logger.debug("drop "+idToMove+" at "+location);
        	StringTokenizer tok = new StringTokenizer(idToMove,":");

        	tok.nextToken();
        	int index  = Integer.parseInt(tok.nextToken());
        	CarriedItem toMove = model.getItems().get(index);

           	move(toMove, location);

            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
	}

	public List<CarriedItem> getItems() {
		// TODO Auto-generated method stub
		return list.getItems();
	}

}

class CarriedItemCell extends ListCell<CarriedItem> {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private GridPane grid;
	private ImageView image;
	private Label name;
	private Label extra;
	private Label statsWeapon;
	private Label statsLongRg;
	private Label statsArmor;
	private Label statsShield;
	private Label statsCommon;
	private Spinner<Integer> spCount;

	private transient CarriedItem data;

	//-------------------------------------------------------------------
	public CarriedItemCell() {
		image = new ImageView();
		image.setFitHeight(48);
		image.setFitWidth(48);
		name = new Label();
		name.getStyleClass().add("base");
		extra = new Label();
		extra.getStyleClass().add("caption");
		extra.setStyle("-fx-text-fill: textcolor-highlight-primary");
		spCount = new Spinner<>(1, 20, 1);
		spCount.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		spCount.setPrefWidth(80);
		statsWeapon = new Label();
//		statsWeapon.getStyleClass().add("caption");
		statsWeapon.setWrapText(true);
		statsLongRg = new Label();
//		statsLongRg.getStyleClass().add("caption");
		statsArmor = new Label();
//		statsArmor.getStyleClass().add("caption");
		statsShield = new Label();
//		statsShield.getStyleClass().add("caption");
		statsCommon = new Label();
//		statsCommon.getStyleClass().add("caption");

		grid = new GridPane();
		grid.setHgap(2);
		grid.add(image, 0, 0, 1, 7);
		grid.add(name , 1, 0);
		grid.add(extra, 1, 1);
		grid.add(statsWeapon, 1, 2);
		grid.add(statsLongRg, 1, 3);
		grid.add(statsArmor , 1, 4);
		grid.add(statsShield, 1, 5);
		grid.add(statsCommon, 1, 6);
		grid.add(spCount    , 2, 0, 1, 7);
		grid.getStyleClass().add("content");

		GridPane.setHgrow(name, Priority.ALWAYS);
		GridPane.setHgrow(extra, Priority.ALWAYS);


		spCount.valueProperty().addListener( (ov,o,n) -> data.setCount(n));
	}

	//--------------------------------------------------------------------
	static String getWeaponDamageString(int damage) {
		try {
			return new WeaponDamageConverter().write(damage);
		} catch (Exception e) {
			return "";
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CarriedItem item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;

		setGraphicTextGap(0);
		if (empty || item==null) {
			setText(null);
			setGraphic(null);
			return;
		} else {
			setGraphic(grid);
			fillGrid(item);
		}
	}

	//-------------------------------------------------------------------
	private void fillGrid(CarriedItem item) {
		name.setText(item.getName());
		image.setImage(null);
		spCount.getValueFactory().setValue(item.getCount());

		// Weapon
		grid.getChildren().remove(statsWeapon);
		if (item.getItem().isType(ItemType.WEAPON)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.WEAPON).getImage());
			Weapon weapon = item.getItem().getType(Weapon.class);

			String minAttr = weapon.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
					                                          .map(AttributeRequirement.class::cast)
					                                          .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
					                                          .collect(Collectors.joining(","));
			statsWeapon.setText(String.format("%s:%s  %s:%d\n%s:%s  %s:%s",
					ItemAttribute.DAMAGE.getShortName(), getWeaponDamageString(item.getDamage(ItemType.WEAPON)),
					ItemAttribute.SPEED.getShortName(), item.getSpeed(ItemType.WEAPON),
					ItemAttribute.ATTRIBUTES.getShortName(), item.getAttribute1(ItemType.WEAPON).getShortName()+"+"+item.getAttribute2(ItemType.WEAPON).getShortName(),
					(minAttr.length()>0)?ItemAttribute.MIN_ATTRIBUTES.getShortName():"", minAttr));
			grid.add(statsWeapon, 1, 2);
		}

		// Long Range Weapon
		grid.getChildren().remove(statsLongRg);
		if (item.getItem().isType(ItemType.LONG_RANGE_WEAPON)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.LONG_RANGE_WEAPON).getImage());
			LongRangeWeapon weapon = item.getItem().getType(LongRangeWeapon.class);

			String minAttr = weapon.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
								                     .map(AttributeRequirement.class::cast)
								                     .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
								                     .collect(Collectors.joining(","));
			statsLongRg.setText(String.format("%s:%s %s:%d\n%s:%s %s:%s %s:%s",
					ItemAttribute.DAMAGE.getShortName(), getWeaponDamageString(item.getDamage(ItemType.LONG_RANGE_WEAPON)),
					ItemAttribute.SPEED.getShortName(), item.getSpeed(ItemType.LONG_RANGE_WEAPON),
					ItemAttribute.RANGE.getShortName(), weapon.getRange(),
					ItemAttribute.ATTRIBUTES.getShortName(), item.getAttribute1(ItemType.LONG_RANGE_WEAPON).getShortName()+"+"+item.getAttribute2(ItemType.LONG_RANGE_WEAPON).getShortName(),
					ItemAttribute.MIN_ATTRIBUTES.getShortName(), minAttr));
			grid.add(statsLongRg, 1, 3);
		}

		// Armor
		grid.getChildren().remove(statsArmor);
		if (item.getItem().isType(ItemType.ARMOR)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.ARMOR).getImage());
			Armor armor = item.getItem().getType(Armor.class);

			String minAttr = armor.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
								                    .map(AttributeRequirement.class::cast)
								                    .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
								                    .collect(Collectors.joining(","));
			statsArmor.setText(String.format("%s:%d %s:%d %s:%d %s:%d %s:%s",
					ItemAttribute.DEFENSE.getShortName(), item.getDefense(ItemType.ARMOR),
					ItemAttribute.DAMAGE_REDUCTION.getShortName(), item.getDamageReduction(ItemType.ARMOR),
					ItemAttribute.HANDICAP.getShortName(), item.getHandicap(ItemType.ARMOR),
					ItemAttribute.TICK_MALUS.getShortName(), item.getTickMalus(ItemType.ARMOR),
					(minAttr.length()>0)?ItemAttribute.MIN_ATTRIBUTES.getShortName():"", minAttr));
			grid.add(statsArmor , 1, 4);
		}

		// Shield
		grid.getChildren().remove(statsShield);
		if (item.getItem().isType(ItemType.SHIELD)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.SHIELD).getImage());
			Shield shield = item.getItem().getType(Shield.class);

			String minAttr = shield.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
								                     .map(AttributeRequirement.class::cast)
								                     .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
								                     .collect(Collectors.joining(","));
			statsShield.setText(String.format("%s:%d %s:%d %s:%d %s:%d %s:%s",
					ItemAttribute.DEFENSE.getShortName(), item.getDefense(ItemType.SHIELD),
					ItemAttribute.DAMAGE_REDUCTION.getShortName(), item.getDamageReduction(ItemType.SHIELD),
					ItemAttribute.HANDICAP.getShortName(), item.getHandicap(ItemType.SHIELD),
					ItemAttribute.TICK_MALUS.getShortName(), item.getTickMalus(ItemType.SHIELD),
					(minAttr.length()>0)?ItemAttribute.MIN_ATTRIBUTES.getShortName():"", minAttr));
			grid.add(statsShield, 1, 5);
		}

		if (item.getItem().isType(ItemType.POTION)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.POTION).getImage());
		}
		if (item.getItem().isType(ItemType.CONTAINER)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.CONTAINER).getImage());
		}

		statsCommon.setText(String.format("%s: %d  %s: %d",
				ItemAttribute.LOAD.getShortName(), item.getItem().getLoad(),
				ItemAttribute.RIGIDITY.getShortName(), item.getItem().getRigidity()));

		grid.getChildren().remove(extra);
		Stream.Builder<String> buf = Stream.builder();
		for (EnhancementReference enRef : item.getEnhancements()) {
			if (enRef.getSpellValue()!=null) {
				buf.add(String.format(UI.getString("screen.enhancements.embedspell.cell"), enRef.getSpellValue().getSpell().getName()));
			} else if (enRef.getSkillSpecialization()!=null) {
				buf.add(enRef.getSkillSpecialization().getName()+"+1");
			} else {
				buf.add(enRef.getEnhancement().getName());
			}
		}
		for (PersonalizationReference enRef :item.getPersonalizations()) {
			buf.add(enRef.getName());
		}
		String textExtra = buf.build().collect(Collectors.joining(","));
		if (!textExtra.isEmpty()) {
			grid.add(extra, 1, 1);
			extra.setText(textExtra);
		}
	}
}