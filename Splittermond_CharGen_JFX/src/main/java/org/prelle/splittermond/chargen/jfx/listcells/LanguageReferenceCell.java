package org.prelle.splittermond.chargen.jfx.listcells;

import org.prelle.splimo.LanguageReference;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class LanguageReferenceCell extends ListCell<LanguageReference> {
	
	private Label lbName;
	
	//-------------------------------------------------------------------
	public LanguageReferenceCell() {
		lbName = new Label();
		lbName.getStyleClass().add("base");
		lbName.setWrapText(true);
		lbName.setMaxWidth(Double.MAX_VALUE);
//		setStyle("-fx-pref-width: 25em");
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(LanguageReference item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item==null || empty ) {
			this.setGraphic(null);
			return;
		}
		
		lbName.setText(item.getLanguage().getName());
		this.setGraphic(lbName);
	}

}
