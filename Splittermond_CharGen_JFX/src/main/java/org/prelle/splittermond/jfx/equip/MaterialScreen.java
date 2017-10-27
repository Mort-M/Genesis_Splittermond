/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.TiledCell;
import org.prelle.javafx.TiledListView;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.items.Material;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class MaterialScreen extends ManagedScreen implements GenerationEventListener {

	private final static ResourceBundle UI = ResourceBundle.getBundle("i18n/splimo-chargen");

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private NewItemController control;

	private Label lblQuality;
	private TiledListView<Material,String> lvMaterials;
	private Label lblName;
	private Label lblProduct;
	private Label lblDescription;

	//-------------------------------------------------------------------
	public MaterialScreen(NewItemController ctrl) {
		this.control = ctrl;

		initComponents();
		initLayout();
		initInteractivity();

		getNavigButtons().add(CloseType.OK);
		setTitle(UI.getString("label.material"));
		setSkin(new ManagedScreenStructuredSkin(this));

		update();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblQuality    = new Label();
		lblQuality.setTextAlignment(TextAlignment.CENTER);
		lblQuality.getStyleClass().add("text-header");

		lvMaterials    = new TiledListView<>();
		lvMaterials.setCellFactory(new Callback<TiledListView<Material,String>, TiledCell<Material,String>>() {
			public TiledCell<Material, String> call(TiledListView<Material, String> param) {
				return new MaterialListCell(lvMaterials);
			}
		});
		lvMaterials.setSectionFactory(new Callback<Material,String>() {
			public String call(Material param) {
				return param.getProductName();
			}
		});

		lblName = new Label();
		lblName.getStyleClass().add("text-small-subheader");
		lblProduct = new Label();
		lblProduct.getStyleClass().add("text-small-secondary");
		lblDescription = new Label();
		lblDescription.setWrapText(true);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		// Column 1
		VBox bxSide = new VBox();
		bxSide.setStyle("-fx-vgap: 1em");
		bxSide.getChildren().add(lblQuality);
		VBox.setVgrow(lblQuality, Priority.ALWAYS);
		lblQuality.setMaxHeight(Double.MAX_VALUE);
		lblQuality.setAlignment(Pos.CENTER);
		bxSide.getStyleClass().add("base-block");
		bxSide.setMaxHeight(Double.MAX_VALUE);

		// Column 3
		VBox bxDescr = new VBox(20);
		bxDescr.getChildren().addAll(lblName, lblProduct, lblDescription);
		bxDescr.setStyle("-fx-max-width: 30em");

		// Column 2
		Label lblMaterials = new Label(UI.getString("label.materials.known"));
		lblMaterials.getStyleClass().add("text-small-subheader");
		lvMaterials.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		VBox bxContent = new VBox(20);
		bxContent.getChildren().addAll(lblMaterials, lvMaterials);
		bxContent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		bxContent.setStyle("-fx-max-width: 42em");
		VBox.setVgrow(lvMaterials, Priority.ALWAYS);


		HBox content = new HBox(20);
		content.getChildren().addAll(bxSide, bxContent, bxDescr);
		HBox.setMargin(bxSide   , new Insets(0,0,20,0));
		HBox.setMargin(bxContent, new Insets(0,0,20,0));
		HBox.setMargin(bxDescr  , new Insets(0,0,20,0));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		lvMaterials.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null)
				control.setMaterial(n);
			showDescription(n);
		});
	}

	//-------------------------------------------------------------------
	private void update() {
		lvMaterials.getItems().clear();
		lvMaterials.getItems().addAll(control.getAvailableMaterials());

		lblQuality.setText(control.getPointsLeft()+"/");
	}

	//-------------------------------------------------------------------
	private void showDescription(Material data) {
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

class MaterialListCell extends TiledCell<Material, String> {

	private Label lblName;
	private Label lblPlugin;
	private Label lblValue;
	private HBox  layout;

	//-------------------------------------------------------------------
	public MaterialListCell(TiledListView<Material, String> listView) {
		super(listView);

		lblName = new Label();
		lblName.getStyleClass().add("text-small-subheader");
		lblName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		lblPlugin = new Label();
		lblValue = new Label();
		lblValue.getStyleClass().add("text-subheader");
		lblValue.setAlignment(Pos.CENTER_RIGHT);

		VBox col1 = new VBox(lblName, lblPlugin);
		VBox.setVgrow(lblName, Priority.ALWAYS);
		col1.setMaxWidth(Double.MAX_VALUE);

		layout = new HBox(10, lblValue, col1);
		layout.setAlignment(Pos.CENTER_LEFT);
		layout.setMaxWidth(Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.TiledCell#updateItem(java.lang.Object, boolean)
	 */
	public void updateItem(Material item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setGraphic(null);
			setText(null);
		} else {
			lblName.setText(item.getName());
			lblPlugin.setText(item.getProductName());
			lblValue.setText(item.getId().startsWith("common")?"0":"1");
			setGraphic(layout);
		}
	}
}

