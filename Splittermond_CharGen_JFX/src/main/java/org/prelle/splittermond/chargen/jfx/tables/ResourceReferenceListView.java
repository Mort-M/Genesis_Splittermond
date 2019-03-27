package org.prelle.splittermond.chargen.jfx.tables;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.listcells.ResourceReferenceListCell;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class ResourceReferenceListView extends ListView<ResourceReference> implements ScreenManagerProvider {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private CharacterController control;
	private SpliMoCharacter model;
	private ScreenManager manager;
	private LetUserChooseListener callback;

	//--------------------------------------------------------------------
	public ResourceReferenceListView(CharacterController control, LetUserChooseListener callback) {
		this.control = control;
		this.callback = callback;

		initComponents();
		initValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	public void updateItemController(CharacterController control) {
		this.control = control;
	}

	//--------------------------------------------------------------------
	public void setManager(ScreenManager mgr) {
		this.manager = mgr;
	}

	//--------------------------------------------------------------------
	public ScreenManager getScreenManager() {
		return manager;
	}

	//--------------------------------------------------------------------
	public SpliMoCharacter getCharacter() {
		return model;
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Label ph = new Label(UI.getString("placeholder.resources.selected"));
		ph.setWrapText(true);
        setPlaceholder(ph);
        setStyle("-fx-min-width: 15em; -fx-pref-width: 22em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black; -fx-padding: 2px");
	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<ResourceReference>, ListCell<ResourceReference>>() {
			public ListCell<ResourceReference> call(ListView<ResourceReference> p) {
				return new ResourceReferenceListCell(control, ResourceReferenceListView.this);
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
            String enhanceID = db.getString();
        	logger.debug("Dropped "+enhanceID);
        	Resource res = SplitterMondCore.getResource(enhanceID);
        	if (res!=null) // && control.canBeAdded(res))
        		control.getResourceController().openResource(res);
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

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	SpliMoCharacter getData() {
		return model;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the callback
	 */
	public LetUserChooseListener getCallback() {
		return callback;
	}

}

