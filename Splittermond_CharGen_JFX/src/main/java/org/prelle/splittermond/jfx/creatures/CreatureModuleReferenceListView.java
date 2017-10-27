/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.prelle.javafx.ScreenManager;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CommonCreatureController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModuleReference;

/**
 * @author prelle
 *
 */
public class CreatureModuleReferenceListView extends ListView<CreatureModuleReference> {

	private static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CommonCreatureController control;
	private ScreenManager manager;
	private LetUserChooseListener callback;

	//--------------------------------------------------------------------
	public CreatureModuleReferenceListView(CommonCreatureController control, LetUserChooseListener callback) {
		this.control = control;
		this.callback = callback;

		initComponents();
		initValueFactories();
		initInteractivity();
	}

//	//--------------------------------------------------------------------
//	public void updateCreatureController(CreatureController control) {
//		this.control = control;
//	}

	//--------------------------------------------------------------------
	public void setManager(ScreenManager mgr) {
		this.manager = mgr;
	}

	//--------------------------------------------------------------------
	public ScreenManager getScreenManager() {
		return manager;
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Label ph = new Label(UI.getString("placeholder.creaturemodulereferences.selected"));
		ph.setWrapText(true);
        setPlaceholder(ph);
        setStyle("-fx-min-width: 26em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black;");
	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<CreatureModuleReference>, ListCell<CreatureModuleReference>>() {
			public ListCell<CreatureModuleReference> call(ListView<CreatureModuleReference> p) {
//				return new CreatureModuleReferenceListCell(control, CreatureModuleReference.this);
				return new CreatureModuleReferenceListCell();
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}

	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString().substring(db.getString().indexOf(":")+1);
        	logger.debug("Dropped "+enhanceID);
        	CreatureModule res = SplitterMondCore.getCreatureModule(enhanceID);
        	if (res!=null) {// && control.canBeAdded(res))
        		Platform.runLater(new Runnable(){
					public void run() {
			       		control.selectOption(res);
					}
        			
        		});
         	}
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
	public ScreenManager getManager() {
		return manager;
	}

//	//-------------------------------------------------------------------
//	public void setData(SpliMoCharacter model) {
//		this.model = model;
//	}
//
//	//-------------------------------------------------------------------
//	SpliMoCharacter getData() {
//		return model;
//	}

	//-------------------------------------------------------------------
	/**
	 * @return the callback
	 */
	public LetUserChooseListener getCallback() {
		return callback;
	}

}

class CreatureModuleReferenceListCell extends ListCell<CreatureModuleReference> {

	private static Logger logger = Logger.getLogger("splittermond.jfx");

	private Label lbName;
	private Label lbReference;
	private Label lbCost;
	private HBox  layout;
	
	private CreatureModuleReference data;

	//-------------------------------------------------------------------
	public CreatureModuleReferenceListCell() {
		lbName = new Label();
		lbName.setStyle("-fx-font-weight: bold");
		lbReference = new Label();
		lbCost = new Label();
		lbCost.setStyle("-fx-font-weight: bold; -fx-font-size: 200%");

		VBox col1 = new VBox(5, lbName, lbReference);
		col1.setMaxWidth(Double.MAX_VALUE);
		layout = new HBox(col1, lbCost);
		layout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(col1, Priority.ALWAYS);
		
		initInteractivity();
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	public void updateItem(CreatureModuleReference item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(layout);
			lbName.setText(item.getModule().getName());
			lbReference.setText(item.getModule().getProductName()+" "+item.getModule().getPage());
			lbCost.setText(String.valueOf(item.getModule().getCost()));
		}
	}
	

	//-------------------------------------------------------------------
	private void initInteractivity() {
		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.debug("drag started for "+data);
		if (data==null)
			return;
//		logger.debug("canBeDeselected = "+charGen.canBeDeselected(data));
//		logger.debug("canBeTrashed    = "+((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true));
//		if (!charGen.canBeDeselected(data) && !((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true))
//			return;

		Node source = (Node) event.getSource();
		logger.debug("drag src = "+source);

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        String id = "creaturemodule:"+data.getUniqueId();
        content.putString(id);
        db.setContent(content);

        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);

        event.consume();
    }

}