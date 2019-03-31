package org.prelle.splittermond.chargen.jfx;

import org.prelle.splimo.BasePluginData;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DescriptionBox extends VBox {

	private Label lblName;
	private Label lblProduct;
	private Label lblDescr;

	//-------------------------------------------------------------------
	public DescriptionBox() {
		lblName = new Label();
		lblName.getStyleClass().add("text-subheader");
		lblProduct = new Label();
		lblDescr = new Label();
		lblDescr.setWrapText(true);
		
		getChildren().addAll(lblName, lblProduct, lblDescr);
		VBox.setMargin(lblDescr, new Insets(20, 20, 0, 0));
		setStyle("-fx-pref-width: 25em");
	}

	//-------------------------------------------------------------------
	public void showData(BasePluginData data) {
		if (data==null) {
			lblName.setText(null);
			lblProduct.setText(null);
			lblDescr.setText(null);
		} else {
			lblName.setText(data.getName());
			lblProduct.setText(data.getProductName()+" "+data.getPage());
			lblDescr.setText(data.getHelpText());
		}
	}

}
