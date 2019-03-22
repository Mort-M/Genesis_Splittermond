package org.prelle.splittermond.chargen.free.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.free.FreeSelectionGenerator;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FreeResourcePane extends HBox implements EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle res = SpliMoCharGenJFXConstants.UI;

	private FreeSelectionGenerator control;
	private FreeSelectionDialog parent;

	private TableView<ResourceModification> resources;
	private ChoiceBox<Resource> resource_cb;
	private Button newResource;
	private Label pointsLeft;
	
	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public FreeResourcePane(FreeSelectionGenerator ctrl, FreeSelectionDialog parent) {
		this.parent  = parent;
		this.control = ctrl;

		doInit();
		doValueFactories();
		initInteractivity();

		updateResources();
	}
	
	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		HBox line = new HBox(10);
		newResource = new Button(res.getString("button.add"));
		resource_cb = new ChoiceBox<Resource>(FXCollections.observableArrayList(control.getAvailableResources()));
		resource_cb.setConverter(new StringConverter<Resource>() {
			public String toString(Resource data) { return data.getName(); }
			public Resource fromString(String data) { return null; }
		});
		line.getChildren().addAll(resource_cb, newResource);
		
		// Content
		resources = new TableView<ResourceModification>();
		TableColumn<ResourceModification, String> nameCol = new TableColumn<ResourceModification, String>(res.getString("table.resources.name"));
		TableColumn<ResourceModification, Number> pointsCol = new TableColumn<ResourceModification, Number>(res.getString("label.points"));
		nameCol.setCellValueFactory(new PropertyValueFactory<ResourceModification, String>("ResourceName"));
		pointsCol.setCellValueFactory(new PropertyValueFactory<ResourceModification, Number>("Value"));
		pointsCol.setCellFactory(new Callback<TableColumn<ResourceModification,Number>, TableCell<ResourceModification,Number>>() {
            public TableCell<ResourceModification,Number> call(TableColumn<ResourceModification,Number> p) {
                 return new ResourceModCell(control, FreeResourcePane.this);
             }
         });
		resources.getColumns().addAll(nameCol, pointsCol);
		resources.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resources.setPlaceholder(new Text(res.getString("placeholder.resource")));
		nameCol.setMinWidth(200);
		nameCol.setPrefWidth(300);
		pointsCol.setMinWidth(80);


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
		topDown.getChildren().addAll(line, resources);
		topDown.getStyleClass().add("wizard-content");
		VBox.setVgrow(resources, Priority.SOMETIMES);

		// Add to layout
		getChildren().addAll(topDown, sidebar);
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		newResource.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Resource selection = resource_cb.getSelectionModel().getSelectedItem();
				if (selection!=null) {
					control.set(selection, 1);
					updateResources();
				}
			}
		});
	}

	//-------------------------------------------------------------------
	void updateResources() {
		logger.debug("update resources "+control.getSelectedResources());
		resources.getItems().clear();
		resources.getItems().addAll(control.getSelectedResources());
		resource_cb.setItems(FXCollections.observableArrayList(control.getAvailableResources()));
		pointsLeft.setText(String.valueOf(control.getPointsResources()));
		
		parent.updateButtons();
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource()==newResource) {
			logger.debug("add clicked");
			Resource selection = resource_cb.getSelectionModel().getSelectedItem();
			if (selection!=null) {
				control.set(selection, 1);
				updateResources();
			}
		}
	}

}

class ResourceModCell extends TableCell<ResourceModification, Number> implements ChangeListener<Integer>{

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private Spinner<Integer> box;
	private FreeSelectionGenerator charGen;
	private boolean setByEvent;
	private ResourceModification data;
	private FreeResourcePane parent;
	
	//-------------------------------------------------------------------
	public ResourceModCell(FreeSelectionGenerator charGen, FreeResourcePane parent) {
		if (charGen==null)
			throw new NullPointerException();
		this.parent  = parent;
		this.charGen = charGen;
		box = new Spinner<>(-2,4,1);
		box.valueProperty().addListener(this);
//		box.setCyclic(false);
		box.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		setAlignment(Pos.CENTER);
	}
	
//	//-------------------------------------------------------------------
//	@SuppressWarnings("rawtypes")
//	private void removeScrollHandler(Spinner pf) {
//	    try {
//	    	SpinnerSkin skin = (SpinnerSkin) pf.getSkin();
//			Field f = skin.getClass().getDeclaredField("skinNode");
//			f.setAccessible(true);
//			BorderPane skinNode = (BorderPane) f.get(skin);
//			if (skinNode!=null) {
//				skinNode.setOnScroll(null);
//			}
//		} catch (Exception e) {
//			logger.warn("Error removing scroll handler from spinner: "+e);
//		}
//	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Number item, boolean empty) {
		super.updateItem(item, empty);
		if (item==null || empty || super.getTableRow()==null) {
			this.setGraphic(null);
			return;
		}
		
		data = (ResourceModification) getTableRow().getItem();
		if (empty || data==null) {
			this.setGraphic(null);
			return;
		}
		box.getValueFactory().setValue(data.getValue());
		
		this.setGraphic(box);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
		if (setByEvent) {
			setByEvent = false;
			return;
		}
		setByEvent = false;
		charGen.set(data.getResource(), val);
		parent.updateResources();
		
//		removeScrollHandler(box);
	}
	
	//-------------------------------------------------------------------
	void setByEvent(int val) {
		setByEvent = true;
		box.getValueFactory().setValue(val);
	}
}
