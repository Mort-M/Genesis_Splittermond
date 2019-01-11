/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.fluent.NodeWithTitle;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.PersonalizationReference;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class NewItemGeneratorPane extends VBox implements GenerationEventListener, ScreenManagerProvider, CommonItemGeneratorMethods {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle RES = SpliMoCharGenConstants.RES;

	private NewItemController control;
	private ScreenManager manager;

	private TextField tfName;
	private Label     lblType;
	private HBox dataLine;
	private BasicItemDataPane paneBasic;
	private WeaponDataPane    paneWeapon;
	private RangeWeaponDataPane    paneRanged;
	private ArmorDataPane     paneArmor;
	private ShieldDataPane    paneShield;
	private TextArea 		  paneDescr;

	private Label heaBasic;
	private Label heaWeapon;
	private Label heaRanged;
	private Label heaArmor;
	private Label heaShield;
	private Label heaDescr;

	private VBox boxWeapon, boxRanged, boxArmor, boxShield, boxDescr;

	private ChoiceBox<PersonalizationReference> cbPersonal1;
	private ChoiceBox<PersonalizationReference> cbPersonal2;

	private Button btnMaterial;
	private Label  lblMaterial;
	private VBox   boxMaterial;
	private Map<EnhancementType, Button> buttons;
	private Map<EnhancementType, Label>  counter;
	private Map<EnhancementType, VBox>   list;

	//--------------------------------------------------------------------
	public NewItemGeneratorPane() {
		initComponents();
		initLayout();
		initInteractivity();

		updateContent();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		tfName    = new TextField();
		lblType   = new Label();
		paneBasic = new BasicItemDataPane();
		paneWeapon= new WeaponDataPane();
		paneRanged= new RangeWeaponDataPane();
		paneArmor = new ArmorDataPane();
		paneShield= new ShieldDataPane();
		paneDescr = new TextArea();

		heaBasic  = new Label(RES.getString("label.itemdata.basic"));
		heaWeapon = new Label(RES.getString("label.itemdata.weapon"));
		heaRanged = new Label(RES.getString("label.itemdata.ranged"));
		heaArmor  = new Label(RES.getString("label.itemdata.armor"));
		heaShield = new Label(RES.getString("label.itemdata.shield"));
		heaDescr  = new Label(RES.getString("label.itemdata.descr"));
		heaBasic.getStyleClass().add("text-subheader");
		heaWeapon.getStyleClass().add("text-subheader");
		heaRanged.getStyleClass().add("text-subheader");
		heaArmor.getStyleClass().add("text-subheader");
		heaShield.getStyleClass().add("text-subheader");
		heaDescr.getStyleClass().add("text-subheader");

		/*
		 * Material and personalizations
		 */
		btnMaterial = new Button(null, new ImageView(new Image(ClassLoader.getSystemResourceAsStream("images/icon_material.png"))));
		btnMaterial.setTooltip(new Tooltip(RES.getString("label.material")));
		btnMaterial.setShape(new Circle(64, Color.WHITE));
		btnMaterial.setStyle("-fx-padding: 10px;");
		lblMaterial = new Label();
		boxMaterial = new VBox();
		boxMaterial.setAlignment(Pos.CENTER);

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

		/*
		 * Enhancement types
		 */
		buttons = new HashMap<EnhancementType, Button>();
		counter = new HashMap<EnhancementType, Label>();
		list    = new HashMap<>();
		for (EnhancementType type : EnhancementType.values()) {
			Button btn = new Button(null, new ImageView(new Image(ClassLoader.getSystemResourceAsStream("images/icon_enhancement_"+type.name().toLowerCase()+".png"))));
			btn.setTooltip(new Tooltip(type.getName()));
			btn.setShape(new Circle(64, Color.WHITE));
			btn.setStyle("-fx-padding: 10px;");
			buttons.put(type, btn);

			counter.put(type, new Label());

			VBox listBox = new VBox();
			listBox.setAlignment(Pos.CENTER);
			list.put(type, listBox);
		}
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setStyle("-fx-spacing: 2em");
		Label lbName = new Label(RES.getString("label.name"));
		lbName.getStyleClass().add("base");

		boxDescr = new VBox(5);
		boxDescr.getChildren().addAll(heaDescr, paneDescr);
		boxDescr.setMaxHeight(Double.MAX_VALUE);
		paneDescr.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(paneDescr, Priority.ALWAYS);

		HBox grid = new HBox(5);
		grid.setAlignment(Pos.CENTER_LEFT);
		grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		grid.getChildren().addAll(lbName, tfName, lblType);

		/*
		 * Data line
		 */
		VBox boxBasic = new VBox(5);
		boxBasic.getChildren().addAll(heaBasic, paneBasic);
		boxWeapon = new VBox(5);
		boxWeapon.getChildren().addAll(heaWeapon, paneWeapon);
		boxRanged = new VBox(5);
		boxRanged.getChildren().addAll(heaRanged, paneRanged);
		boxArmor = new VBox(5);
		boxArmor.getChildren().addAll(heaArmor, paneArmor);
		boxShield = new VBox(5);
		boxShield.getChildren().addAll(heaShield, paneShield);
		dataLine = new HBox(20);
		dataLine.getChildren().addAll(boxBasic);

		/*
		 * Material and personalizations
		 */
		Label heaPers1 = new Label(RES.getString("label.personalization.first"));
		Label heaPers2 = new Label(RES.getString("label.personalization.second"));
		heaPers1.getStyleClass().add("base");
		heaPers2.getStyleClass().add("base");
		GridPane gridPers = new GridPane();
		gridPers.setVgap(5);
		gridPers.setHgap(5);
		gridPers.add(heaPers1   , 0, 1);
		gridPers.add(cbPersonal1, 1, 1);
		gridPers.add(heaPers2   , 0, 2);
		gridPers.add(cbPersonal2, 1, 2);
		gridPers.getStyleClass().add("content");
		this.getChildren().addAll(gridPers);

		VBox bxDataAndPerso = new VBox(20);
		bxDataAndPerso.getChildren().addAll(dataLine, gridPers);
		HBox bxAllWithDescr = new HBox(20);
		bxAllWithDescr.getChildren().addAll(bxDataAndPerso, boxDescr);

		this.getChildren().addAll(grid, bxAllWithDescr);
//		VBox.setVgrow(columns, Priority.ALWAYS);

		/*
		 * Button Line
		 */
		HBox buttonLine = new HBox();
		buttonLine.setMaxWidth(Double.MAX_VALUE);
		// Material
		VBox boxM = new VBox(btnMaterial, lblMaterial, boxMaterial);
		boxM.setAlignment(Pos.TOP_CENTER);
		boxM.setStyle("-fx-spacing: 0.5em;");
		HBox.setHgrow(boxM , Priority.ALWAYS);
		buttonLine.getChildren().add(boxM);


		for (EnhancementType type : EnhancementType.values()) {
			VBox box = new VBox(
					buttons.get(type),
					counter.get(type),
					list.get(type)
					);
			box.setAlignment(Pos.TOP_CENTER);
			box.setStyle("-fx-spacing: 0.5em;");
			HBox.setHgrow(box , Priority.ALWAYS);
			buttonLine.getChildren().add(box);
		}


		buttonLine.setAlignment(Pos.CENTER);
//		buttonLine.setStyle("-fx-spacing: 3em;");
		this.getChildren().add(buttonLine);

//		setContent(layout);

		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		this.setPrefWidth(1200);
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


		btnMaterial.setOnAction(event -> openMaterialScreen());
		for (EnhancementType type : EnhancementType.values())
			buttons.get(type).setOnAction(event -> openSubScreen(type));
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
		lblType.setText(model.getItem().getName());

		paneBasic.refresh();
		paneWeapon.refresh();
		paneRanged.refresh();
		paneArmor.refresh();
		paneShield.refresh();
		 
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

		dataLine.getChildren().removeAll(boxWeapon, boxRanged, boxArmor, boxShield);
		if (model.isType(ItemType.WEAPON))
			dataLine.getChildren().add(boxWeapon);
		if (model.isType(ItemType.LONG_RANGE_WEAPON))
			dataLine.getChildren().add(boxRanged);
		if (model.isType(ItemType.ARMOR))
			dataLine.getChildren().add(boxArmor);
		if (model.isType(ItemType.SHIELD))
			dataLine.getChildren().add(boxShield);
//		dataLine.getChildren().add(boxDescr);

		// Special handling for material
		lblMaterial.setText(model.getMaterial().getId().startsWith("common")?"0":"1");
		boxMaterial.getChildren().clear();
		if (model.getMaterial()!=null)
			boxMaterial.getChildren().add(new Label(model.getMaterial().getName()));

		for (EnhancementType type : EnhancementType.values()) {
			if (type==EnhancementType.RELIC)
				buttons.get(type).setDisable(!model.isRelic());
			counter.get(type).setText(String.valueOf(model.getQuality(type)));
			VBox listBox = list.get(type);
			listBox.getChildren().clear();
			for (EnhancementReference ref : model.getEnhancements(type)) {
				StringBuffer buf = new StringBuffer(ref.getName());
				if (buf.indexOf("(")>0)
					buf.insert(buf.indexOf("("), '\n');
				Label lbl = new Label(buf.toString());
				lbl.setWrapText(true);
				lbl.setTextAlignment(TextAlignment.CENTER);
				listBox.getChildren().add(lbl);
			}
		}
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
		paneDescr.setText(control.getItem().getDescription());

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

	//--------------------------------------------------------------------
	private void openSubScreen(EnhancementType type) {
		logger.debug("openSubScreen("+type+")");
		NodeWithTitle screen = null;
		switch (type) {
		case SAINT:
			screen = new SaintPowersScreen(type, control);
			break;
		default:
			screen = new EnhancementTypeScreen(type, control);
		}
		GenerationEventDispatcher.addListener((GenerationEventListener) screen);

		manager.showAlertAndCall(AlertType.CONFIRMATION, screen.getTitle(), screen.getContent());
		GenerationEventDispatcher.removeListener((GenerationEventListener) screen);
	}

	//--------------------------------------------------------------------
	private void openMaterialScreen() {
		logger.debug("openMaterialScreen()");
		MaterialScreen screen = new MaterialScreen(control);
		GenerationEventDispatcher.addListener(screen);

//		Object obj = manager.showAndWait(screen);
		manager.showAlertAndCall(AlertType.CONFIRMATION, screen.getTitle(), screen.getContent());
//		logger.info("obj = "+obj);
		GenerationEventDispatcher.removeListener(screen);
	}

//	//--------------------------------------------------------------------
//	private void openHolyItemScreen() {
//		logger.debug("openHolyItemScreen()");
//		HolyItemScreen screen = new MaterialScreen(control);
//		GenerationEventDispatcher.addListener(screen);
//
//		Object obj = manager.showAndWait(screen);
//		logger.info("obj = "+obj);
//		GenerationEventDispatcher.removeListener(screen);
//	}

}
