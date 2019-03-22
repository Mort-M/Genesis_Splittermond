/**
 *
 */
package org.prelle.splittermond.chargen.jfx.sections;

import java.io.IOException;
import java.util.Arrays;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.SingleSection;
import org.prelle.splimo.Background;
import org.prelle.splimo.BasePluginData;
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
import org.prelle.splittermond.chargen.jfx.ViewMode;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class BasicDataSection extends SingleSection {

	private final static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(BasicDataSection.class.getName());

	private CharacterController control;
	private ViewMode mode;
	private CharacterHandle handle;
	private SpliMoCharacter model;

	private TextField            tfName;
	private ComboBox<Education>  cbEducation;
	private ComboBox<Culture>    cbCulture;
	private ComboBox<Background> cbBackground;
	private ChoiceBox<Race>      cbRace;
	private ChoiceBox<Gender>    cbGender;
	private ChoiceBox<Moonsign>  cbMoonsign;
	private ChoiceBox<Deity>     cbDeity;


	private ObjectProperty<BasePluginData> showHelpFor = new SimpleObjectProperty<>();

	//-------------------------------------------------------------------
	public BasicDataSection(String title, CharacterController ctrl, CharacterHandle handle, ViewMode mode, ScreenManagerProvider provider) {
		super(provider, title, null);
		control = ctrl;
		this.handle = handle;
		this.mode = mode;
		model = ctrl.getModel();

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

		cbEducation.setEditable(true);
		cbCulture.setEditable(true);
		cbBackground.setEditable(true);
		cbDeity.setDisable(true);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label hdName      = new Label(RES.getString("label.name"));
		Label hdEducation = new Label(RES.getString("label.education"));
		Label hdCulture   = new Label(RES.getString("label.culture"));
		Label hdBackground= new Label(RES.getString("label.background"));
		Label hdRace      = new Label(RES.getString("label.race"));
		Label hdGender    = new Label(RES.getString("label.gender"));
		Label hdMoonsign  = new Label(RES.getString("label.moonsign"));

		hdName.getStyleClass().addAll("base","table-head");
		hdEducation.getStyleClass().addAll("base","table-head");
		hdCulture.getStyleClass().addAll("base","table-head");
		hdBackground.getStyleClass().addAll("base","table-head");
		hdRace.getStyleClass().addAll("base","table-head");
		hdGender.getStyleClass().addAll("base","table-head");
		hdMoonsign.getStyleClass().addAll("base","table-head");
		
		GridPane grid = new GridPane();
		grid.add(hdName      , 0,0);
		grid.add(tfName      , 1,0);
		grid.add(hdEducation , 0,1);
		grid.add(cbEducation , 1,1);
		grid.add(hdCulture   , 0,2);
		grid.add(cbCulture   , 1,2);
		grid.add(hdBackground, 0,3);
		grid.add(cbBackground, 1,3);
		grid.add(hdRace      , 0,4);
		grid.add(cbRace      , 1,4);
		grid.add(hdGender    , 0,5);
		grid.add(cbGender    , 1,5);
		grid.add(hdMoonsign  , 0,6);
		grid.add(cbMoonsign  , 1,6);
		
		
		for (Node child : grid.getChildren()) {
			int x = GridPane.getColumnIndex(child);
			// Let node fill every available space
			if (x==0)
				((Region)child).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			if (x==1)
				GridPane.setMargin(child, new Insets(4));
		}
		
		setContent(grid);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
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
	}

	//-------------------------------------------------------------------
	public void refresh() {
		logger.debug("refresh");
		SpliMoCharacter model = control.getModel();
		tfName.setText(model.getName());
		cbEducation.setValue(model.getEducation());
		cbCulture.setValue(model.getCulture());
		cbBackground.setValue(model.getBackground());
		cbRace.setValue(model.getRace());
		cbGender.setValue(model.getGender());
		cbMoonsign.setValue(model.getSplinter());

//		if (mode==ViewMode.GENERATION) {
//			((ChoiceBox<MetaType>)cbMetatype).getSelectionModel().select(model.getMetatype());
//			((ChoiceBox<MagicOrResonanceType>)cbMagORRes).getSelectionModel().select(model.getMagicOrResonanceType());
//		} else {
//			((Label)cbMetatype).setText(model.getMetatype().getName());
//			((Label)cbMagORRes).setText(model.getMagicOrResonanceType().getName());
//		}
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<BasePluginData> showHelpForProperty() {
		return showHelpFor;
	}

}
