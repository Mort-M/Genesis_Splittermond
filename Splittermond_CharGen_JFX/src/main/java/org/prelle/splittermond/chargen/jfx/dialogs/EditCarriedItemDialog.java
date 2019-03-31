package org.prelle.splittermond.chargen.jfx.dialogs;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.rpgframework.jfx.OptionalDescriptionPane;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemType;
import org.prelle.splittermond.chargen.jfx.DescriptionBox;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.equip.ArmorDataPane;
import org.prelle.splittermond.chargen.jfx.equip.BasicItemDataPane;
import org.prelle.splittermond.chargen.jfx.equip.OtherDataPane;
import org.prelle.splittermond.chargen.jfx.equip.RangeWeaponDataPane;
import org.prelle.splittermond.chargen.jfx.equip.ShieldDataPane;
import org.prelle.splittermond.chargen.jfx.equip.WeaponDataPane;
import org.prelle.splittermond.chargen.jfx.listcells.EnhancementListCell;
import org.prelle.splittermond.chargen.jfx.listcells.EnhancementReferenceListCell;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan Prelle
 *
 */
public class EditCarriedItemDialog extends ManagedDialog {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditCarriedItemDialog.class.getName());

	//-------------------------------------------------------------------
	class BasicItemDataVBox extends VBox {
		BasicItemDataPane pane;
		public BasicItemDataVBox() {
			super(5);
			pane = new BasicItemDataPane();
			Label title = new Label(UI.getString("pane.basic"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class WeaponDataVBox extends VBox {
		WeaponDataPane pane;
		public WeaponDataVBox() {
			super(5);
			pane = new WeaponDataPane();
			Label title = new Label(UI.getString("pane.weapon"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class RangeWeaponDataVBox extends VBox {
		RangeWeaponDataPane pane;
		public RangeWeaponDataVBox() {
			super(5);
			pane = new RangeWeaponDataPane();
			Label title = new Label(UI.getString("pane.rangeweapon"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class ArmorDataVBox extends VBox {
		ArmorDataPane pane;
		public ArmorDataVBox() {
			super(5);
			pane = new ArmorDataPane();
			Label title = new Label(UI.getString("pane.armor"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class ShieldDataVBox extends VBox {
		ShieldDataPane pane;
		public ShieldDataVBox() {
			super(5);
			pane = new ShieldDataPane();
			Label title = new Label(UI.getString("pane.shield"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class OtherDataVBox extends VBox {
		OtherDataPane pane;
		public OtherDataVBox() {
			super(5);
			pane = new OtherDataPane();
			Label title = new Label(UI.getString("pane.other"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	private CharacterController charGen;
	private NewItemController itemCtrl;

	private FlowPane flow;
	private BasicItemDataVBox paneBasic;
	private WeaponDataVBox 	  paneWeapon;
	private RangeWeaponDataVBox paneRange;
	private ShieldDataVBox 	  paneShield;
	private ArmorDataVBox 	  paneArmor;
	private OtherDataVBox 	  paneOther;
	private TextArea 		  paneDescr;
	
	private ChoiceBox<EnhancementType> cbType;
	private ListView<Enhancement> lvAvailable;
	private ListView<EnhancementReference> lvSelected;
	private OptionalDescriptionPane pane;
	private DescriptionBox descr;
	
	//-------------------------------------------------------------------
	/**
	 * @param title
	 * @param content
	 * @param buttons
	 */
	public EditCarriedItemDialog(CharacterController ctrl, NewItemController itemCtrl) {
		super(ctrl.getModel().getName(), null, CloseType.OK, CloseType.CANCEL);
		this.charGen = ctrl;
		this.itemCtrl= itemCtrl;
		setTitle(UI.getString("dialog.editcarrieditem.title"));
		
		initComponents();
		initLayout();
		initInteractivity();
		
		cbType.setValue(EnhancementType.NORMAL);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		paneBasic = new BasicItemDataVBox();
		paneWeapon= new WeaponDataVBox();
		paneRange = new RangeWeaponDataVBox();
		paneArmor = new ArmorDataVBox();
		paneShield= new ShieldDataVBox();
		paneOther = new OtherDataVBox();
		paneDescr = new TextArea();
		paneDescr.setStyle("-fx-pref-height: 5em; -fx-pref-width: 40em;");
		
		cbType = new ChoiceBox<>();
		cbType.getItems().addAll(EnhancementType.values());
		cbType.setConverter(new StringConverter<Enhancement.EnhancementType>() {
			public String toString(EnhancementType value) { return value.getName();}
			public EnhancementType fromString(String string) { return null; }
		});
		
		lvAvailable = new ListView<Enhancement>();
		lvSelected  = new ListView<EnhancementReference>();
		
		lvAvailable.setCellFactory(lv -> new EnhancementListCell(itemCtrl));
		lvSelected .setCellFactory(lv -> new EnhancementReferenceListCell(itemCtrl));
		
		descr = new DescriptionBox();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		lvAvailable.setStyle("-fx-pref-width: 25em");
		lvSelected.setStyle("-fx-pref-width: 25em");
		
		flow = new FlowPane(paneBasic, paneDescr);
		flow.setStyle("-fx-hgap: 0.5em");
		
		VBox col1 = new VBox(5, cbType, lvAvailable);
		VBox col2 = new VBox(5, lvSelected);
		
		HBox bxLists = new HBox(col1, col2);
		bxLists.setStyle("-fx-spacing: 1em");
		
		pane = new OptionalDescriptionPane(bxLists, descr);
		VBox layout = new VBox(flow, pane);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		lvAvailable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> descr.showData(n));
		lvSelected.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null)
				descr.showData(n.getEnhancement());
			else
				descr.showData(null);
			});
		
		cbType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			lvAvailable.getItems().clear();
			if (n!=null) {
				lvAvailable.getItems().addAll(itemCtrl.getAvailableEnhancements(n));
			}
		});
	}

	//-------------------------------------------------------------------
	public void setData(CarriedItem value) {
		paneBasic.setData(value);
		paneDescr.setText(value.getDescription());
		paneWeapon.setData(value);
		paneArmor.setData(value);
		
		flow.getChildren().retainAll(paneBasic);
		if (value.isType(ItemType.WEAPON))
			flow.getChildren().add(paneWeapon);
		if (value.isType(ItemType.LONG_RANGE_WEAPON))
			flow.getChildren().add(paneRange);
		if (value.isType(ItemType.ARMOR))
			flow.getChildren().add(paneArmor);
		if (value.isType(ItemType.SHIELD))
			flow.getChildren().add(paneShield);
		flow.getChildren().add(paneOther);
		flow.getChildren().add(paneDescr);
		
		lvSelected.getItems().clear();
		lvSelected.getItems().addAll(value.getEnhancements());
	}


}
