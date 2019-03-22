package org.prelle.splittermond.chargen.jfx.listcells;

import org.prelle.javafx.SymbolIcon;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.CultureLoreController;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class NewCultureLoreEditingCell extends ListCell<CultureLoreReference> {
	
	private transient CultureLoreReference data;
	
	private CultureLoreController charGen;
	private SymbolIcon icoLock;
	private HBox layout;
	private Label name;
	
	//-------------------------------------------------------------------
	public NewCultureLoreEditingCell(CharacterController charGen) {
		this.charGen = charGen.getCultureLoreController();
		
		layout  = new HBox();
		icoLock = new SymbolIcon("lock");
		name    = new Label();
		layout.getChildren().addAll(name, icoLock);
		
		name.getStyleClass().add("text-small-subheader");
		icoLock.setStyle("-fx-font-size: 200%");
		name.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(name, Priority.ALWAYS);

//		setPrefWidth(250);

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
			data = item;
			
			setGraphic(layout);
			icoLock.setManaged(!charGen.canBeDeselected(item));
			name.setText(item.getCultureLore().getName());
		}

	}

}
