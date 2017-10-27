/**
 * 
 */
package org.prelle.splittermond.jfx.equip.input;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.Shield;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splimo.requirements.Requirement;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jfxtras.scene.control.ListSpinner;

/**
 * @author prelle
 *
 */
public class ShieldTemplateDataPane extends GridPane {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private final static ResourceBundle RES = ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private Shield model;
	
	protected TextField tfDefense;
	protected TextField tfHandicap;
	protected TextField tfTickMalus;
	private ChoiceBox<AttributeRequirement> cbMinStr;
	protected ChoiceBox<FeatureType> cbFeatures;
	protected Button btnAddFeature;
	protected TableView<Feature> lvFeatures;

	private TableColumn<Feature, String> nameCol;
	private TableColumn<Feature, Number> valueCol;

	//-------------------------------------------------------------------
	public ShieldTemplateDataPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	protected void initComponents() {
		tfDefense   = new TextField();
		tfHandicap  = new TextField();
		tfTickMalus = new TextField();
		cbMinStr    = new ChoiceBox<AttributeRequirement>();
		for (int i=1; i<=4; i++)
			cbMinStr.getItems().add(new AttributeRequirement(Attribute.STRENGTH, i));
		for (int i=1; i<=4; i++)
			cbMinStr.getItems().add(new AttributeRequirement(Attribute.AGILITY, i));
		cbMinStr.setConverter(new StringConverter<AttributeRequirement>() {
			public String toString(AttributeRequirement object) { return object.getAttribute().getShortName()+" "+object.getValue(); }
			public AttributeRequirement fromString(String string) { return null; }
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
				return new ShieldFeatureEditingCell(ShieldTemplateDataPane.this);
			}
		});
		
		
		btnAddFeature = new Button(RES.getString("button.add"));
	}

	//-------------------------------------------------------------------
	protected void initLayout() {
		Label heaDefense   = new Label(ItemAttribute.DEFENSE.getShortName());
		Label heaHandicap  = new Label(ItemAttribute.HANDICAP.getShortName());
		Label heaTickMalus = new Label(ItemAttribute.TICK_MALUS.getShortName());
		Label heaMinStr    = new Label(ItemAttribute.MIN_ATTRIBUTES.getShortName());
		Label heaFeatures  = new Label(ItemAttribute.FEATURES.getShortName());
		
		GridPane gridFeatures = new GridPane();
		gridFeatures.setHgap(5);
		gridFeatures.setVgap(5);
		gridFeatures.add(cbFeatures   , 0, 0);
		gridFeatures.add(btnAddFeature, 1, 0);
		gridFeatures.add(lvFeatures   , 0, 1, 2,1);
		lvFeatures.setStyle("-fx-pref-height: 12em");
		
		add(heaDefense   , 0,0);
		add(tfDefense    , 1,0);
		add(heaHandicap  , 0,2);
		add(tfHandicap   , 1,2);
		add(heaTickMalus , 0,3);
		add(tfTickMalus  , 1,3);
		add(heaMinStr    , 0,4);
		add(cbMinStr     , 1,4);
		add(heaFeatures  , 0,5);
		add(gridFeatures , 1,5);
		
		heaFeatures.setAlignment(Pos.TOP_LEFT);
		heaFeatures.setMaxHeight(Double.MAX_VALUE);
		
		setVgap(2);
		setHgap(5);
		
		/*
		 * Styles
		 */
		this.getStyleClass().addAll("content","text-body","bordered");
		heaDefense  .getStyleClass().add("text-small-subheader");
		heaHandicap .getStyleClass().add("text-small-subheader");
		heaTickMalus.getStyleClass().add("text-small-subheader");
		heaMinStr   .getStyleClass().add("text-small-subheader");
		heaFeatures .getStyleClass().add("text-small-subheader");

		tfDefense.setStyle("-fx-max-width: 2.5em");
		tfHandicap.setStyle("-fx-max-width: 2.5em");
		tfTickMalus.setStyle("-fx-max-width: 2.5em");
	}

	//-------------------------------------------------------------------
	protected void initInteractivity() {
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
			model.addFeature(new Feature(fType));
			cbFeatures.getSelectionModel().clearSelection();
			refresh();
		});
		
		cbMinStr.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			model.getRequirements().clear();
			if (n!=null) {
				model.addRequirement(n);			
				logger.debug("Added requirement "+n+"    --> "+model);
			} else {
				logger.debug("Clear requirements");
			}
			});
		tfDefense.textProperty().addListener( (ov,o,n)-> model.setDefense(Integer.parseInt(tfDefense.getText())));
		tfHandicap.textProperty().addListener( (ov,o,n)-> model.setHandicap(Integer.parseInt(tfHandicap.getText())));
		tfTickMalus.textProperty().addListener( (ov,o,n)-> model.setTickMalus(Integer.parseInt(tfTickMalus.getText())));
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		logger.debug("refresh "+model);
		if (model==null)
			return;
		// Defense
		tfDefense.setText(String.valueOf(model.getDefense()));
		tfHandicap.setText(String.valueOf(model.getHandicap()));
		tfTickMalus.setText(String.valueOf(model.getTickMalus()));
//		cbMinStr.getSelectionModel().clearSelection();
		for (Requirement req : model.getRequirements() ) {
			logger.debug("* "+req);
			if (req instanceof AttributeRequirement) {
				AttributeRequirement aReq = null;
				for (AttributeRequirement cmp : cbMinStr.getItems()) {
					if (cmp.equals(req)) {
						aReq = cmp;
						break;
					}
						
				}
				if (aReq!=null) {
					logger.debug("Select "+aReq);
					cbMinStr.getSelectionModel().select(aReq);
				} else
					logger.warn("Cannot select "+aReq);
			}
		}
		lvFeatures.getItems().clear();
		lvFeatures.getItems().addAll(model.getFeatures());		
	}

	//-------------------------------------------------------------------
	public void setData(Shield weapon) {
		if (weapon==null)
			throw new NullPointerException();
		logger.debug("overwrite with = "+weapon);
		model = weapon;
		refresh();
	}

	//-------------------------------------------------------------------
	Shield getModel() {
		return model;
	}

}

class ShieldFeatureEditingCell extends TableCell<Feature, Number> implements ChangeListener<Integer>{

	private ListSpinner<Integer> box;
	private ShieldTemplateDataPane charGen;
	private Feature feature;

	//-------------------------------------------------------------------
	public ShieldFeatureEditingCell(ShieldTemplateDataPane charGen) {
		this.charGen = charGen;
		box = new ListSpinner<>(-1, 6, 1);
		box.valueProperty().addListener(this);
		box.setCyclic(false);
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
		box.valueProperty().set(feature.getLevel());

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
			box.valueProperty().set(feature.getLevel());
		}
	}

}

