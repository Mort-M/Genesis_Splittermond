/**
 * 
 */
package org.prelle.splittermond.jfx.equip.input;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.LongRangeWeapon;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class RangeWeaponTemplateDataPane extends GridPane {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private final static ResourceBundle RES = ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private LongRangeWeapon model;
	
	private ChoiceBox<Skill> cbSkill;
	private TextField tfDamageDC;
	private TextField tfDamageDT;
	private TextField tfDamageP;
	private TextField tfSpeed;
	private TextField tfRange;
	private ChoiceBox<Attribute> cbAttr1, cbAttr2;
	private ChoiceBox<FeatureType> cbFeatures;
	private Button btnAddFeature;
	private TableView<Feature> lvFeatures;

	private TableColumn<Feature, String> nameCol;
	private TableColumn<Feature, Number> valueCol;

	//-------------------------------------------------------------------
	public RangeWeaponTemplateDataPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initComponents() {
		cbSkill    = new ChoiceBox<>();
		cbSkill.getItems().addAll(SplitterMondCore.getSkills(SkillType.COMBAT));
		cbSkill.setConverter(new StringConverter<Skill>() {
			public String toString(Skill object) { return object.getName(); }
			public Skill fromString(String string) { return null; }
		});
		tfDamageDC = new TextField();
		tfDamageDT = new TextField();
		tfDamageP = new TextField();
		tfSpeed = new TextField();
		tfRange = new TextField();
		cbAttr1 = new ChoiceBox<>();
		cbAttr1.getItems().addAll(Attribute.primaryValues());
		cbAttr1.setConverter(new StringConverter<Attribute>() {
			public String toString(Attribute object) { return object.getShortName(); }
			public Attribute fromString(String string) { return null; }
		});
		cbAttr2 = new ChoiceBox<>();
		cbAttr2.getItems().addAll(Attribute.primaryValues());
		cbAttr2.setConverter(new StringConverter<Attribute>() {
			public String toString(Attribute object) { return object.getShortName(); }
			public Attribute fromString(String string) { return null; }
		});
		cbFeatures = new ChoiceBox<>();
		cbFeatures.getItems().addAll(SplitterMondCore.getFeatureTypes());
		cbFeatures.setConverter(new StringConverter<FeatureType>() {
			public String toString(FeatureType object) { return object.getName(); }
			public FeatureType fromString(String string) { return null; }
		});
		

		lvFeatures = new TableView<Feature>();
		lvFeatures.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		lvFeatures.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
 
		nameCol = new TableColumn<Feature, String>(RES.getString("label.name"));
		valueCol = new TableColumn<Feature, Number>(RES.getString("label.value"));

		nameCol.setMinWidth(100);
		nameCol.setMaxWidth(300);
		valueCol.setMinWidth(80);
		valueCol.setMaxWidth(160);
		lvFeatures.getColumns().addAll(nameCol, valueCol);
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<Feature, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Feature, String> p) {
				Feature item = p.getValue();
				return new SimpleStringProperty(item.getType().getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<Feature, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<Feature, Number> p) {
				return new SimpleIntegerProperty( p.getValue().getLevel() );
			}
		});

		valueCol.setCellFactory(new Callback<TableColumn<Feature,Number>, TableCell<Feature,Number>>() {
			public TableCell<Feature,Number> call(TableColumn<Feature,Number> p) {
				return new FeatureEditingCellRanged(RangeWeaponTemplateDataPane.this);
			}
		});
		
		
		btnAddFeature = new Button(RES.getString("button.add"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label heaSkill     = new Label(RES.getString("label.skill"));
		Label heaDamage    = new Label(ItemAttribute.DAMAGE.getShortName());
		Label heaSpeed     = new Label(ItemAttribute.SPEED.getShortName());
		Label heaAttribute = new Label(ItemAttribute.ATTRIBUTES.getShortName());
		Label heaRange     = new Label(ItemAttribute.RANGE.getShortName());
		Label heaFeatures  = new Label(ItemAttribute.FEATURES.getShortName());
		
		HBox lineDamage = new HBox(5);
		lineDamage.setAlignment(Pos.CENTER_LEFT);
		lineDamage.getChildren().addAll(
				tfDamageDC,
				new Label(RES.getString("label.dice.short")),
				tfDamageDT,
				new Label("+"),
				tfDamageP
				);
		
		HBox lineAttrib = new HBox(5);
		lineAttrib.getChildren().addAll(cbAttr1, cbAttr2);
		
		GridPane gridFeatures = new GridPane();
		gridFeatures.setHgap(5);
		gridFeatures.setVgap(5);
		gridFeatures.add(cbFeatures   , 0, 0);
		gridFeatures.add(btnAddFeature, 1, 0);
		gridFeatures.add(lvFeatures   , 0, 1, 2,1);
		lvFeatures.setStyle("-fx-pref-height: 12em");
		
		add(heaSkill      , 0,0);
		add(cbSkill       , 1,0);
		add(heaDamage     , 0,1);
		add(lineDamage    , 1,1);
		add(heaSpeed      , 0,2);
		add(tfSpeed       , 1,2);
		add(heaAttribute  , 0,3);
		add(lineAttrib    , 1,3);
		add(heaRange      , 0,4);
		add(tfRange       , 1,4);
		add(heaFeatures   , 0,5);
		add(gridFeatures  , 1,5);
		
		heaFeatures.setAlignment(Pos.TOP_LEFT);
		heaFeatures.setMaxHeight(Double.MAX_VALUE);
		
		setVgap(2);
		setHgap(5);
		
		/*
		 * Styles
		 */
		this.getStyleClass().addAll("content","text-body","bordered");
		heaSkill.getStyleClass().add("text-small-subheader");
		heaDamage.getStyleClass().add("text-small-subheader");
		heaSpeed.getStyleClass().add("text-small-subheader");
		heaAttribute.getStyleClass().add("text-small-subheader");
		heaRange.getStyleClass().add("text-small-subheader");
		heaFeatures.getStyleClass().add("text-small-subheader");

		tfDamageDC.setStyle("-fx-max-width: 2.5em");
		tfDamageDT.setStyle("-fx-max-width: 2.5em");
		tfDamageP.setStyle("-fx-max-width: 2.5em");
		tfSpeed.setStyle("-fx-max-width: 2.5em");
		tfRange.setStyle("-fx-max-width: 2.5em");
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnAddFeature.setOnAction(event -> {
			FeatureType fType = cbFeatures.getSelectionModel().getSelectedItem();
			if (fType==null) 
				return;
			// Search previous
			for (Feature feature : model.getFeatures()) {
				if (feature.getType()==fType) {
					feature.setLevel(feature.getLevel()+1);
					return;
				}
			}
			model.getFeatures().add(new Feature(fType));
			cbFeatures.getSelectionModel().clearSelection();
			refresh();
		});
		
		cbSkill.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setSkill(n));
		cbAttr1.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setAttribute1(n));
		cbAttr2.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setAttribute2(n));
		tfSpeed.textProperty().addListener( (ov,o,n)-> model.setSpeed(Integer.parseInt(tfSpeed.getText())));
		tfRange.textProperty().addListener( (ov,o,n)-> model.setRange(Integer.parseInt(tfRange.getText())));
		tfDamageDC.textProperty().addListener( (ov,o,n)-> {
			model.setDamage(Integer.parseInt(n)*10000 + model.getDamage()%10000);
			});
		tfDamageDT.textProperty().addListener( (ov,o,n)-> {
			int dc = (model.getDamage()/10000)*10000;
			int dt = Integer.parseInt(n)*100;
			model.setDamage(dc + dt + model.getDamage()%100);
			});
		tfDamageP .textProperty().addListener( (ov,o,n)-> {
			int mod = Integer.parseInt(n);
			if (mod<0)
				mod = 100-mod;
			model.setDamage(model.getDamage()/100*100 + mod);
			});
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (model==null)
			throw new NullPointerException();
		cbSkill.getSelectionModel().select(model.getSkill());
		tfDamageDC.setText( String.valueOf(LongRangeWeapon.getDamageDiceCount(model)) );
		tfDamageDT.setText( String.valueOf(LongRangeWeapon.getDamageDiceType(model)) );
		tfDamageP.setText( String.valueOf(LongRangeWeapon.getDamageModifier(model)) );
		tfSpeed.setText( String.valueOf(model.getSpeed() ));
		tfRange.setText( String.valueOf(model.getRange() ));
		cbAttr1.getSelectionModel().select(model.getAttribute1());
		cbAttr2.getSelectionModel().select(model.getAttribute2());
		lvFeatures.getItems().clear();
		lvFeatures.getItems().addAll(model.getFeatures());
	}

	//-------------------------------------------------------------------
	public void setData(LongRangeWeapon weapon) {
		if (weapon==null)
			throw new NullPointerException();
		logger.debug("overwrite with = "+weapon);
		model = weapon;
		refresh();
	}

	//-------------------------------------------------------------------
	LongRangeWeapon getModel() {
		return model;
	}

}

class FeatureEditingCellRanged extends TableCell<Feature, Number> implements ChangeListener<Integer>{

	private Spinner<Integer> box;
	private RangeWeaponTemplateDataPane charGen;
	private Feature feature;

	//-------------------------------------------------------------------
	public FeatureEditingCellRanged(RangeWeaponTemplateDataPane charGen) {
		this.charGen = charGen;
		box = new Spinner<>(-1, 6, 1);
		box.valueProperty().addListener(this);
		box.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
//		box.setCyclic(false);
		setAlignment(Pos.CENTER);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Number item, boolean empty) {
		super.updateItem(item, empty);

		if (item==null || empty || getTableRow()==null) {
			this.setGraphic(null);
			return;
		}

		feature = (Feature) getTableRow().getItem();
		if (feature==null)
			return;
		//		parent.setMapping(resource, this);
		box.getValueFactory().setValue(feature.getLevel());

		this.setGraphic(box);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
		if (val>feature.getLevel() && feature.getLevel()<4) {
			feature.setLevel(feature.getLevel()+1);
		} else if (val<feature.getLevel() && feature.getLevel()>0) {
			feature.setLevel(feature.getLevel()-1);
		} else if (val<feature.getLevel() && feature.getLevel()==0) {
			charGen.getModel().getFeatures().remove(feature);
			charGen.refresh();
		} else {
			// Revert back
			box.getValueFactory().setValue(feature.getLevel());
		}
	}

}
