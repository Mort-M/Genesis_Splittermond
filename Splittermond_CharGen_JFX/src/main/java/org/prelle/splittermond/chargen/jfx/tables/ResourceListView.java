package org.prelle.splittermond.chargen.jfx.tables;

import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.listcells.ResourceListCell;

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
public class ResourceListView extends ListView<Resource> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private ResourceController control;

	//--------------------------------------------------------------------
	public ResourceListView(ResourceController control) {
		this.control = control;

		initComponents();
		initValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		Label ph = new Label(UI.getString("placeholder.resources.available"));
		ph.setWrapText(true);
		setPlaceholder(ph);
		setStyle("-fx-min-width: 10em; -fx-pref-width: 15em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black;");
	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<Resource>, ListCell<Resource>>() {
			public ListCell<Resource> call(ListView<Resource> p) {
				return new ResourceListCell(control);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}

	//--------------------------------------------------------------------
	public void updateItemController(ResourceController control) {
		this.control = control;
	}

	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
		/* if there is a string data on dragboard, read it and use it */
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasString()) {
			String enhanceID = db.getString();
			logger.debug("Dropped "+enhanceID);
			// Get reference for ID
			if (enhanceID.startsWith("resource:")) {
				StringTokenizer tok = new StringTokenizer(enhanceID.substring(9),"|");
				String resID = tok.nextToken();
				String descr = tok.nextToken();
				String idref = tok.nextToken();
				if (descr!=null) descr=descr.substring(5);
				if (idref!=null) idref=idref.substring(6);
				if ("null".equals(descr)) descr=null;
				if ("null".equals(idref)) idref=null;

				Resource res = SplitterMondCore.getResource(resID);
				ResourceReference ref = control.findResourceReference(res, descr, idref);
				if (ref!=null) {
					logger.info("Deselect resource "+ref);
					control.deselect(ref);
				} else {
					logger.warn("Cannot deselect unknown resource reference: "+enhanceID);
				}

			}
			//        	for (ResourceReference tmp : control.getItem().getResources()) {
			//        		if (tmp.getID().equals(enhanceID)) {
			//        			ref = tmp;
			//        			break;
			//        		}
			//        	}
			//        	if (ref==null) {
			//        		logger.warn("Unknown reference to remove");
			//        	} else if (control.canBeRemoved(ref)) {
			//        		control.removeResource(ref);
			//        	}
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

}

