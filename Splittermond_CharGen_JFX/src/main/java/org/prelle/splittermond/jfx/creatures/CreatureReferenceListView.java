/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureReference;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
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
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class CreatureReferenceListView extends ListView<CreatureReference> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;
	
	private SpliMoCharacter model;
	
	//--------------------------------------------------------------------
	public CreatureReferenceListView() {
		initComponents();
		initValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setPlaceholder(new Text(UI.getString("placeholder.enhancement")));
        setStyle("-fx-pref-width: 20em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black;");
	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<CreatureReference>, ListCell<CreatureReference>>() {
			public ListCell<CreatureReference> call(ListView<CreatureReference> p) {
				return new CreatureReferenceListCell();
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}
	
	//--------------------------------------------------------------------
	public void setModel(SpliMoCharacter model) {
		this.model = model;
	}
	
	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String creatureID = db.getString();
        	logger.debug("Dropped "+creatureID);
        	Creature res = SplitterMondCore.getCreature(creatureID);
        	if (res!=null) {
        		CreatureReference ref = new CreatureReference(res);
        		model.addCreature(ref);
        	} else
        		logger.debug("Cannot add "+res);
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

class CreatureReferenceListCell extends ListCell<CreatureReference> {
	
	private transient CreatureReference data;
	
	private CheckBox checkBox;
	private HBox layout;
	private Label name;
	
	//-------------------------------------------------------------------
	public CreatureReferenceListCell() {
		layout  = new HBox();
		checkBox= new CheckBox();
		name    = new Label();
		layout.getChildren().addAll(checkBox, name);
		
		name.getStyleClass().add("text-small-subheader");
		checkBox.getStyleClass().add("text-subheader");
		

		setPrefWidth(250);
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				CreatureReferenceListCell.this.changed(arg0, arg1, arg2);
			}});
		

		setAlignment(Pos.CENTER);
		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
//		if (!charGen.canBeDeselected(data))
//			return;
		if (data==null)
			return;
		
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(data.getUniqueId().toString());
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
	protected void updateItem(CreatureReference item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
//			setStyle("-fx-border: 0px; -fx-border-color: transparent; -fx-padding: 2px; -fx-background-color: transparent");
			return;
		} else {
			data = item;
			checkBox.setSelected(true);
//			checkBox.setDisable(!charGen.canBeRemoved(data));
			
			setGraphic(layout);
			name.setText(item.getTemplate().getName());
//			if (item.getSkillSpecialization()!=null) 
//				name.setText(item.getSkillSpecialization().getSkill().getName()+"/"+item.getSkillSpecialization().getName()+" +1");
////			if (charGen.canBeDeselected(item)) {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("selectable-list-item");
////			} else {
////				layout.getStyleClass().clear();
////				layout.getStyleClass().add("unselectable-list-item");
////			}
		}

	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
//		if (old==true && val==false) {
//			charGen.removeCreature(data);
//		}
	}

}

