package org.prelle.splittermond.jfx.resources;

import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.ScreenManager;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.levelling.ResourceLeveller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.skin.ContextMenuSkin;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

public class ResourcePane2 extends HBox implements GenerationEventListener {

	private static Logger logger = Logger.getLogger("chargen.ui");

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private ResourceController control;
	private SpliMoCharacter model;

	private ResourceListView lvAvailable;
	private ResourceReferenceListView lvSelected;
	private Label lblTrash;

	private ContextMenu contextMenu;
	private MenuItem join;
	private MenuItem split;
	
	private ObjectProperty<Resource> selectedItem;

	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public ResourcePane2(ResourceController ctrl, boolean withContext, LetUserChooseListener callback) {
		this.control = ctrl;

		GenerationEventDispatcher.addListener(this);
		initComponents(callback);
		initLayout();
		if (withContext)
			initContextMenu();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;

		updateContent();
		lvSelected.setData(model);
	}

	//--------------------------------------------------------------------
	private void initComponents(LetUserChooseListener callback) {
		lvAvailable = new ResourceListView(control);
		lvSelected  = new ResourceReferenceListView(control, callback);
		Image img = new Image(ClassLoader.getSystemResourceAsStream("images/icon_trashcan.png"));
		ImageView iView = new ImageView(img);
		iView.setFitHeight(128);
		iView.setFitWidth(128);
		lblTrash    = new Label(uiResources.getString("resourcescreen.trash"), iView);
		lblTrash.setWrapText(true);
		lblTrash.setContentDisplay(ContentDisplay.BOTTOM);
		lblTrash.setTextAlignment(TextAlignment.CENTER);
		
		selectedItem = new SimpleObjectProperty<Resource>();
	}

	//--------------------------------------------------------------------
	private void initContextMenu() {
		join  = new MenuItem(uiResources.getString("wizard.distrResource.join"));
		split = new MenuItem(uiResources.getString("wizard.distrResource.split"));
		contextMenu = new ContextMenu();
		contextMenu.setSkin(new ContextMenuSkin(contextMenu));
		contextMenu.getSkin().getNode().setEffect(new DropShadow());
		contextMenu.getItems().addAll(join,split);		
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		getChildren().addAll(lvAvailable, lvSelected);
		if (control instanceof ResourceLeveller) {
			lblTrash.setMaxHeight(Double.MAX_VALUE);
			lblTrash.setAlignment(Pos.BOTTOM_CENTER);
			getChildren().add(lblTrash);
		}
		setSpacing(20);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {

		if (contextMenu==null)
			return;

		/*
		 * Split and join
		 */
		lvSelected.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount()==1 && event.getButton()==MouseButton.SECONDARY) {
					List<ResourceReference> selected = lvSelected.getSelectionModel().getSelectedItems();
					ResourceReference[] selected2 = new ResourceReference[selected.size()];
					selected.toArray(selected2);
					split.setDisable(selected.size()!=1 || !control.canBeSplit(selected.get(0)));
					join.setDisable(!control.canBeJoined(selected2));
					contextMenu.show(lvSelected, event.getScreenX(), event.getScreenY());
				}
			}
		});

		// Join
		join.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				List<ResourceReference> selected = lvSelected.getSelectionModel().getSelectedItems();
				ResourceReference[] selected2 = new ResourceReference[selected.size()];
				selected.toArray(selected2);
				control.join(selected2);
			}
		});

		// Split
		split.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				control.split(lvSelected.getSelectionModel().getSelectedItem());
			}
		});
		
		// Listen to selections
		lvAvailable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> selectedItem.set(n));
		lvSelected.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> selectedItem.set( (n!=null)?n.getResource():null));
		
		/*
		 * Drag & Drop over trash can
		 */
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
        			logger.info("Drop resource "+ref);
        			if (control.canBeTrashed(ref)) {
        				control.trash(ref);
        			} else {
        		        event.setDropCompleted(success);
        		        
        		        event.consume();
        				lvSelected.getManager().showAlertAndCall(AlertType.NOTIFICATION, 
        						uiResources.getString("resourcescreen.trash.fail.title"), 
        						uiResources.getString("resourcescreen.trash.fail.desc"));
        				return;
        			}
        		} else {
        			logger.warn("Cannot drop unknown resource reference: "+enhanceID);
        		}
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
		if (event.getGestureSource() != target && event.getDragboard().hasString() && event.getDragboard().getString().startsWith("resource:")) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		if (model!=null) {
			lvAvailable.getItems().clear();
			lvAvailable.getItems().addAll(control.getAvailableResources());
			lvSelected.getItems().clear();
			lvSelected.getItems().addAll(model.getResources());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.debug("RCV "+event);
		switch (event.getType()) {
		case RESOURCE_ADDED:
		case RESOURCE_REMOVED:
		case RESOURCE_CHANGED:
			logger.debug("Resources changed - refresh view");
			updateContent();
			break;
		case EXPERIENCE_CHANGED:
			updateContent();
			break;
		}
	}

	//--------------------------------------------------------------------
	public void setManager(ScreenManager manager) {
		lvSelected.setManager(manager);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the selectedItem
	 */
	public ObjectProperty<Resource> getSelectedItem() {
		return selectedItem;
	}

}
