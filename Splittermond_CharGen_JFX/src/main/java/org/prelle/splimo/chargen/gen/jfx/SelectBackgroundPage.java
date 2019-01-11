/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Background;
import org.prelle.splimo.charctrl.BackgroundController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.free.jfx.FreeSelectionDialog;
import org.prelle.splimo.free.FreeSelectionGenerator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class SelectBackgroundPage extends WizardPage implements ChangeListener<Background>, GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	private SpliMoCharacterGenerator charGen;
	private BackgroundController bgGen;
	private LetUserChooseListener choiceCallback;
	
	private ListView<Background> backgList;
	private CheckBox includeUnusual;
	private Button freeCreation_b;
	private ImageView image;
	
	private HBox content;
	private VBox context;
	private Background selected;

	//-------------------------------------------------------------------
	public SelectBackgroundPage(SpliMoCharacterGenerator chGen, LetUserChooseListener choiceCallback) {
		super(uiResources.getString("wizard.selectBackground.title"), 
				new Image(SelectBackgroundPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		this.charGen = chGen;
		this.bgGen = chGen.getBackgroundGenerator();
		GenerationEventDispatcher.addListener(this);
		this.choiceCallback = choiceCallback;
		nextButton.setDisable(true);
		finishButton.setDisable(true);
		initInteractivity();
		setData();
	}

	//-------------------------------------------------------------------
	private void setData() {
		backgList.getItems().addAll(bgGen.getAvailableBackgrounds());
		Collections.sort(backgList.getItems());
		backgList.getSelectionModel().select(1);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case BACKGROUND_OFFER_CHANGED:
			logger.debug("Offered backgrounds changed");
			backgList.getItems().clear();
			backgList.setItems(FXCollections.observableArrayList((List<Background>)event.getKey()));
			Collections.sort(backgList.getItems());
			break;
		default:
		}
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends Background> property, Background oldModel,
			Background newModel) {
		selected = newModel;
		
		logger.info("Background now "+selected);
		this.nextButton.setDisable(selected==null);

		/*
		 * There aren't images for the backgrounds yet
		 */
//		Image img = imageByBackground.get(selected);
//		if (img==null) {
//			String fname = "data/background_"+selected.getKey()+".png";
//			logger.debug("Load "+fname);
//			InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
//			if (in!=null) {
//				img = new Image(in);
//				imageByBackground.put(selected, img);
//				image.setImage(img);
//			} else
//				logger.warn("Missing image at "+fname);
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		Image img = null;
		String fname = "data/Background.png";
		logger.debug("Load "+fname);
		InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
		if (in!=null) {
			img = new Image(in);
		} else
			logger.warn("Missing image at "+fname);
		image = new ImageView(img);
		image.setFitHeight(388);
		image.setFitWidth(250);

		backgList = new ListView<Background>();
//		backgList.setPrefWidth(200);
		backgList.setMinWidth(100);
		backgList.setMinHeight(100);
//		backgList.setPrefHeight(200);
		
//		backgList.setCyclic(true);
//		backgList.setStringConverter(new StringConverter<Background>() {
//			@Override
//			public String toString(Background backg) {
//				return backg.getName();
//			}
//			@Override
//			public Background fromString(String backg) {
//				return null;
//			}
//		});
		
		content = new HBox(10);
		content.getChildren().addAll(backgList, image);
		return content;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContextMenu()
	 */
	@Override
	public Parent getContextMenu() {
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");
		includeUnusual = new CheckBox(uiResources.getString("wizard.selectBackground.showUnsual"));
		includeUnusual.setWrapText(true);
		includeUnusual.getStyleClass().add("wizard-context");
		freeCreation_b = new Button(uiResources.getString("wizard.freeCreation"));	
		
		context = new VBox(15);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, includeUnusual, freeCreation_b);
		return context;
	}
	

	//-------------------------------------------------------------------
	private void initInteractivity() {

		backgList.getSelectionModel().selectedItemProperty().addListener(this);

		includeUnusual.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> checkbox,
					Boolean oldVal, Boolean newVal) {
				bgGen.setIncludeUncommonBackgrounds(newVal);
			}
		});
		
		// Listen to button
		freeCreation_b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FreeSelectionGenerator freeGen = charGen.getFreeCreatorBackground();
				// Initialize with current selection - if there is any
				Background sel = backgList.getSelectionModel().getSelectedItem();
				if (sel!=null) {
					logger.debug("pre-fill free selection dialog with "+sel);
					freeGen.setFrom(sel);
				}
				
				FreeSelectionDialog dialog = new FreeSelectionDialog(freeGen);
				Stage stage = new Stage();
				stage.setTitle(uiResources.getString("freeselect.title.background"));
				Scene scene2 = new Scene(dialog);
				scene2.getStylesheets().add("css/default.css");
				stage.setScene(scene2);
				stage.showAndWait();
				
				if (!dialog.hasBeenCancelled()) {
					selected = freeGen.getAsBackground();
					logger.debug("Select custom background "+selected.getModifications());
					backgList.getItems().add(selected);
					backgList.getSelectionModel().select(selected);
					nextButton.setDisable(false);
					SelectBackgroundPage.this.nextPage();
//				} else {
//					selected = null;
				}
			}
		});
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		if (selected==null)
			throw new IllegalStateException("Nothing selected");
		
		this.setDisable(true);
		bgGen.select(selected, choiceCallback);
		this.setDisable(false);

		super.nextPage();
	}

}
