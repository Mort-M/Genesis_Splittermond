package org.prelle.splittermond.jfx.resources;

import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ResourcePane extends VBox implements GenerationEventListener, EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private ResourceController control;
	private SpliMoCharacter model;

	private ChoiceBox<Resource> addChoice;
	private Button add;
	private TableView<ResourceReference> table;

	private TableColumn<ResourceReference, String> nameCol;
	private TableColumn<ResourceReference, Number> valueCol;
	private TableColumn<ResourceReference, String> notesCol;

	private ContextMenu contextMenu;
	private MenuItem join;
	private MenuItem split;

	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public ResourcePane(ResourceController ctrl, boolean withNotes, boolean withContext) {
		this.control = ctrl;

		GenerationEventDispatcher.addListener(this);
		doInit();
		doValueFactories();
		if (withContext)
			initContextMenu();
		if (!withNotes)
			table.getColumns().remove(notesCol);

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
		addChoice.setPrefWidth(200);
		addChoice.setConverter(new StringConverter<Resource>() {
			public String toString(Resource object) {return object.getName();}
			public Resource fromString(String string) {return null;}
		});
		addChoice.getItems().addAll(control.getAvailableResources());
		add = new Button(uiResources.getString("button.add"));
		HBox addLine = new HBox(5);
		addLine.getChildren().addAll(addChoice,add);
		HBox.setHgrow(addChoice, Priority.ALWAYS);

		table = new TableView<ResourceReference>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setPlaceholder(new Text(uiResources.getString("placeholder.resource")));
        table.setEditable(true);

		nameCol = new TableColumn<ResourceReference, String>(uiResources.getString("label.resource"));
		valueCol = new TableColumn<ResourceReference, Number>(uiResources.getString("label.value"));
		notesCol = new TableColumn<ResourceReference, String>(uiResources.getString("label.notes"));

		nameCol.setMinWidth(200);
		nameCol.setMaxWidth(300);
		valueCol.setMinWidth(80);
		valueCol.setMaxWidth(160);
		notesCol.setMinWidth(100);
		notesCol.setMaxWidth(Double.MAX_VALUE);
		table.getColumns().addAll(nameCol, valueCol, notesCol);

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

	//--------------------------------------------------------------------
	private void initContextMenu() {
		join  = new MenuItem(uiResources.getString("wizard.distrResource.join"));
		split = new MenuItem(uiResources.getString("wizard.distrResource.split"));
		contextMenu = new ContextMenu();
//		contextMenu.setSkin(new ContextMenuSkin(contextMenu));
		contextMenu.getSkin().getNode().setEffect(new DropShadow());
		contextMenu.getItems().addAll(join,split);		
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<ResourceReference, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<ResourceReference, String> p) {
				ResourceReference item = p.getValue();
				return new SimpleStringProperty(item.getResource().getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<ResourceReference, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<ResourceReference, Number> p) {
				return new SimpleIntegerProperty( p.getValue().getValue() );
			}
		});

		valueCol.setCellFactory(new Callback<TableColumn<ResourceReference,Number>, TableCell<ResourceReference,Number>>() {
			public TableCell<ResourceReference,Number> call(TableColumn<ResourceReference,Number> p) {
				return new ResourceEditingCell(control);
			}
		});
		notesCol.setCellValueFactory(
	            new PropertyValueFactory<ResourceReference, String>("description"));
		notesCol.setCellFactory(TextFieldTableCell.<ResourceReference>forTableColumn());
		notesCol.setEditable(true);
		notesCol.setOnEditCommit(
			    new EventHandler<CellEditEvent<ResourceReference, String>>() {
			        @Override
			        public void handle(CellEditEvent<ResourceReference, String> t) {
			        	logger.warn("editCommit");
			            ((ResourceReference) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setDescription(t.getNewValue());
			        }
			    }
			);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		add.setOnAction(this);
		add.setDisable(true);
		
		addChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Resource>() {
			public void changed(ObservableValue<? extends Resource> item,
					Resource arg1, Resource newVal) {
				add.setDisable(newVal==null);
			}
		});

		if (contextMenu==null)
			return;

		/*
		 * Split and join
		 */
		table.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount()==1 && event.getButton()==MouseButton.SECONDARY) {
					List<ResourceReference> selected = table.getSelectionModel().getSelectedItems();
					ResourceReference[] selected2 = new ResourceReference[selected.size()];
					selected.toArray(selected2);
					split.setDisable(selected.size()!=1 || !control.canBeSplit(selected.get(0)));
					join.setDisable(!control.canBeJoined(selected2));
					contextMenu.show(table, event.getScreenX(), event.getScreenY());
				}
			}
		});

		// Join
		join.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				List<ResourceReference> selected = table.getSelectionModel().getSelectedItems();
				ResourceReference[] selected2 = new ResourceReference[selected.size()];
				selected.toArray(selected2);
				control.join(selected2);
			}
		});

		// Split
		split.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				control.split(table.getSelectionModel().getSelectedItem());
			}
		});
	}

	//-------------------------------------------------------------------
	public TableView<ResourceReference> getTable() {
		return table;
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		if (model!=null) {
			table.getItems().clear();
			table.getItems().addAll(model.getResources());
			addChoice.getItems().clear();
			addChoice.getItems().addAll(control.getAvailableResources());
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
		case RESOURCE_ADDED:
		case RESOURCE_REMOVED:
		case RESOURCE_CHANGED:
			logger.debug("Resources changed - refresh view");
			updateContent();
			addChoice.requestFocus();
			add.setDisable(addChoice.getSelectionModel().getSelectedIndex()==-1);
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
			Resource res = addChoice.getSelectionModel().getSelectedItem();
			ResourceReference ref = control.openResource(res);
			logger.debug("  resource added: "+ref);
			table.getItems().add(ref);
		}
	}

	//-------------------------------------------------------------------
	public ObservableList<ResourceReference> getResources() {
		return table.getItems();
	}

}

class ResourceEditingCell extends TableCell<ResourceReference, Number> implements ChangeListener<Integer>{

	private Spinner<Integer> box;
	private ResourceController charGen;
	private ResourceReference resource;

	//-------------------------------------------------------------------
	public ResourceEditingCell(ResourceController charGen) {
		this.charGen = charGen;
		box = new Spinner<>(-2, 6, 1);
		box.valueProperty().addListener(this);
//		box.setCyclic(false);
		//		ListSpinnerSkin<Integer> skin = new ListSpinnerSkin<Integer>(box);
		//		skin.setArrowPosition(ArrowPosition.SPLIT);
		//		box.setSkin(skin);
		////		((ListSpinnerSkin<Race>)box.getSkin()).setArrowPosition(ArrowPosition.SPLIT);
		setAlignment(Pos.CENTER);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Number item, boolean empty) {
		super.updateItem(item, empty);

		if (item==null || empty || getTableRow()==null) {
			this.setGraphic(null);
			return;
		}

		resource = (ResourceReference) getTableRow().getItem();
		if (resource==null)
			return;
		//		parent.setMapping(resource, this);
		box.getValueFactory().setValue(resource.getValue());

		this.setGraphic(box);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
		if (val>resource.getValue() && charGen.canBeIncreased(resource)) {
			charGen.increase(resource);
		} else if (val<resource.getValue() && charGen.canBeDecreased(resource)) {
			charGen.decrease(resource);
		} else {
			// Revert back
			box.getValueFactory().setValue(resource.getValue());
		}
	}

}