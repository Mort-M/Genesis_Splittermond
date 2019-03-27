package org.prelle.splittermond.chargen.jfx.listcells;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.charctrl.ResourceController;
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
import javafx.scene.layout.VBox;

public class ResourceListCell extends ListCell<Resource> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private Resource data;
	private ResourceController charGen;
	private HBox layout;
	private VBox layoutWithoutGraphic;
	private Label name;
	private HBox flow;

	//-------------------------------------------------------------------
	public ResourceListCell(ResourceController charGen) {
		this.charGen = charGen;
		layoutWithoutGraphic = new VBox();
		name   = new Label();
		flow   = new HBox(2);
		layoutWithoutGraphic.getChildren().addAll(name, flow);
		layout = new HBox(2);
		layout.getChildren().addAll(layoutWithoutGraphic);
		layout.getStyleClass().add("content");

		name.getStyleClass().add("text-small-subheader");
		flow.getStyleClass().add("text-tertiary-info");

		setStyle("-fx-pref-width: 13em");
		//		setPrefWidth(280);


		this.setOnTouchMoved(event -> logger.debug("onTouchMoved "+event));
		this.setOnDragDetected(event -> dragStarted(event));
		this.setOnMouseClicked(event -> clicked(event));
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
		if (data==null)
			return;
		content.putString(data.getId());
		db.setContent(content);

		/* Drag image */
		WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
		db.setDragView(snapshot);

		event.consume();
	}

	//-------------------------------------------------------------------
	private void clicked(MouseEvent event) {
		if (event.getClickCount()!=2)
			return;
		if (data==null)
			return;
		LogManager.getLogger("splittermond.jfx").debug("Select "+data);
		charGen.openResource(data);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Resource item, boolean empty) {
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
			String product = item.getProductName()+" "+item.getPage();
			flow.getChildren().add(new Label(product));
		}

	}

}