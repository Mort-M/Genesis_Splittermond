/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Race;
import org.prelle.splimo.Size;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
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
public class SelectNamePage extends WizardPage implements ChangeListener<String>, GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	private SpliMoCharacterGenerator charGen;
	
	private GridPane content;
	private TextField name;
	private ChoiceBox<Gender> gender;
	private TextField hairColor;
	private TextField eyeColor;
	private TextField weight;
	private TextField size_tf;
	private Slider size;
	private ImageView portrait;
	private byte[] imgData;
	
	//-------------------------------------------------------------------
	public SelectNamePage(SpliMoCharacterGenerator chGen) {
		this.charGen = chGen;
		if (charGen==null)
			throw new NullPointerException();
		pageInit(uiResources.getString("wizard.selectName.title"), 
				new Image(SelectNamePage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		GenerationEventDispatcher.addListener(this);
		finishButton.setDisable(true);
		
		
		// Set data
		gender.getSelectionModel().select(Gender.MALE);
		
		if (charGen.getSelectedRace()!=null) {
			Size dat = charGen.getSelectedRace().getSize();
			int min = dat.getSizeBase();
			int max = dat.getSizeBase()+10*dat.getSizeD10()+6*dat.getSizeD6();
			int avg = (max-min)/2 + min;
			size.setMin(min-10);
			size.setMax(max+10);
			size.setValue(avg);
			logger.debug("Set Size to "+min+"-"+max+"  with avg "+avg);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		name      = new TextField();
		hairColor = new TextField();
		eyeColor  = new TextField();
		weight    = new TextField();
		size_tf   = new TextField();
		weight.setPromptText(uiResources.getString("prompt.weight"));
		size_tf.setPromptText(uiResources.getString("prompt.size"));
		name.textProperty().addListener(this);
		hairColor.textProperty().addListener(this);
		eyeColor.textProperty().addListener(this);
		
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
		portrait.setImage(new Image(ClassLoader.getSystemResourceAsStream(SpliMoCharGenJFXConstants.PREFIX+"/images/guest-256.png")));
		
		/*
		 * Button hair color
		 */
		Button randomHair = new Button(uiResources.getString("button.roll"));
		randomHair.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				hairColor.setText(charGen.rollHair());
			}
		});
		
		/*
		 * Button eye color
		 */
		Button randomEyes = new Button(uiResources.getString("button.roll"));
		randomEyes.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				eyeColor.setText(charGen.rollEyes());
			}
		});
		
		/*
		 * Button size
		 */
		Button randomSize = new Button(uiResources.getString("button.roll"));
		randomSize.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				int[] sAw = charGen.rollSizeAndWeight();
//				size.setValue(sAw[0]);
				size_tf.setText(String.valueOf(sAw[0]));
				weight.setText(sAw[1]+"");
			}
		});
		
		/*
		 * Button portrait selection
		 */
		Button openFileChooser = new Button(uiResources.getString("button.select"));
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
		VBox portBox = new VBox(10);
		portBox.setAlignment(Pos.TOP_CENTER);
		portBox.getChildren().addAll(portrait, openFileChooser);
		
		content = new GridPane();
		content.setVgap(5);
		content.setHgap(5);
		content.add(new Label(uiResources.getString("label.name"  )), 0, 0);
		content.add(new Label(uiResources.getString("label.gender")), 0, 1);
		content.add(new Label(uiResources.getString("label.size"  )), 0, 2);
		content.add(new Label(uiResources.getString("label.weight")), 0, 3);
		content.add(new Label(uiResources.getString("label.hair"  )), 0, 4);
		content.add(new Label(uiResources.getString("label.eyes"  )), 0, 5);
		content.add(name     , 1, 0);
		content.add(gender   , 1, 1);
		content.add(size_tf  , 1, 2);
		content.add(weight   , 1, 3);
		content.add(hairColor, 1, 4);
		content.add(eyeColor , 1, 5);
		content.add(randomSize, 2, 2);
		content.add(randomHair, 2, 4);
		content.add(randomEyes, 2, 5);
		
		Region padding = new Region();
		content.add(padding, 2, 6);
		GridPane.setVgrow(padding, Priority.ALWAYS);

		content.add(portBox  , 3, 0, 1,7);
		GridPane.setValignment(portBox, VPos.TOP);
		
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).valignment(VPos.TOP).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.NEVER).build());
//		content.getRowConstraints().add(RowConstraintsBuilder.create().vgrow(Priority.SOMETIMES).valignment(VPos.TOP).build());
		
		return content;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		try {
			charGen.setName(name.getText());
			charGen.setHairColor(hairColor.getText());
			charGen.setEyeColor(eyeColor.getText());
			if (weight.getText().length()>0)
				charGen.setWeight((int)Math.round(Double.parseDouble(weight.getText())));
			charGen.setGender(gender.getValue());
			if (size_tf.getText().length()>0)
				charGen.setSize((int)Math.round(Double.parseDouble(size_tf.getText())));
			if (imgData!=null)
				charGen.setImageBytes(imgData);
			
			super.nextPage();
		} catch (Exception e) {
			MessageDialog.showConfirmMessage("Error validating character.\n"+e);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends String> textfield, String oldVal,	String newVal) {
		if (
				name.getText().length()>0 && 
				hairColor.getText().length()>0 &&
				eyeColor.getText().length()>0 
				)
			finishButton.setDisable(false);
		else
			finishButton.setDisable(true);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.RACE_SELECTED) {
			logger.debug("handleGenerationEvent("+event+")");
			Race race = (Race) event.getKey();
			logger.debug("Set "+race);
			Size dat = race.getSize();
			size.setMin(dat.getSizeBase()-10);
			size.setMax(dat.getSizeBase()+10*dat.getSizeD10()+6*dat.getSizeD6()+10);			
		}
	}

}
