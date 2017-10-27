/**
 * 
 */
package org.prelle.splittermond.jfx.equip;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.rpgframework.jfx.ThreeColumnPane;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * @author prelle
 *
 */
public class EnhancementTypeScreen extends ManagedScreen implements GenerationEventListener {

	private final static ResourceBundle UI = ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private final static Logger logger = Logger.getLogger("splittermond.jfx"); 
	
	private EnhancementType type;
	private NewItemController control;
	
	private Label lblQuality;
	private EnhancementListView lvAvailable;
	private EnhancementReferenceListView lvEnhancements;
	private Label lblName;
	private Label lblProduct;
	private Label lblDescription;

	//-------------------------------------------------------------------
	public EnhancementTypeScreen(EnhancementType type, NewItemController ctrl) {
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
		bxDescr.setStyle("-fx-max-width: 30em");		
		
		ThreeColumnPane threeCols = new ThreeColumnPane();
		threeCols.setColumn1Header(UI.getString("label.available"));
		threeCols.setColumn2Header(UI.getString("label.selected"));
		threeCols.setColumn3Header(UI.getString("label.description"));
		threeCols.setColumn1Node(lvAvailable);
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
		lvEnhancements.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> showDescription(n!=null?n.getEnhancement():null));
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
