/**
 *
 */
package org.prelle.splittermond.chargen.fluent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Background;
import org.prelle.splimo.Culture;
import org.prelle.splimo.Deity;
import org.prelle.splimo.Education;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.Race;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class BaseDataGrid extends GridPane {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle RES = SpliMoCharGenJFXConstants.UI;

	private CharacterController  control;
	private CharacterHandle      handle;

	private TextField            tfName;
	private ComboBox<Education>  cbEducation;
	private ComboBox<Culture>    cbCulture;
	private ComboBox<Background> cbBackground;
	private ChoiceBox<Race>      cbRace;
	private ChoiceBox<Gender>    cbGender;
	private ChoiceBox<Moonsign>  cbMoonsign;
	private ChoiceBox<Deity>     cbDeity;
	private TextField            tfHair;
	private TextField            tfEyes;
	private TextField            tfSize;
	private TextField            tfWeight;
	private TextField            tfSkin;
	private TextField            tfBirth;

	private ImageView            ivPortrait;
	private Button               btnPortrait;

	//-------------------------------------------------------------------
	public BaseDataGrid(CharacterController control, CharacterHandle handle) {
		this.control = control;
		this.handle  = handle;
		initComponents();
		initLayout();

		refresh();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tfName      = new TextField();
		cbEducation = new ComboBox<>(); cbEducation.getItems().addAll((control instanceof SpliMoCharacterGenerator)?((SpliMoCharacterGenerator)control).getEducationGenerator().getAvailableEducations():SplitterMondCore.getEducations());
		cbCulture   = new ComboBox<>(); cbCulture.getItems().addAll( (control instanceof SpliMoCharacterGenerator)?((SpliMoCharacterGenerator)control).getAvailableCultures():SplitterMondCore.getCultures());
		cbBackground= new ComboBox<>(); cbBackground.getItems().addAll( (control instanceof SpliMoCharacterGenerator)?((SpliMoCharacterGenerator)control).getBackgroundGenerator().getAvailableBackgrounds():SplitterMondCore.getBackgrounds());
		cbRace      = new ChoiceBox<>(); cbRace.getItems().addAll( SplitterMondCore.getRaces() );
		cbGender    = new ChoiceBox<>(); cbGender.getItems().addAll( Arrays.asList(Gender.values()) );
		cbMoonsign  = new ChoiceBox<>(FXCollections.observableArrayList(Moonsign.values()));
		cbDeity     = new ChoiceBox<>(FXCollections.observableArrayList(SplitterMondCore.getDeities()));
		tfHair      = new TextField();
		tfEyes      = new TextField();
		tfSize      = new TextField();
		tfWeight    = new TextField();
		tfSkin      = new TextField();
		tfBirth     = new TextField();

		cbEducation.setEditable(true);
		cbCulture.setEditable(true);
		cbBackground.setEditable(true);
		cbDeity.setDisable(true);

		cbEducation.setConverter(new StringConverter<Education>() {
			public String toString(Education value) { return (value!=null)?value.getName():"?";}
			public Education fromString(String string) {
				for (Education edu : SplitterMondCore.getEducations()) {
					if (edu.getName().equalsIgnoreCase(string))
						return edu;
				}
				return new Education("custom", null, string);
			}
		});
		cbEducation.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n==null) return;
			if ("custom".equals(n.getId()))
				control.getModel().setOwnEducation(n);
			else {
				control.getModel().setOwnEducation(null);
				control.getModel().setEducation(n.getId());
			}
		});

		cbCulture.setConverter(new StringConverter<Culture>() {
			public String toString(Culture value) { return (value!=null)?value.getName():"?";}
			public Culture fromString(String string) {
				for (Culture edu : SplitterMondCore.getCultures()) {
					if (edu.getName().equalsIgnoreCase(string))
						return edu;
				}
				return new Culture("custom", string);
			}
		});
		cbCulture.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n==null) return;
			if ("custom".equals(n.getId()))
				control.getModel().setOwnCulture(n);
			else {
				control.getModel().setOwnCulture(null);
				control.getModel().setCulture(n.getId());
			}
		});

		cbBackground.setConverter(new StringConverter<Background>() {
			public String toString(Background value) { return (value!=null)?value.getName():"?";}
			public Background fromString(String string) {
				for (Background edu : SplitterMondCore.getBackgrounds()) {
					if (edu.getName().equalsIgnoreCase(string))
						return edu;
				}
				return new Background("custom", string);
			}
		});
		cbBackground.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n==null) return;
			if ("custom".equals(n.getId()))
				control.getModel().setOwnBackground(n);
			else {
				control.getModel().setOwnBackground(null);
				control.getModel().setBackground(n.getId());
			}
		});
		cbRace.setConverter(new StringConverter<Race>() {
			public String toString(Race value) { return (value!=null)?value.getName():"?";}
			public Race fromString(String string) { return null; }
		});
//		cbGender.setConverter(new StringConverter<Gender>() {
//			public String toString(Gender value) { return value.toString());}
//			public Gender fromString(String string) { return null; }
//		});
		cbDeity.setConverter(new StringConverter<Deity>() {
			public String toString(Deity value) { return (value!=null)?value.getName():"?";}
			public Deity fromString(String string) { return null; }
		});

		ivPortrait  = new ImageView();
		ivPortrait.setPreserveRatio(true);
		ivPortrait.setFitHeight(200);
		ivPortrait.setFitWidth(200);
		btnPortrait = new Button(null, ivPortrait);
//		btnPortrait.maxHeightProperty().bind(this.heightProperty().subtract(2));
//		ivPortrait.fitHeightProperty().bind(this.heightProperty().subtract(2));
////		ivPortrait.fitWidthProperty().bind(this.heightProperty().subtract(2));
////		btnPortrait.prefWidthProperty().bind(this.heightProperty());
		btnPortrait.setStyle("-fx-padding: 0");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setStyle("-fx-background-color: white");
		Label hdName      = new Label(RES.getString("label.name"));
		Label hdEducation = new Label(RES.getString("label.education"));
		Label hdCulture   = new Label(RES.getString("label.culture"));
		Label hdBackground= new Label(RES.getString("label.background"));
		Label hdRace      = new Label(RES.getString("label.race"));
		Label hdGender    = new Label(RES.getString("label.gender"));
		Label hdMoonsign  = new Label(RES.getString("label.moonsign"));
		Label hdDeity     = new Label(RES.getString("label.deity"));
		Label hdHair      = new Label(RES.getString("label.hair"));
		Label hdEyes      = new Label(RES.getString("label.eyes"));
		Label hdSize      = new Label(RES.getString("label.size"));
		Label hdWeight    = new Label(RES.getString("label.weight"));
		Label hdSkin      = new Label(RES.getString("label.skin"));
		Label hdBirth     = new Label(RES.getString("label.birthplace"));


		add(hdName      , 0,0);
		add(tfName      , 1,0, 3,1);
		add(hdEducation , 0,1);
		add(cbEducation , 1,1, 3,1);
		add(hdCulture   , 0,2);
		add(cbCulture   , 1,2);
		add(hdBackground, 2,2);
		add(cbBackground, 3,2);
		add(hdRace      , 0,3);
		add(cbRace      , 1,3);
		add(hdGender    , 2,3);
		add(cbGender    , 3,3);

		add(hdMoonsign  , 0,4);
		add(cbMoonsign  , 1,4);
		add(hdDeity     , 2,4);
		add(cbDeity     , 3,4);
		add(hdHair      , 0,5);
		add(tfHair      , 1,5);
		add(hdSize      , 2,5);
		add(tfSize      , 3,5);
		add(hdEyes      , 0,6);
		add(tfEyes      , 1,6);
		add(hdWeight    , 2,6);
		add(tfWeight    , 3,6);
		add(hdSkin      , 0,7);
		add(tfSkin      , 1,7);
		add(hdBirth     , 2,7);
		add(tfBirth     , 3,7);

		add(btnPortrait , 4,0, 1,7);

		for (Node child : this.getChildren()) {
			int x = GridPane.getColumnIndex(child);
			// Set style
			if (x==0 || x==2)
				child.getStyleClass().addAll("base","table-head");

			// Let node fill every available space
			if (child instanceof Region && !(child instanceof Button)) {
				((Region)child).setMaxWidth(Double.MAX_VALUE);
				if (x==0 || x==2)
					((Region)child).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			}

			if (x==1 || x==3) {
				GridPane.setMargin(child, new Insets(4));

			}
		}

		setMaxWidth(Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	public void initInteractivity() {
		tfName.textProperty().addListener( (ov,o,n) -> {
			if (n==null) {
				logger.error("Ignore renaming to null");
				return;
			}
			logger.info("rename character from "+control.getModel().getName()+" to "+n);
			control.getModel().setName(n);
			try {
				if (handle!=null)
					RPGFrameworkLoader.getInstance().getCharacterService().renameCharacter(handle, n);
			} catch (IOException e) {
				logger.error("Renaming failed",e);
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Renaming failed: "+e);
			}
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, control.getModel()));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, control.getModel()));
		});
		cbMoonsign.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> control.getModel().setSplinter(n));
		cbGender.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> control.getModel().setGender(n));
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
	}

	//-------------------------------------------------------------------
	public void refresh() {
		SpliMoCharacter model = control.getModel();
		tfName.setText(model.getName());
		cbEducation.setValue(model.getEducation());
		cbCulture.setValue(model.getCulture());
		cbBackground.setValue(model.getBackground());
		cbRace.setValue(model.getRace());
		cbGender.setValue(model.getGender());
		cbMoonsign.setValue(model.getSplinter());
		tfHair.setText(model.getHairColor());
		tfEyes.setText(model.getEyeColor());
		tfSize.setText(String.valueOf(model.getSize()));
		tfWeight.setText(String.valueOf(model.getWeight()));
		tfSkin.setText(model.getFurColor());
		tfBirth.setText(model.getBirthplace());

		if (model.getImage()!=null) {
			ivPortrait.setImage(new Image(new ByteArrayInputStream(model.getImage())));
		}
	}

}
