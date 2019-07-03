package org.prelle.splittermond.chargen.jfx.attributes;

import java.util.PropertyResourceBundle;

import org.prelle.javafx.skin.GridPaneTableViewSkin;
import org.prelle.rpgframework.jfx.NumericalValueTableCell;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.ViewMode;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Stefan Prelle
 *
 */
public class AttributesPrimaryTableView extends TableView<AttributeValue> {

	private static PropertyResourceBundle RES = SpliMoCharGenJFXConstants.UI;

	private CharacterController control;
	private ViewMode mode;
	
	private TableColumn<AttributeValue, String> priAttrLong;
	private TableColumn<AttributeValue, String> priAttrShort;
	private TableColumn<AttributeValue, Number> priStart;
	private TableColumn<AttributeValue, AttributeValue> priValue;
	private TableColumn<AttributeValue, Number> priSum;

	//-------------------------------------------------------------------
	public AttributesPrimaryTableView(CharacterController control, ViewMode mode) {
		this.control = control;
		this.mode    = mode;
		initColumns();
		setSkin(new GridPaneTableViewSkin<>(this));
		setData(control.getModel());
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initColumns() {		
		priAttrLong = new TableColumn<AttributeValue, String>(RES.getString("label.name"));
		priAttrShort= new TableColumn<AttributeValue, String>();
		priStart    = new TableColumn<AttributeValue, Number>(RES.getString((mode==ViewMode.GENERATION)?"label.modified.short":"label.start"));
		priValue    = new TableColumn<AttributeValue, AttributeValue>(RES.getString((mode==ViewMode.GENERATION)?"label.points":"label.value"));
		priSum      = new TableColumn<AttributeValue, Number>(RES.getString("label.value"));

		priAttrLong.setPrefWidth(150); // Percent
		priAttrShort.setMinWidth(50);
		priStart.setPrefWidth(50);
		priValue.setPrefWidth(140);
		priSum.setPrefWidth(50);
		
		priStart.setStyle("-fx-alignment:center");
		priSum.setStyle("-fx-alignment:center");

		getColumns().addAll(priAttrLong, priAttrShort, priStart, priValue);
		if (mode==ViewMode.GENERATION)
			getColumns().add(priSum);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		priAttrLong.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getName()));
		priAttrShort.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getAttribute().getShortName()));
		priStart.setCellValueFactory( param-> new SimpleIntegerProperty((mode==ViewMode.GENERATION)?param.getValue().getModifier():param.getValue().getStart()));
		priValue.setCellValueFactory( param-> new SimpleObjectProperty<AttributeValue>(param.getValue()));
		priSum.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getValue()));
		
		priValue.setCellFactory( (col) -> new NumericalValueTableCell<Attribute,AttributeValue,AttributeValue>(control.getAttributeController()));
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model ) {
		getItems().clear();
		for (Attribute key : Attribute.primaryValues() ) {
			getItems().add(model.getAttribute(key));
		}
	}

}
