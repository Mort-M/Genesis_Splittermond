/**
 *
 */
package org.prelle.splittermond.jfx.equip.input;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.skin.NavigButtonControl;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

/**
 * @author prelle
 *
 */
public class UploadPane extends VBox {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private NavigButtonControl buttonControl;
	private TextField tfEMail;
	private TextField tfName;
	private TextArea  taMessage;

	//-------------------------------------------------------------------
	public UploadPane(NavigButtonControl buttonControl) {
		this.buttonControl = buttonControl;
		initComponents();
		initLayout();
		initInteractivity();

		checkInput();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tfEMail = new TextField();
		tfEMail.setPromptText(UI.getString("pane.upload.mail.from.prompt"));
		if (DataInputScreen.CONFIG.get(DataInputScreen.KEY_MAIL, null)!=null)
			tfEMail.setText(DataInputScreen.CONFIG.get(DataInputScreen.KEY_MAIL, null));

		tfName = new TextField();
		tfName.setPromptText(UI.getString("pane.upload.mail.name.prompt"));
		if (DataInputScreen.CONFIG.get(DataInputScreen.KEY_NAME, null)!=null)
			tfName.setText(DataInputScreen.CONFIG.get(DataInputScreen.KEY_NAME, null));

		taMessage = new TextArea();
		taMessage.setPromptText(UI.getString("pane.upload.message.prompt"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label lblEMail = new Label(UI.getString("pane.upload.mail.from"));
		Label lblName  = new Label(UI.getString("pane.upload.mail.name"));
		Label lblMess  = new Label(UI.getString("pane.upload.message"));

		lblEMail.getStyleClass().add("text-small-subheader");
		lblName .getStyleClass().add("text-small-subheader");
		lblMess .getStyleClass().add("text-small-subheader");

		getChildren().addAll(lblEMail, tfEMail, lblName, tfName, lblMess, taMessage);
		VBox.setMargin(lblName, new Insets(20,0,0,0));
		VBox.setMargin(lblMess, new Insets(20,0,0,0));
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tfEMail.textProperty().addListener( (ov,o,n) -> checkInput());
		tfName.textProperty().addListener( (ov,o,n) -> checkInput());
	}

	//-------------------------------------------------------------------
	private void checkInput() {
		buttonControl.setDisabled(CloseType.OK, !isMinimalDataPresent());
	}

	//-------------------------------------------------------------------
	public boolean isMinimalDataPresent() {
		return tfEMail.getText()!=null && !tfEMail.getText().isEmpty() && tfName.getText()!=null && !tfName.getText().isEmpty();
	}

	//-------------------------------------------------------------------
	public String getFromAddress() {
		return tfEMail.getText();
	}

	//-------------------------------------------------------------------
	public String getDisplayName() {
		return tfName.getText();
	}

	//-------------------------------------------------------------------
	public String getDescription() {
		return taMessage.getText();
	}

}
