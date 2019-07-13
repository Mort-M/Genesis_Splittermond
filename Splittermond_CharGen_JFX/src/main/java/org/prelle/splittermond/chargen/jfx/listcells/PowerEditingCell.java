package org.prelle.splittermond.chargen.jfx.listcells;

import org.prelle.rpgframework.jfx.NumericalValueField;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.charctrl.PowerController;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PowerEditingCell extends ListCell<PowerReference> {

//	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private Label lbName;
	private Label lbShortDesc;
	private NumericalValueField<Power, PowerReference> spinner;
	private HBox layout;
	private PowerController charGen;
	private VBox col1;
	
	//-------------------------------------------------------------------
	public PowerEditingCell(PowerController charGen) {
		this.charGen = charGen;
		
		lbName = new Label();
		lbName.getStyleClass().add("base");
		lbName.setWrapText(true);
		lbName.setMaxWidth(Double.MAX_VALUE);
		lbShortDesc = new Label();
		lbShortDesc.setWrapText(true);
		
		col1 = new VBox(2, lbName, lbShortDesc);
		
		layout = new HBox(10, col1);
		HBox.setHgrow(col1, Priority.ALWAYS);
		layout.setAlignment(Pos.CENTER_RIGHT);
		setStyle("-fx-pref-width: 25em");
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(PowerReference item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item==null || empty ) {
			this.setGraphic(null);
			return;
		}
		
		lbName.setText(item.getPower().getName());
		lbShortDesc.setText(item.getPower().getDescription());
		layout.getChildren().retainAll(col1);
		switch (item.getPower().getSelectable()) {
		case ALWAYS:
		case GENERATION:
			break;
		case LEVEL:
		case MAX3:
		case MULTIPLE:
			spinner = new NumericalValueField<Power, PowerReference>(item, charGen);
//			spinner.setStyle("-fx-pref-width: 6em");
			layout.getChildren().add(spinner);
			break;
		}
		this.setGraphic(layout);
	}

}
