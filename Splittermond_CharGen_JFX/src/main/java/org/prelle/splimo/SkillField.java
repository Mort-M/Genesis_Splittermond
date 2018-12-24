package org.prelle.splimo;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SkillField extends HBox {
	public Button dec;
	public Button inc;
	private TextField value;
	
	//--------------------------------------------------------------------
	public SkillField() {
		dec  = new Button("\uE0C6");
		inc  = new Button("\uE0C5");
		inc.getStyleClass().add("mini-button");
		dec.getStyleClass().add("mini-button");
		value = new TextField();
		value.setPrefColumnCount(1);
		value.setEditable(false);
		value.setFocusTraversable(false);
		
		this.getChildren().addAll(dec, value, inc);
		setStyle("-fx-min-width: 7.5em");
	}
	
	//--------------------------------------------------------------------
	public SkillField(String text) {
		dec  = new Button("\uE0C6");
		inc  = new Button("\uE0C5");
		inc.getStyleClass().add("mini-button");
		dec.getStyleClass().add("mini-button");
		value = new TextField();
		value.setPrefColumnCount(text.length());
		value.setText(text);
		value.setEditable(false);
		value.setFocusTraversable(false);
		this.getChildren().addAll(dec, value, inc);
		setStyle("-fx-min-width: 7.5em");
	}
	
	//--------------------------------------------------------------------
	public void setText(String txt) {
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