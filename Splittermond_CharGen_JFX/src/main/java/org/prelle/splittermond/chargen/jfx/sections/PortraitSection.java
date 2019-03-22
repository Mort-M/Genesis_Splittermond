/**
 *
 */
package org.prelle.splittermond.chargen.jfx.sections;

import java.io.ByteArrayInputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.SymbolIcon;
import org.prelle.rpgframework.jfx.SingleSection;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.Deity;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import de.rpgframework.character.CharacterHandle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class PortraitSection extends SingleSection {

	private final static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(PortraitSection.class.getName());

	private CharacterController control;
	private CharacterHandle handle;
	private SpliMoCharacter model;

	private ChoiceBox<Deity>     cbDeity;
	private TextField            tfHair;
	private TextField            tfEyes;
	private TextField            tfSize;
	private TextField            tfWeight;
	private TextField            tfSkin;
	private TextField            tfBirth;
	private ImageView            ivPortrait;

	private ObjectProperty<BasePluginData> showHelpFor = new SimpleObjectProperty<>();

	//-------------------------------------------------------------------
	public PortraitSection(String title, CharacterController ctrl, CharacterHandle handle, ScreenManagerProvider provider) {
		super(provider, title, null);
		control = ctrl;
		this.handle = handle;
		model = ctrl.getModel();

		initComponents();
		initLayout();
		refresh();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setDeleteButton( new Button(null, new SymbolIcon("delete")) );
		setAddButton( new Button(null, new SymbolIcon("add")) );
		getDeleteButton().setTooltip(new Tooltip(RES.getString("button.delete.tooltip")));
		getAddButton().setTooltip(new Tooltip(RES.getString("button.add.tooltip")));

		cbDeity     = new ChoiceBox<>(FXCollections.observableArrayList(SplitterMondCore.getDeities()));
		tfHair      = new TextField();
		tfEyes      = new TextField();
		tfSize      = new TextField();
		tfWeight    = new TextField();
		tfSkin      = new TextField();
		tfBirth     = new TextField();

		cbDeity.setDisable(true);
		ivPortrait  = new ImageView();
		ivPortrait.setPreserveRatio(true);
		ivPortrait.setFitHeight(200);
		ivPortrait.setFitWidth(200);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label hdDeity     = new Label(RES.getString("label.deity"));
		Label hdHair      = new Label(RES.getString("label.hair"));
		Label hdEyes      = new Label(RES.getString("label.eyes"));
		Label hdSize      = new Label(RES.getString("label.size"));
		Label hdWeight    = new Label(RES.getString("label.weight"));
		Label hdSkin      = new Label(RES.getString("label.skin"));
		Label hdBirth     = new Label(RES.getString("label.birthplace"));

		hdDeity.getStyleClass().addAll("base","table-head");
		hdHair.getStyleClass().addAll("base","table-head");
		hdEyes.getStyleClass().addAll("base","table-head");
		hdSize.getStyleClass().addAll("base","table-head");
		hdWeight.getStyleClass().addAll("base","table-head");
		hdSkin.getStyleClass().addAll("base","table-head");
		hdBirth.getStyleClass().addAll("base","table-head");
		
		GridPane grid = new GridPane();
		grid.add(hdDeity     , 0,0);
		grid.add(cbDeity     , 1,0);
		grid.add(hdHair      , 0,1);
		grid.add(tfHair      , 1,1);
		grid.add(hdSize      , 0,2);
		grid.add(tfSize      , 1,2);
		grid.add(hdEyes      , 0,3);
		grid.add(tfEyes      , 1,3);
		grid.add(hdWeight    , 0,4);
		grid.add(tfWeight    , 1,4);
		grid.add(hdSkin      , 0,5);
		grid.add(tfSkin      , 1,5);
		grid.add(hdBirth     , 0,6);
		grid.add(tfBirth     , 1,6);		
		grid.add(ivPortrait, 2, 0, 1,7);
		
		for (Node child : grid.getChildren()) {
			int x = GridPane.getColumnIndex(child);
			// Let node fill every available space
			if (x==0)
				((Region)child).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			if (x==1) {
				GridPane.setMargin(child, new Insets(4));
				if (child instanceof Region)
					((Region)child).setMaxWidth(Double.MAX_VALUE);
			}
			if (x==2)
				GridPane.setMargin(child, new Insets(4,0,4,20));
		}
		setContent(grid);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tfBirth.textProperty().addListener( (ov,o,n) -> control.getModel().setBirthPlace(n));
		tfEyes.textProperty().addListener( (ov,o,n) -> control.getModel().setEyeColor(n));
		tfHair.textProperty().addListener( (ov,o,n) -> control.getModel().setHairColor(n));
		tfSkin.textProperty().addListener( (ov,o,n) -> control.getModel().setFurColor(n));
		tfSize.textProperty().addListener( (ov,o,n) -> control.getModel().setSize(Integer.parseInt(n)));
		tfWeight.textProperty().addListener( (ov,o,n) -> control.getModel().setWeight(Integer.parseInt(n)));
		cbDeity.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.warn("TODO: implement setting deity");
//			control.getModel().setDeity(n);
		});
		getAddButton().setOnAction(ev -> onAdd());
		getDeleteButton().setOnAction(ev -> onDelete());
	}

	//-------------------------------------------------------------------
	public void refresh() {
		logger.debug("refresh");
		SpliMoCharacter model = control.getModel();
		tfHair.setText(model.getHairColor());
		tfEyes.setText(model.getEyeColor());
		tfSize.setText(String.valueOf(model.getSize()));
		tfWeight.setText(String.valueOf(model.getWeight()));
		tfSkin.setText(model.getFurColor());
		tfBirth.setText(model.getBirthplace());
//		cbDeity.getSelectionModel().select(model.getDeity());

		if (model.getImage()!=null) {
			ivPortrait.setImage(new Image(new ByteArrayInputStream(model.getImage())));
		}
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<BasePluginData> showHelpForProperty() {
		return showHelpFor;
	}

	//-------------------------------------------------------------------
	private void onAdd() {
		logger.debug("opening image selection dialog");
		
//		ItemTemplateSelector selector = new ItemTemplateSelector(allowedItemTypes, ctrl.getEquipmentController());
//		SelectorWithHelp<ItemTemplate> pane = new SelectorWithHelp<ItemTemplate>(selector);
//		ManagedDialog dialog = new ManagedDialog(UI.getString("selectiondialog.title"), pane, CloseType.OK, CloseType.CANCEL);
//		
//		CloseType close = (CloseType) getManagerProvider().getScreenManager().showAndWait(dialog);
//		logger.debug("Closed with "+close);
//		if (close==CloseType.OK) {
//			ItemTemplate selected = selector.getSelectedItem();
//			logger.debug("Selected gear: "+selected);
//			if (selected!=null) {
//				CarriedItem item = ctrl.getEquipmentController().select(selected);
//				if (item==null) {
//					getManagerProvider().getScreenManager().showAlertAndCall(AlertType.ERROR, "header", "Cannot select "+selected);
//				}
//				refresh();
//			}
//		}
	}

	//-------------------------------------------------------------------
	private void onDelete() {
		model.setImage(null);
		ivPortrait.setImage(null);
	}

}
