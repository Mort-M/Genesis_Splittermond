/**
 * 
 */
package org.prelle.splittermond.jfx.equip;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.EnhancementReference;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class EnhancementListView extends ListView<Enhancement> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private NewItemController control;
	
	//--------------------------------------------------------------------
	public EnhancementListView(NewItemController control) {
		this.control = control;
		
		initComponents();
		initValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setPlaceholder(new Text(UI.getString("placeholder.enhancement.possible")));
        setStyle("-fx-pref-width: 22em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black;");
 	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<Enhancement>, ListCell<Enhancement>>() {
			public ListCell<Enhancement> call(ListView<Enhancement> p) {
				return new EnhancementListCell(control);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}
	
	//--------------------------------------------------------------------
	public void updateItemController(NewItemController control) {
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
        	EnhancementReference ref = null;
        	for (EnhancementReference tmp : control.getItem().getEnhancements()) {
        		if (tmp.getID().equals(enhanceID)) {
        			ref = tmp;
        			break;
        		}
        	}
        	if (ref==null) {
        		logger.warn("Unknown reference to remove");
        	} else if (control.canBeRemoved(ref)) {
        		control.removeEnhancement(ref);
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

}

class EnhancementListCell extends ListCell<Enhancement> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static Map<Enhancement.EnhancementType, Image> IMAGES = new HashMap<>();

	private Enhancement data;
	private NewItemController charGen;
	private HBox layout;
	private ImageView iView;
	private VBox layoutWithoutGraphic;
	private Label name;
	private FlowPane flow;
	private Label cost;
	
	//-------------------------------------------------------------------
	static {
		IMAGES.put(EnhancementType.NORMAL, new Image(ClassLoader.getSystemResourceAsStream("images/icon_enhancement_normal.png")));
		IMAGES.put(EnhancementType.MAGIC , new Image(ClassLoader.getSystemResourceAsStream("images/icon_enhancement_magic.png")));
		IMAGES.put(EnhancementType.RELIC , new Image(ClassLoader.getSystemResourceAsStream("images/icon_enhancement_relic.png")));
		IMAGES.put(EnhancementType.ALCHEMY, new Image(ClassLoader.getSystemResourceAsStream("images/icon_enhancement_alchemy.png")));
		IMAGES.put(EnhancementType.DIVINE, new Image(ClassLoader.getSystemResourceAsStream("images/icon_enhancement_divine.png")));
		IMAGES.put(EnhancementType.SAINT, new Image(ClassLoader.getSystemResourceAsStream("images/icon_enhancement_saint.png")));
	}
	
	//-------------------------------------------------------------------
	public EnhancementListCell(NewItemController charGen) {
		this.charGen = charGen;
		
		layoutWithoutGraphic = new VBox();
		name   = new Label();
		cost   = new Label();
		iView  = new ImageView();
		iView.setFitHeight(48);
		iView.setFitWidth(48);
		flow   = new FlowPane(Orientation.HORIZONTAL);
		layoutWithoutGraphic.getChildren().addAll(name, flow);
		layout = new HBox(2);
		layout.getChildren().addAll(iView, layoutWithoutGraphic, cost);
		layout.getStyleClass().add("content");
		HBox.setMargin(cost, new Insets(0,5,0,5));
		
		name.getStyleClass().add("base");
		flow.getStyleClass().add("text-tertiary-info");
		cost.getStyleClass().add("text-subheader");
		
		setStyle("-fx-pref-width: 20em");
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
	protected void updateItem(Enhancement item, boolean empty) {
		super.updateItem(item, empty);
		data = item;
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
			cost.setText(null);
			return;
		} else {
			setGraphic(layout);
			name.setText(item.getName());
			cost.setText(String.valueOf(item.getSize()));
			iView.setImage(IMAGES.get(item.getType()));
			flow.getChildren().clear();
			String product = item.getProductName()+" "+item.getPage();
			flow.getChildren().add(new Label(product));
//			boolean fulfilled = item.getLanguages().isEmpty();
//			Iterator<Language> it = item.getLanguages().iterator();
//			while (it.hasNext()) {
//				Language lang = it.next();
//				Label label = new Label(lang.getName());
//				// Color language depending on its availability
//				if (parent.getData()!=null) {
//					if (parent.getData().hasLanguage(lang)) {
//						label.setStyle("-fx-text-fill: green");
//						fulfilled = true;
//					} else
//						label.setStyle("-fx-text-fill: red");
//				}
//				flow.getChildren().add(label);
//				if (it.hasNext())
//					flow.getChildren().add(new Label(", "));
//			}
//			if (fulfilled) {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("selectable-list-item");
//			} else {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("unselectable-list-item");
//			}
		}
		
	}

}