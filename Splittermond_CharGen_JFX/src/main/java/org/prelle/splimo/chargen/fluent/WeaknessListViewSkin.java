/**
 * 
 */
package org.prelle.splimo.chargen.fluent;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class WeaknessListViewSkin extends SkinBase<WeaknessListControl> {

	private ListView<String> list;
	
	//-------------------------------------------------------------------
	public WeaknessListViewSkin(WeaknessListControl control) {
		super(control);
		initComponents();
		initLayout();
		
		// Initial data
		list.itemsProperty().set(control.getItems());
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getSkinnable().selectedItemProperty().set(n));
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		list = new ListView<>();
		list.setCellFactory( (param) -> new WeaknessListCell(getSkinnable().getController().getModel()));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getChildren().add(list);
	}

}

class WeaknessListCell extends ListCell<String> {
	
	private transient String data;
	
	private CheckBox checkBox;
	private HBox layout;
	private Label name;
	
	//-------------------------------------------------------------------
	public WeaknessListCell(SpliMoCharacter model) {
		layout  = new HBox();
		checkBox= new CheckBox();
		name    = new Label();
		layout.getChildren().addAll(checkBox, name);
		getStyleClass().add("weakness-cell");
		
//		name.getStyleClass().add("text-small-subheader");
		checkBox.getStyleClass().add("text-subheader");
		checkBox.selectedProperty().addListener( (ov,o,n) -> {
			if (n==false) {
				model.getWeaknesses().remove(data);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.WEAKNESS_REMOVED, data));
			}
		});

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
        content.putString(data);
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
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
			return;
		} else {
			data = item;
			checkBox.setSelected(true);
			
			setGraphic(layout);
			name.setText(item);
		}

	}
	
}