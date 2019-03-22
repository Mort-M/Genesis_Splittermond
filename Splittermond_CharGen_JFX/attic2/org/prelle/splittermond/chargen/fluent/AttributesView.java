/**
 * 
 */
package org.prelle.splittermond.chargen.fluent;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.WindowMode;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.charctrl.CharacterController;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author prelle
 *
 */
public class AttributesView extends HBox implements ResponsiveControl {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle RES = SpliMoCharGenConstants.RES;
	
	private CharacterController charControl;
	
	private TableView<AttributeValue> tablePrimary;
	private TableView<AttributeValue> tableSecondary;
	
	private TableColumn<AttributeValue, String> priAttrLong;
	private TableColumn<AttributeValue, String> priAttrShort;
	private TableColumn<AttributeValue, Number> priStart;
	private TableColumn<AttributeValue, Number> priValue;
	
	private TableColumn<AttributeValue, String> secAttrLong;
	private TableColumn<AttributeValue, String> secAttrShort;
	private TableColumn<AttributeValue, Number> secNormal;
	private TableColumn<AttributeValue, Number> secMod;
	private TableColumn<AttributeValue, Number> secValue;

	//-------------------------------------------------------------------
	public AttributesView(CharacterController ctrl) {
		this.charControl = ctrl;
		initComponents();
		initLayout();
		fillInitially();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initPrimary();
		initSecondary();
	}

	//-------------------------------------------------------------------
	private void initPrimary() {		
		priAttrLong = new TableColumn<AttributeValue, String>(RES.getString("label.attributes"));
		priAttrShort= new TableColumn<AttributeValue, String>();
		priStart    = new TableColumn<AttributeValue, Number>(RES.getString("label.start"));
		priValue    = new TableColumn<AttributeValue, Number>(RES.getString("label.value"));

		tablePrimary = new TableView<AttributeValue>();
		tablePrimary.getColumns().addAll(priAttrLong, priAttrShort, priStart, priValue);
		tablePrimary.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		priAttrLong.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getName()));
		priAttrShort.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getShortName()));
		priStart.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getStart()));
		priValue.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getStart()));
	}

	//-------------------------------------------------------------------
	private void initSecondary() {
		secAttrLong = new TableColumn<AttributeValue, String>(RES.getString("label.derived_values"));
		secAttrShort= new TableColumn<AttributeValue, String>();
		secMod      = new TableColumn<AttributeValue, Number>(RES.getString("label.modified.short"));

		tableSecondary = new TableView<AttributeValue>();
		tableSecondary.getColumns().addAll(secAttrLong, secAttrShort, secMod);
		tableSecondary.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		secAttrLong.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getName()));
		secAttrShort.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getShortName()));
		secMod.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getModifier()));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getChildren().addAll(tablePrimary, tableSecondary); 
		HBox.setHgrow(tablePrimary, Priority.ALWAYS);
		HBox.setHgrow(tableSecondary, Priority.ALWAYS);
	}

	//-------------------------------------------------------------------
	private void fillInitially() {
		AttributeController control = charControl.getAttributeController();
		
		for (Attribute key : Attribute.primaryValues()) {
			AttributeValue aVal = charControl.getModel().getAttribute(key);
			tablePrimary.getItems().add(aVal);
			logger.debug("Add "+aVal);
		}
		
		for (Attribute key : Attribute.secondaryValues()) {
			AttributeValue aVal = charControl.getModel().getAttribute(key);
			tableSecondary.getItems().add(aVal);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		// TODO Auto-generated method stub

	}

}
