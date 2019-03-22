/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.creature.CreatureTypeValue;
import org.prelle.splimo.npc.CreatureTypeController;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author Stefan
 *
 */
public class CreatureTypePane extends HBox implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private Creature model;
	private CreatureTypeController control;
	
	private ListView<CreatureType> listAvailable;
	private ListView<CreatureTypeValue> listSelected;
	
	//--------------------------------------------------------------------
	public CreatureTypePane(CreatureTypeController ctrl) {
		this.control = ctrl;
		initComponents();
		initLayout();
		initInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		Label phAvail  = new Label(UI.getString("pane.creaturetype.avail.placeholder"));
		Label phSelect = new Label(UI.getString("pane.creaturetype.select.placeholder"));
		phAvail.setWrapText(true);
		phSelect.setWrapText(true);
		listAvailable = new ListView<CreatureType>();
		listAvailable.setPlaceholder(phAvail);
		listSelected  = new ListView<CreatureTypeValue>();
		listSelected.setPlaceholder(phSelect);
		
		listAvailable.setCellFactory(new Callback<ListView<CreatureType>, ListCell<CreatureType>>() {
			@Override
			public ListCell<CreatureType> call(ListView<CreatureType> arg0) {
				return new CreatureTypeCell(CreatureTypePane.this);
			}
		});
		
		listSelected.setCellFactory(new Callback<ListView<CreatureTypeValue>, ListCell<CreatureTypeValue>>() {
			@Override
			public ListCell<CreatureTypeValue> call(
					ListView<CreatureTypeValue> arg0) {
				return new CreatureTypeValueCell(control, CreatureTypePane.this);
			}
		});
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setSpacing(20);
		getChildren().addAll(listAvailable, listSelected);
		
	}
	
	//--------------------------------------------------------------------
	private void initInteractivity() {
		listSelected.setOnDragDropped(event -> dragDroppedSelected(event));
		listSelected.setOnDragOver(event -> dragOver(event));
		listAvailable.setOnDragDropped(event -> dragDroppedAvailable(event));
		listAvailable.setOnDragOver(event -> dragOver(event));
	}
	
	//-------------------------------------------------------------------
	private void dragDroppedSelected(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString();
        	logger.debug("Dropped "+enhanceID);
        	CreatureType res = SplitterMondCore.getCreatureType(enhanceID.substring("creaturetype:".length()));
        	if (res!=null) // && control.canBeAdded(res))
        		control.select(res);
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
	
	//-------------------------------------------------------------------
	private void dragDroppedAvailable(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString();
        	logger.debug("Dropped "+enhanceID);
        	CreatureType res = SplitterMondCore.getCreatureType(enhanceID.substring("creaturetype:".length()));
        	if (res!=null) {
        		for (CreatureTypeValue data : model.getCreatureTypes()) {
        			if (data.getType()==res)
                		control.deselect(data);
        		}
        	}
        }
        /* let the source know whether the string was successfully 
         * transferred and used */
        event.setDropCompleted(success);
        
        event.consume();
	}

	//--------------------------------------------------------------------
	public void refresh() {
		listAvailable.getItems().clear();
		listAvailable.getItems().addAll(control.getAvailable());
		
		listSelected.getItems().clear();
		listSelected.getItems().addAll(model.getCreatureTypes());
	}

	//--------------------------------------------------------------------
	public void setData(Creature model) {
		this.model = model;
		
		refresh();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CREATURETYPES_CHANGED:
			logger.debug("RCV "+event);
			refresh();
			break;
		}
	}

}

//--------------------------------------------------------------------
//--------------------------------------------------------------------
class CreatureTypeCell extends ListCell<CreatureType> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private CreatureTypePane parent;
	private CreatureType data;
	
	private VBox layout;
	private Label name;
	private Label tfDescr;

	//--------------------------------------------------------------------
	public CreatureTypeCell(CreatureTypePane parent) {
		this.parent  = parent;

		layout  = new VBox(5);
		name    = new Label();
		tfDescr = new Label();
		
		initStyle();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initStyle() {	
		name.getStyleClass().add("text-small-subheader");

//		setStyle("-fx-pref-width: 24em");
		layout.getStyleClass().add("content");		
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {		
		layout.getChildren().addAll(name, tfDescr);
		
//		layout.setMaxWidth(Double.MAX_VALUE);
//		HBox.setHgrow(layout, Priority.ALWAYS);
//		
//		setAlignment(Pos.TOP_LEFT);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.trace("drag started for "+data);
		if (data==null)
			return;
		
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        String id = "creaturetype:"+data.getKey();
        content.putString(id);
        db.setContent(content);
        
        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);
        
        event.consume();	
    }
	
	//--------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CreatureType item, boolean isEmpty) {
		super.updateItem(item, isEmpty);
		data = item;
		
		if (isEmpty) {
			setText(null);
			setGraphic(null);
		} else {
			name.setText(item.getName());
			tfDescr.setText(item.getProductName()+" "+item.getPage());
			setGraphic(layout);
		}		
	}
}

//--------------------------------------------------------------------
//--------------------------------------------------------------------
class CreatureTypeValueCell extends ListCell<CreatureTypeValue> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);

	private CreatureTypeController charGen;
	private CreatureTypePane parent;
	private CreatureTypeValue data;
	
	private HBox layout;
	private Label name;
	private TilePane tiles;
	private Button btnDec;
	private Label  lblVal;
	private Button btnInc;
	private Label tfDescr;

	//--------------------------------------------------------------------
	public CreatureTypeValueCell(CreatureTypeController control, CreatureTypePane parent) {
		this.parent  = parent;
		this.charGen = control;

		layout  = new HBox(5);
		name    = new Label();
		tfDescr = new Label();
		btnDec  = new Button("-");
		lblVal  = new Label("?");
		btnInc  = new Button("+");
		
		initStyle();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initStyle() {	
		btnDec.setStyle("-fx-background-color: transparent");
		btnInc.setStyle("-fx-background-color: transparent");
		btnDec.getStyleClass().add("bordered");
		btnInc.getStyleClass().add("bordered");
		name.getStyleClass().add("text-small-subheader");
		lblVal.getStyleClass().add("text-subheader");

//		setStyle("-fx-pref-width: 24em");
		layout.getStyleClass().add("content");		
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {		
		VBox bxCenter = new VBox(2);
		bxCenter.getChildren().addAll(name, tfDescr);

		btnDec.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnInc.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		tiles = new TilePane(Orientation.HORIZONTAL);
		tiles.setPrefColumns(3);
		tiles.setHgap(4);
		tiles.getChildren().addAll(btnDec, lblVal, btnInc);
		tiles.setAlignment(Pos.CENTER_LEFT);
		layout.getChildren().addAll(bxCenter, tiles);
		
		bxCenter.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(bxCenter, Priority.ALWAYS);
		
		
		setAlignment(Pos.CENTER);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnInc .setOnAction(event -> {charGen.increase(data); updateItem(data, false); parent.refresh();});
		btnDec .setOnAction(event -> {charGen.decrease(data); updateItem(data, false); parent.refresh();});

		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.trace("drag started for "+data);
		if (data==null)
			return;
		
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        String id = "creaturetype:"+data.getType().getKey();
        content.putString(id);
        db.setContent(content);
        
        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);
        
        event.consume();	
    }
	
	//--------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CreatureTypeValue item, boolean isEmpty) {
		super.updateItem(item, isEmpty);
		data = item;
		
		if (isEmpty) {
			setText(null);
			setGraphic(null);
		} else {
			name.setText(item.getName());
			tfDescr.setText(item.getType().getProductName()+" "+item.getType().getPage());
			tiles.setVisible(item.getType().hasLevel());
			lblVal.setText(String.valueOf(item.getLevel()));
			setGraphic(layout);
		}		
	}
}