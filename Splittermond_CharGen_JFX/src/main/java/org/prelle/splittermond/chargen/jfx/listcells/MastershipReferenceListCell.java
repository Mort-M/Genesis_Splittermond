/**
 *
 */
package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.SkillSpecializationValue;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MastershipReferenceListCell extends ListCell<MastershipReference> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private MastershipController control;
	private Label lblName;
	private Label lblRequire;
	private Label lblLevel;
	private ValueField field;
	private StackPane stack;

	private MastershipReference data;

	//-------------------------------------------------------------------
	public MastershipReferenceListCell(MastershipController ctrl) {
		this.control = ctrl;

		initComponents();
		initLayout();
		initInteractivity();
		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblName    = new Label();
		lblRequire = new Label();
		lblLevel   = new Label();
		field      = new ValueField();

		lblName.getStyleClass().add("base");
		lblLevel.getStyleClass().add("text-subheader");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		VBox box = new VBox();
		box.getChildren().addAll(lblName, lblRequire);

		stack = new StackPane();
		stack.getChildren().addAll(lblLevel, box, field);
		StackPane.setAlignment(box, Pos.TOP_LEFT);
		StackPane.setAlignment(lblLevel, Pos.TOP_RIGHT);
		StackPane.setAlignment(field, Pos.TOP_RIGHT);
		stack.getStyleClass().add("content");

		field.setMaxWidth(Double.MAX_VALUE);
		field.setAlignment(Pos.CENTER_RIGHT);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		field.dec.setOnAction(event -> control.deselect(data.getSpecialization().getSpecial(), data.getSpecialization().getLevel()));
		field.inc.setOnAction(event -> control.select(data.getSpecialization().getSpecial(), data.getSpecialization().getLevel()+1));
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(MastershipReference item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(stack);
			if (item.getMastership()!=null) {
				lblName.setText(item.getMastership().getName());
				lblLevel.setText(String.valueOf(item.getMastership().getLevel()));
				field.setVisible(false);
				lblLevel.setVisible(true);
				stack.setDisable(!control.canBeDeselected(item.getMastership()));
			} else if (item.getSpecialization()!=null) {
				SkillSpecializationValue special = item.getSpecialization();
				lblName.setText(item.getSpecialization().getName());
				lblRequire.setText(UI.getString("label.specialization")+" "+item.getSpecialization().getSpecial().getSkill().getName());
				lblLevel.setText(String.valueOf(special.getLevel()));
				field.setValue(String.valueOf(special.getLevel()));
				field.setVisible(true);
				field.dec.setDisable(!control.canBeDeselected(special.getSpecial(), special.getLevel()));
				field.inc.setDisable(!control.canBeSelected(special.getSpecial(), special.getLevel()+1));
				lblLevel.setVisible(false);
				stack.setDisable(!control.canBeDeselected(item.getSpecialization().getSpecial(), item.getSpecialization().getLevel()));
			}
		}
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        if (data==null)
        	return;
        if (data.getMastership()!=null)
        	content.putString("master:deselect:"+data.getMastership().getSkill().getId()+"/"+data.getMastership().getKey());
        else
        	content.putString("special:deselect:"+data.getSpecialization().getSpecial().getSkill()+"/"+data.getSpecialization().getSpecial().getId()+"/"+data.getSpecialization().getSpecial().getLevel());
        db.setContent(content);
        logger.debug("Drag started: "+content.getString());

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
        if (data.getMastership()!=null) {
        	LogManager.getLogger("splittermond.jfx").debug("Deselect "+data);
            control.deselect( data.getMastership() );
        } else {
        	LogManager.getLogger("splittermond.jfx").debug("Deselect "+data.getSpecialization().getSpecial().getSkill()+"/"+data.getSpecialization().getSpecial().getId()+"/"+data.getSpecialization().getSpecial().getLevel());
        	if (!control.canBeDeselected(data.getSpecialization().getSpecial(), 1))
        		return;
            control.deselect( data.getSpecialization().getSpecial(), 1);
        }
    }
}
