/**
 *
 */
package org.prelle.splittermond.chargen.jfx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.Race;
import org.prelle.splimo.Size;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class WizardPageName extends WizardPage implements GenerationEventListener, ChangeListener<String> {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacterGenerator charGen;

	private GridPane content;
	private TextField name;
	private ChoiceBox<Gender> gender;
	private TextField hairColor;
	private TextField skinColor;
	private TextField eyeColor;
	private TextField weight;
	private TextField birthPlace;
	private TextField size_tf;
	private Slider size;
	private ImageView portrait;
	private byte[] imgData;

	private Button randomHair;
	private Button randomEyes;
	private Button randomSize;
	private Button openFileChooser;

	//-------------------------------------------------------------------
	public WizardPageName(Wizard wizard, SpliMoCharacterGenerator charGen) {
		super(wizard);
		this.charGen = charGen;

		GenerationEventDispatcher.addListener(this);

		initComponents();
		initLayout();
		initInteractivity();

		nextButton.set(false);
		finishButton.set(false);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setTitle(UI.getString("wizard.selectName.title"));

		name      = new TextField();
		hairColor = new TextField();
		skinColor  = new TextField();
		eyeColor  = new TextField();
		weight    = new TextField();
		birthPlace    = new TextField();
		size_tf   = new TextField();
		weight.setPromptText(UI.getString("prompt.weight"));
		size_tf.setPromptText(UI.getString("prompt.size"));

		int min = 50;
		int max = 250;
		int avg = (max-min)/2 + min;
		size      = new Slider(min,max,avg);
		size.setShowTickLabels(true);
		size.setShowTickMarks(true);
//		size.setMajorTickUnit(50);
		size.setMinorTickCount(5);
		gender    = new ChoiceBox<Gender>(FXCollections.observableArrayList(Gender.MALE, Gender.FEMALE));
		portrait = new ImageView();
		portrait.setFitHeight(200);
		portrait.setFitWidth(200);
		portrait.setPreserveRatio(true);
		portrait.setImage(new Image(getClass().getResourceAsStream("images/guest-256.png")));

		/*
		 * Buttons
		 */
		randomHair = new Button(UI.getString("button.roll"));
		randomEyes = new Button(UI.getString("button.roll"));
		randomSize = new Button(UI.getString("button.roll"));

		/*
		 * Button portrait selection
		 */
		openFileChooser = new Button(UI.getString("button.select"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		VBox portBox = new VBox(10);
		portBox.setAlignment(Pos.TOP_CENTER);
		portBox.getChildren().addAll(portrait, openFileChooser);

		content = new GridPane();
		content.setVgap(5);
		content.setHgap(5);
		content.add(new Label(UI.getString("label.name"  )), 0, 0);
		content.add(new Label(UI.getString("label.gender")), 0, 1);
		content.add(new Label(UI.getString("label.size"  )), 0, 2);
		content.add(new Label(UI.getString("label.weight")), 0, 3);
		content.add(new Label(UI.getString("label.hair"  )), 0, 4);
		content.add(new Label(UI.getString("label.eyes"  )), 0, 5);
		content.add(new Label(UI.getString("label.skin"  )), 0, 6);
		content.add(new Label(UI.getString("label.birthplace"  )), 0, 7);
		content.add(name     , 1, 0);
		content.add(gender   , 1, 1);
		content.add(size_tf  , 1, 2);
		content.add(weight   , 1, 3);
		content.add(hairColor, 1, 4);
		content.add(eyeColor , 1, 5);
		content.add(skinColor , 1, 6);
		content.add(birthPlace , 1, 7);
		content.add(randomSize, 2, 2);
		content.add(randomHair, 2, 4);
		content.add(randomEyes, 2, 5);

		Region padding = new Region();
		content.add(padding, 2, 8);
		GridPane.setVgrow(padding, Priority.ALWAYS);

		content.add(portBox  , 3, 0, 1,9);
		GridPane.setValignment(portBox, VPos.TOP);

//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).valignment(VPos.TOP).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.SOMETIMES).valignment(VPos.TOP).build());
		super.setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		name.textProperty().addListener(this);
		hairColor.textProperty().addListener(this);
		eyeColor.textProperty().addListener(this);
		skinColor.textProperty().addListener(this);
		birthPlace.textProperty().addListener(this);

		randomHair.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				hairColor.setText(charGen.rollHair());
			}
		});
		randomEyes.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				eyeColor.setText(charGen.rollEyes());
			}
		});
		randomSize.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				int[] sAw = charGen.rollSizeAndWeight();
//				size.setValue(sAw[0]);
				size_tf.setText(String.valueOf(sAw[0]));
				weight.setText(sAw[1]+"");
			}
		});
		openFileChooser.setOnAction(new EventHandler<ActionEvent>() {
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
						imgData = imgBytes;
					} catch (IOException e) {
						logger.warn("Failed loading image from "+selection+": "+e);
					}
				}
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends String> textfield, String oldVal,	String newVal) {
		String n = name.getText();
		if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); name.setText(n); }
		if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); name.setText(n); }
		if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); name.setText(n); }
		if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); name.setText(n); }

		copyToCharacter();
		if (
				name.getText().length()>0 &&
				hairColor.getText().length()>0 &&
				eyeColor.getText().length()>0
				)
			finishButton.set(true);
		else
			finishButton.set(false);
	}

	//-------------------------------------------------------------------
	private void copyToCharacter() {
		charGen.setName(name.getText());
		charGen.setHairColor(hairColor.getText());
		charGen.setEyeColor(eyeColor.getText());
		charGen.setGender(gender.getValue());
		charGen.setFurColor(skinColor.getText());
		charGen.setBirthPlace(birthPlace.getText());
		try {
			if (weight.getText().length()>0)
				charGen.setWeight((int)Math.round(Double.parseDouble(weight.getText())));
			if (size_tf.getText().length()>0)
				charGen.setSize((int)Math.round(Double.parseDouble(size_tf.getText())));
			if (imgData!=null)
				charGen.setImageBytes(imgData);
		} catch (Exception e) {
			wizard.getScreenManager().showAlertAndCall(AlertType.ERROR, "Invalid data", "Error validating character.\n"+e);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case RACE_SELECTED:
			Platform.runLater(new Runnable(){
				public void run() {
					logger.debug("handleGenerationEvent("+event+")");
					Race race = (Race) event.getKey();
					logger.debug("Set "+race);
					Size dat = race.getSize();
					size.setMin(dat.getSizeBase()-10);
					size.setMax(dat.getSizeBase()+10*dat.getSizeD10()+6*dat.getSizeD6()+10);
				}
			});
			break;
		default:
			logger.debug("RCV "+event.getType());
			finishButton.set(wizard.canBeFinished());
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageLeft(org.prelle.javafx.CloseType)
	 */
	@Override
	public void pageLeft(CloseType type) {
		if (type==CloseType.CANCEL || type==CloseType.PREVIOUS)
			return;

		copyToCharacter();
	}

}
