/**
 * 
 */
package org.prelle.splittermond.jfx.equip;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.rpgframework.jfx.ThreeColumnPane;
import org.prelle.splimo.Deity;
import org.prelle.splimo.DeityType;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SaintPowersScreen extends ManagedScreen implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;
	
	private EnhancementType type;
	private NewItemController control;
	
	private Label lblQuality;
	private ChoiceBox<Deity> cbDeities;
	private ChoiceBox<DeityType> cbDeityTypes;
	private EnhancementListView lvAvailable;
	private EnhancementReferenceListView lvEnhancements;
	private Label lblName;
	private Label lblProduct;
	private Label lblDescription;

	//-------------------------------------------------------------------
	public SaintPowersScreen(EnhancementType type, NewItemController ctrl) {
		this.type = type;
		this.control = ctrl;
		
		initComponents();
		initLayout();
		initInteractivity();
		
		getNavigButtons().add(CloseType.OK);
		setTitle(type.getName());
		setSkin(new ManagedScreenStructuredSkin(this));
		
		update();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblQuality    = new Label();
		lblQuality.setTextAlignment(TextAlignment.CENTER);
		lblQuality.getStyleClass().add("text-header");
		
		lvEnhancements = new EnhancementReferenceListView(control, this);
		lvAvailable    = new EnhancementListView(control);
		
		lblName = new Label();
		lblName.getStyleClass().add("text-small-subheader");
		lblProduct = new Label();
		lblProduct.getStyleClass().add("text-small-secondary");
		lblDescription = new Label();
		lblDescription.setWrapText(true);
		
		cbDeities = new ChoiceBox<>();
		cbDeities.setConverter(new StringConverter<Deity>() {
			public String toString(Deity data) { return data.getName(); }
			public Deity fromString(String string) { return null; }
		});
		cbDeities.getItems().addAll(SplitterMondCore.getDeities());
		cbDeityTypes = new ChoiceBox<>();
		cbDeityTypes.setConverter(new StringConverter<DeityType>() {
			public String toString(DeityType data) { return data.getName(); }
			public DeityType fromString(String string) { return null; }
		});
		cbDeityTypes.getItems().addAll(SplitterMondCore.getDeityTypes());
		
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		
		VBox bxSide = new VBox();
		bxSide.setStyle("-fx-vgap: 1em");		
		bxSide.getChildren().add(lblQuality);
		VBox.setVgrow(lblQuality, Priority.ALWAYS);
		lblQuality.setMaxHeight(Double.MAX_VALUE);
		lblQuality.setAlignment(Pos.CENTER);
		bxSide.getStyleClass().add("base-block");
		bxSide.setMaxHeight(Double.MAX_VALUE);
		
		VBox bxDescr = new VBox(20);
		bxDescr.getChildren().addAll(lblName, lblProduct, lblDescription);
		
		// Column 1
		GridPane grid = new GridPane();
		grid.add(new Label(UI.getString("label.deity")), 0, 0);
		grid.add(cbDeities, 1, 0);
		grid.add(new Label(UI.getString("label.deitytype")), 0, 1);
		grid.add(cbDeityTypes, 1, 1);
		grid.setStyle("-fx-vgap: 0.5em; -fx-hgap: 0.5em;"); 
		
		VBox column2 = new VBox(10, grid, lvAvailable);
		
		ThreeColumnPane threeCols = new ThreeColumnPane();
		threeCols.setHeadersVisible(true);
		threeCols.setColumn1Header(UI.getString("label.available"));
		threeCols.setColumn2Header(UI.getString("label.selected"));
		threeCols.setColumn3Header(UI.getString("label.description"));
		threeCols.setColumn1Node(column2);
		threeCols.setColumn2Node(lvEnhancements);
		threeCols.setColumn3Node(bxDescr);
		threeCols.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		HBox content = new HBox(20);
		content.getChildren().addAll(bxSide, threeCols);
		HBox.setMargin(bxSide, new Insets(0,0,20,0));
		HBox.setMargin(threeCols, new Insets(0,0,20,0));
		
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		lvAvailable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> showDescription(n));
		lvEnhancements.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> showDescription(n.getEnhancement()));
		
		cbDeities.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> selectDeity(n));
		cbDeityTypes.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> selectDeityType(n));
	}

	//-------------------------------------------------------------------
	private void selectDeity(Deity data) {
		cbDeityTypes.getItems().clear();
		
		if (data==null)
			cbDeityTypes.getItems().addAll(SplitterMondCore.getDeityTypes());
		else {
			logger.warn("TODO: find deity types of deity "+data);
		}
	}

	//-------------------------------------------------------------------
	private void selectDeityType(DeityType data) {
		lvAvailable.getItems().clear();
		
		if (data!=null) {
			lvAvailable.getItems().addAll(control.getAvailableEnhancements(data));
		}
	}

	//-------------------------------------------------------------------
	private void update() {
		lvAvailable.getItems().clear();
		lvAvailable.getItems().addAll(control.getAvailableEnhancements(type));
		lvEnhancements.getItems().clear();
		lvEnhancements.getItems().addAll(control.getItem().getEnhancements(type));
		
		lblQuality.setText(control.getPointsLeft()+"/");
	}

	//-------------------------------------------------------------------
	private void showDescription(Enhancement data) {
		if (data==null) {
			lblName.setText(null);
			lblProduct.setText(null);
			lblDescription.setText(null);
		} else {
			lblName.setText(data.getName());
			lblProduct.setText(data.getProductName()+" "+data.getPage());
			lblDescription.setText(data.getHelpText());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.debug("RCV "+event);
		switch (event.getType()) {
		case ITEM_CHANGED:
			logger.debug("RCV "+event);
			update();
			break;
		default:
		}
	}

}
