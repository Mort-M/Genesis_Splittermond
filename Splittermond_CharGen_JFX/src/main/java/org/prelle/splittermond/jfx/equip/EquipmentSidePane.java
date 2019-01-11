/**
 * 
 */
package org.prelle.splittermond.jfx.equip;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class EquipmentSidePane extends VBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle SPLIMO_CHARGEN = SpliMoCharGenJFXConstants.UI;

	private TextField lblSol;
	private TextField lblLun;
	private TextField lblTel;
	
	private SpliMoCharacter model;
	
	//-------------------------------------------------------------------
	/**
	 */
	public EquipmentSidePane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblSol  = new TextField("0");
		lblLun  = new TextField("0");
		lblTel  = new TextField("0");
		
		lblSol.setStyle("-fx-max-width: 4em");
		lblLun.setStyle("-fx-max-width: 4em");
		lblTel.setStyle("-fx-max-width: 4em");
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		setAlignment(Pos.TOP_CENTER);
		getStyleClass().add("section-bar");
//		setPrefWidth(300);
		setMinWidth(200);
		setMinHeight(300);

		Label heaSol  = new Label(SPLIMO_CHARGEN.getString("label.solare"));
		Label heaLun  = new Label(SPLIMO_CHARGEN.getString("label.lunare"));
		Label heaTel  = new Label(SPLIMO_CHARGEN.getString("label.telare"));
		heaSol.getStyleClass().add("text-subheader");
		heaLun.getStyleClass().add("text-subheader");
		heaTel.getStyleClass().add("text-subheader");
		
		getChildren().addAll(heaSol, lblSol, heaLun, lblLun, heaTel, lblTel);
		
		Region spacing = new Region();
		spacing.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(spacing, Priority.ALWAYS);
		getChildren().addAll(spacing);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		lblSol.textProperty().addListener( (ov,o,n) -> {
			int sol = Integer.parseInt(n);
			model.setTelare(sol*10000 + model.getTelare()%10000);
		});
		lblLun.textProperty().addListener( (ov,o,n) -> {
			int lun = Integer.parseInt(n);
			int sol = model.getTelare()/10000;
			int tel = model.getTelare()%100;
			logger.debug("sol="+sol+" lun="+lun+" tel="+tel);
			model.setTelare(sol*10000 + lun*100 + tel);
		});
		lblTel.textProperty().addListener( (ov,o,n) -> {
			int tel = Integer.parseInt(n);
			model.setTelare(tel + (model.getTelare()/100)*100);
		});
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		lblSol.setText(String.valueOf(model.getTelare()/10000));
		lblLun.setText(String.valueOf( (model.getTelare()/100)%100 ));
		lblTel.setText(String.valueOf(model.getTelare()%100));
	}

}
