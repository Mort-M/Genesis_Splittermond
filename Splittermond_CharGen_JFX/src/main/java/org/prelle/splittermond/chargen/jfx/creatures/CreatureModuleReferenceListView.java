/**
 * 
 */
package org.prelle.splittermond.chargen.jfx.creatures;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManager;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CommonCreatureController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.listcells.CreatureModuleReferenceListCell;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;

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

/**
 * @author prelle
 *
 */
public class CreatureModuleReferenceListView extends ListView<CreatureModuleReference> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(CompanionSection.class.getName());

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
        setStyle("-fx-min-width: 20em; -fx-pref-width: 26em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black;");
	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<CreatureModuleReference>, ListCell<CreatureModuleReference>>() {
			public ListCell<CreatureModuleReference> call(ListView<CreatureModuleReference> p) {
//				return new CreatureModuleReferenceListCell(control, CreatureModuleReference.this);
				return new CreatureModuleReferenceListCell(control);
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
