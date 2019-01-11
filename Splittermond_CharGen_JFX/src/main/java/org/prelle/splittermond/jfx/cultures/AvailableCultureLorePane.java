package org.prelle.splittermond.jfx.cultures;

import java.util.Collection;
import java.util.Iterator;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.Language;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class AvailableCultureLorePane extends VBox implements GenerationEventListener {

	enum DisplayMode {
		/*
		 * Show all entries that are currently unselected
		 * and can be selected
		 */
		AVAILABLE,
		// Show all unselected entries - available or not
		UNSELECTED,
	}

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private CultureLoreController control;
	private SpliMoCharacter model;
	private DisplayMode mode;

	private ListView<CultureLore> table;
	private EventHandler<ActionEvent> onAction;
	

	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public AvailableCultureLorePane(CultureLoreController ctrl, DisplayMode mode) {
		if (ctrl==null)
			throw new NullPointerException();
		this.control = ctrl;
		this.mode    = mode;

		GenerationEventDispatcher.addListener(this);
		doInit();
		doValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		table = new ListView<CultureLore>();
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setPlaceholder(new Text(UI.getString("placeholder.culturelore")));
        table.setStyle("-fx-pref-width: 20em;");

		// Add to layout
		super.getChildren().addAll(table);
		super.setSpacing(5);
		VBox.setVgrow(table, Priority.ALWAYS);
		table.setMaxHeight(Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		table.setCellFactory(new Callback<ListView<CultureLore>, ListCell<CultureLore>>() {
			public ListCell<CultureLore> call(ListView<CultureLore> param) {
				return new AvailableCultureLoreCell(control, AvailableCultureLorePane.this);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		table.setOnDragDropped(event -> dragDropped(event));
		table.setOnDragOver(event -> dragOver(event));
	}
	
	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String cultLoreID = db.getString();
        	logger.debug("Dropped "+cultLoreID);
        	CultureLore cult = SplitterMondCore.getCultureLore(cultLoreID);
        	CultureLoreReference res = null;
        	for (CultureLoreReference tmp : model.getCultureLores())
        		if (tmp.getCultureLore()==cult)
        			res = tmp;
        	if (control.canBeDeselected(res))
        		control.deselect(res);
        }
        /* let the source know whether the string was successfully 
         * transferred and used */
        event.setDropCompleted(success);
        
        event.consume();
	}

	//-------------------------------------------------------------------
	private void dragOver(DragEvent event) {
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
	}


	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;

		updateContent();
	}

	//--------------------------------------------------------------------
	SpliMoCharacter getData() {
		return model;
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		if (model!=null) {
			table.getItems().clear();
			table.getItems().addAll(control.getAvailableCultureLores());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings({ "incomplete-switch", "unchecked" })
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CULTURELORE_ADDED:
		case CULTURELORE_REMOVED:
			updateContent();
			break;
		case CULTURELORE_AVAILABLE_CHANGED:
			table.getItems().clear();
			table.getItems().addAll( (Collection<CultureLore>)event.getKey() );
			break;
		}
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<CultureLore> selectedItemProperty() {
		return table.getSelectionModel().selectedItemProperty();
	}

	//-------------------------------------------------------------------
	public ListView<CultureLore> getList() {
		return table;
	}

	//--------------------------------------------------------------------
	public void setOnAction(EventHandler<ActionEvent> onAction) {
		this.onAction = onAction;
	}

	//--------------------------------------------------------------------
	void fireAction(CultureLore item) {
		ActionEvent event = new ActionEvent(item, this);
		if (onAction!=null)
			onAction.handle(event);
	}

}


class AvailableCultureLoreCell extends ListCell<CultureLore> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private CultureLore data;
	private CultureLoreController charGen;
	private AvailableCultureLorePane parent;
	private VBox layout;
	private Label name;
	private FlowPane flow;
	
	//-------------------------------------------------------------------
	public AvailableCultureLoreCell(CultureLoreController charGen, AvailableCultureLorePane parent) {
		this.charGen = charGen;
		this.parent  = parent;
		
		layout = new VBox();
		name   = new Label();
		flow   = new FlowPane(Orientation.HORIZONTAL);
		layout.getChildren().addAll(name, flow);
		
		name.getStyleClass().add("text-small-subheader");
		flow.getStyleClass().add("text-tertiary-info");
		
		setPrefWidth(250);
		
		
//		this.setOnTouchMoved(event -> logger.debug("onTouchMoved "+event));
		this.setOnDragDetected(event -> dragStarted(event));
		this.setOnMouseClicked(event -> {
			if (event.getClickCount()==2)
				parent.fireAction(data);
		});
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.debug("Drag started "+event.getSource());
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(data.getKey());
        db.setContent(content);
        
        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);
        
        event.consume();	
    }
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(CultureLore item, boolean empty) {
		super.updateItem(item, empty);
		data = item;
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
			return;
		} else {
			setGraphic(layout);
			name.setText(item.getName());
			flow.getChildren().clear();
			boolean fulfilled = item.getLanguages().isEmpty();
			Iterator<Language> it = item.getLanguages().iterator();
			while (it.hasNext()) {
				Language lang = it.next();
				Label label = new Label(lang.getName());
				// Color language depending on its availability
				if (parent.getData()!=null) {
					if (parent.getData().hasLanguage(lang)) {
						label.setStyle("-fx-text-fill: green");
						fulfilled = true;
					} else
						label.setStyle("-fx-text-fill: red");
				}
				flow.getChildren().add(label);
				if (it.hasNext())
					flow.getChildren().add(new Label(", "));
			}
			if (fulfilled) {
				layout.getStyleClass().clear();
				layout.getStyleClass().add("selectable-list-item");
			} else {
				layout.getStyleClass().clear();
				layout.getStyleClass().add("unselectable-list-item");
			}
		}
		
	}

}
