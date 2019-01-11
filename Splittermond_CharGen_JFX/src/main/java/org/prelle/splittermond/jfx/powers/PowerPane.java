package org.prelle.splittermond.jfx.powers;

import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class PowerPane extends VBox implements GenerationEventListener, EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private PowerController control;
	private SpliMoCharacter model;
	private boolean withNotes;

	private ChoiceBox<Power> addChoice;
	private Button add;
	private TableView<PowerReference> table;
	
	private TableColumn<PowerReference, String> nameCol;
	private TableColumn<PowerReference, Object> valueCol; // Integer or Boolean
	private TableColumn<PowerReference, String> notesCol;
	
	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public PowerPane(PowerController ctrl, boolean withNotes) {
		this.control = ctrl;
		this.withNotes = withNotes;

		GenerationEventDispatcher.addListener(this);
		doInit();
		doValueFactories();
		initInteractivity();
	}
	
	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		
		updateContent();
	}

	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		addChoice = new ChoiceBox<>();
		addChoice.setMinWidth(200);
		addChoice.getItems().addAll(control.getAvailablePowers());
		addChoice.setConverter(new StringConverter<Power>(){

			@Override
			public Power fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toString(Power power) {
				return power.getName()+" ("+power.getCost()+")";
			}});
		add = new Button(UI.getString("button.add"));
		HBox addLine = new HBox(5);
		addLine.getChildren().addAll(addChoice,add);
		HBox.setHgrow(addChoice, Priority.ALWAYS);
		
		table = new TableView<PowerReference>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Text(UI.getString("placeholder.power")));

		nameCol = new TableColumn<PowerReference, String>(UI.getString("label.power"));
		valueCol = new TableColumn<PowerReference, Object>(UI.getString("label.value"));
		notesCol = new TableColumn<PowerReference, String>(UI.getString("label.notes"));
		nameCol.setMinWidth(220);
		valueCol.setMinWidth(140);
		table.getColumns().addAll(nameCol, valueCol);
		if (withNotes) 
			table.getColumns().add(notesCol);

		notesCol.setMaxWidth(Double.MAX_VALUE);

		// Let table scroll
		ScrollPane scroll = new ScrollPane(table);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		
		// Add to layout
		super.getChildren().addAll(addLine, scroll);
		super.setSpacing(5);
		VBox.setVgrow(table, Priority.SOMETIMES);
		
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<PowerReference, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<PowerReference, String> p) {
				PowerReference item = p.getValue();
				return new SimpleStringProperty(item.getPower().getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<PowerReference, Object>, ObservableValue<Object>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public ObservableValue<Object> call(CellDataFeatures<PowerReference, Object> p) {
				PowerReference ref = p.getValue();
				switch (ref.getPower().getSelectable()) {
				case ALWAYS:
				case GENERATION:
					return new SimpleObjectProperty(true);
				default:
				}
				return new SimpleObjectProperty( p.getValue().getCount() );
			}
		});
		notesCol.setCellValueFactory(new Callback<CellDataFeatures<PowerReference, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<PowerReference, String> p) {
				PowerReference item = p.getValue();
				return new SimpleStringProperty(item.getPower().getDescription());
			}
		});

		valueCol.setCellFactory(new Callback<TableColumn<PowerReference,Object>, TableCell<PowerReference,Object>>() {
            public TableCell<PowerReference,Object> call(TableColumn<PowerReference,Object> p) {
                 return new PowerEditingCell(control);
             }
         });
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		add.setOnAction(this);
		add.setDisable(true);
		
		addChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Power>() {
			public void changed(ObservableValue<? extends Power> item,
					Power arg1, Power newVal) {
				add.setDisable(newVal==null);
			}
		});
	}

	//-------------------------------------------------------------------
	public TableView<PowerReference> getTable() {
		return table;
	}

	//-------------------------------------------------------------------
	public ChoiceBox<Power> getChoiceBox() {
		return addChoice;
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		table.getItems().clear();
		table.getItems().addAll(this.model.getPowers());		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings({ "incomplete-switch", "unchecked" })
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		PowerReference ref = null;;
		switch (event.getType()) {
		case POWER_ADDED:
			ref = (PowerReference) event.getKey();
			updateContent();
//			table.getItems().add(ref);
////			Collections.sort(table.getItems());
//			addChoice.getItems().clear();
//			addChoice.getItems().addAll(control.getAvailablePowers());
			break;
		case POWER_REMOVED:
			ref = (PowerReference) event.getKey();
			table.getItems().remove(ref);
			addChoice.getItems().clear();
			addChoice.getItems().addAll(control.getAvailablePowers());
			break;
		case POWER_CHANGED:
			logger.debug("Powers changed - refresh view");
			updateContent();
			break;
		case POWER_AVAILABLE_ADDED:
			logger.debug("Add available powers: "+event.getKey());
			addChoice.getItems().addAll((List<Power>) event.getKey());
			Collections.sort(addChoice.getItems());
			add.setDisable(addChoice.getSelectionModel().getSelectedIndex()==-1);
			break;
		case POWER_AVAILABLE_REMOVED:
			logger.debug("Remove available powers: "+event.getKey());
			List<Power> toRem = (List<Power>) event.getKey();
			addChoice.getItems().removeAll(toRem);
			addChoice.requestFocus();
			add.setDisable(addChoice.getSelectionModel().getSelectedIndex()==-1);
			break;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource()==add) {
			Power res = addChoice.getSelectionModel().getSelectedItem();
			control.select(res);
		}
	}

}

class PowerEditingCell extends TableCell<PowerReference, Object> {
	
	private Spinner<Integer> box;
	private CheckBox checkBox;
	private PowerController charGen;
	private PowerReference data;
	
	//-------------------------------------------------------------------
	public PowerEditingCell(PowerController charGen) {
		this.charGen = charGen;
		checkBox = new CheckBox();
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				PowerEditingCell.this.changed(arg0, arg1, arg2);
			}});
		
		box = new Spinner<>(0, 20, 1);
		box.valueProperty().addListener(new ChangeListener<Integer>() {
			public void changed(ObservableValue<? extends Integer> arg0,
					Integer arg1, Integer arg2) {
				PowerEditingCell.this.changed(arg0, arg1, arg2);
			}
		});
		box.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
//		box.setCyclic(false);
//		ListSpinnerSkin<Integer> skin = new ListSpinnerSkin<Integer>(box);
//		skin.setArrowPosition(ArrowPosition.SPLIT);
//		box.setSkin(skin);
////		((ListSpinnerSkin<Race>)box.getSkin()).setArrowPosition(ArrowPosition.SPLIT);
		setAlignment(Pos.CENTER);
	}
	
//	//-------------------------------------------------------------------
//	@SuppressWarnings("rawtypes")
//	private void removeScrollHandler(ListSpinner pf) {
//	    try {
//	    	ListSpinnerSkin skin = (ListSpinnerSkin) pf.getSkin();
//			Field f = skin.getClass().getDeclaredField("skinNode");
//			f.setAccessible(true);
//			BorderPane skinNode = (BorderPane) f.get(skin);
//			if (skinNode!=null) {
//				skinNode.setOnScroll(null);
//			}
//		} catch (Exception e) {
//		}
//	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item==null || empty || getTableRow()==null) {
			this.setGraphic(null);
			return;
		}
		
		data = (PowerReference) getTableRow().getItem();
		if (data==null)
			return;
//		parent.setMapping(resource, this);
		box.getValueFactory().setValue(data.getCount());
		
		if (item instanceof Number)
			this.setGraphic(box);
		else {
			checkBox.setSelected((Boolean)item);
			checkBox.setVisible(charGen.canBeDeselected(data));
			this.setGraphic(checkBox);
		}
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
//		logger.info("changed: val="+val);
//		logger.info("changed: data.getCount="+data.getCount());
//		logger.info("changed: canBeDecreased="+charGen.canBeDecreased(data));
		if (val>data.getCount() && charGen.canBeIncreased(data)) {
			charGen.increase(data);
		} else if (val<data.getCount() && charGen.canBeDecreased(data)) {
			charGen.decrease(data);
		} else {
			// Revert back
			box.getValueFactory().setValue(data.getCount());
		}
		
//		removeScrollHandler(box);
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			charGen.deselect(data);
		}
		
//		removeScrollHandler(box);
	}

}
