/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.FontIcon;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.charctrl.LanguageController;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.jfx.cultures.OldCultureLorePane;
import org.prelle.splittermond.jfx.languages.LanguagePane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class TopBlock extends HBox implements GenerationEventListener {

	private BaseDataBlock base;
	private DetailsBlock  detail;
	private MoonsignBlock moon;

	//-------------------------------------------------------------------
	/**
	 */
	public TopBlock(CultureLoreController cultCtrl, LanguageController langCtrl) {
		super(10);
		
		doInit(cultCtrl, langCtrl);
		doLayout();
	}

	//-------------------------------------------------------------------
	private void doInit(CultureLoreController cultCtrl, LanguageController langCtrl) {
		base = new BaseDataBlock();
		detail = new DetailsBlock(cultCtrl, langCtrl);
		moon   = new MoonsignBlock();

		VBox box = new VBox(10);
		box.getChildren().addAll(base, detail);
		
		HBox.setHgrow(moon, Priority.ALWAYS);
		getChildren().addAll(box, moon);
	}

	//-------------------------------------------------------------------
	private void doLayout() {
	}

	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter model, MastershipController control) {
		GenerationEventDispatcher.addListener(this);
		
		base.setContent(model);
		moon.setContent(model);
		detail.setContent(model);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case RACE_SELECTED:
		case BACKGROUND_SELECTED:
		case BACKGROUND_OFFER_CHANGED:
		case CULTURE_SELECTED:
		case CULTURE_OFFER_CHANGED:
		case EDUCATION_SELECTED:
			base.handleGenerationEvent(event);
			break;
		}
	}
	
}

class BaseDataBlock extends GridPane implements GenerationEventListener {
	
	private static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private Label name_l, education_l, culture_l, race_l, background_l, gender_l;
	private Label hair_l, eyes_l, skin_l, size_l, weight_l, birthplace_l;
	private Label name_tf;
	private Label education_cb;
	private Label culture_cb;
	private Label background_cb;
	private Label race_cb;
//	private ChoiceBox<Gender> gender_cb;
	private Label gender;
	private Label hair_tf;
	private Label eyes_tf;
	private Label skin_tf;
	private Label size_tf;
	private Label weight_tf;
	private Label birthplace_tf;
	private SpliMoCharacter model;

	//-------------------------------------------------------------------
	/**
	 */
	public BaseDataBlock() {
		doInit();
		doLayout();
	}

	//-------------------------------------------------------------------
	private void doInit() {
		name_l      = new Label(uiResources.getString("label.name"));
		education_l = new Label(uiResources.getString("label.education"));
		culture_l   = new Label(uiResources.getString("label.culture"));
		background_l= new Label(uiResources.getString("label.background"));
		race_l      = new Label(uiResources.getString("label.race"));
		gender_l    = new Label(uiResources.getString("label.gender"));
		hair_l      = new Label(uiResources.getString("label.hair"));
		size_l      = new Label(uiResources.getString("label.size"));
		eyes_l      = new Label(uiResources.getString("label.eyes"));
		weight_l    = new Label(uiResources.getString("label.weight"));
		skin_l      = new Label(uiResources.getString("label.skin"));
		birthplace_l= new Label(uiResources.getString("label.birthplace"));

		name_l.getStyleClass().add("table-head");
		education_l.getStyleClass().add("table-head");
		culture_l.getStyleClass().add("table-head");
		background_l.getStyleClass().add("table-head");
		race_l.getStyleClass().add("table-head");
		gender_l.getStyleClass().add("table-head");
		hair_l.getStyleClass().add("table-head");
		size_l.getStyleClass().add("table-head");
		eyes_l.getStyleClass().add("table-head");
		weight_l.getStyleClass().add("table-head");
		skin_l.getStyleClass().add("table-head");
		birthplace_l.getStyleClass().add("table-head");
		
		name_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		education_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		culture_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		background_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		race_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		gender_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		hair_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		size_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		eyes_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		weight_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		skin_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		birthplace_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		
		name_tf      = new Label();
		education_cb = new Label();
		culture_cb   = new  Label();
		race_cb      = new Label();
		background_cb = new Label();
//		gender_cb = new ChoiceBox<Gender>(FXCollections.observableArrayList(Gender.values()));
		gender  = new Label();
		hair_tf = new Label();
		eyes_tf = new Label();
		skin_tf = new Label();
		size_tf = new Label();
		weight_tf = new Label();
		birthplace_tf = new Label();
		
		name_tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		education_cb.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		culture_cb.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		race_cb.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		background_cb.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		gender_cb.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		gender.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		hair_tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		eyes_tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		skin_tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		size_tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		weight_tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		birthplace_tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	private void doLayout() {
		this.setHgap(2);
		
		this.add(name_l      , 0,0);
		this.add(name_tf     , 1,0, 4,1);
		
		this.add(education_l , 0,2);
		this.add(education_cb, 1,2, 4,1);
		
		this.add(culture_l   , 0,3);
		this.add(culture_cb  , 1,3, 2,1);
		this.add(background_l, 3,3);
		this.add(background_cb, 4,3, 2,1);
		
		this.add(race_l      , 0,4);
		this.add(race_cb     , 1,4, 2,1);
		this.add(gender_l    , 3,4);
//		this.add(gender_cb   , 4,4, 2,1);
		this.add(gender      , 4,4, 2,1);
		
		this.add(hair_l      , 0,5);
		this.add(hair_tf     , 1,5);
//		this.add(rollHair    , 2,5);
		this.add(size_l      , 3,5);
		this.add(size_tf     , 4,5);
//		this.add(rollSize    , 5,5);
		
		this.add(eyes_l      , 0,6);
		this.add(eyes_tf     , 1,6);
//		this.add(rollEyes    , 2,6);
		this.add(weight_l    , 3,6);
		this.add(weight_tf   , 4,6);
//		this.add(rollWeight  , 5,6);
		
		this.add(skin_l      , 0,7);
		this.add(skin_tf     , 1,7, 2,1);
		this.add(birthplace_l, 3,7);
		this.add(birthplace_tf, 4,7, 2,1);
		
		// Lines
//		name_tf.getStyleClass().add("even");
//		education_cb.getStyleClass().add("border-all");
//		culture_cb.getStyleClass().add("border-all");
//		weight_tf.getStyleClass().add("even");
//		hair_tf.getStyleClass().addAll("even","border-all");
//		size_tf.getStyleClass().addAll("even","border-all");
//		eyes_tf.getStyleClass().addAll("odd","border-all");
//		weight_tf.getStyleClass().addAll("odd","border-all");
//		skin_tf.getStyleClass().addAll("even","border-all");
//		birthplace_tf.getStyleClass().addAll("even","border-all");

		this.getStyleClass().add("border-all");
	}

	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter charac) {
		this.model = charac;
		
		name_tf.setText(charac.getName());
//		education_cb.getSelectionModel().select(charac.getEducation());
		if (model.getRace()!=null)
			race_cb   .setText(model.getRace().getName());
		if (model.getCulture()!=null)
			culture_cb.setText(model.getCulture().getName());
		if (model.getBackground()!=null)
			background_cb.setText(model.getBackground().getName());
		if (model.getEducation()!=null)
			education_cb .setText(model.getEducation().getName());
		size_tf.setText(Integer.toString(model.getSize()));
		weight_tf.setText(Integer.toString(model.getWeight()));
		hair_tf.setText(model.getHairColor());
		eyes_tf.setText(model.getEyeColor());
		skin_tf.setText(model.getFurColor());
		birthplace_tf.setText(model.getBirthplace());
//		gender_cb.getSelectionModel().select(model.getGender());
		gender.setText(String.valueOf(model.getGender()));
	}

	//-------------------------------------------------------------------
	@SuppressWarnings({ "incomplete-switch"})
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.info("handle "+event);
		switch (event.getType()) {
		case RACE_SELECTED:
			logger.debug("Race selected: "+event.getKey());
			race_cb   .setText(model.getRace().getName());
			break;
		case CULTURE_SELECTED:
			logger.debug("Culture selected: "+event.getKey());
			culture_cb.setText(model.getCulture().getName());
			break;
		case BACKGROUND_SELECTED:
			logger.debug("Background selected: "+event.getKey());
			background_cb.setText(model.getBackground().getName());
			break;
		case EDUCATION_SELECTED:
			logger.debug("Education selected: "+event.getKey());
			education_cb .setText(model.getEducation().getName());
			break;
		case BASE_DATA_CHANGED:
			name_tf.setText(model.getName());
			weight_tf.setText(Integer.toString(model.getWeight()));
			size_tf.setText(Integer.toString(model.getSize()));
			eyes_tf.setText(model.getEyeColor());
			hair_tf.setText(model.getHairColor());
//			gender_cb.getSelectionModel().select( model.getGender() );
			gender.setText(model.getGender().toString());
			skin_tf.setText(model.getFurColor());
			birthplace_tf.setText(model.getBirthplace());
			break;
		}
	}
	
}


class DetailsBlock extends VBox implements GenerationEventListener {
		
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private SpliMoCharacter model;
	
	private Label weaknesses_l, languages_l, cultlore_l;
	private TextField weakLine1, weakLine2, weakLine3;
	private LanguagePane langPane;
	private OldCultureLorePane cultPane;

	//-------------------------------------------------------------------
	/**
	 */
	public DetailsBlock(CultureLoreController cultCtrl, LanguageController langCtrl) {
		doInit(cultCtrl, langCtrl);
		doLayout();
	}

	//-------------------------------------------------------------------
	private void doInit(CultureLoreController cultCtrl, LanguageController langCtrl) {
		weaknesses_l = new Label(uiResources.getString("label.weaknesses"));
		languages_l  = new Label(uiResources.getString("label.languages"));
		cultlore_l   = new Label(uiResources.getString("label.culturelore"));
		weakLine1    = new TextField();
		weakLine2    = new TextField();
		weakLine3    = new TextField();
		langPane     = new LanguagePane(langCtrl);
		cultPane     = new OldCultureLorePane(cultCtrl);

		weaknesses_l.getStyleClass().add("table-head");
		languages_l.getStyleClass().add("table-head");
		cultlore_l.getStyleClass().add("table-head");
		weakLine1.getStyleClass().add("even");
		weakLine2.getStyleClass().add("odd");
		weakLine3.getStyleClass().add("even");
		
		weaknesses_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		languages_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		cultlore_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		weakLine1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		weakLine2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		weakLine3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		cultPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		langPane.setStyle("-fx-max-height: 12em");
		cultPane.setStyle("-fx-max-height: 12em");
		
		weakLine1.setDisable(true);
		weakLine2.setDisable(true);
		weakLine3.setDisable(true);
	}

	//-------------------------------------------------------------------
	private void doLayout() {
		VBox left = new VBox();
		left.getChildren().addAll(languages_l, langPane);
		VBox right = new VBox();
		right.getChildren().addAll(cultlore_l, cultPane);
		right.getStyleClass().add("bordered");
		left.getStyleClass().add("bordered");
		HBox ltr = new HBox(20);
		ltr.getChildren().addAll(left, right);
		
		GridPane weak = new GridPane();
		GridPane.setHgrow(weakLine1, Priority.ALWAYS);
		GridPane.setHgrow(weakLine2, Priority.ALWAYS);
		GridPane.setHgrow(weakLine3, Priority.ALWAYS);
		
		weak.add(weaknesses_l, 0,0);
		weak.add(weakLine1   , 1,0);
		weak.add(weakLine2   , 0,1, 2,1);
//		weak.add(weakLine3   , 0,2, 2,1);
		weak.getStyleClass().add("bordered");
		
		/* Highest layout */
		setSpacing(10);
		getChildren().addAll(ltr);

//		this.getStyleClass().add("border-all");
	}

	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter charac) {
		this.model = charac;
		
		cultPane.setData(model);
		langPane.setData(model);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()!=GenerationEventType.BASE_DATA_CHANGED) 
			return;
		
//		level.setText(Integer.toString(model.getLevel()));
	}
	
}


class MoonsignBlock extends GridPane implements GenerationEventListener {
	
	private static Logger logger = Logger.getLogger("chargen.ui");
	
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private SpliMoCharacter model;

	private Label moonsign_l, splinter_l, portrait_l;
	private Label moonsign;
	private TextField[] splinter;
	private ImageView portrait;
	private Button editPortrait, deletePortrait;

	//-------------------------------------------------------------------
	/**
	 */
	public MoonsignBlock() {
		doInit();
		doLayout();
		initInteractivity();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void doInit() {
		moonsign_l   = new Label(uiResources.getString("label.moonsign"));
		splinter_l   = new Label(uiResources.getString("label.splinterpoints"));
		portrait_l   = new Label(uiResources.getString("label.portrait"));
		moonsign     = new Label();
		splinter = new TextField[3];
		for (int i=0; i<splinter.length; i++) {
			splinter[i] = new TextField();
			splinter[i].setMaxSize(40, 40);
			splinter[i].setDisable(true);
		}
		portrait     = new ImageView();
		portrait.setFitHeight(200);
		
		// Edit portrait
//		Image img = new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("images/appbar.page.edit.png"));
//		ImageView iView = new ImageView(img);
//		iView.setFitWidth(24);
//		iView.setFitHeight(24);
		editPortrait = new Button(null, new FontIcon("\uE104"));
		
		// Delete portrait
//		img = new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("images/appbar.delete.png"));
//		iView = new ImageView(img);
//		iView.setFitWidth(24);
//		iView.setFitHeight(24);
		deletePortrait = new Button(null, new FontIcon("\uE107"));

		moonsign_l.getStyleClass().add("table-head");
		splinter_l.getStyleClass().add("table-head");
		portrait_l.getStyleClass().add("table-head");
		moonsign.getStyleClass().add("even");
		moonsign.setPrefWidth(250);
		portrait.getStyleClass().add("border-all");
//		editPortrait.setStyle("-fx-background-color: dark");
		
		moonsign_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		splinter_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		portrait_l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		moonsign  .setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		this  .setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	private void doLayout() {
		setVgap(10);
		
		HBox portraitTitleLine = new HBox();
		HBox.setHgrow(portrait_l, Priority.ALWAYS);
		portraitTitleLine.setMaxWidth(Double.MAX_VALUE);
		portraitTitleLine.getChildren().addAll(portrait_l, deletePortrait,editPortrait);
		
		VBox portraitBox = new VBox();
		portraitBox.getChildren().addAll(portraitTitleLine, portrait);
		portraitBox.getStyleClass().add("bordered");
		
		GridPane.setVgrow(portrait, Priority.ALWAYS);
		GridPane.setHgrow(portrait_l, Priority.NEVER);
		GridPane.setVgrow(portrait_l, Priority.NEVER);
		GridPane.setHgrow(moonsign, Priority.ALWAYS);
		
		this.add(moonsign_l  , 0,0);
		this.add(moonsign    , 1,0, splinter.length,1);
		this.add(splinter_l  , 0,1);
		for (int i=0; i<splinter.length; i++)
			this.add(splinter[i], i+1,1);
		
		this.add(portraitBox , 0,2, splinter.length+1,1);
		
		
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		editPortrait.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				chooser.setInitialDirectory(new File(System.getProperty("user.home")));
				chooser.getExtensionFilters().addAll(
		                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
		                new FileChooser.ExtensionFilter("PNG", "*.png")
		            );
				File selection = chooser.showOpenDialog(new Stage());
				if (selection!=null) {
					try {
						byte[] imgBytes = Files.readAllBytes(selection.toPath());
						portrait.setImage(new Image(new ByteArrayInputStream(imgBytes)));
						model.setImage(imgBytes);
					} catch (IOException e) {
						logger.warn("Failed loading image from "+selection+": "+e);
					}
				}
			}
		});

		deletePortrait.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				model.setImage(null);
				portrait.setImage(null);
				portrait.setPreserveRatio(true);
				portrait.setFitWidth(0);
				portrait.setFitHeight(200);
			}
		});

	}
	
	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter charac) {
		this.model = charac;
		if (charac.getSplinter()!=null)
			moonsign.setText(charac.getSplinter().getName());
		
		splinter[0].setText(Integer.toString(charac.getAttribute(Attribute.SPLINTER).getValue()));
		if (charac.getImage()!=null) {
			Image image = new Image(new ByteArrayInputStream(charac.getImage()));
			portrait.setImage(image);
			// Since the height is fixed to 200, calculate the width
			double factor = 200.0 / image.getHeight();
			portrait.setFitWidth(image.getWidth()*factor);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.ATTRIBUTE_CHANGED) {
			Attribute attr = (Attribute)event.getKey();
			if (attr==Attribute.SPLINTER) {
				splinter[0].setText(Integer.toString(model.getAttribute(Attribute.SPLINTER).getValue()));
			}
		}
	}
	
}