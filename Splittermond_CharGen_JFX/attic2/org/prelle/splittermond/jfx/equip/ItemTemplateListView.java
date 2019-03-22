/**
 * 
 */
package org.prelle.splittermond.jfx.equip;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class ItemTemplateListView extends VBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	public interface DataProvider {
		
		public List<ItemTemplate> getItems(ItemType type);
	}
	
	private DataProvider provider;
	private ChoiceBox<ItemType> type;
	private ListView<ItemTemplate> items;
	
	//--------------------------------------------------------------------
	/**
	 */
	public ItemTemplateListView(DataProvider provider) {	
		this.provider = provider;
		
		initComponents();
		initLayout();
		initInteractivity();
		
		type.getSelectionModel().select(ItemType.WEAPON);
	}
	
	//--------------------------------------------------------------------
	private void initComponents() {
		type = new ChoiceBox<>();
		type.getItems().addAll(ItemType.values());
		type.setConverter(new StringConverter<ItemType>() {
			public ItemType fromString(String value) {	return null; }
			public String toString(ItemType value) {
				return value.getName();
			}
		});
		
		items = new ListView<ItemTemplate>();
		items.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        
        StringConverter<ItemTemplate> sv = new StringConverter<ItemTemplate>() {
        	public String toString(ItemTemplate object) {return object.getName();}
        	public ItemTemplate fromString(String string) {return null;	}
        };
        items.setCellFactory(new Callback<ListView<ItemTemplate>, ListCell<ItemTemplate>>() {
        	public ListCell<ItemTemplate> call(ListView<ItemTemplate> param) {
        		TextFieldListCell<ItemTemplate> cell = new TextFieldListCell<>();
        		cell.setConverter(sv);
        		return cell;
        	}
        });
}
	
	//--------------------------------------------------------------------
	private void initLayout() {
		HBox line1 = new HBox(5);
		line1.getChildren().addAll(type);
		
		items.setPlaceholder(new Label("nix"));
		items.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(items, Priority.ALWAYS);
		
		setSpacing(10);
		getChildren().addAll(line1, items);
	}
	
	//--------------------------------------------------------------------
	private void initInteractivity() {
		type.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> showItemType(n));

	}
	
	//--------------------------------------------------------------------
	public ReadOnlyObjectProperty<ItemType> getSelectedItemTypeProperty() {
		return type.getSelectionModel().selectedItemProperty();
	}
	
	//--------------------------------------------------------------------
	public ReadOnlyObjectProperty<ItemTemplate> getSelectedItemProperty() {
		return items.getSelectionModel().selectedItemProperty();
	}

	//-------------------------------------------------------------------
	private void showItemType(ItemType type) {
		logger.debug("showItemType("+type+")");
		items.getItems().clear();
		if (type!=null) {
			logger.debug("  contact provider "+provider.getClass());
			items.getItems().addAll(provider.getItems(type));
//			if (items.getItems().size() > 0) {
//				items.getSelectionModel().select(0);
//			}
		}
	}

	//-------------------------------------------------------------------
	public ObservableList<ItemTemplate> getItems() {
		return items.getItems();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (type.getValue()!=null)
			showItemType(type.getValue());
	}
	
	//--------------------------------------------------------------------
	public void setCellFactory(Callback<ListView<ItemTemplate>, ListCell<ItemTemplate>> value) {
		items.setCellFactory(value);
	}
	
	//--------------------------------------------------------------------
	public MultipleSelectionModel<ItemTemplate> getSelectionModel() {
		return items.getSelectionModel();
	}

}
