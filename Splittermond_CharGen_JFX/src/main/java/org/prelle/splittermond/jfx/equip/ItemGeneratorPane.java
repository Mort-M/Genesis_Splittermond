/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import org.apache.log4j.Logger;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.Material;
import org.prelle.splimo.items.PersonalizationReference;

/**
 * @author Stefan
 *
 */
public class ItemGeneratorPane extends Region implements GenerationEventListener, ScreenManagerProvider, CommonItemGeneratorMethods {

	private final static ResourceBundle RES = ResourceBundle.getBundle("i18n/splimo-chargen");

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private NewItemController control;
	private ScreenManager manager;

	private TextField tfName;
	private BasicItemDataPane paneBasic;
	private WeaponDataPane    paneWeapon;
	private RangeWeaponDataPane    paneRanged;
	private ArmorDataPane     paneArmor;
	private ShieldDataPane    paneShield;

	private Label heaBasic;
	private Label heaWeapon;
	private Label heaRanged;
	private Label heaArmor;
	private Label heaShield;

	private EnhancementListView lvAvailable;
	private EnhancementReferenceListView lvEnhancements;
	private ChoiceBox<Material> cbMaterial;
	private ChoiceBox<PersonalizationReference> cbPersonal1;
	private ChoiceBox<PersonalizationReference> cbPersonal2;
	private Label lblItemQuality;
	private Label lblArtiQuality;
	private Label lblPrice;

	//--------------------------------------------------------------------
	public ItemGeneratorPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		tfName    = new TextField();
		paneBasic = new BasicItemDataPane();
		paneWeapon= new WeaponDataPane();
		paneRanged= new RangeWeaponDataPane();
		paneArmor = new ArmorDataPane();
		paneShield= new ShieldDataPane();

		heaBasic  = new Label(RES.getString("label.itemdata.basic"));
		heaWeapon = new Label(RES.getString("label.itemdata.weapon"));
		heaRanged = new Label(RES.getString("label.itemdata.ranged"));
		heaArmor  = new Label(RES.getString("label.itemdata.armor"));
		heaShield = new Label(RES.getString("label.itemdata.shield"));
		heaBasic.getStyleClass().add("text-subheader");
		heaWeapon.getStyleClass().add("text-subheader");
		heaRanged.getStyleClass().add("text-subheader");
		heaArmor.getStyleClass().add("text-subheader");
		heaShield.getStyleClass().add("text-subheader");

		lvEnhancements = new EnhancementReferenceListView(control, this);
		lvAvailable    = new EnhancementListView(control);
		cbMaterial     = new ChoiceBox<>();
		cbMaterial.setConverter(new StringConverter<Material>() {
			public String toString(Material data) {
				return data.getName();
			}
			public Material fromString(String data) {
				return null;
			}
		});
		cbPersonal1    = new ChoiceBox<>();
		cbPersonal1.setConverter(new StringConverter<PersonalizationReference>() {
			public String toString(PersonalizationReference data) {
				return data != null ? data.getName() : "";
			}
			public PersonalizationReference fromString(String data) {
				return null;
			}
		});
		cbPersonal2    = new ChoiceBox<>();
		cbPersonal2.setConverter(new StringConverter<PersonalizationReference>() {
			public String toString(PersonalizationReference data) {
				return data != null ? data.getName() : "";
			}
			public PersonalizationReference fromString(String data) {
				return null;
			}
		});
		lblItemQuality = new Label();
		lblPrice   = new Label();
		lblArtiQuality = new Label();
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label lbName = new Label(RES.getString("label.name"));
		lbName.getStyleClass().add("base");

		// Enhancements column
		Label lbEnhance = new Label(RES.getString("label.enhancements.applied"));
		lbEnhance.getStyleClass().add("text-subheader");
		VBox colEnhance = new VBox(5);
		colEnhance.getChildren().addAll(lbEnhance, lvEnhancements);

		// Available column
		Label lbAvailable = new Label(RES.getString("label.enhancements.available"));
		lbAvailable.getStyleClass().add("text-subheader");
		VBox colAvailable = new VBox(5);
		colAvailable.getChildren().addAll(lbAvailable, lvAvailable);

		// Material and Cost column
		Label lbMaterial = new Label(RES.getString("heading.enhancements.material"));
		Label lbCost     = new Label(RES.getString("heading.enhancements.cost"));
		lbMaterial.getStyleClass().add("text-subheader");
		lbCost    .getStyleClass().add("text-subheader");

		Label heaMaterial = new Label(RES.getString("label.material"));
		Label heaPers1 = new Label(RES.getString("label.personalization.first"));
		Label heaPers2 = new Label(RES.getString("label.personalization.second"));
		heaMaterial.getStyleClass().add("base");
		heaPers1.getStyleClass().add("base");
		heaPers2.getStyleClass().add("base");
		GridPane gridPers = new GridPane();
		gridPers.setVgap(5);
		gridPers.setHgap(5);
		gridPers.add(heaMaterial, 0, 0);
		gridPers.add(cbMaterial , 1, 0);
		gridPers.add(heaPers1   , 0, 1);
		gridPers.add(cbPersonal1, 1, 1);
		gridPers.add(heaPers2   , 0, 2);
		gridPers.add(cbPersonal2, 1, 2);
		gridPers.getStyleClass().add("content");

		VBox colMaterialAndCost = new VBox(5);
		Label heaItemQuality = new Label(RES.getString("label.itemQuality"));
		Label heaArtiQuality = new Label(RES.getString("label.artifactQuality"));
		Label heaPrice   = new Label(RES.getString("label.finalCost"));
		heaItemQuality.getStyleClass().add("text-small-subheader");
		heaArtiQuality.getStyleClass().add("text-small-subheader");
		heaPrice.getStyleClass().add("text-small-subheader");
		GridPane gridMAC = new GridPane();
		gridMAC.setHgap(5);
		gridMAC.add(heaItemQuality, 0, 0);
		gridMAC.add(lblItemQuality    , 1, 0);
		gridMAC.add(heaArtiQuality, 0, 1);
		gridMAC.add(lblArtiQuality, 1, 1);
		gridMAC.add(heaPrice  , 0, 2);
		gridMAC.add(lblPrice  , 1, 2);
		gridMAC.getStyleClass().add("content");
		colMaterialAndCost.getChildren().addAll(lbMaterial, gridPers, lbCost, gridMAC);

		HBox grid = new HBox(5);
		grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		grid.getChildren().addAll(lbName, tfName);

		/*
		 * Data line
		 */
		VBox boxBasic = new VBox(5);
		boxBasic.getChildren().addAll(heaBasic, paneBasic);
		VBox boxWeapon = new VBox(5);
		boxWeapon.getChildren().addAll(heaWeapon, paneWeapon);
		VBox boxRanged = new VBox(5);
		boxRanged.getChildren().addAll(heaRanged, paneRanged);
		VBox boxArmor = new VBox(5);
		boxArmor.getChildren().addAll(heaArmor, paneArmor);
		VBox boxShield = new VBox(5);
		boxShield.getChildren().addAll(heaShield, paneShield);
		HBox dataLine = new HBox(20);
		dataLine.getChildren().addAll(boxBasic, boxWeapon, boxRanged, boxArmor, boxShield);

		HBox columns = new HBox(20);
		columns.setMaxWidth(Double.MAX_VALUE);
		columns.getChildren().addAll(colAvailable, colEnhance, colMaterialAndCost);
		columns.setAlignment(Pos.BOTTOM_LEFT);

		VBox layout = new VBox(20);
		layout.getChildren().addAll(grid, dataLine, columns);
		VBox.setVgrow(columns, Priority.ALWAYS);

//		setContent(layout);
		getChildren().add(layout);

		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		this.setPrefWidth(1200);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		tfName.textProperty().addListener( (ov,o,n) -> {
			if (n==null || n.length()==0)
				control.getItem().setCustomName(null);
			else {
				try {
					if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); tfName.setText(n); }
					if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); tfName.setText(n); }
					if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); tfName.setText(n); }
					if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); tfName.setText(n); }

					if (!n.equals(control.getItem().getItem().getName())) {
						control.getItem().setCustomName(n);
					}
				} catch (Exception e) {
					logger.error("Failed setting name",e);
				}
			}
		});

		cbPersonal1.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.debug("1. Personalization now = "+n);
			control.setFirstPersonalization(n);
		});
		cbPersonal2.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.debug("2. Personalization now = "+n);
			control.setSecondPersonalization(n);
		});
		cbMaterial.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.debug("Material now = "+n);
			if (n!=null)
				control.setMaterial(n);
		});
	}

	//--------------------------------------------------------------------
	public void setScreenManager(ScreenManager manager) {
		this.manager = manager;
	}

	//--------------------------------------------------------------------
	public ScreenManager getScreenManager() {
		return manager;
	}

	//--------------------------------------------------------------------
	public void updateContent() {
		if (control==null)
			return;
		CarriedItem model = control.getItem();
		if (model==null)
			return;

		tfName.setText(model.getName());

		paneBasic.refresh();
		paneWeapon.refresh();
		paneRanged.refresh();
		paneArmor.refresh();
		paneShield.refresh();

		paneWeapon.setVisible(model.isType(ItemType.WEAPON));
		paneRanged.setVisible(model.isType(ItemType.LONG_RANGE_WEAPON));
		paneArmor.setVisible(model.isType(ItemType.ARMOR));
		paneShield.setVisible(model.isType(ItemType.SHIELD));

		heaWeapon.setVisible(model.isType(ItemType.WEAPON));
		heaRanged.setVisible(model.isType(ItemType.LONG_RANGE_WEAPON));
		heaArmor.setVisible(model.isType(ItemType.ARMOR));
		heaShield.setVisible(model.isType(ItemType.SHIELD));

		lvAvailable.getItems().clear();
		logger.debug("avail = "+control.getAvailableEnhancements());
		lvAvailable.getItems().addAll(control.getAvailableEnhancements());
		lvEnhancements.getItems().clear();
		lvEnhancements.getItems().addAll(model.getEnhancements());
		cbMaterial.getItems().clear();
		cbMaterial.getItems().addAll(control.getAvailableMaterials());
		cbMaterial.getSelectionModel().select(model.getMaterial());

		filterCombobox(cbPersonal1, control.getAvailableFirstPersonalizations(),  Optional.of(model.getPersonalizations())
				                                                                          .filter(l -> l.size() >= 2)
				                                                                          .map(l -> l.get(1)));
		filterCombobox(cbPersonal2, control.getAvailableSecondPersonalizations(), Optional.of(model.getPersonalizations())
																				          .filter(l -> !l.isEmpty())
																				          .map(l -> l.get(0)));
		if  (model.getPersonalizations().isEmpty()) {
			cbPersonal1.getSelectionModel().clearSelection();
		} else {
			cbPersonal1.getSelectionModel().select(model.getPersonalizations().get(0));
		}
		if (model.getPersonalizations().size() < 2) {
			cbPersonal2.getSelectionModel().clearSelection();
		} else {
			cbPersonal2.getSelectionModel().select(model.getPersonalizations().get(1));
		}

		lblItemQuality.setText(String.valueOf(model.getItemQuality()));
		lblArtiQuality.setText(String.valueOf(model.getArtifactQuality()));
		lblPrice.setText(SplitterTools.telareAsCurrencyString(control.getPrice()));

		cbPersonal2.setDisable(!control.isSecondPersonalizationAllowed());
	}

	private void filterCombobox(ChoiceBox<PersonalizationReference>  box, List<PersonalizationReference> allItems, Optional<PersonalizationReference> template) {
		List<PersonalizationReference> actual = box.getItems();
		for (int i = 0; i < allItems.size(); i++) {
			PersonalizationReference test = allItems.get(i);
			if (!actual.contains(test)) {
				actual.add(i, test);
			}
		}
		List<PersonalizationReference> filteredItems = template.map(t -> control.makeCompartibleList(t, allItems)).orElse(allItems);
		if (filteredItems.size() != allItems.size()) {
			for (Iterator<PersonalizationReference> iter = actual.iterator(); iter.hasNext();) {
				if (!filteredItems.contains(iter.next())) {
					iter.remove();
				}
			}
		}
	}

	//--------------------------------------------------------------------
	public void setData(NewItemController control) {
		this.control = control;
		if (control.getItem()==null)
			throw new NullPointerException("Controller without item");

		paneBasic.setData(control.getItem());
		paneWeapon.setData(control.getItem());
		paneRanged.setData(control.getItem());
		paneArmor.setData(control.getItem());
		paneShield.setData(control.getItem());

		lvAvailable.updateItemController(control);
		lvEnhancements.updateItemController(control);

		cbPersonal1.getItems().clear();
		cbPersonal2.getItems().clear();
		cbPersonal1.getItems().addAll(control.getAvailableFirstPersonalizations());
		cbPersonal2.getItems().addAll(control.getAvailableSecondPersonalizations());

		updateContent();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.debug("RCV "+event);
		switch (event.getType()) {
		case ITEM_CHANGED:
			if (control!=null && event.getKey()==control.getItem())
				updateContent();
			break;
		default:
		}
	}

}
