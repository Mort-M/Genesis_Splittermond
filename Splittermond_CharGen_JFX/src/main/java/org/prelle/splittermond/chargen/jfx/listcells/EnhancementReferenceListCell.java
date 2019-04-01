package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.dialogs.EditCarriedItemDialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

public class EnhancementReferenceListCell extends ListCell<EnhancementReference> {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditCarriedItemDialog.class.getName());

	private transient EnhancementReference data;

	private NewItemController charGen;
	private CheckBox checkBox;
	private HBox layout;
	private Label name;

	//-------------------------------------------------------------------
	public EnhancementReferenceListCell(NewItemController charGen) {
		this.charGen = charGen;

		layout  = new HBox();
		checkBox= new CheckBox();
		name    = new Label();
		layout.getChildren().addAll(checkBox, name);
//		layout.getStyleClass().add("content");

		name.getStyleClass().add("base");
		checkBox.getStyleClass().add("text-subheader");


		setPrefWidth(250);
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				EnhancementReferenceListCell.this.changed(arg0, arg1, arg2);
			}});


		setAlignment(Pos.CENTER);
		this.setOnDragDetected(event -> dragStarted(event));
		setStyle("-fx-pref-width: 20em");
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
        content.putString(data.getID());
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
	protected void updateItem(EnhancementReference item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setGraphic(null);
			name.setText(null);
//			setStyle("-fx-border: 0px; -fx-border-color: transparent; -fx-padding: 2px; -fx-background-color: transparent");
			return;
		} else {
			data = item;
			checkBox.setSelected(true);
			checkBox.setDisable(!charGen.canBeRemoved(data));

			setGraphic(layout);
			name.setText(item.getName());
			if (item.getSkillSpecialization()!=null)
				name.setText(item.getSkillSpecialization().getSkill().getName()+"/"+item.getSkillSpecialization().getName()+" +1");
			if (item.getSpellValue()!=null)
				name.setText(String.format(UI.getString("screen.enhancements.embedspell.cell"), item.getSpellValue().getSpell().getName()));
//			if (charGen.canBeDeselected(item)) {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("selectable-list-item");
//			} else {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("unselectable-list-item");
//			}
		}

	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME).debug("call removeEnhancement("+data+")");
			charGen.removeEnhancement(data);
		}
	}

}
