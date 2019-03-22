package org.prelle.splittermond.chargen.jfx.attributes;

import java.util.PropertyResourceBundle;

import org.prelle.javafx.skin.GridPaneTableViewSkin;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Stefan Prelle
 *
 */
public class AttributesSecondaryTableView extends TableView<AttributeValue> {

	private static PropertyResourceBundle RES = SpliMoCharGenJFXConstants.UI;

	private TableColumn<AttributeValue, String> secAttrLong;
	private TableColumn<AttributeValue, String> secAttrShort;
	private TableColumn<AttributeValue, Number> secNormal;
	private TableColumn<AttributeValue, Number> secMod;
	private TableColumn<AttributeValue, Number> secValue;

	//-------------------------------------------------------------------
	public AttributesSecondaryTableView() {
		initColumns();
		setSkin(new GridPaneTableViewSkin<>(this));
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initColumns() {		
		secAttrLong = new TableColumn<AttributeValue, String>(RES.getString("label.derived_values"));
		secAttrShort= new TableColumn<AttributeValue, String>();
		secNormal   = new TableColumn<AttributeValue, Number>("");
		secMod      = new TableColumn<AttributeValue, Number>(RES.getString("label.modified.short"));
		secValue    = new TableColumn<AttributeValue, Number>(RES.getString("label.value"));
		
		secAttrLong.setId("attrtable-name");
		secAttrShort.setId("attrtable-short");
		secNormal.setId("attrtable-normal");
		secMod   .setId("attrtable-mod");
		secValue .setId("attrtable-val");

		secAttrLong.setPrefWidth(170); // Percent
		secAttrShort.setMinWidth(50);
		secNormal.setMinWidth(50);
		secMod.setPrefWidth(50);
		secValue.setPrefWidth(60);
		

		getColumns().addAll(secAttrLong, secAttrShort, secNormal, secMod, secValue);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		secAttrLong.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getName()));
		secAttrShort.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getShortName()));
		secNormal.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getDistributed()));
		secMod.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getModifier()));
		secValue.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getValue()));
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model ) {
		getItems().clear();
		for (Attribute key : Attribute.secondaryValuesWithoutDR() ) {
			getItems().add(model.getAttribute(key));
		}
		refresh();
	}

}
