package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AttentionPane;
import org.prelle.javafx.SymbolIcon;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CommonCreatureController;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureModuleReference.NecessaryChoice;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
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

public class CreatureModuleReferenceListCell extends ListCell<CreatureModuleReference> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private CommonCreatureController control;
	
	private Button btnEdit;
	private Label lbName;
	private Label lbReference;
	private Label lbCost;
	private HBox  layout;
	private AttentionPane attention;
	
	private CreatureModuleReference data;

	//-------------------------------------------------------------------
	public CreatureModuleReferenceListCell(CommonCreatureController control) {
		this.control = control;
		btnEdit = new Button(null, new SymbolIcon("edit"));
		lbName = new Label();
		lbName.setStyle("-fx-font-weight: bold");
		lbReference = new Label();
		lbReference.setWrapText(true);
		attention = new AttentionPane(lbReference);
		lbCost = new Label();
		lbCost.setStyle("-fx-font-weight: bold; -fx-font-size: 200%");

		VBox col1 = new VBox(5, lbName, attention);
		col1.setMaxWidth(Double.MAX_VALUE);
		layout = new HBox(btnEdit, col1, lbCost);
		layout.setStyle("-fx-spacing: 0.2em");
		layout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(col1, Priority.ALWAYS);
		
		
		initInteractivity();
//        setStyle("-fx-pref-width: 25em;");
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	public void updateItem(CreatureModuleReference item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(layout);
			lbName.setText(item.getModule().getName());
			lbReference.setText(item.getModule().getProductName()+" "+item.getModule().getPage());
			lbCost.setText(String.valueOf(item.getModule().getCost()));
			
			List<String> choices = new ArrayList<>();
			List<String> origin = new ArrayList<>();
			boolean requiresAttention = false;
			for (NecessaryChoice choice : control.getChoicesToMake()) {
				if (choice.originModule!=item)
					continue;
				if (choice.getMadeChoice()!=null) {
					choices.add(SplitterTools.getModificationString(choice.getMadeChoice()));
				} else {
					origin.add(SplitterTools.getModificationString(choice.getOriginChoice()));
					requiresAttention = true;
				}
			}
			lbReference.setText(String.join(",\n", choices));
			attention.setAttentionFlag(requiresAttention);
			attention.setAttentionToolTip(origin);
			btnEdit.setVisible(requiresAttention || !choices.isEmpty()); 
			btnEdit.setUserData(data);
		}
	}
	

	//-------------------------------------------------------------------
	private void initInteractivity() {
		this.setOnDragDetected(event -> dragStarted(event));
		
		setOnMouseClicked(ev -> {
			if (ev.getClickCount()==2) {
				btnEdit.fireEvent(new ActionEvent(btnEdit, btnEdit));
			}
		});
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

		Node source = (Node) event.getSource();
		logger.debug("drag src = "+source);

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        String id = "creaturemodule:"+data.getUniqueId();
        content.putString(id);
        db.setContent(content);

        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);

        event.consume();
    }

	//-------------------------------------------------------------------
	public void setOnAction(EventHandler<ActionEvent> onEdit) {
		btnEdit.setOnAction(onEdit);
	}
}