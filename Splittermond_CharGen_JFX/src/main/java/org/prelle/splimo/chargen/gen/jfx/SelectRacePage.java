/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Race;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SelectRacePage extends WizardPage implements ChangeListener<Race> {

	private final static Logger logger = Logger.getLogger("fxui");

	private static Map<Race,Image> imageByRace;

	private static PropertyResourceBundle ruleResources = SplitterMondCore.getI18nResources();
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/ui");

	private SpliMoCharacterGenerator charGen;
	private LetUserChooseListener choiceCallback;
	
	private Spinner<Race> raceSpinner;
	private ImageView image;
	
	private VBox content;

	//-------------------------------------------------------------------
	static {
		imageByRace = new HashMap<Race, Image>();
	}

	//-------------------------------------------------------------------
	public SelectRacePage(SpliMoCharacterGenerator charGen, LetUserChooseListener choiceCallback) {
		super(uiResources.getString("wizard.selectRace.title"), 
				new Image(SelectRacePage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		this.charGen = charGen;
		this.choiceCallback = choiceCallback;
		
		finishButton.setDisable(true);
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends Race> property, Race oldRace,
			Race newRace) {
		logger.debug("Currently display race "+newRace);

		Image img = imageByRace.get(newRace);
		if (img==null) {
			String fname = "data/race_"+newRace.getKey()+".png";
			logger.trace("Load "+fname);
			InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
			if (in!=null) {
				img = new Image(in);
				imageByRace.put(newRace, img);
			} else
				logger.warn("Missing image at "+fname);
		}
		image.setImage(img);
		
		if (charGen!=null)
			finishButton.setDisable(!charGen.hasEnoughData());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		content = new VBox();
		content.setSpacing(5);
		
//		content.setGridLinesVisible(false);
//		content.setHgap(5);
//		content.setVgap(5);
//		content.setPadding(new Insets(3));

		image = new ImageView();
		image.setFitHeight(465);
		image.setFitWidth(500);

		raceSpinner = new Spinner<Race>(FXCollections.observableArrayList(SplitterMondCore.getRaces()));
		raceSpinner.setPrefWidth(200);
//		raceSpinner.setCyclic(true);
		raceSpinner.getValueFactory().setConverter(new StringConverter<Race>() {
			@Override
			public String toString(Race race) {
				return ruleResources.getString("race."+race.getKey());
			}
			@Override
			public Race fromString(String race) {
				return null;
			}
		});

		raceSpinner.valueProperty().addListener(this);
		raceSpinner.increment();

//		ListSpinnerSkin<Race> skin = new ListSpinnerSkin<Race>(raceSpinner);
//		skin.setArrowPosition(ArrowPosition.SPLIT);
//		raceSpinner.setSkin(skin);
//		((ListSpinnerSkin<Race>)raceSpinner.getSkin()).setArrowPosition(ArrowPosition.SPLIT);

//		content.add(raceSpinner, 0, 0);
//		content.add(image, 0, 1);
		content.getChildren().addAll(raceSpinner, image);
		return content;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		this.setDisable(true);
		charGen.selectRace(raceSpinner.getValue(), choiceCallback);
		this.setDisable(false);

		super.nextPage();
	}

}
