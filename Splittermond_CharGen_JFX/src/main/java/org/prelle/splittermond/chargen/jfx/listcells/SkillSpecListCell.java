package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.skills.ListElemSpecialization;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SkillSpecListCell extends  ListCell<ListElemSpecialization> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;
	
	private CheckBox check1;
	private CheckBox check2;
	private CheckBox check3;
	private CheckBox check4;
	private Label label;
	private Label count;
	private HBox box;
	private MastershipController control;
	
	//--------------------------------------------------------------------
	public SkillSpecListCell(CharacterController ctrl) {
		this.control = ctrl.getMastershipController();
		check1 = new CheckBox();
		check2 = new CheckBox();
		check3 = new CheckBox();
		check4 = new CheckBox();
		label  = new Label();
		count  = new Label();
		box = new HBox(5);
		box.getChildren().addAll(check1, check2, check3, check4, label, count);
		HBox.setHgrow(label, Priority.ALWAYS);
		label.setMaxWidth(Double.MAX_VALUE);
		this.setPrefWidth(0);
		box.prefWidthProperty().bind(this.widthProperty());
		
		check1.setOnAction(event -> changed(1, check1.selectedProperty().get()));
		check2.setOnAction(event -> changed(2, check2.selectedProperty().get()));
		check3.setOnAction(event -> changed(3, check3.selectedProperty().get()));
		check4.setOnAction(event -> changed(3, check3.selectedProperty().get()));
	}
	
	//--------------------------------------------------------------------
	private void changed(int level, boolean selected) {
		logger.debug("SkillSpecListCell.changed("+level+", "+selected+")");
		if (selected)
			control.select(getItem().data, level);
		else
			control.deselect(getItem().data, level);
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(ListElemSpecialization item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			setText(null);
		} else {
			label.setText(item.data.getName());
			count.setText(item.count+"x");
			setGraphic(box);
			check1.setSelected(item.level>0);
			check2.setSelected(item.level>1);
			check3.setSelected(item.level>2);
			check4.setSelected(item.level>3);
			check1.setDisable(!control.isEditable(item.data, 1));
			check2.setDisable(!control.isEditable(item.data, 2));
			check3.setDisable(!control.isEditable(item.data, 3));
			check4.setDisable(!control.isEditable(item.data, 4));
		}
		
	}
}