/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class MessageDialog  {

	private final static ResourceBundle genRes = ResourceBundle.getBundle("i18n/ui");

	//--------------------------------------------------------------------
	/**
	 * @return TRUE if OK
	 */
	public static boolean showConfirmMessage(String text) {
		final VBox layout = new VBox(10);
		layout.setPadding(new Insets(3));
		layout.setAlignment(Pos.CENTER);
		Label mess = new Label(text);

		Button ok = new Button(genRes.getString("button.really"));
		ok.setMaxWidth(Double.MAX_VALUE);
		Button cancel = new Button(genRes.getString("button.mistake"));

		TilePane buttonBar = new TilePane();
		buttonBar.setHgap(10);
		buttonBar.setAlignment(Pos.CENTER);
		buttonBar.getChildren().addAll(ok, cancel);
		buttonBar.getStyleClass().add("wizard-buttonbar");
		
		layout.getChildren().addAll(mess, buttonBar);
		
		ok.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				layout.setUserData(true);
				layout.getScene().getWindow().hide();
			}
		});
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				layout.setUserData(false);
				layout.getScene().getWindow().hide();
			}
		});
		
		Scene scene = new Scene(layout);
		scene.getStylesheets().add("css/default.css");
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setAlwaysOnTop(true);
//		stage.setTitle(res.getString("dialog.title.question"));
		stage.setScene(scene);
		stage.showAndWait();
		
		return (Boolean)layout.getUserData();
	}

}
