/**
 * 
 */
package org.prelle.splittermond.jfx.equip;
 
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.fluent.CommandBar;
import org.prelle.rpgframework.jfx.SettingsAndCommandBar;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.ViewMode;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan
 *
 */
public class EquipmentScreen extends ManagedScreen implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

//	private CharacterController control;
//	private ViewMode mode;
	private SpliMoCharacter model;

	private TextField lblSol;
	private TextField lblLun;
	private TextField lblTel;
	private CommandBar commands;
	private SettingsAndCommandBar firstLine;

	private EquipmentListBox bxInventory;
	private EquipmentListBox bxBody;
	private EquipmentListBox bxBeast;
	
	private SelectItemDialogScreen dia;
	private ItemLocationType location;
	private ScreenManagerProvider provider;

	//--------------------------------------------------------------------
	/**
	 */
	public EquipmentScreen(CharacterController control, ViewMode mode, ScreenManagerProvider provider) {
		model = control.getModel();
		this.provider = provider;
//		this.control = control;
//		this.mode    = mode;
		
		initComponents();
		initLayout();
		initInteractivity();
		GenerationEventDispatcher.addListener(this);
		
		refresh();
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setTitle(UI.getString("screen.equipment.title"));
		
		/*
		 * Exp & Co.
		 */
		Label hdSol = new Label(SpliMoCharGenConstants.RES.getString("label.solare")+": ");
		Label hdLun = new Label(SpliMoCharGenConstants.RES.getString("label.lunare")+": ");
		Label hdTel = new Label(SpliMoCharGenConstants.RES.getString("label.telare")+": ");
		lblSol = new TextField("0");
		lblLun = new TextField("0");
		lblTel = new TextField("0");
		lblSol.setPrefColumnCount(4);
		lblLun.setPrefColumnCount(4);
		lblTel.setPrefColumnCount(4);
		hdSol.getStyleClass().add("base");
		hdLun.getStyleClass().add("base");
		hdTel.getStyleClass().add("base");
		
		commands = new CommandBar();
		commands.getItems().add(new MenuItem("Drucken", new Label("\uD83D\uDDB6")));
		
		HBox expLine = new HBox(5);
		expLine.getChildren().addAll(hdSol, lblSol, hdLun, lblLun, hdTel, lblTel);
		expLine.setAlignment(Pos.CENTER_LEFT);
		HBox.setMargin(hdLun, new Insets(0,0,0,20));
		HBox.setMargin(hdTel, new Insets(0,0,0,20));
		expLine.getStyleClass().add("character-document-view-firstline");
		
		firstLine = new SettingsAndCommandBar();
		firstLine.setSettings(expLine);
//		firstLine.setCommandBar(commands);
		
		/*
		 * Lists
		 */
		bxInventory = new EquipmentListBox(ItemLocationType.CONTAINER, model);
		bxBody      = new EquipmentListBox(ItemLocationType.BODY, model);
		bxBeast     = new EquipmentListBox(ItemLocationType.BEASTOFBURDEN, model);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		FlowPane columns = new FlowPane(20,20);
		columns.getChildren().addAll(bxInventory, bxBody, bxBeast);
		columns.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		HBox.setHgrow(bxInventory, Priority.ALWAYS);
		HBox.setHgrow(bxBody, Priority.ALWAYS);
		HBox.setHgrow(bxBeast, Priority.SOMETIMES);
		VBox.setVgrow(columns, Priority.ALWAYS);
		
		ScrollPane scroll = new ScrollPane(columns);
		scroll.setFitToWidth(true);
		VBox.setVgrow(scroll, Priority.ALWAYS);
		
		VBox content = new VBox();
		content.setSpacing(20);
		content.getChildren().addAll(firstLine, scroll);
		content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		content.getStyleClass().add("equipment-screen");
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		bxInventory.setOnEdit( item -> edit(item));
		bxInventory.setOnAdd ( loc -> selectAndAddTo(loc));
		bxInventory.setOnRemove( item -> remove(item));
		bxInventory.setOnMove( (item, loc) -> move(item, loc));
		
		bxBody.setOnEdit( item -> edit(item));
		bxBody.setOnAdd ( loc -> selectAndAddTo(loc));
		bxBody.setOnRemove( item -> remove(item));
		bxBody.setOnMove( (item, loc) -> move(item, loc));
		
		bxBeast.setOnEdit( item -> edit(item));
		bxBeast.setOnAdd ( loc -> selectAndAddTo(loc));
		bxBeast.setOnRemove( item -> remove(item));
		bxBeast.setOnMove( (item, loc) -> move(item, loc));
		lblSol.textProperty().addListener( (ov,o,n) -> {
			int sol = Integer.parseInt(n);
			model.setTelare(sol*10000 + model.getTelare()%10000);
		});
		lblLun.textProperty().addListener( (ov,o,n) -> {
			int lun = Integer.parseInt(n);
			int sol = model.getTelare()/10000;
			int tel = model.getTelare()%100;
			logger.debug("sol="+sol+" lun="+lun+" tel="+tel);
			model.setTelare(sol*10000 + lun*100 + tel);
		});
		lblTel.textProperty().addListener( (ov,o,n) -> {
			int tel = Integer.parseInt(n);
			model.setTelare(tel + (model.getTelare()/100)*100);
		});
	}

	//-------------------------------------------------------------------
	private void selectAndAddTo(ItemLocationType location) {
		logger.debug("selectAndAddTo("+location+")");
		dia = new SelectItemDialogScreen(provider.getScreenManager());
		this.location = location;
		dia.startListenForEvents();
		CloseType close = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, dia.getTitle(), dia.getContent());
		dia.stopListenForEvents();
		if (close==CloseType.OK) {
			CarriedItem item = dia.getSelectedItem();
			item.setItemLocation(location);
			logger.info("Add "+item+" to "+location);
			model.addItem(item);
			if (location==ItemLocationType.BODY)
				EquipmentTools.equip(model, item);
			refresh();
		}
		
	}

	//-------------------------------------------------------------------
	private void edit(CarriedItem item) {
		logger.debug("Edit "+item);
		NewItemController control = new ItemLevellerAndGenerator(item, item.getArtifactQuality()+item.getItemQuality());
//		NewItemGeneratorPane pane = new NewItemGeneratorPane();
//		pane.setData(control);
//		pane.setScreenManager(getScreenManager());
//		
//		manager.showAlertAndCall(AlertType.NOTIFICATION, UI.getString("dialog.edit_relic.title"), pane);
		
		EditItemScreen newScreen = new EditItemScreen();
		newScreen.setData(model, control);
		EquipmentTools.unequip(model, item);
		provider.getScreenManager().show(newScreen);
//		Object obj = provider.getScreenManager().showAndWait(newScreen);
//		logger.info("obj = "+obj);
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
			CloseType closed = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, title, content);
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
		
		logger.info("Remove item: "+item);
		model.removeItem(item);
		switch (item.getLocation()) {
		case BODY:
			bxBody.getItems().remove(item);
			EquipmentTools.unequip(model, item);
			break;
		case BEASTOFBURDEN:
			bxBeast.getItems().remove(item);
			break;
		default:
			bxInventory.getItems().remove(item);
		}
	}

	//-------------------------------------------------------------------
	private void move(CarriedItem item, ItemLocationType newLoc) {
		// Remove from old position
		switch (item.getLocation()) {
		case BODY:
			bxBody.getItems().remove(item);
			EquipmentTools.unequip(model, item);
			break;
		case CONTAINER:
		case SOMEWHEREELSE:
			bxInventory.getItems().remove(item);
			break;
		case BEASTOFBURDEN:
			bxBeast.getItems().remove(item);
			break;
		}
		
		// Add add new location
		item.setItemLocation(newLoc);
		switch (newLoc) {
		case BODY:
			bxBody.getItems().add(item);
			EquipmentTools.equip(model, item);
			break;
		case CONTAINER:
		case SOMEWHEREELSE:
			bxInventory.getItems().add(item);
			break;
		case BEASTOFBURDEN:
			bxBeast.getItems().add(item);
			break;
		}
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
				logger.debug("BODY before "+bxBody.getItems());
				bxBody.getItems().add(item);
				EquipmentTools.equip(model, item);
				logger.debug("BODY now "+bxBody.getItems());
				break;
			case BEASTOFBURDEN:
				bxBeast.getItems().add(item);
				break;
			default:
				bxInventory.getItems().add(item);
				logger.debug("INV now "+bxInventory.getItems());
			}
		}
		logger.debug("done");
	}

	//-------------------------------------------------------------------
	private void refresh() {
		lblSol.setText(String.valueOf(model.getTelare()/10000));
		lblLun.setText(String.valueOf( (model.getTelare()/100)%100 ));
		lblTel.setText(String.valueOf(model.getTelare()%100));
		
		bxBody.getItems().clear();
		bxInventory.getItems().clear();
		bxBeast.getItems().clear();
		
		setTitle(model.getName()+"/"+UI.getString("screen.equipment.title"));
		
		for (CarriedItem item : model.getItems()) {
			switch (item.getLocation()) {
			case BODY:
				bxBody.getItems().add(item);
				break;
			case BEASTOFBURDEN:
				bxBeast.getItems().add(item);
				break;
			default:
				bxInventory.getItems().add(item);
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
			refresh();
			break;
		case MONEY_CHANGED:
			lblSol.setText(String.valueOf(model.getTelare()/10000));
			lblLun.setText(String.valueOf( (model.getTelare()/100)%100 ));
			lblTel.setText(String.valueOf(model.getTelare()%100));
			break;
		default:
			break;
		}		
	}

}
