/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.persist.WeaponDamageConverter;

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
public class WeaponBlock extends TableView<CarriedItem> implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacter model;

	private TableColumn<CarriedItem, String> nameCol;
	private TableColumn<CarriedItem, String> skillCol;
	private TableColumn<CarriedItem, String> att1Col;
	private TableColumn<CarriedItem, String> att2Col;
	private TableColumn<CarriedItem, Number> valueCol;
	private TableColumn<CarriedItem, Number> speedCol;
	private TableColumn<CarriedItem, String> dmgCol;
	private TableColumn<CarriedItem, String> featCol;
    private TableColumn<CarriedItem, ItemLocationType> carriageLocationCol;

    private static WeaponDamageConverter CONVERTER = new WeaponDamageConverter();
	
	//-------------------------------------------------------------------
	static String convertDamage(int damage) {
		try {
			return CONVERTER.write(damage);
		} catch (Exception e) {
			logger.error("Failed converting "+damage);
			return "FEHLER";
		}
	}
	
	//-------------------------------------------------------------------
	/**
	 */
	public WeaponBlock() {		
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
        setPlaceholder(new Text(uiResources.getString("placeholder.weapon")));
        nameCol  = new TableColumn<CarriedItem, String>(uiResources.getString("label.weapon"));
		skillCol = new TableColumn<CarriedItem, String>(uiResources.getString("label.combatskill.short"));
		att1Col  = new TableColumn<CarriedItem, String>(uiResources.getString("label.attribute1.short"));
		att2Col  = new TableColumn<CarriedItem, String>(uiResources.getString("label.attribute2.short"));
		valueCol = new TableColumn<CarriedItem, Number>(uiResources.getString("label.value"));
		speedCol = new TableColumn<CarriedItem, Number>(ItemAttribute.SPEED.getShortName());
		dmgCol   = new TableColumn<CarriedItem, String>(ItemAttribute.DAMAGE.getShortName());
		featCol  = new TableColumn<CarriedItem, String>(ItemAttribute.FEATURES.getName());
        carriageLocationCol = new TableColumn<>(uiResources.getString("label.carriage.location"));
		
		getColumns().addAll(
				nameCol,
				skillCol,
				att1Col,
				att2Col,
				valueCol,
				speedCol,
				dmgCol,
				featCol,
                carriageLocationCol
				);
		
		nameCol.setSortable(true);
		skillCol.setSortable(true);

        this.setMinHeight(175);

	}

	//-------------------------------------------------------------------

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				CarriedItem item = p.getValue();
				return new SimpleStringProperty(item.getItem().getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
				if (p.getValue().isType(ItemType.WEAPON)) {					
					return new SimpleIntegerProperty( SplitterTools.getWeaponValueFor(model, p.getValue(), ItemType.WEAPON) );
				} else {
					return new SimpleIntegerProperty( SplitterTools.getWeaponValueFor(model, p.getValue(), ItemType.LONG_RANGE_WEAPON) );
				}
			}
		});
		skillCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				if (p.getValue().isType(ItemType.WEAPON)) {					
					Weapon weapon = p.getValue().getItem().getType(Weapon.class);
					if (weapon==null) {
						logger.error("Item is WEAPON but has no weapon data: "+p.getValue().getUniqueId()+" // "+p.getValue().getItem());
						return new SimpleStringProperty("ERROR No Skill");
					}
					if (weapon.getSkill()==null) {
						logger.warn("Found weapon without a skill: "+p.getValue().getUniqueId()+" // "+p.getValue().getItem());
						return new SimpleStringProperty("ERROR No Skill");
					}
					return new SimpleStringProperty(weapon.getSkill().getName());
				} else {
					LongRangeWeapon weapon = p.getValue().getItem().getType(LongRangeWeapon.class);
					return new SimpleStringProperty(weapon.getSkill().getName());
				}
			}
		});
		att1Col.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				if (p.getValue().isType(ItemType.WEAPON)) {					 
					return new SimpleStringProperty(p.getValue().getAttribute1(ItemType.WEAPON).getShortName());
				} 
				return new SimpleStringProperty(p.getValue().getAttribute1(ItemType.LONG_RANGE_WEAPON).getShortName());				 
			}
		});
		att2Col.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				if (p.getValue().isType(ItemType.WEAPON)) {					 
					return new SimpleStringProperty(p.getValue().getAttribute2(ItemType.WEAPON).getShortName());
				}  
				return new SimpleStringProperty(p.getValue().getAttribute2(ItemType.LONG_RANGE_WEAPON).getShortName()); 
			}
		});
		speedCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<CarriedItem, Number> p) {
				if (p.getValue().isType(ItemType.WEAPON)) {
					CarriedItem item = p.getValue();
					return new SimpleIntegerProperty(SplitterTools.getWeaponSpeedFor(model, p.getValue(), ItemType.WEAPON));
				} else {
					CarriedItem item = p.getValue();
					return new SimpleIntegerProperty(SplitterTools.getWeaponSpeedFor(model, p.getValue(), ItemType.LONG_RANGE_WEAPON));
				}
			}

		});
		dmgCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {
				if (p.getValue().isType(ItemType.WEAPON)) {					
					CarriedItem item = p.getValue();
					return new SimpleStringProperty(convertDamage(item.getDamage(ItemType.WEAPON)));
				} else {
					CarriedItem item = p.getValue();
					return new SimpleStringProperty(convertDamage(item.getDamage(ItemType.LONG_RANGE_WEAPON)));
				}
			}
		});
		featCol.setCellValueFactory(new Callback<CellDataFeatures<CarriedItem, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CarriedItem, String> p) {  
					ItemType itemType = p.getValue().getItem().getType(Weapon.class) != null ? ItemType.WEAPON : ItemType.LONG_RANGE_WEAPON;
					return new SimpleStringProperty(p.getValue().getFeatures(itemType).stream()
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
                if (item.isType(ItemType.WEAPON) || item.isType(ItemType.LONG_RANGE_WEAPON))
                    toSet.add(item);
            }

            getItems().addAll(toSet);
            this.setPrefHeight(40 + getItems().size() * 30);
        }
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
			default:
		}
	}

}
