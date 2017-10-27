/**
 * 
 */
package org.prelle.splittermond.jfx.equip;
 
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.ViewMode;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
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
import org.prelle.splimo.jaxb.WeaponDamageAdapter;
import org.prelle.splimo.requirements.AttributeRequirement; 
 
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
 * @author Stefan
 *
 */
public class EquipmentScreen extends ManagedScreen implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

//	private CharacterController control;
//	private ViewMode mode;
	private SpliMoCharacter model;

	private EquipmentSidePane sidebar;
	private ImageView silhouette;
	private ListView<CarriedItem> inventory;
	private ListView<CarriedItem> body;
	private ListView<CarriedItem> beast;
	private Button btnInventory;
	private Button btnBody;
	private Button btnBeast;
	private Button btnDelInventory;
	private Button btnDelBody;
	private Button btnDelBeast;
	
	private SelectItemDialogScreen dia;
	private ItemLocationType location;
	
	//--------------------------------------------------------------------
	/**
	 */
	public EquipmentScreen(CharacterController control, ViewMode mode) {
//		this.control = control;
//		this.mode    = mode;
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("screen.equipment.title"));
		
		sidebar = new EquipmentSidePane();
		
		Image img = new Image(ClassLoader.getSystemResourceAsStream("images/silouette.gif"));		
		silhouette = new ImageView(img);
		silhouette.setStyle("-fx-opacity: 0.5");
		
		inventory = new ListView<CarriedItem>();
		body      = new ListView<CarriedItem>();
		beast     = new ListView<CarriedItem>();
		btnInventory = new Button(null, new FontIcon("\uE17E\uE109"));
		btnBody      = new Button(null, new FontIcon("\uE17E\uE109"));
		btnBeast     = new Button(null, new FontIcon("\uE17E\uE109"));
		btnDelInventory = new Button(null, new FontIcon("\uE17E\uE107"));
		btnDelBody      = new Button(null, new FontIcon("\uE17E\uE107"));
		btnDelBeast     = new Button(null, new FontIcon("\uE17E\uE107"));
		btnDelInventory.setDisable(true);
		btnDelBody.setDisable(true);
		btnDelBeast.setDisable(true);
		
		inventory.getStyleClass().add("bordered");
		inventory.setMinWidth(350);
		inventory.setCellFactory(new Callback<ListView<CarriedItem>, ListCell<CarriedItem>>() {
			public ListCell<CarriedItem> call(ListView<CarriedItem> param) {
				CarriedItemCell cell = new CarriedItemCell();
				cell.setOnDragDetected(event -> dragDetected(event, inventory, cell));
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
		inventory.setOnDragOver    (event -> dragOver(event));
		inventory.setOnDragDropped (event -> dragDropped(event));
		
		body.getStyleClass().add("bordered");
		body.setMinWidth(350);
		body.setCellFactory(new Callback<ListView<CarriedItem>, ListCell<CarriedItem>>() {
			public ListCell<CarriedItem> call(ListView<CarriedItem> param) {
				CarriedItemCell cell = new CarriedItemCell();
				cell.setOnDragDetected(event -> dragDetected(event, body, cell));
				cell.setOnMouseClicked(event -> {if (event.getClickCount()==2) edit(cell.getItem());});
				return cell;
			}
		});
		body.setOnDragOver    (event -> dragOver(event));
		body.setOnDragDropped (event -> dragDropped(event));
		
		beast.getStyleClass().add("bordered");
		beast.setMinWidth(350);
		beast.setCellFactory(new Callback<ListView<CarriedItem>, ListCell<CarriedItem>>() {
			public ListCell<CarriedItem> call(ListView<CarriedItem> param) {
				CarriedItemCell cell = new CarriedItemCell();
				cell.setOnDragDetected(event -> dragDetected(event, beast, cell));
				cell.setOnMouseClicked(event -> {if (event.getClickCount()==2) edit(cell.getItem());});
				return cell;
			}
		});
		beast.setOnDragOver    (event -> dragOver(event));
		beast.setOnDragDropped (event -> dragDropped(event));
	}

	//-------------------------------------------------------------------
	protected void dragDetected(MouseEvent event, ListView<CarriedItem> list, CarriedItemCell cell) {
		logger.debug("Drag started "+event.getSource());
		Node source = (Node) event.getSource();
		
		CarriedItem item = cell.getItem();
		String dragID = "";
		if (list==body)
			dragID="body";
		else if (list==beast)
			dragID="beast";
		else if (list==inventory)
			dragID="inventory";
		dragID += ":"+list.getItems().indexOf(item);
		
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
	@SuppressWarnings("unchecked")
	private void dragDropped(DragEvent event) {
		Node target = (Node) event.getSource();

		Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String idToMove = db.getString();
        	CarriedItem toMove = null;
        	StringTokenizer tok = new StringTokenizer(idToMove,":");
        	String key = tok.nextToken();
        	// What is the new location
        	ItemLocationType newLocation = null;
        	if (target==body)
        		newLocation = ItemLocationType.BODY;
        	else if (target==inventory)
        		newLocation = ItemLocationType.CONTAINER;
        	if (target==beast)
        		newLocation = ItemLocationType.BEASTOFBURDEN;
        	
        	int index  = Integer.parseInt(tok.nextToken());
        	if (key.equals("inventory")) {
        		toMove = inventory.getItems().remove(index);
        	} else if (key.equals("body")) {
        		toMove = body.getItems().remove(index);
        	} else if (key.equals("beast")) {
        		toMove = beast.getItems().remove(index);
        	}
        	ItemLocationType oldLocation = toMove.getLocation();
        	
        	logger.debug("  equip="+(newLocation==ItemLocationType.BODY)+"  unequip="+(oldLocation==ItemLocationType.BODY));
        	
    		((ListView<CarriedItem>)target).getItems().add(toMove);
    		
    		if (oldLocation==ItemLocationType.BODY)
    			EquipmentTools.unequip(model, toMove);
    		if (newLocation==ItemLocationType.BODY)
    			EquipmentTools.equip(model, toMove);
    		toMove.setItemLocation(newLocation);
        		
            success = true;
        }
        /* let the source know whether the string was successfully 
         * transferred and used */
        event.setDropCompleted(success);
        
        event.consume();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");
		VBox.setMargin(sidebar, new Insets(0, 0, 20, 0));
		
		/* 
		 * Column Inventory
		 */
		Label lblInvent = new Label(UI.getString("screen.equipment.column.inventory"));
		lblInvent.getStyleClass().add("text-subheader");
		lblInvent.setMaxWidth(Double.MAX_VALUE);
		HBox lineInvent = new HBox(lblInvent,btnDelInventory, btnInventory);
		lineInvent.setSpacing(10);
		HBox.setHgrow(lblInvent, Priority.ALWAYS);
		VBox colInvent = new VBox(20);
		colInvent.getChildren().addAll(lineInvent, inventory);
		VBox.setVgrow(inventory, Priority.ALWAYS);
		VBox.setMargin(inventory, new Insets(0, 0, 20, 0));
		inventory.setMaxHeight(Double.MAX_VALUE);
		inventory.setStyle("-fx-background-image: url('images/icon_inventory_large.png'); -fx-background-position: bottom center; -fx-background-repeat: no-repeat;");
		
		/* 
		 * Column Body
		 */
		Label lblBody = new Label(UI.getString("screen.equipment.column.body"));
		lblBody.getStyleClass().add("text-subheader");
		lblBody.setMaxWidth(Double.MAX_VALUE);
		HBox lineBody = new HBox(lblBody, btnDelBody, btnBody);
		lineBody.setSpacing(10);
		HBox.setHgrow(lblBody, Priority.ALWAYS);
		VBox colBody = new VBox(20);
		colBody.getChildren().addAll(lineBody, body);
		VBox.setVgrow(body, Priority.ALWAYS);
		VBox.setMargin(body, new Insets(0, 0, 20, 0));
		body.setMaxHeight(Double.MAX_VALUE);
		body.setStyle("-fx-background-image: url('images/icon_body_large.png'); -fx-background-position: bottom center; -fx-background-repeat: no-repeat;");
		
		/* 
		 * Column Beast of burden
		 */
		Label lblBeast = new Label(UI.getString("screen.equipment.column.beast"));
		lblBeast.getStyleClass().add("text-subheader");
		lblBeast.setMaxWidth(Double.MAX_VALUE);
		HBox lineBeast = new HBox(lblBeast, btnDelBeast, btnBeast);
		lineBeast.setSpacing(10);
		HBox.setHgrow(lblBeast, Priority.ALWAYS);
		VBox colBeast = new VBox(20);
		colBeast.getChildren().addAll(lineBeast, beast);
		VBox.setVgrow(beast, Priority.ALWAYS);
		VBox.setMargin(beast, new Insets(0, 0, 20, 0));
		beast.setMaxHeight(Double.MAX_VALUE);
		beast.setStyle("-fx-background-image: url('images/icon_beast_large.png'); -fx-background-position: bottom center; -fx-background-repeat: no-repeat;");
		
		HBox columns = new HBox(20);
		columns.getChildren().addAll(colInvent, colBody, colBeast);
		columns.setMaxHeight(Double.MAX_VALUE);
		ScrollPane scroll = new ScrollPane(columns);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		scroll.setMaxHeight(Double.MAX_VALUE);
		
		HBox content = new HBox();
		content.setSpacing(20);
		content.getChildren().addAll(sidebar, scroll);
		content.setMaxHeight(Double.MAX_VALUE);
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnInventory.setOnAction(event -> selectAndAddTo(ItemLocationType.CONTAINER));
		btnBody     .setOnAction(event -> selectAndAddTo(ItemLocationType.BODY));
		btnBeast    .setOnAction(event -> selectAndAddTo(ItemLocationType.BEASTOFBURDEN));
		
		btnDelInventory.setOnAction(event -> remove(inventory.getSelectionModel().getSelectedItem()));
		btnDelBody     .setOnAction(event -> remove(body .getSelectionModel().getSelectedItem()));
		btnDelBeast    .setOnAction(event -> remove(beast.getSelectionModel().getSelectedItem()));
		
		inventory.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnDelInventory.setDisable(n==null));
		body     .getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnDelBody     .setDisable(n==null));
		beast    .getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnDelBeast    .setDisable(n==null));
	}

	//-------------------------------------------------------------------
	private void selectAndAddTo(ItemLocationType location) {
		dia = new SelectItemDialogScreen();
		this.location = location;
		dia.startListenForEvents();
		manager.show(dia);
	}

	//-------------------------------------------------------------------
	private void edit(CarriedItem item) {
		logger.debug("Edit "+item);
		NewItemController control = new ItemLevellerAndGenerator(item, item.getArtifactQuality()+item.getItemQuality());
//		ItemGeneratorPane pane = new ItemGeneratorPane();
//		pane.setData(control);
//		pane.setScreenManager(getScreenManager());
//		
//		manager.showAlertAndCall(AlertType.NOTIFICATION, UI.getString("dialog.edit_relic.title"), pane);
		
		EditItemScreen newScreen = new EditItemScreen();
		newScreen.setData(model, control);
		EquipmentTools.unequip(model, item);
		Object obj = manager.showAndWait(newScreen);
		logger.info("obj = "+obj);
		EquipmentTools.equip(model, item);
	}

	//-------------------------------------------------------------------
	private void remove(CarriedItem item) {
		if (item.isRelic()) {
			logger.debug("Delete relic resource "+item);
			String title = UI.getString("dialog.delete_relic.title");
			Label msg = new Label(String.format(UI.getString("dialog.delete_relic.message"), item.getName()));
			ChoiceBox<String> choice = new ChoiceBox<>();
			choice.getItems().add(UI.getString("dialog.delete_relic.optionDelete"));
			choice.getItems().add(UI.getString("dialog.delete_relic.optionUnlink"));
			choice.getSelectionModel().select(1);
			VBox content = new VBox(20);
			content.getChildren().addAll(msg, choice);
			CloseType closed = manager.showAlertAndCall(AlertType.QUESTION, title, content);
			if (closed==CloseType.CANCEL)
				return;
			
			if (choice.getSelectionModel().getSelectedIndex()==0) {
				// Delete resource
				logger.debug("  also delete resource");
				model.removeResource(item.getResource());
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.RESOURCE_REMOVED, item.getResource()));
			} else {
				logger.debug("  keep resource");
				item.getResource().setIdReference(null);
				item.getResource().setDescription(null);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.RESOURCE_CHANGED, item.getResource()));
			}
		}
		
		model.removeItem(item);
		switch (item.getLocation()) {
		case BODY:
			body.getItems().remove(item);
			EquipmentTools.unequip(model, item);
			break;
		case BEASTOFBURDEN:
			beast.getItems().remove(item);
			break;
		default:
			inventory.getItems().remove(item);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.CloseType)
	 */
	@Override
	public boolean close(CloseType closeType) {
		logger.debug("fireItemChange");
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.ITEM_CHANGED, model)
				);
		GenerationEventDispatcher.removeListener(this);
		return true;
	}
	
	//-------------------------------------------------------------------
	public void childClosed(ManagedScreen child, CloseType result) {
		if (dia==null)
			return;
		dia.stopListenForEvents();
		
		logger.debug("Result was "+result);
		if (result==CloseType.OK) {
			CarriedItem item = dia.getSelectedItem();
			item.setItemLocation(location);
			logger.debug("Add item: "+item+" to "+location);
			model.addItem(item);
//			setData(model);
			switch (item.getLocation()) {
			case BODY:
				logger.debug("BODY before "+body.getItems());
				body.getItems().add(item);
				EquipmentTools.equip(model, item);
				logger.debug("BODY now "+body.getItems());
				break;
			case BEASTOFBURDEN:
				beast.getItems().add(item);
				break;
			default:
				inventory.getItems().add(item);
				logger.debug("INV now "+inventory.getItems());
			}
		}
		logger.debug("done");
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		
		sidebar.setData(model);
		
		body.getItems().clear();
		inventory.getItems().clear();
		beast.getItems().clear();
		
		setTitle(model.getName()+"/"+UI.getString("screen.equipment.title"));
		
		for (CarriedItem item : model.getItems()) {
			switch (item.getLocation()) {
			case BODY:
				body.getItems().add(item);
				break;
			case BEASTOFBURDEN:
				beast.getItems().add(item);
				break;
			default:
				inventory.getItems().add(item);
			}
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case ITEM_CHANGED:
			body.getItems().clear();
			inventory.getItems().clear();
			beast.getItems().clear();
			
			setTitle(model.getName()+"/"+UI.getString("screen.equipment.title"));
			
			for (CarriedItem item : model.getItems()) {
				switch (item.getLocation()) {
				case BODY:
					body.getItems().add(item);
					break;
				case BEASTOFBURDEN:
					beast.getItems().add(item);
					break;
				default:
					inventory.getItems().add(item);
				}
			}
			break;
		case MONEY_CHANGED:
			sidebar.refresh();
			break;
		default:
			break;
		}		
	}

}

class CarriedItemCell extends ListCell<CarriedItem> {
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private GridPane grid;
	private ImageView image;
	private Label name;
	private Label extra;
	private Label statsWeapon;
	private Label statsLongRg;
	private Label statsArmor;
	private Label statsShield;
	private Label statsCommon;
	
	//-------------------------------------------------------------------
	public CarriedItemCell() {
		image = new ImageView();
		image.setFitHeight(48);
		image.setFitWidth(48);
		name = new Label();
		name.getStyleClass().add("text-small-subheader");
		extra = new Label();
		extra.getStyleClass().add("text-secondary-info");
		extra.setStyle("-fx-text-fill: textcolor-highlight-primary");
		statsWeapon = new Label();
		statsWeapon.getStyleClass().add("text-secondary-info");
		statsWeapon.setWrapText(true);
		statsLongRg = new Label();
		statsLongRg.getStyleClass().add("text-secondary-info");
		statsArmor = new Label();
		statsArmor.getStyleClass().add("text-secondary-info");
		statsShield = new Label();
		statsShield.getStyleClass().add("text-secondary-info");
		statsCommon = new Label();
		statsCommon.getStyleClass().add("text-tertiary-info");
		
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
		grid.getStyleClass().add("content");
	}

	//--------------------------------------------------------------------
	static String getWeaponDamageString(int damage) {
		try {
			return new WeaponDamageAdapter().marshal(damage);
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