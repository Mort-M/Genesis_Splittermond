/**
 * 
 */
package org.prelle.splittermond.chargen.fluent;

import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.charctrl.LanguageController;

import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.Priority;

/**
 * @author prelle
 *
 */
public class LanguageReferenceListViewSkin extends SkinBase<LanguageReferenceListControl> {

	private ListView<LanguageReference> list;
	
	//-------------------------------------------------------------------
	public LanguageReferenceListViewSkin(LanguageReferenceListControl control) {
		super(control);
		initComponents();
		initLayout();
		// Initial data
		list.itemsProperty().set(control.getItems());
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getSkinnable().selectedItemProperty().set(n));
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		list = new ListView<>();
		list.setCellFactory( (param) -> new LanguageListCell(getSkinnable().getController().getLanguageController()));
		list.setFixedCellSize(35);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getChildren().add(list);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		list.prefHeightProperty().bind(Bindings.size(list.getItems()).multiply(list.getFixedCellSize()).add(5));
	}

}

class LanguageListCell extends ListCell<LanguageReference> {
	
	private transient LanguageReference data;
	
	private LanguageController control;
	private CheckBox checkBox;
	private HBox layout;
	private Label name;
	
	//-------------------------------------------------------------------
	public LanguageListCell(LanguageController control) {
		this.control = control;
		layout  = new HBox();
		checkBox= new CheckBox();
		name    = new Label();
		getStyleClass().add("power-reference-cell");

		name.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(name, Priority.ALWAYS);
		
		layout.getChildren().addAll(checkBox, name);
		layout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(name, Priority.ALWAYS);
		layout.setAlignment(Pos.CENTER);
		
		name.getStyleClass().add("base");
		checkBox.setStyle("-fx-font-size: 150%");
		checkBox.selectedProperty().addListener( (ov,o,n) -> {
			if (n==false)
				control.deselect(data);
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
        content.putString("language:"+data.getLanguage().getId().toString());
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
	protected void updateItem(LanguageReference item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
			setStyle("-fx-border-color: transparent;");
			return;
		} else {
			setStyle("-fx-border-color: -fx-base; ");
			data = item;
			checkBox.setSelected(true);
			checkBox.setDisable(!control.canBeDeselected(data));
			
			setGraphic(layout);
			name.setText(item.getLanguage().getName());
		}

	}

}