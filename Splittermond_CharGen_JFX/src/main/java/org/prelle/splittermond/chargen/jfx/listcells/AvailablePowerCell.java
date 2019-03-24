package org.prelle.splittermond.chargen.jfx.listcells;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Power;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

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
import javafx.scene.layout.VBox;

public class AvailablePowerCell extends ListCell<Power> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private Power data;
	private CharacterController charGen;
	private PowerController control;
	private HBox layout;
	private Label cost;
	private Label name;
	private Label descr;
	
	//-------------------------------------------------------------------
	public AvailablePowerCell(CharacterController charGen) {
		this.charGen = charGen;
		this.control = charGen.getPowerController();
		
		name   = new Label();
		descr  = new Label();
		VBox col1 = new VBox(name,descr);
		cost  = new Label();
		cost.setMaxWidth(Double.MAX_VALUE);
		layout = new HBox();
		layout.getChildren().addAll(col1, cost);
		HBox.setHgrow(col1, Priority.ALWAYS);
		
		name.getStyleClass().add("text-small-subheader");
		descr.getStyleClass().add("text-tertiary-info");
		cost.setStyle("-fx-font-size: 200%");
		
		setPrefWidth(250);
		
		this.setOnDragDetected(event -> dragStarted(event));
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
        content.putString("power:"+data.getId());
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
	protected void updateItem(Power item, boolean empty) {
		super.updateItem(item, empty);
		data = item;
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
			return;
		} else {
			setGraphic(layout);
			name.setText(item.getName());
			descr.setText(item.getDescription());
			cost.setText(item.getCost()+"");
			setGraphic(layout);
		}
		
	}

}
