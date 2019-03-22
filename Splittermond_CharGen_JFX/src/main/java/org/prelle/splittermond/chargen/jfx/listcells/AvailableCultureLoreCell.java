package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.Language;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class AvailableCultureLoreCell extends ListCell<CultureLore> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private CultureLore data;
	private CharacterController charGen;
	private CultureLoreController control;
	private VBox layout;
	private Label name;
	private FlowPane flow;
	
	//-------------------------------------------------------------------
	public AvailableCultureLoreCell(CharacterController charGen) {
		this.charGen = charGen;
		this.control = charGen.getCultureLoreController();
		
		layout = new VBox();
		name   = new Label();
		flow   = new FlowPane(Orientation.HORIZONTAL);
		layout.getChildren().addAll(name, flow);
		
		name.getStyleClass().add("text-small-subheader");
		flow.getStyleClass().add("text-tertiary-info");
		
		setPrefWidth(250);
		
		
//		this.setOnTouchMoved(event -> logger.debug("onTouchMoved "+event));
		this.setOnDragDetected(event -> dragStarted(event));
		this.setOnMouseClicked(event -> {
			if (event.getClickCount()==2 && data!=null) {
				control.select(data);
			}
		});
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
        content.putString(data.getKey());
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
	protected void updateItem(CultureLore item, boolean empty) {
		super.updateItem(item, empty);
		data = item;
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
			return;
		} else {
			setGraphic(layout);
			name.setText(item.getName());
			flow.getChildren().clear();
			boolean fulfilled = item.getLanguages().isEmpty();
			Iterator<Language> it = item.getLanguages().iterator();
			while (it.hasNext()) {
				Language lang = it.next();
				Label label = new Label(lang.getName());
				// Color language depending on its availability
				if (charGen.getModel().hasLanguage(lang)) {
					label.setStyle("-fx-text-fill: green");
					fulfilled = true;
				} else {
					label.setStyle("-fx-text-fill: red");
				}
				flow.getChildren().add(label);
				if (it.hasNext())
					flow.getChildren().add(new Label(", "));
			}
			if (fulfilled) {
				layout.getStyleClass().clear();
				layout.getStyleClass().add("selectable-list-item");
			} else {
				layout.getStyleClass().clear();
				layout.getStyleClass().add("unselectable-list-item");
			}
		}
		
	}

}
