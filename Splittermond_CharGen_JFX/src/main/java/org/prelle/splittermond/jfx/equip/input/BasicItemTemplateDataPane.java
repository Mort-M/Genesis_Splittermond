/**
 * 
 */
package org.prelle.splittermond.jfx.equip.input;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.rpgframework.jfx.DataInputPane;
import org.prelle.splimo.items.Availability;
import org.prelle.splimo.items.Complexity;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.MaterialType;

import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class BasicItemTemplateDataPane extends GridPane implements DataInputPane<ItemTemplate> {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private final static ResourceBundle RES = ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private ItemTemplate model;

	private ChoiceBox<Availability> cbAvailability;
	private TextField tfPriceL;
	private TextField tfPriceT;
	private TextField tfLoad;
	private TextField tfRigidity;
	private ChoiceBox<Complexity> cbComplexity;
	private ChoiceBox<MaterialType> cbMaterial;
	
	//-------------------------------------------------------------------
	public BasicItemTemplateDataPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	public BasicItemTemplateDataPane(ItemTemplate model) {
		this.model = model;
		initComponents();
		initLayout();
		initInteractivity();
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		cbAvailability = new ChoiceBox<>();
		cbAvailability.getItems().addAll(Availability.values());
		cbAvailability.setConverter(new StringConverter<Availability>() {
			public String toString(Availability object) { return object.getName(); }
			public Availability fromString(String string) { return null; }
		});
		cbAvailability.getSelectionModel().select(0);
		tfPriceL   = new TextField();
		tfPriceT   = new TextField();
		tfLoad     = new TextField();
		tfRigidity = new TextField();
		tfPriceL.setStyle("-fx-max-width: 2.5em");
		tfPriceT.setStyle("-fx-max-width: 2.5em");
		tfLoad.setStyle("-fx-max-width: 2.5em");
		tfRigidity.setStyle("-fx-max-width: 2.5em");
		cbComplexity = new ChoiceBox<>();
		cbComplexity.getItems().addAll(Complexity.values());
		cbComplexity.setConverter(new StringConverter<Complexity>() {
			public String toString(Complexity object) { return object.getName(); }
			public Complexity fromString(String string) { return null; }
		});
		cbMaterial = new ChoiceBox<>();
		cbMaterial.getItems().addAll(MaterialType.values());
		cbMaterial.setConverter(new StringConverter<MaterialType>() {
			public String toString(MaterialType object) { return object.getName(); }
			public MaterialType fromString(String string) { return null; }
		});
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label heaAvailability = new Label(ItemAttribute.AVAILABILITY.getShortName());
		Label heaPrice        = new Label(ItemAttribute.PRICE.getShortName());
		Label heaLoad         = new Label(ItemAttribute.LOAD.getShortName());
		Label heaRigidity     = new Label(ItemAttribute.RIGIDITY.getShortName());
		Label heaComplexity   = new Label(ItemAttribute.COMPLEXITY.getShortName());
		Label heaMaterial     = new Label(ItemAttribute.MATERIAL_TYPE.getShortName());
		
		HBox linePrice = new HBox(5);
		linePrice.setAlignment(Pos.CENTER_LEFT);
		linePrice.getChildren().addAll(
				tfPriceL,
				new Label(RES.getString("currency.lunare.short")),
				tfPriceT,
				new Label(RES.getString("currency.telare.short"))
				);


		add(heaAvailability, 0,0);
		add(cbAvailability , 1,0);
		add(heaPrice       , 0,1);
		add(linePrice      , 1,1);
		add(heaLoad        , 0,2);
		add(tfLoad         , 1,2);
		add(heaRigidity    , 0,3);
		add(tfRigidity     , 1,3);
		add(heaComplexity  , 0,4);
		add(cbComplexity   , 1,4);
		add(heaMaterial    , 0,5);
		add(cbMaterial     , 1,5);
		
		setVgap(5);
		setHgap(5);
		
		/*
		 * Styles
		 */
		this.getStyleClass().addAll("content","text-body","bordered");
		heaAvailability.getStyleClass().add("text-small-subheader");
		heaPrice.getStyleClass().add("text-small-subheader");
		heaLoad.getStyleClass().add("text-small-subheader");
		heaRigidity.getStyleClass().add("text-small-subheader");
		heaComplexity.getStyleClass().add("text-small-subheader");
		heaMaterial.getStyleClass().add("text-small-subheader");
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		
		cbAvailability.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setAvailability(n));
		cbComplexity.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setComplexity(n));
		cbMaterial.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setMaterialType(n));
		tfLoad.textProperty().addListener( (ov,o,n)-> model.setLoad(Integer.parseInt(tfLoad.getText())));
		tfRigidity.textProperty().addListener( (ov,o,n)-> model.setRigidity(Integer.parseInt(tfRigidity.getText())));
		tfPriceL.textProperty().addListener( (ov,o,n)-> model.setPrice(Integer.parseInt(tfPriceL.getText())*100 + model.getPrice()%100));
		tfPriceT.textProperty().addListener( (ov,o,n)-> {
			int mod = Integer.parseInt(tfPriceT.getText());
			model.setPrice(model.getPrice()/100*100 + mod);
			});
	}

	//-------------------------------------------------------------------
	public void setData(ItemTemplate model)  {
		this.model = model;
		logger.debug("setData("+model+")");
//		initInteractivity();
		refresh();
	}

	//-------------------------------------------------------------------
	private void refresh() {
		if (model==null)
			return;
		cbAvailability.getSelectionModel().select(model.getAvailability());
		cbComplexity.getSelectionModel().select(model.getComplexity());
		cbMaterial.getSelectionModel().select(model.getMaterialType());
		tfRigidity.setText(String.valueOf(model.getRigidity()));
		tfLoad.setText(String.valueOf(model.getLoad()));
		tfPriceL.setText(String.valueOf(model.getPrice()/100));
		tfPriceT.setText(String.valueOf(model.getPrice()%100));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.DataInputPane#writeIntoData(java.lang.Object)
	 */
	@Override
	public void writeIntoData(ItemTemplate data) {
		model.setAvailability(cbAvailability.getValue());
		model.setComplexity(cbComplexity.getValue());
		model.setMaterialType(cbMaterial.getValue());
		model.setLoad(Integer.parseInt(tfLoad.getText()));
		model.setRigidity(Integer.parseInt(tfRigidity.getText()));
		int telare = Integer.parseInt(tfPriceT.getText()) + Integer.parseInt(tfPriceL.getText())*100;
		model.setPrice(telare);
	}


}
