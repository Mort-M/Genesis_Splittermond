package org.prelle.splittermond.jfx.master;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ValueField extends HBox {
	public Button dec;
	public Button inc;
	private Label value;
	
	//--------------------------------------------------------------------
	public ValueField() {
		dec  = new Button("<");
		inc  = new Button(">");
		value = new Label();
		this.getChildren().addAll(dec, value, inc);
		value.getStyleClass().add("text-subheader");
	}
	
	//--------------------------------------------------------------------
	public ValueField(String text) {
		this();
		value.setText(text);
	}
	
	//--------------------------------------------------------------------
	public void setValue(String txt) {
		this.value.setText(txt);
	}

	//--------------------------------------------------------------------
	public int getInt() {
		try {
			return Integer.parseInt(value.getText());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}