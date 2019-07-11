package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CreatureReferenceListCell extends ListCell<CreatureReference> {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CompanionSection.class.getName());

	private transient CreatureReference data;

	private CharacterController control;
	private ResourceController charGen;
	private ScreenManagerProvider provider;

	private VBox layout;
	private Label name;
	private Button btnEdit;
	private Label lbType;
	private Label lbTrain;

	private transient CreatureReference item = null;

	//-------------------------------------------------------------------
	public CreatureReferenceListCell(CharacterController charGen, ScreenManagerProvider provider) {
		this.control = charGen;
		this.charGen = charGen.getResourceController();
		this.provider= provider;

		initComponents();
		initStyle();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		name    = new Label();
		btnEdit = new Button("\uE1C2");
		lbType  = new Label("?");
		lbTrain = new Label("?");
	}

	//-------------------------------------------------------------------
	private void initStyle() {
		name.getStyleClass().add("text-small-subheader");
		name.getStyleClass().add("text-small-subheader");

		btnEdit.setStyle("-fx-background-color: transparent");

//		setStyle("-fx-pref-width: 15em");
//		layout.getStyleClass().add("content");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label hdType  = new Label(RES.getString("label.type"));
		Label hdTrain = new Label(RES.getString("label.training"));
		hdType.getStyleClass().add("base");
		hdTrain.getStyleClass().add("base");
		
		GridPane grid = new GridPane();
		grid.add(hdType , 0, 0);
		grid.add(lbType , 1, 0);
		grid.add(hdTrain, 0, 1);
		grid.add(lbTrain, 1, 1);
		grid.setVgap(10);
		
		HBox line2 = new HBox(5);
		line2.getChildren().addAll(btnEdit, grid);

		grid.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(grid, Priority.ALWAYS);

		layout  = new VBox(5, name, line2);

		
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		this.setOnDragDetected(event -> dragStarted(event));
		this.setOnMouseClicked(event -> clicked(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.debug("drag started for "+data);
		if (data==null)
			return;
//		logger.debug("canBeDeselected = "+charGen.canBeDeselected(data));
//		logger.debug("canBeTrashed    = "+((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true));
//		if (!charGen.canBeDeselected(data) && !((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true))
//			return;
//
//		Node source = (Node) event.getSource();
//		logger.debug("drag src = "+source);
//
//		/* drag was detected, start a drag-and-drop gesture*/
//        /* allow any transfer mode */
//        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
//
//        /* Put a string on a dragboard */
//        ClipboardContent content = new ClipboardContent();
//        String id = "resource:"+data.getCreature().getId()+"|desc="+data.getDescription()+"|idref="+data.getIdReference();
//        content.putString(id);
//        db.setContent(content);
//
//        /* Drag image */
//        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
//        db.setDragView(snapshot);
//
//        event.consume();
    }

	//-------------------------------------------------------------------
	private void clicked(MouseEvent event) {
		if (event.getClickCount()!=2)
			return;
		if (data==null)
			return;
//		LogManager.getLogger("splittermond.jfx").debug("Deselect "+data);
//		charGen.deselect(data);
	}

//	//-------------------------------------------------------------------
//	private void askName(CreatureReference ref) {
//		Label lbInput = new Label(UI.getString("screen.creatures.namedialog.mess"));
//		TextField tfInput = new TextField();
//		tfInput.setStyle("-fx-pref-width: 30em");
//
//		VBox layout = new VBox(5, lbInput, tfInput);
//		CloseType close = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, UI.getString("screen.creatures.namedialog.title"), layout);
//		if (close==CloseType.OK) {
//			String name = tfInput.getText();
//			logger.info("Rename creature "+ref+" to \""+name+"\"");
//			ref.setName(name);
//		}
//	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(CreatureReference resRef, boolean empty) {
		super.updateItem(resRef, empty);

		if (empty) {
			setGraphic(null);
			name.setText(null);
//			setStyle("-fx-border: 0px; -fx-border-color: transparent; -fx-padding: 2px; -fx-background-color: transparent");
			return;
		} else {
			data = resRef;
			name.setText(resRef.getName());
			// Build list of creature types
			List<String> names = new ArrayList<>();
			resRef.getCreatureTypes().forEach(tmp -> names.add(tmp.getName()));
			lbType.setText(String.join(", ", names));
			// Trainings
			names.clear();
			resRef.getTrainings().forEach(tmp -> names.add(tmp.getModule().getName()));
			lbTrain.setText(String.join(", ", names));

			setGraphic(layout);
		}

	}

	//-------------------------------------------------------------------
	public void setOnAction(EventHandler<ActionEvent> handler) {
		btnEdit.setOnAction(handler);
	}

}