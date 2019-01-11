/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CommonCreatureController;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.requirements.Requirement;

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

public class CreatureModuleListCell extends ListCell<CreatureModule> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);

	private Label lbName;
	private Label lbReference;
	private Label lbCost;
	private HBox  layout;
	
	private CreatureModule data;
	private CommonCreatureController control;

	//-------------------------------------------------------------------
	public CreatureModuleListCell(CommonCreatureController ctrl) {
		this.control = ctrl;
		lbName = new Label();
		lbName.setStyle("-fx-font-weight: bold");
		lbReference = new Label();
		lbCost = new Label();
		lbCost.setStyle("-fx-font-weight: bold; -fx-font-size: 200%");

		VBox col1 = new VBox(5, lbName, lbReference);
		col1.setMaxWidth(Double.MAX_VALUE);
		layout = new HBox(col1, lbCost);
		layout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(col1, Priority.ALWAYS);
		
		initInteractivity();
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	public void updateItem(CreatureModule item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(layout);
			lbName.setText(item.getName());
			lbReference.setText(item.getProductName()+" "+item.getPage());
			lbCost.setText(String.valueOf(item.getCost()));
			lbReference.setStyle("-fx-text-fill: black");
			
			if (!control.canSelect(data)) {
				List<String> missing = new ArrayList<>();
				for (Requirement req : data.getRequirements()) {
					if (!control.isRequirementMet(req))
						missing.add(SplitterTools.getRequirementString(req));
				}
				lbReference.setText(String.join(", ", missing));
				lbReference.setStyle("-fx-text-fill: red");
			}
			this.setDisable(!control.canSelect(data));
		}
	}
	

	//-------------------------------------------------------------------
	private void initInteractivity() {
		this.setOnDragDetected(event -> dragStarted(event));
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
        String id = "creaturemodule:"+data.getId();
        content.putString(id);
        db.setContent(content);

        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);

        event.consume();
    }

}