/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemType;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author rupp
 *
 */
public class GeneralEquipmentBlock extends TableView<CarriedItem> implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private static PropertyResourceBundle res = SplitterMondCore.getI18nResources();

	private SpliMoCharacter model;

	private TableColumn<CarriedItem, String> nameCol;
    private TableColumn<CarriedItem, ItemLocationType> carriageLocationCol;
    private TableColumn<CarriedItem, Number> loadCol;
    private TableColumn<CarriedItem, Number> robustCol;


	//-------------------------------------------------------------------
	/**
	 */
	public GeneralEquipmentBlock() {
		doInit();
		doValueFactories();
		doInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}
	

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		setEditable(true);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setPlaceholder(new Text(uiResources.getString("placeholder.equipment")));
        nameCol  = new TableColumn<CarriedItem, String>(uiResources.getString("label.name"));
		loadCol   = new TableColumn<CarriedItem, Number>(ItemAttribute.LOAD.getShortName());
        robustCol = new TableColumn<CarriedItem, Number>(ItemAttribute.RIGIDITY.getShortName());
        carriageLocationCol = new TableColumn<>(uiResources.getString("label.carriage.location"));

        getColumns().addAll(
				nameCol,
                carriageLocationCol,
                loadCol,
                robustCol
				);
		
		nameCol.setSortable(true);
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				CarriedItem item = p.getValue();
				return new SimpleStringProperty(item.getItem().getName());
			}
		});
		loadCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
				return new SimpleIntegerProperty( p.getValue().getDefense(ItemType.ARMOR)  );
			}
		});
        robustCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
            public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
                return new SimpleIntegerProperty( p.getValue().getDamageReduction(ItemType.ARMOR)  );
            }
        });




		final StringConverter<ItemLocationType> stringConverter = new StringConverter<ItemLocationType>() {
			@Override
			public String toString(ItemLocationType object) {
				if (object != null)
					return ((ItemLocationType) object).getName();
				return null;
			}
			@Override
			public ItemLocationType fromString(String string) {
				return null;
			}
		};

		carriageLocationCol.setEditable(true);
		carriageLocationCol.setCellFactory(new Callback<TableColumn<CarriedItem, ItemLocationType>, TableCell<CarriedItem, ItemLocationType>>() {
			@Override
			public TableCell<CarriedItem, ItemLocationType> call(TableColumn<CarriedItem, ItemLocationType> param) {
				ChoiceBoxTableCell<CarriedItem, ItemLocationType> box = new ChoiceBoxTableCell<CarriedItem, ItemLocationType>();
				box.setConverter(stringConverter);
				box.getItems().addAll(ItemLocationType.values());
				return box;
			}
		});

		carriageLocationCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<CarriedItem, ItemLocationType>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<CarriedItem, ItemLocationType> event) {
				ItemLocationType newValue = event.getNewValue();
				if (newValue != null) {
					CarriedItem selectedItem = getSelectionModel().getSelectedItem();
					selectedItem.setItemLocation(newValue);
				}
			}
		});

		carriageLocationCol.setCellValueFactory(
				new PropertyValueFactory<CarriedItem,ItemLocationType>("location")
		);
    }

	//-------------------------------------------------------------------
	private void doInteractivity() {
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		getItems().clear();
		
		List<CarriedItem> toSet = new ArrayList<CarriedItem>();
		for (CarriedItem item : model.getItems()) {
			if (item.isType(ItemType.OTHER))
				toSet.add(item);
		}
		
		getItems().addAll(toSet);	
		this.setPrefHeight(40+getItems().size()*30);
	}

	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter model) {
		this.model   = model;
		
		updateContent();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
//		switch (event.getType()) {
//		case ATTRIBUTE_CHANGED:
//			// Base values for skills changed
//			updateContent();
//			break;
//		case SKILL_CHANGED:
//			logger.debug("Skill changed: "+event.getKey());
//			if (((Skill)event.getKey()).getType()==SkillType.COMBAT)
//				updateContent();
//			break;
//		case ITEMS_CHANGED:
//			logger.debug("Items changed: "+event);
//			break;
//		}
	}

}
