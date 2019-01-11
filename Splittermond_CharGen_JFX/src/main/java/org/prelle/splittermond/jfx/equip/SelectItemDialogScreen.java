/**
 * 
 */
package org.prelle.splittermond.jfx.equip;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.fluent.NodeWithTitleSkeleton;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplittermondCustomDataCore;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.Availability;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Complexity;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splittermond.jfx.equip.input.EnterItemTemplatePane;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
 

/**
 * @author prelle
 *
 */
public class SelectItemDialogScreen extends NodeWithTitleSkeleton {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private ScreenManager manager;
	
	private ItemTemplateListView selectPane;
    private CommonItemGeneratorMethods dataView;
    private ImageView imageView;

    private NewItemController control;
    private CarriedItem selectedItem;
    private int qualityLimit;
    private ResourceReference asRelic;

    private MenuItem btnAdd;
    private Button btnEdit;
    private Button btnDelete;
	private boolean developerMode;

    //-------------------------------------------------------------------
	public SelectItemDialogScreen(ScreenManager manager) {
		this.manager = manager;
		initComponents();
		initLayout();
		initInteractivity();
		
//		getNavigButtons().addAll(CloseType.OK, CloseType.CANCEL);
		setTitle(UI.getString("screen.selectequipment.title"));
//		setSkin(new ManagedScreenStructuredSkin(this));
		
		qualityLimit = Integer.MAX_VALUE;
		
		selectPane.getSelectionModel().clearSelection();
		Optional.of(selectPane.getItems())
		        .filter(l -> !l.isEmpty())
		        .ifPresent(l -> selectPane.getSelectionModel().select(0)); 
	}

    //-------------------------------------------------------------------
	public SelectItemDialogScreen(ScreenManager manager, int limit, ResourceReference asRelic) {
		this(manager);
		qualityLimit = limit;
		this.asRelic = asRelic;
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		selectPane = new ItemTemplateListView(new ItemTemplateListView.DataProvider() {
			public List<ItemTemplate> getItems(ItemType type) {
				List<ItemTemplate> ret = new ArrayList<ItemTemplate>(SplitterMondCore.getItems(type));
				ret.addAll(SplittermondCustomDataCore.getItems(type));
				Collections.sort(ret);
				return ret ;
			}
		});
		developerMode = SplittermondRules.isDeveloperMode();
//		developerMode = true;
		if (developerMode)
			dataView  = new NewItemGeneratorPane();
		else
			dataView  = new ItemGeneratorPane();
		dataView.setScreenManager(manager);
		imageView = new ImageView();

		// Add button
		FontIcon iconAdd = new FontIcon();
		iconAdd.addFontSymbol("\uE17E\uE109");
		btnAdd   = new MenuItem(UI.getString("button.customItem.add"), iconAdd);
//		btnAdd  .setTooltip(new Tooltip(UI.getString("button.customItem.add")));
		btnAdd  .setStyle("-fx-padding: 8px");
//		getStaticButtons().add(btnAdd);

		// Edit button
		FontIcon iconEdit = new FontIcon();
		iconEdit.addFontSymbol("\uE17E\uE104");
		btnEdit   = new Button(null, iconEdit);
		btnEdit  .setTooltip(new Tooltip(UI.getString("button.customItem.edit")));
		btnEdit  .setStyle("-fx-padding: 8px");

		// Delete button
		FontIcon iconDel = new FontIcon();
		iconDel.addFontSymbol("\uE17E\uE107");
		btnDelete= new Button(null, iconDel);
		btnDelete.setTooltip(new Tooltip(UI.getString("button.customItem.del")));
		btnDelete.setStyle("-fx-padding: 8px");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		ScrollPane flowScroll = new ScrollPane((Region)dataView);
		
		HBox hbox = new HBox(20);
		hbox.getChildren().addAll(selectPane, flowScroll);
		
		setContent(hbox);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		selectPane.getSelectedItemProperty().addListener( (ov,o,n) -> selectionChanged(n) );
		
		btnAdd.setOnAction(event -> customAddClicked());
		btnEdit.setOnAction(event -> customEditClicked());
		btnDelete.setOnAction(event -> customDeleteClicked());
	}

//	//-------------------------------------------------------------------
//	@Override
//	public void setScreenManager(ScreenManager manager) {
//		super.setScreenManager(manager);
//		dataView.setScreenManager(manager);
//	}

	//-------------------------------------------------------------------
	private void selectionChanged(ItemTemplate item) {
		logger.debug("Selected "+item);
		
		selectedItem = new CarriedItem();
		selectedItem.setResource(asRelic); // Mark as relic
		selectedItem.setItem(item);
		control = new ItemLevellerAndGenerator(selectedItem, qualityLimit);
		dataView.setData(control);
	}

	//-------------------------------------------------------------------
	public CarriedItem getSelectedItem() {
		return selectedItem;
	}

    //-------------------------------------------------------------------
	public void startListenForEvents() {
		GenerationEventDispatcher.addListener(dataView);
	}

    //-------------------------------------------------------------------
	public void stopListenForEvents() {
		GenerationEventDispatcher.removeListener(dataView);
	}

	//-------------------------------------------------------------------
	/**
	 * Add button to define custom items clicked
	 */
	private void customAddClicked() {
		logger.debug("customAddClicked");
		ItemTemplate newItem = new ItemTemplate();
		newItem.setAvailability(Availability.VILLAGE);
		newItem.setComplexity(Complexity.INEXPERIENCED);
		EnterItemTemplatePane pane = new EnterItemTemplatePane(newItem);
		CloseType type = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("screen.equipment.enter.title"), pane);
		logger.debug("exited with "+type);
		
		if (type==CloseType.OK) {
			newItem.setCustomName(pane.getName());
			logger.debug("To add: "+newItem);
			logger.debug("To add2: "+newItem.getClass());
			logger.debug("To add3: "+newItem.getTypeData());
			
			SplittermondCustomDataCore.addItem(newItem);
			try {
				SplittermondCustomDataCore.saveEquipment();
			} catch (IOException e) {
				StringWriter out = new StringWriter();
				e.printStackTrace(new PrintWriter(out));
				StringBuffer mess = new StringBuffer(UI.getString("screen.equipment.enter.error.mess"));
				mess.append("\n"+out);
				manager.showAlertAndCall(AlertType.ERROR, UI.getString("screen.equipment.enter.error.title"), mess.toString());
			}
			
			selectPane.getItems().add(newItem);
			selectPane.refresh();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Add button to define custom items clicked
	 */
	private void customEditClicked() {
		ItemTemplate newItem = selectPane.getSelectedItemProperty().get();
		EnterItemTemplatePane pane = new EnterItemTemplatePane(newItem);
		CloseType type = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("screen.equipment.enter.title"), pane);
		logger.debug("exited with "+type);
		
		if (type==CloseType.OK) {
			try {
				SplittermondCustomDataCore.saveEquipment();
				dataView.updateContent();
			} catch (IOException e) {
				logger.error("Failed saving custom items",e);
				StringWriter out = new StringWriter();
				e.printStackTrace(new PrintWriter(out));
				StringBuffer mess = new StringBuffer(UI.getString("screen.equipment.enter.error.mess"));
				mess.append("\n"+out);
				manager.showAlertAndCall(AlertType.ERROR, UI.getString("screen.equipment.enter.error.title"), mess.toString());
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Add button to define custom items clicked
	 */
	private void customDeleteClicked() {
		ItemTemplate newItem = selectPane.getSelectedItemProperty().get();
		Label desc = new Label(String.format(UI.getString("screen.equipment.delete.desc"), newItem.getName()));
		CloseType type = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("screen.equipment.delete.title"), desc);
		logger.debug("exited with "+type);

		if (type==CloseType.OK) {
			SplittermondCustomDataCore.removeItem(newItem);
			try {
				SplittermondCustomDataCore.saveEquipment();
			} catch (IOException e) {
				StringWriter out = new StringWriter();
				e.printStackTrace(new PrintWriter(out));
				StringBuffer mess = new StringBuffer(UI.getString("screen.equipment.enter.error.mess"));
				mess.append("\n"+out);
				manager.showAlertAndCall(AlertType.ERROR, UI.getString("screen.equipment.enter.error.title"), mess.toString());
			}
			dataView.updateContent();
			selectPane.refresh();
		}
	}

}