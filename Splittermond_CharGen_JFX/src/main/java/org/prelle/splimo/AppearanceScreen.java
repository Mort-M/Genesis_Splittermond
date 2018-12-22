/**
 *
 */
package org.prelle.splimo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;

import de.rpgframework.RPGFramework;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class AppearanceScreen extends ManagedScreen {

	private final static Logger logger = Logger.getLogger("splimo.jfx");
	private final static String INVALID = "invalid-data";

	private static Preferences CONFIG = Preferences.userRoot().node(RPGFramework.LAST_OPEN_DIR);

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CharacterController control;
	private ViewMode mode;
	private SpliMoCharacter model;

	private TextField tfName;
	private TextField tfSize;
	private TextField tfWeight;
	private TextField tfHair;
	private TextField tfEyes;
	private TextField tfSkin;
	private TextField tfBirthPlace;
	private ChoiceBox<Gender> cbGender;
	private ChoiceBox<Moonsign> cbMoonsign;

	private ImageView ivPortrait;
	private Button    btnOpen;
	private Button    btnDel;

	//-------------------------------------------------------------------
	/**
	 */
	public AppearanceScreen(CharacterController control, ViewMode mode) {
		this.control = control;
		this.mode    = mode;

		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(uiResources.getString("label.appearance"));

		tfName = new TextField();
		tfSize = new TextField();
		tfWeight = new TextField();
		tfHair = new TextField();
		tfEyes = new TextField();
		tfSkin = new TextField();
		tfBirthPlace = new TextField();
		cbGender = new ChoiceBox<>();
		cbGender.getItems().addAll(Gender.values());
		cbMoonsign = new ChoiceBox<>();
		cbMoonsign.getItems().addAll(Moonsign.values());

		ivPortrait = new ImageView();
		ivPortrait.setFitHeight(400);
		ivPortrait.setFitWidth(400);
		btnOpen = new Button(null, new FontIcon("\uE227\uE187",32));
		btnDel  = new Button(null, new FontIcon("\uE227\uE107",32));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label heaName      = new Label(uiResources.getString("label.name"));
		Label heaSize      = new Label(uiResources.getString("label.size"));
		Label heaWeight    = new Label(uiResources.getString("label.weight"));
		Label heaHair      = new Label(uiResources.getString("label.hair"));
		Label heaEyes      = new Label(uiResources.getString("label.eyes"));
		Label heaSkin      = new Label(uiResources.getString("label.skin"));
		Label heaBirthPlace    = new Label(uiResources.getString("label.birthplace"));
		Label heaGender    = new Label(uiResources.getString("label.gender"));
		Label heaMoonsign  = new Label(uiResources.getString("label.moonsign"));

		HBox sizeLine = new HBox(5);
		sizeLine.getChildren().addAll(tfSize, new Label(uiResources.getString("label.size.unit")));
		HBox wghtLine = new HBox(5);
		wghtLine.getChildren().addAll(tfWeight, new Label(uiResources.getString("label.weight.unit")));
		sizeLine.setAlignment(Pos.CENTER_LEFT);
		wghtLine.setAlignment(Pos.CENTER_LEFT);

		GridPane grid = new GridPane();
		grid.add(heaName  , 0, 0);
		grid.add( tfName  , 1, 0);
		grid.add(heaSize  , 0, 1);
		grid.add( sizeLine, 1, 1);
		grid.add(heaWeight, 0, 2);
		grid.add( wghtLine, 1, 2);
		grid.add(heaHair  , 0, 3);
		grid.add( tfHair  , 1, 3);
		grid.add(heaEyes  , 0, 4);
		grid.add( tfEyes  , 1, 4);
		grid.add(heaSkin  , 0, 5);
		grid.add( tfSkin  , 1, 5);
		grid.add(heaBirthPlace, 0, 6);
		grid.add( tfBirthPlace, 1, 6);
		grid.add(heaGender, 0, 7);
		grid.add( cbGender, 1, 7);
		grid.add(heaMoonsign, 0, 8);
		grid.add( cbMoonsign, 1, 8);

		grid.setHgap(20);
		grid.setVgap(20);

		TilePane buttons = new TilePane(Orientation.HORIZONTAL, 10, 10);
		buttons.getChildren().addAll(btnOpen, btnDel);
		buttons.setAlignment(Pos.CENTER);
		VBox bxPortrait = new VBox(20);
		bxPortrait.getChildren().addAll(ivPortrait, buttons);

		HBox content = new HBox(40);
		content.getChildren().addAll(grid, bxPortrait);

		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		if (control instanceof SpliMoCharacterGenerator) {
			SpliMoCharacterGenerator charGen = (SpliMoCharacterGenerator)control;
			tfName.textProperty().addListener( (ov,o,n) -> {
				if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); tfName.setText(n); }
				if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); tfName.setText(n); }
				if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); tfName.setText(n); }
				if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); tfName.setText(n); }
				charGen.setName(tfName.getText());
				setTitle(model.getName()+" / "+uiResources.getString("label.appearance"));
				});
		} else {
			tfName.textProperty().addListener( (ov,o,n) -> {
				if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); tfName.setText(n); }
				if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); tfName.setText(n); }
				if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); tfName.setText(n); }
				if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); tfName.setText(n); }
				model.setName(tfName.getText());
				setTitle(model.getName()+" / "+uiResources.getString("label.appearance"));
				});
		}

		btnDel.setOnAction(event -> {
			model.setImage(null);
			ivPortrait.setImage(null);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
		});
		btnOpen.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				String lastDir = CONFIG.get(RPGFramework.PROP_LAST_OPEN_IMAGE_DIR, System.getProperty("user.home"));
				chooser.setInitialDirectory(new File(lastDir));
				chooser.getExtensionFilters().addAll(
		                new FileChooser.ExtensionFilter("All", "*.*"),
		                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
		                new FileChooser.ExtensionFilter("PNG", "*.png")
		            );
				File selection = chooser.showOpenDialog(new Stage());
				if (selection!=null) {
					CONFIG.put(RPGFramework.PROP_LAST_OPEN_IMAGE_DIR, selection.getParentFile().getAbsolutePath().toString());
					try {
						byte[] imgBytes = Files.readAllBytes(selection.toPath());
						ivPortrait.setImage(new Image(new ByteArrayInputStream(imgBytes)));
						model.setImage(imgBytes);
						GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
					} catch (IOException e) {
						logger.warn("Failed loading image from "+selection+": "+e);
					}
				}
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.ManagedScreen.CloseType)
	 */
	@Override
	public boolean close(CloseType type) {
		logger.info("close("+type+")");
		try {
			model.setSize(Integer.parseInt(tfSize.getText()));
			tfSize.getStyleClass().remove(INVALID);
		} catch (NumberFormatException e) {
			tfSize.getStyleClass().add(INVALID);
			return false;
		}
		try {
			model.setWeight(Integer.parseInt(tfWeight.getText()));
			tfWeight.getStyleClass().remove(INVALID);
		} catch (NumberFormatException e) {
			tfWeight.getStyleClass().add(INVALID);
			return false;
		}

		model.setHairColor(tfHair.getText());
		model.setEyeColor(tfEyes.getText());
		model.setFurColor(tfSkin.getText());
		model.setBirthPlace(tfBirthPlace.getText());
		model.setGender(cbGender.getValue());
		model.setSplinter(cbMoonsign.getValue());

		logger.info("fire event");
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#childClosed(org.prelle.javafx.ManagedScreen, org.prelle.javafx.ManagedScreen.CloseType)
	 */
	@Override
	public void childClosed(ManagedScreen child, CloseType type) {
		logger.info("childClosed("+type+")");
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		setTitle(model.getName()+" / "+uiResources.getString("label.appearance"));

		tfName.setText(model.getName());
//		if (model.getRace()!=null)
//			cbRace.getSelectionModel().select(model.getRace());
//
//		// Culture
//		if (model.getCulture()!=null) {
//			tfCulture.setText(model.getCulture().getName());
//			cbCulture.getSelectionModel().select(model.getCulture());
//		}

		tfSize.setText(String.valueOf(model.getSize()));
		tfWeight.setText(String.valueOf(model.getWeight()));
		tfHair.setText(model.getHairColor());
		tfEyes.setText(model.getEyeColor());
		tfSkin.setText(model.getFurColor());
		tfBirthPlace.setText(model.getBirthplace());
		cbGender.setValue(model.getGender());
		cbMoonsign.setValue(model.getSplinter());
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));

		if (model.getImage()!=null) {
			logger.debug("Found image with "+model.getImage().length+" bytes");
			ivPortrait.setImage(new Image(new ByteArrayInputStream(model.getImage())));
		} else {
			ivPortrait.setImage(new Image(ClassLoader.getSystemResourceAsStream("images/guest-256.png")));
		}
	}

}
