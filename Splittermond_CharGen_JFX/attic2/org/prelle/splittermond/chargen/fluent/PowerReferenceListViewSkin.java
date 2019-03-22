/**
 * 
 */
package org.prelle.splittermond.chargen.fluent;

import org.prelle.splimo.Power.SelectionType;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splittermond.chargen.jfx.SkillField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Spinner;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class PowerReferenceListViewSkin extends SkinBase<PowerReferenceListControl> {

	private ListView<PowerReference> list;
	
	//-------------------------------------------------------------------
	public PowerReferenceListViewSkin(PowerReferenceListControl control) {
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
		list.setCellFactory( (param) -> new PowerReferenceListCell(getSkinnable().getController().getPowerController()));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getChildren().add(list);
	}

}

class PowerReferenceListCell extends ListCell<PowerReference> {
	
	private transient PowerReference data;
	
	private PowerController control;
	private CheckBox checkBox;
	private HBox layout;
	private Label name;
	private Label shortDesc;
	private SkillField spinner;
	
	//-------------------------------------------------------------------
	public PowerReferenceListCell(PowerController control) {
		this.control = control;
		layout  = new HBox();
		checkBox= new CheckBox();
		name    = new Label();
		shortDesc=new Label();
		getStyleClass().add("power-reference-cell");

		spinner = new SkillField();

		name.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(name, Priority.ALWAYS);
		
		VBox bxNameAndDesc = new VBox(3, name, shortDesc);
		layout.getChildren().addAll(checkBox, bxNameAndDesc, spinner);
		layout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(bxNameAndDesc, Priority.ALWAYS);
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
        content.putString(data.getPower().getId().toString());
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
	protected void updateItem(PowerReference item, boolean empty) {
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
			
			boolean hideSpinner = item.getPower().getSelectable()==SelectionType.ALWAYS || item.getPower().getSelectable()==SelectionType.GENERATION;
			spinner.setVisible(!hideSpinner);
			spinner.setManaged(!hideSpinner);
			spinner.setText(item.getCount()+"");
			spinner.dec.setDisable(!control.canBeDecreased(item));
			spinner.inc.setDisable(!control.canBeIncreased(item));
			
			setGraphic(layout);
			name.setText(item.getPower().getName());
			shortDesc.setText(item.getPower().getDescription());
			
//			System.out.println("Cell = "+getStyleClass()+"  // "+getStyle());
//			System.out.println("  scene = "+this.getScene().getStylesheets());
		}

	}

}