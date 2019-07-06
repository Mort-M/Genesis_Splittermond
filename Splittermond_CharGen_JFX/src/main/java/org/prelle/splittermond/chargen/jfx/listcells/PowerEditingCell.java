package org.prelle.splittermond.chargen.jfx.listcells;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PowerEditingCell extends ListCell<PowerReference> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private Label lbName;
	private Label lbShortDesc;
	private Spinner<Integer> spinner;
	private HBox layout;
	private PowerController charGen;
	private PowerReference data;
	
	//-------------------------------------------------------------------
	public PowerEditingCell(PowerController charGen) {
		this.charGen = charGen;
		
		lbName = new Label();
		lbName.getStyleClass().add("base");
		lbName.setWrapText(true);
		lbName.setMaxWidth(Double.MAX_VALUE);
		lbShortDesc = new Label();
		lbShortDesc.setWrapText(true);
		spinner = new Spinner<Integer>(0, 20, 1);
		spinner.valueProperty().addListener( (ov,o,n) -> changed(ov, o, n) );
		spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		spinner.setStyle("-fx-pref-width: 6em");
		
		VBox col1 = new VBox(2, lbName, lbShortDesc);
		
		layout = new HBox(10, col1, spinner);
		HBox.setHgrow(col1, Priority.ALWAYS);
		setStyle("-fx-pref-width: 25em");
		
		spinner.valueProperty().addListener( (ov,o,n) -> {
			PowerReference data = PowerEditingCell.this.getItem();
			if (data==null)
				return;
			
			if (o<n) {
				logger.debug("Increase "+data);
				charGen.increase(data);
			} else if (o>n) {
				logger.debug("Decrease "+data);
				charGen.decrease(data);
			}
		});
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(PowerReference item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;
		
		if (item==null || empty ) {
			this.setGraphic(null);
			return;
		}
		
		lbName.setText(item.getPower().getName());
		lbShortDesc.setText(item.getPower().getDescription());
		spinner.getValueFactory().setValue(item.getCount());
		spinner.setUserData(item);
		switch (item.getPower().getSelectable()) {
		case ALWAYS:
		case GENERATION:
			spinner.setVisible(false);
			spinner.setManaged(false);
			break;
		case LEVEL:
		case MAX3:
		case MULTIPLE:
			spinner.setVisible(true);
			spinner.setManaged(true);
			break;
		}
		this.setGraphic(layout);
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
		if (val>data.getCount() && charGen.canBeIncreased(data)) {
			charGen.increase(data);
		} else if (val<data.getCount() && charGen.canBeDecreased(data)) {
			charGen.decrease(data);
		} else {
			// Revert back
			spinner.getValueFactory().setValue(data.getCount());
		}
		
//		removeScrollHandler(box);
	}

}
