package org.prelle.splittermond.chargen.jfx.wizard;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.Race;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;

import de.rpgframework.ResourceI18N;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class WizardPageRace extends WizardPage implements ChangeListener<Race> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle(CharGenWizardSpliMo.class.getName());

	private static Map<Race,Image> imageByRace;

	private SpliMoCharacterGenerator charGen;
	private LetUserChooseListener choiceCallback;

	private Spinner<Race> raceSpinner;
	private Label description;
	private Label statsAttributes;
	private Label statsPowers;

	private VBox content;

	//-------------------------------------------------------------------
	static {
		imageByRace = new HashMap<Race, Image>();
	}

	//-------------------------------------------------------------------
	public WizardPageRace(Wizard wizard, SpliMoCharacterGenerator charGen, LetUserChooseListener choiceCallback) {
		super(wizard);
		initComponents();
		initLayout();
		initInteractivity();
		this.charGen = charGen;
		this.choiceCallback = choiceCallback;

		// Select human race
		updateRaceView(SplitterMondCore.getRaces().get(0));
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setTitle(uiResources.getString("wizard.selectRace.title"));

		raceSpinner = new Spinner<Race>();
		raceSpinner.setPrefWidth(300);
		raceSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		ListSpinnerValueFactory<Race> factory = new ListSpinnerValueFactory<Race>(FXCollections.observableArrayList(SplitterMondCore.getRaces()));
		factory.setWrapAround(true);
		factory.setConverter(new StringConverter<Race>() {
			public String toString(Race race) { return race.getName();}
			public Race fromString(String string) {return null;}
		});
		raceSpinner.setValueFactory(factory);

		content = new VBox();
		content.setSpacing(5);

		description = new Label();
		description.setWrapText(true);
		description.getStyleClass().add("text-body");

		statsAttributes = new Label();
		statsAttributes.setWrapText(true);
		statsAttributes.getStyleClass().addAll("text-body","stats-block");

		statsPowers = new Label();
		statsPowers.setWrapText(true);
		statsPowers.getStyleClass().addAll("text-body","stats-block");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		description.setPrefWidth(600);
//		description.setStyle("-fx-max-width: 20em");
//		description.setStyle("-fx-pref-height: 12em");
		description.setAlignment(Pos.TOP_LEFT);

//		statsAttributes.setPrefWidth(140);
//		statsAttributes.setStyle("-fx-min-width: 12em");
		statsAttributes.setAlignment(Pos.TOP_LEFT);
		statsAttributes.setStyle("-fx-min-height: 10em");
//		statsPowers.setPrefWidth(400);
		statsPowers.setStyle("-fx-pref-width: 24em");
//		statsPowers.setStyle("-fx-min-height: 10em");
		statsPowers.setAlignment(Pos.TOP_LEFT);

		HBox sideBySide = new HBox(20);
		sideBySide.getChildren().add(statsAttributes);
		sideBySide.getChildren().add(statsPowers);

		content.getChildren().addAll(raceSpinner, description, sideBySide);
		content.setStyle("-fx-pref-height: 20em");
		setImageInsets(new Insets(-40,0,0,0));
		super.setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		raceSpinner.valueProperty().addListener(this);

		statsAttributes.prefHeightProperty().bind(statsPowers.heightProperty());
		statsPowers.prefHeightProperty().bind(statsAttributes.heightProperty());
	}

	//-------------------------------------------------------------------
	private void updateRaceView(Race newRace) {
		Image img = imageByRace.get(newRace);
		if (img==null) {
			String fname = "data/race_"+newRace.getKey()+".png";
			logger.trace("Load "+fname);
			InputStream in = SpliMoCharGenJFXConstants.class.getResourceAsStream(fname);
			if (in!=null) {
				img = new Image(in);
				imageByRace.put(newRace, img);
			} else
				logger.warn("Missing image at "+fname);
		}
		setImage(img);

		if (uiResources.containsKey("descr.race."+newRace.getKey())) {
			description.setText( uiResources.getString("descr.race."+newRace.getKey()) );
		} else {
			description.setText( newRace.getHelpText() );
		}
		if (uiResources.containsKey("descr.race."+newRace.getKey()+".attr")) {
			statsAttributes.setText( uiResources.getString("descr.race."+newRace.getKey()+".attr") );
		} else {
			statsAttributes.setText( ResourceI18N.get(newRace.getHelpResourceBundle(), "descr.race."+newRace.getKey()+".attr") );
		}
		if ( uiResources.containsKey("descr.race."+newRace.getKey()+".powers") ) {
			statsPowers.setText( uiResources.getString("descr.race."+newRace.getKey()+".powers") );
		} else {
			statsPowers.setText( ResourceI18N.get(newRace.getHelpResourceBundle(), "descr.race."+newRace.getKey()+".powers") );
		}
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends Race> property, Race oldRace,
			Race newRace) {
		logger.debug("Currently display race "+newRace);

		updateRaceView(newRace);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageLeft(org.prelle.javafx.CloseType)
	 */
	@Override
	public void pageLeft() {
			/*
			 * Call in extra thread, since it invokes blocking dialogs
			 */
			charGen.selectRace(raceSpinner.getValue(), choiceCallback);
//			Runnable run = new Runnable() {
//				public void run() {	charGen.selectRace(raceSpinner.getValue(), choiceCallback); }
//			};
//			Thread thread = new Thread(run,"BlocksInLetUserChoose");
//			thread.start();
	}

}
