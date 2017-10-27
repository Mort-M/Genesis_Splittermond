package org.prelle.splittermond.jfx.skills;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SkillField extends HBox {
	public Button dec;
	public Button inc;
	private TextField value;
	
	//--------------------------------------------------------------------
	public SkillField() {
		dec  = new Button("<");
		inc  = new Button(">");
		value = new TextField();
		value.setPrefColumnCount(1);
		this.getChildren().addAll(dec, value, inc);
	}
	
	//--------------------------------------------------------------------
	public SkillField(String text) {
		dec  = new Button("<");
		inc  = new Button(">");
		value = new TextField();
		value.setPrefColumnCount(text.length());
		value.setText(text);
		this.getChildren().addAll(dec, value, inc);
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