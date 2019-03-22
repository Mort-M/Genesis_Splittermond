package org.prelle.splittermond.jfx.cultures;

import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
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

public class OldCultureLorePane extends VBox implements GenerationEventListener, EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private CultureLoreController control;
	private SpliMoCharacter model;

	private ChoiceBox<CultureLore> addChoice;
	private Button add;
	private TableView<CultureLoreReference> table;

	private TableColumn<CultureLoreReference, String> nameCol;
	private TableColumn<CultureLoreReference, Boolean> valueCol;

	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public OldCultureLorePane(CultureLoreController ctrl) {
		this.control = ctrl;

		GenerationEventDispatcher.addListener(this);
		doInit();
		doValueFactories();
		initInteractivity();
		add.setDisable(true);
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
		addChoice.setPrefWidth(100);
		addChoice.getItems().addAll(control.getAvailableCultureLores());
		addChoice.setConverter(new StringConverter<CultureLore>() {
			public String        toString(CultureLore object) { return object.getName(); }
			public CultureLore fromString(String string)      { return null; }
		});
		add = new Button(UI.getString("button.add"));
		HBox addLine = new HBox(5);
		addLine.getChildren().addAll(addChoice,add);
		HBox.setHgrow(addChoice, Priority.ALWAYS);

		table = new TableView<CultureLoreReference>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setPlaceholder(new Text(UI.getString("placeholder.culturelore")));

		nameCol = new TableColumn<CultureLoreReference, String>(UI.getString("label.culturelore"));
		valueCol = new TableColumn<CultureLoreReference, Boolean>();

//		nameCol.setMinWidth(80);
		nameCol.setPrefWidth(120);
		nameCol.setMaxWidth(Double.MAX_VALUE);
		valueCol.setMinWidth(40);
		valueCol.setMaxWidth(60);
		table.getColumns().addAll(nameCol, valueCol);


		// Add to layout
		super.getChildren().addAll(addLine, table);
		super.setSpacing(5);
		VBox.setVgrow(table, Priority.ALWAYS);
		table.setMaxHeight(Double.MAX_VALUE);
		
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<CultureLoreReference, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CultureLoreReference, String> p) {
				CultureLoreReference item = p.getValue();
				if (item==null) return null;
				return new SimpleStringProperty(item.getCultureLore().getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<CultureLoreReference, Boolean>, ObservableValue<Boolean>>() {
			public ObservableValue<Boolean> call(CellDataFeatures<CultureLoreReference, Boolean> p) {
				return new SimpleBooleanProperty(true);
			}
		});

		valueCol.setCellFactory(new Callback<TableColumn<CultureLoreReference,Boolean>, TableCell<CultureLoreReference,Boolean>>() {
			public TableCell<CultureLoreReference,Boolean> call(TableColumn<CultureLoreReference,Boolean> p) {
				return new OldCultureLoreEditingCell(control);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		add.setOnAction(this);
		
		addChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CultureLore>() {
			public void changed(ObservableValue<? extends CultureLore> arg0,
					CultureLore arg1, CultureLore newSel) {
				add.setDisable(newSel==null);
			}
		});
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		if (model!=null) {
			table.getItems().clear();
			table.getItems().addAll(model.getCultureLores());
			addChoice.getItems().clear();
			addChoice.getItems().addAll(control.getAvailableCultureLores());
			add.setDisable(true);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CULTURELORE_ADDED:
			addChoice.getItems().remove(((CultureLoreReference)event.getKey()).getCultureLore());
			updateContent();
			break;
		case CULTURELORE_REMOVED:
			addChoice.getItems().add(((CultureLoreReference)event.getKey()).getCultureLore());
			Collections.sort(addChoice.getItems());
			updateContent();
			break;
		case EXPERIENCE_CHANGED:
			updateContent();
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
			CultureLore res = addChoice.getSelectionModel().getSelectedItem();
			CultureLoreReference ref = control.select(res);
			if (ref==null) {
				logger.error("Adding "+res+" failed");
			}
		}
	}

	//-------------------------------------------------------------------
	public List<CultureLoreReference> getCultureLores() {
		return table.getItems();
	}

}


class OldCultureLoreEditingCell extends TableCell<CultureLoreReference, Boolean> {
	
	private CheckBox checkBox;
	private CultureLoreController charGen;
	private CultureLoreReference data;
	
	//-------------------------------------------------------------------
	public OldCultureLoreEditingCell(CultureLoreController charGen) {
		this.charGen = charGen;
		checkBox = new CheckBox();
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				OldCultureLoreEditingCell.this.changed(arg0, arg1, arg2);
			}});
		

		setAlignment(Pos.CENTER);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item==null || empty || getTableRow()==null) {
			this.setGraphic(null);
			return;
		}
		
		data = (CultureLoreReference) getTableRow().getItem();
		if (data==null)
			return;
		
			checkBox.setSelected((Boolean)item);
			checkBox.setVisible(charGen.canBeDeselected(data));
			this.setGraphic(checkBox);
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			charGen.deselect(data);
		}
	}

}
