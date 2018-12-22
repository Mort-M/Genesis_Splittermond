/**
 * 
 */
package org.prelle.splittermond.jfx.resources;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ResourceController;

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
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class ResourceListView extends ListView<Resource> {
	
	private static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

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

class ResourceListCell extends ListCell<Resource> {
	
	private static Logger logger = Logger.getLogger("splittermond.jfx");

	private Resource data;
	private ResourceController charGen;
	private HBox layout;
	private VBox layoutWithoutGraphic;
	private Label name;
	private HBox flow;
	
	//-------------------------------------------------------------------
	public ResourceListCell(ResourceController charGen) {
		this.charGen = charGen;
		layoutWithoutGraphic = new VBox();
		name   = new Label();
		flow   = new HBox(2);
		layoutWithoutGraphic.getChildren().addAll(name, flow);
		layout = new HBox(2);
		layout.getChildren().addAll(layoutWithoutGraphic);
		layout.getStyleClass().add("content");
		
		name.getStyleClass().add("text-small-subheader");
		flow.getStyleClass().add("text-tertiary-info");
		
		setStyle("-fx-pref-width: 13em");
//		setPrefWidth(280);
		
		
//		this.setOnTouchMoved(event -> logger.debug("onTouchMoved "+event));
		this.setOnDragDetected(event -> dragStarted(event));
//		this.setOnMouseClicked(event -> {
//			if (event.getClickCount()==2)
//				parent.fireAction(data);
//		});
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
        if (data==null)
        	return;
        content.putString(data.getId());
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
	protected void updateItem(Resource item, boolean empty) {
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
			String product = item.getProductName()+" "+item.getPage();
			flow.getChildren().add(new Label(product));
		}
		
	}

}