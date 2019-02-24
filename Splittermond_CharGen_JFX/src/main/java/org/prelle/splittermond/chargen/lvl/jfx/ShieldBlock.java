/**
 * 
 */
package org.prelle.splittermond.chargen.lvl.jfx;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.Shield;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

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
 * @author prelle
 *
 */
public class ShieldBlock extends TableView<CarriedItem> implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacter model;

	private TableColumn<CarriedItem, String> nameCol;
	private TableColumn<CarriedItem, Number> abwCol;
	private TableColumn<CarriedItem, String> skillCol;
	private TableColumn<CarriedItem, Number> knockCol;
	private TableColumn<CarriedItem, String> knockDamCol;
	private TableColumn<CarriedItem, Number> defCol;
	private TableColumn<CarriedItem, Number> hcCol;
	private TableColumn<CarriedItem, Number> tickCol;
	private TableColumn<CarriedItem, String> featCol;
    private TableColumn<CarriedItem, ItemLocationType> carriageLocationCol;
	
	//-------------------------------------------------------------------
	/**
	 */
	public ShieldBlock() {		
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
        setPlaceholder(new Text(uiResources.getString("placeholder.shield")));
        nameCol  = new TableColumn<CarriedItem, String>(uiResources.getString("label.shieldparry"));
		abwCol = new TableColumn<CarriedItem, Number>(uiResources.getString("label.abwehr"));
		skillCol  = new TableColumn<CarriedItem, String>(uiResources.getString("label.combatskill.short"));
		knockCol  = new TableColumn<>(uiResources.getString("label.knock"));
		knockDamCol = new TableColumn<CarriedItem, String>(uiResources.getString("label.knockdamage.short"));
		defCol    = new TableColumn<CarriedItem, Number>(ItemAttribute.DEFENSE.getShortName());
		hcCol     = new TableColumn<>(ItemAttribute.HANDICAP.getShortName());
		tickCol   = new TableColumn<>(ItemAttribute.TICK_MALUS.getShortName());
		featCol   = new TableColumn<CarriedItem, String>(ItemAttribute.FEATURES.getName());
        carriageLocationCol = new TableColumn<>(uiResources.getString("label.carriage.location"));
		
		getColumns().addAll(
				nameCol,
				skillCol,
                abwCol,
                knockCol,
                knockDamCol,
				defCol,
                hcCol,
				tickCol,
				featCol,
				carriageLocationCol
				);
		
		nameCol.setSortable(true);
		skillCol.setSortable(true);

        this.setMinHeight(150);
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				CarriedItem item = p.getValue();
				return new SimpleStringProperty(item.getItem().getName());
			}
		});

		skillCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				SkillValue skill = model.getShieldSkillValue();
				if (skill != null)
					return new SimpleStringProperty(skill.getSkill().getName());
				return new SimpleStringProperty("?");
			}
		});

		abwCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
				SkillValue skill = model.getShieldSkillValue();
				int str = model.getAttribute(Attribute.STRENGTH).getValue();
				int INT = model.getAttribute(Attribute.INTUITION).getValue();

				return new SimpleIntegerProperty(skill.getValue()+str+INT);
			}
		});

		knockCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
				SkillValue skill = model.getShieldSkillValue();
				int str = model.getAttribute(Attribute.STRENGTH).getValue();
				int agi = model.getAttribute(Attribute.AGILITY).getValue();
				return new SimpleIntegerProperty(skill.getValue()+str+agi);
			}
		});

		knockDamCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				return new SimpleStringProperty("1W6+1");
			}
		});

		defCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {

				Shield shield = p.getValue().getItem().getType(Shield.class);
				return new SimpleIntegerProperty((shield!=null)?shield.getDefense():0);
			}
		});

		hcCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
				return new SimpleIntegerProperty( p.getValue().getHandicap(ItemType.SHIELD)  );
			}
		});

		tickCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
				return new SimpleIntegerProperty(p.getValue().getTickMalus(ItemType.SHIELD));
			}
		});
		featCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) { 
				return new SimpleStringProperty(p.getValue().getFeatures(ItemType.SHIELD).stream()
			                        						.map(Feature::getName)
			                        					    .collect(Collectors.joining(", ")));
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
					if (ItemLocationType.BODY.equals(newValue)){
						EquipmentTools.equip(model, selectedItem);
                    } else
                    if (ItemLocationType.BODY==event.getOldValue()){
                    	EquipmentTools.unequip(model, selectedItem);
					}
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

        if (model != null) {
            getItems().clear();
            List<CarriedItem> toSet = new ArrayList<CarriedItem>();
            for (CarriedItem item : model.getItems()) {
                if (item.isType(ItemType.SHIELD))
                    toSet.add(item);
            }

            getItems().addAll(toSet);
            this.setPrefHeight(40 + getItems().size() * 30);
        }
	}

	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter model) {
		this.model   = model;
		
		logger.debug("Set content");
		updateContent();
	}



	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case ITEM_CHANGED:
		case ATTRIBUTE_CHANGED:
			// Base values for skills changed
			updateContent();
			break;
		case SKILL_CHANGED:
			if (((Skill)event.getKey()).getType()==SkillType.COMBAT)
				updateContent();
			break;
//		case ITEMS_CHANGED:
//			logger.debug("Items changed: "+event);
//			break;
		}
	}

}
