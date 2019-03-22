/**
 * 
 */
package org.prelle.splittermond.jfx.equip.input;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Education;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class EnterEducationPane extends VBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle RES = SpliMoCharGenJFXConstants.UI;

	private Education model;

	private TextField tfName;
	private TextField tfPage;
	private TextArea  taDesc;

	//-------------------------------------------------------------------
	/**
	 */
	public EnterEducationPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		tfName = new TextField();
		tfPage = new TextField();
		taDesc = new TextArea();
		taDesc.setWrapText(true);

	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		taDesc.setStyle("-fx-pref-height: 7em; -fx-pref-width: 25em");

		Label heaName = new Label(RES.getString("label.name"));
		Label heaPage = new Label(RES.getString("label.page"));
		Label heaDesc = new Label(RES.getString("label.desc"));
		heaName.getStyleClass().add("text-small-subheader");
		heaPage.getStyleClass().add("text-small-subheader");
		heaDesc.getStyleClass().add("text-small-subheader");
		
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		grid.add(heaName, 0, 0);
		grid.add(tfName , 1, 0);
		grid.add(heaPage, 0, 1);
		grid.add(tfPage , 1, 1);		
		grid.add(heaDesc, 2, 0);
		grid.add(taDesc , 2, 1, 1,4);
		
		setSpacing(20);
		getChildren().addAll(grid);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tfName.textProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); tfName.setText(n); }
				if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); tfName.setText(n); }
				if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); tfName.setText(n); }
				if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); tfName.setText(n); }
			}
		});
		tfPage.textProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				try {
					Integer.parseInt(n);
				} catch (NumberFormatException nfe) {
					tfPage.setText(String.valueOf(model.getPage()));
				}
			}
		});
	}
	
	//-------------------------------------------------------------------
	private void refresh() {
		logger.debug("Refresh from "+model);
	}

	//-------------------------------------------------------------------
	public void setData(Education model) {
		this.model = model;
		refresh();
	}
	
}
