package org.prelle.splittermond.jfx.cultures;

import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class CultureLorePane extends VBox implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private CultureLoreController control;
	private SpliMoCharacter model;

	private ListView<CultureLoreReference> table;

	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public CultureLorePane(CultureLoreController ctrl) {
		if (ctrl==null)
			throw new NullPointerException();
		this.control = ctrl;

		GenerationEventDispatcher.addListener(this);
		initComponents();
		doValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;

		updateContent();
	}

	//--------------------------------------------------------------------
	private void initComponents() {

		table = new ListView<CultureLoreReference>();
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
		table.setCellFactory(new Callback<ListView<CultureLoreReference>, ListCell<CultureLoreReference>>() {
			public ListCell<CultureLoreReference> call(ListView<CultureLoreReference> p) {
				return new NewCultureLoreEditingCell(control);
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
        	CultureLore res = SplitterMondCore.getCultureLore(cultLoreID);
        	if (control.canBeSelected(res))
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
	private void updateContent() {
		if (model!=null) {
			table.getItems().clear();
			table.getItems().addAll(model.getCultureLores());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CULTURELORE_ADDED:
//			addChoice.getItems().remove(((CultureLoreReference)event.getKey()).getCultureLore());
			updateContent();
			break;
		case CULTURELORE_REMOVED:
//			addChoice.getItems().add(((CultureLoreReference)event.getKey()).getCultureLore());
//			Collections.sort(addChoice.getItems());
			updateContent();
			break;
		case EXPERIENCE_CHANGED:
			updateContent();
			break;
		}
	}

	//-------------------------------------------------------------------
	public List<CultureLoreReference> getCultureLores() {
		return table.getItems();
	}

	//--------------------------------------------------------------------
	/**
	 * @return the control
	 */
	CultureLoreController getControl() {
		return control;
	}

}


class NewCultureLoreEditingCell extends ListCell<CultureLoreReference> {
	
	private transient CultureLoreReference data;
	
	private CultureLoreController charGen;
	private CheckBox checkBox;
	private HBox layout;
	private Label name;
	
	//-------------------------------------------------------------------
	public NewCultureLoreEditingCell(CultureLoreController charGen) {
		this.charGen = charGen;
		
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
				NewCultureLoreEditingCell.this.changed(arg0, arg1, arg2);
			}});
		

		setAlignment(Pos.CENTER);
		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		if (!charGen.canBeDeselected(data))
			return;
		
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(data.getCultureLore().getKey());
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
	protected void updateItem(CultureLoreReference item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
//			setStyle("-fx-border: 0px; -fx-border-color: transparent; -fx-padding: 2px; -fx-background-color: transparent");
			return;
		} else {
			checkBox.setSelected(true);
			checkBox.setDisable(!charGen.canBeDeselected(item));
			data = item;
			
			setGraphic(layout);
			name.setText(item.getCultureLore().getName());
			if (charGen.canBeDeselected(item)) {
				layout.getStyleClass().clear();
				layout.getStyleClass().add("selectable-list-item");
			} else {
				layout.getStyleClass().clear();
				layout.getStyleClass().add("unselectable-list-item");
			}
		}

	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			charGen.deselect(data);
		}
	}

}
