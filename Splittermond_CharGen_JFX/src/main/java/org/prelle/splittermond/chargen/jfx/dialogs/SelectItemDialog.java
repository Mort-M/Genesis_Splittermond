package org.prelle.splittermond.chargen.jfx.dialogs;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.rpgframework.jfx.OptionalDescriptionPane;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splittermond.chargen.jfx.DescriptionBox;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Stefan Prelle
 *
 */
public class SelectItemDialog extends ManagedDialog {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(SelectItemDialog.class.getName());
	
	private CharacterController charGen;

	private ChoiceBox<ItemType> cbType;
	private ListView<ItemTemplate> list;
	
	private OptionalDescriptionPane pane;
	private DescriptionBox descr;
	
	//-------------------------------------------------------------------
	/**
	 * @param title
	 * @param content
	 * @param buttons
	 */
	public SelectItemDialog(CharacterController ctrl) {
		super(ctrl.getModel().getName(), null, CloseType.OK, CloseType.CANCEL);
		this.charGen = ctrl;
		setTitle(UI.getString("dialog.selectitem.title"));
		
		initComponents();
		initLayout();
		initInteractivity();
		
		cbType.setValue(ItemType.WEAPON);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		cbType = new ChoiceBox<>();
		cbType.getItems().addAll(ItemType.values());
		cbType.setConverter(new StringConverter<ItemType>() {
			public String toString(ItemType object) { return (object!=null)?object.getName():""; }
			public ItemType fromString(String string) { return null; }
		});
		
		list = new ListView<ItemTemplate>();
		StringConverter<ItemTemplate> sv = new StringConverter<ItemTemplate>() {
			public String toString(ItemTemplate object) {
				if (object==null) return "-";
				if (object.getSkill()!=null)
					return object.getName()+" ("+object.getSkill().getName()+")";
				return object.getName();
				}
			public ItemTemplate fromString(String string) {return null;	}
		};
		list.setCellFactory(new Callback<ListView<ItemTemplate>, ListCell<ItemTemplate>>() {
			public ListCell<ItemTemplate> call(ListView<ItemTemplate> param) {
				TextFieldListCell<ItemTemplate> cell = new TextFieldListCell<>();
				cell.setConverter(sv);
				return cell;
			}
		});
		
		descr = new DescriptionBox();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		VBox column = new VBox(5, cbType, list);
		
		descr = new DescriptionBox();
		
		pane = new OptionalDescriptionPane(column, descr);
		setContent(pane);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			list.getItems().clear();
			if (n!=null) {
				list.getItems().addAll(SplitterMondCore.getItems(n));
			}
		});
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> descr.showData(n));
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<ItemTemplate> selectedItemProperty() {
		return list.getSelectionModel().selectedItemProperty();
	}

}
