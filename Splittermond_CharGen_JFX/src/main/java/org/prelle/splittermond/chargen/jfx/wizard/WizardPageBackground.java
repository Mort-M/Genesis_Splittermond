package org.prelle.splittermond.chargen.jfx.wizard;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.Background;
import org.prelle.splimo.charctrl.BackgroundController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class WizardPageBackground extends WizardPage implements ChangeListener<Background>, GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle(CharGenWizardSpliMo.class.getName());

	class BackgroundListCell extends ListCell<Background> {
		public void updateItem(Background item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
			} else {
				setText(item.getName());
			}
		}
	}

	private BackgroundController bgGen;
	private LetUserChooseListener choiceCallback;

	private ListView<Background> backgList;
	private Label description;
	private CheckBox includeUnusual;

	private VBox content;
	private Background selected;

	//-------------------------------------------------------------------
	public WizardPageBackground(Wizard wizard, SpliMoCharacterGenerator charGen, LetUserChooseListener choiceCallback) {
		super(wizard);
		bgGen = charGen.getBackgroundGenerator();
		initComponents();
		initLayout();
		initStyles();
		initInteractivity();
		this.choiceCallback = choiceCallback;
		this.bgGen = charGen.getBackgroundGenerator();
		GenerationEventDispatcher.addListener(this);
		this.choiceCallback = choiceCallback;

//		nextButton.set(false);
//		finishButton.set(false);

		setData();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		// Page Header
		setTitle(uiResources.getString("wizard.selectBackground.title"));
		// Page Image
		Image img = null;
		String fname = "data/Background.png";
		logger.debug("Load "+fname);
		InputStream in = SpliMoCharGenJFXConstants.class.getResourceAsStream(fname);
		if (in!=null) {
			img = new Image(in);
		} else
			logger.warn("Missing image at "+fname);
		setImage(img);

		/*
		 * Page content
		 */
		includeUnusual = new CheckBox("Alle (auch unübliche) anzeigen");
		includeUnusual.setWrapText(false);

		backgList = new ListView<Background>();
		backgList.getItems().addAll(bgGen.getAvailableBackgrounds());
		backgList.setCellFactory(new Callback<ListView<Background>, ListCell<Background>>() {
			public ListCell<Background> call(ListView<Background> param) {
				return new BackgroundListCell();
			}
		});
		Label placeholder = new Label(uiResources.getString("wizard.selectBackground.placeholder"));
		placeholder.setWrapText(true);
		backgList.setPlaceholder(placeholder);

		description = new Label();
		description.setWrapText(true);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		backgList.setMinWidth(100);
		backgList.setMinHeight(100);
		description.setPrefWidth(400);

		HBox contentLTR = new HBox();
		contentLTR.setSpacing(20);
		contentLTR.getChildren().addAll(backgList, description);

		content = new VBox();
		content.setSpacing(10);
		content.getChildren().addAll(includeUnusual, contentLTR);
		setImageInsets(new Insets(-40,0,0,0));
//		setImageSize(388,255);
		super.setContent(content);
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
	}

	//-------------------------------------------------------------------
	private void initStyles() {
		backgList.getStyleClass().add("bordered");
		description.getStyleClass().add("text-body");
	}

	//-------------------------------------------------------------------
	private void setData() {
		backgList.getItems().addAll(bgGen.getAvailableBackgrounds());
		Collections.sort(backgList.getItems());
		backgList.getSelectionModel().select(0);
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
			Platform.runLater(new Runnable(){
				public void run() {
					backgList.getItems().clear();
					backgList.setItems(FXCollections.observableArrayList((List<Background>)event.getKey()));
					Collections.sort(backgList.getItems());
					backgList.getSelectionModel().select(0);
				}});
			break;
		default:
		}
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends Background> property, Background oldModel,
			Background newModel) {
		selected = newModel;

		
		if (newModel!=null) {
			try {
				description.setText(newModel.getHelpText());
			} catch (Exception e) {
				description.setText("Missing property '"+newModel.getHelpI18NKey()+"' in "+newModel.getHelpResourceBundle().getBaseBundleName());
			}
		}

		logger.info("Background now "+selected);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#nextPage()
	 */
	@Override
	public void pageLeft() {
//		if (type!=CloseType.NEXT && type!=CloseType.FINISH)
//			return;
		if (selected!=null)
			bgGen.select(selected, choiceCallback);
		else {
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 0, "Du hast keine Abstammung gewählt.");
		}

	}

}
