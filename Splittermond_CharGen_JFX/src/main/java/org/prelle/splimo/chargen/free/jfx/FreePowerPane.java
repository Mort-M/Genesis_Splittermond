package org.prelle.splimo.chargen.free.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Power;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.free.FreeSelectionGenerator;

import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.Label;
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

public class FreePowerPane extends HBox implements EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiPowers = SpliMoCharGenJFXConstants.UI;

	private FreeSelectionGenerator control;
	private FreeSelectionDialog parent;

	private ChoiceBox<Power> addChoice;
	private Button add;
	private TableView<Power> table;
	private Label pointsLeft;
	
	private TableColumn<Power, String> nameCol;
	private TableColumn<Power, Number> valueCol; 
	private TableColumn<Power, Boolean> undoCol;
	
	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public FreePowerPane(FreeSelectionGenerator ctrl, FreeSelectionDialog parent) {
		this.control = ctrl;
		this.parent  = parent;

		doInit();
		doValueFactories();
		initInteractivity();

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
		add = new Button(uiPowers.getString("button.add"));
		HBox addLine = new HBox(5);
		addLine.getChildren().addAll(addChoice,add);
		HBox.setHgrow(addChoice, Priority.ALWAYS);
		
		table = new TableView<Power>();
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Text(uiPowers.getString("placeholder.power")));

		nameCol  = new TableColumn<Power, String>(uiPowers.getString("label.power"));
		valueCol = new TableColumn<Power, Number>(uiPowers.getString("label.value"));
		undoCol  = new TableColumn<Power, Boolean>(uiPowers.getString("label.points"));
		nameCol.setMinWidth(200);
		valueCol.setMinWidth(80);
		undoCol.setMinWidth(80);
		table.getColumns().addAll(nameCol, valueCol, undoCol);


		/* Sidebar */
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");

		// Line that reflects remaining points to distribute
		pointsLeft = new Label(String.valueOf(control.getPointsPowers()));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		VBox sidebar = new VBox(15);
		sidebar.setPrefWidth(80);
		sidebar.setAlignment(Pos.TOP_CENTER);
		sidebar.getStyleClass().add("wizard-context");
		sidebar.getChildren().addAll(placeholder,pointsLeft);

		VBox topDown = new VBox(5);
		topDown.getChildren().addAll(addLine, table);
		topDown.getStyleClass().add("wizard-content");
		VBox.setVgrow(table, Priority.ALWAYS);

		// Add to layout
		getChildren().addAll(topDown, sidebar);
		
		HBox.setHgrow(topDown, Priority.ALWAYS);
		topDown.setMaxWidth(Double.MAX_VALUE);

	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<Power, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Power, String> p) {
				Power item = p.getValue();
				return new SimpleStringProperty(item.getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<Power, Number>, ObservableValue<Number>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public ObservableValue<Number> call(CellDataFeatures<Power, Number> p) {
				return new SimpleObjectProperty( p.getValue().getCost() );
			}
		});
		undoCol.setCellValueFactory(new Callback<CellDataFeatures<Power, Boolean>, ObservableValue<Boolean>>() {
			public ObservableValue<Boolean> call(CellDataFeatures<Power, Boolean> p) {
				return new SimpleBooleanProperty( true);
			}
		});

		undoCol.setCellFactory(new Callback<TableColumn<Power,Boolean>, TableCell<Power,Boolean>>() {
            public TableCell<Power,Boolean> call(TableColumn<Power,Boolean> p) {
                 return new PowerEditingCell(FreePowerPane.this);
             }
         });
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		add.setOnAction(this);
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		table.getItems().clear();
		table.getItems().addAll(control.getSelectedPowers());
		pointsLeft.setText(String.valueOf(control.getPointsPowers()));
		addChoice.getItems().clear();
		addChoice.getItems().addAll(control.getAvailablePowers());
		
		parent.updateButtons();
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource()==add) {
			logger.debug("add clicked");
			Power res = addChoice.getSelectionModel().getSelectedItem();
			control.selectPower(res, true);
			updateContent();
		}
	}

	//-------------------------------------------------------------------
	public void doDeselect(Power power) {
		control.selectPower(power, false);
		updateContent();
	}

}

class PowerEditingCell extends TableCell<Power, Boolean> {
	
	private CheckBox checkBox;
	private FreePowerPane pane;
	private Power data;
	
	//-------------------------------------------------------------------
	public PowerEditingCell(FreePowerPane pane) {
		this.pane    = pane;
		checkBox = new CheckBox();
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				PowerEditingCell.this.changed(arg0, arg1, arg2);
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
		
		data = (Power) getTableRow().getItem();
		if (data==null)
			return;
		
		checkBox.setSelected((Boolean)item);
		this.setGraphic(checkBox);
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			Power power = (Power) getTableRow().getItem();
			pane.doDeselect(power);
			
		}
	}

}
