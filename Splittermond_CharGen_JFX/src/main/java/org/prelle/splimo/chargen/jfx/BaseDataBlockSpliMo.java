/**
 * 
 */
package org.prelle.splimo.chargen.jfx;

import java.io.ByteArrayInputStream;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ScreenManager;
import org.prelle.splimo.Background;
import org.prelle.splimo.Culture;
import org.prelle.splimo.Education;
import org.prelle.splimo.Race;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.BaseDataBlockSpliMo.View;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class BaseDataBlockSpliMo extends VBox implements GenerationEventListener {
	
	enum View {
		TILES,
		DOCUMENT,
	}

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;
	
	private ViewMode mode;
	private ScreenManager manager;
	private CharacterController control;
	private SpliMoCharacter model;

	private ImageView ivPortrait;
	private Label lblLevel;
	private Label lblExpFree;
	private Label lblExpInv;
	private Label lblRace;
	private Control lblEducation;
	private Control lblCulture;
	private Control lblBackground;
	private Label lblSize;
	private Label lblWeight;
	private Label lblHair;
	private Label lblEyes;
	private Label lblSkin;
	private Label lblBirthPlace;
	private Label lblGender;
	private Label lblMoonsign;
//	private Button btnAddExp;
	private Button btnEditBase;
	
	private Button btnRace;
	private Button btnCult;
	private Button btnBack;
	private Button btnEduc;
	
	private ToggleGroup toggleView;
	private RadioButton btnTiles, btnDocument;
	private ObjectProperty<View> viewProperty;

	//-------------------------------------------------------------------
	/**
	 */
	public BaseDataBlockSpliMo(ViewMode mode, CharacterController control) {
		this.mode = mode;
		this.control = control;
		
		initComponents();
		initLayout();
		initInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		ivPortrait = new ImageView();
		ivPortrait.setFitWidth(256);
		ivPortrait.setFitHeight(256);
		ivPortrait.setImage(new Image(ClassLoader.getSystemResourceAsStream(SpliMoCharGenJFXConstants.PREFIX+"/images/guest-256.png")));
		
		lblLevel   = new Label("1");
		lblExpFree = new Label("0");
		lblExpInv  = new Label("0");
		lblRace  = new Label();
		if (mode==ViewMode.GENERATION) {
			lblCulture   = new TextField();
			lblBackground= new TextField();
			lblEducation = new TextField();
			((TextField)lblCulture   ).setPromptText(uiResources.getString("label.selectorenter"));
			((TextField)lblBackground).setPromptText(uiResources.getString("label.selectorenter"));
			((TextField)lblEducation ).setPromptText(uiResources.getString("label.selectorenter"));
			lblCulture   .getStyleClass().add("transparent-textfield");
			lblBackground.getStyleClass().add("transparent-textfield");
			lblEducation .getStyleClass().add("transparent-textfield");
		} else {
			lblEducation = new Label();
			lblCulture   = new Label();
			lblBackground= new Label();
			((Label)lblEducation).setWrapText(true);
		}
		lblSize      = new Label();
		lblWeight    = new Label();
		lblHair      = new Label();
		lblEyes      = new Label();
		lblSkin      = new Label();
		lblBirthPlace= new Label();
		lblGender    = new Label();
		lblMoonsign  = new Label();
		
		
		lblLevel.getStyleClass().add("text-subheader");
		lblExpFree.getStyleClass().add("text-header");
		lblExpInv .getStyleClass().add("text-header");
		lblRace.getStyleClass().add("text-subheader");
		lblEducation.getStyleClass().add("text-subheader");
		lblCulture.getStyleClass().add("text-subheader");
		lblBackground.getStyleClass().add("text-subheader");
		FontIcon iconAddExp = new FontIcon();
		FontIcon iconEditBase = new FontIcon("\uE17E\uE104");
		iconAddExp.addFontSymbol("\uE17E\uE109");
//		iconEditBase.addFontSymbol("\uE17E\uE104");
//		btnAddExp   = new Button(null, iconAddExp);
		btnEditBase = new Button(null, iconEditBase);
//		btnAddExp  .setTooltip(new Tooltip(uiResources.getString("button.addExp.tooltip")));
		btnEditBase.setTooltip(new Tooltip(uiResources.getString("button.editBase.tooltip")));
//		btnAddExp  .setStyle("-fx-padding: 8px");
		btnEditBase.setStyle("-fx-padding: 8px");
		
		btnRace = new Button(null, new FontIcon("\uE095"));
		btnCult = new Button(null, new FontIcon("\uE095"));
		btnBack = new Button(null, new FontIcon("\uE095"));
		btnEduc = new Button(null, new FontIcon("\uE095"));
		btnRace.setStyle("-fx-font-size: 150%");
		btnCult.setStyle("-fx-font-size: large");
		
		toggleView = new ToggleGroup();
		btnTiles    = new RadioButton(uiResources.getString("button.view.tiles"));
		btnTiles.setToggleGroup(toggleView);
		btnDocument = new RadioButton(uiResources.getString("button.view.document"));
		btnDocument.setToggleGroup(toggleView);
		toggleView.selectToggle(btnTiles);
		viewProperty = new SimpleObjectProperty<View>(View.TILES);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setAlignment(Pos.TOP_CENTER);
		getStyleClass().addAll("section-bar","text-body");
		setPrefWidth(300);
		if (mode==ViewMode.GENERATION) {
			setMinWidth(340);
		} else {
			setMinWidth(320);
		}		
		setMinHeight(300);
		
		getChildren().add(ivPortrait);
		VBox.setMargin(ivPortrait, new Insets(-40,0,0,0));
		
		VBox scrollable = new VBox(); 
		
		// Level
		Label heaExp       = new Label(uiResources.getString("label.experience"));
		Label heaLevel     = new Label(uiResources.getString("label.level"));
		Label heaRace      = new Label(uiResources.getString("label.race"));
		Label heaEducation = new Label(uiResources.getString("label.education"));
		Label heaCulture   = new Label(uiResources.getString("label.culture"));
		Label heaBackground= new Label(uiResources.getString("label.background"));
		Label heaSize      = new Label(uiResources.getString("label.size"));
		Label heaWeight    = new Label(uiResources.getString("label.weight"));
		Label heaHair      = new Label(uiResources.getString("label.hair"));
		Label heaEyes      = new Label(uiResources.getString("label.eyes"));
		Label heaSkin      = new Label(uiResources.getString("label.skin"));
		Label heaBirthPlace= new Label(uiResources.getString("label.birthplace"));
		Label heaGender    = new Label(uiResources.getString("label.gender"));
		Label heaMoonsign  = new Label(uiResources.getString("label.moonsign"));

		// Block experience
		Label expDelim = new Label("/");
		expDelim.getStyleClass().add("text-header");
		HBox lineExp = new HBox(5);
		lineExp.getChildren().addAll(lblExpFree,expDelim, lblExpInv);
		lineExp.setMaxWidth(Double.MAX_VALUE);
		lineExp.setAlignment(Pos.CENTER);
		VBox blkExp = new VBox();
		blkExp.setMaxWidth(Double.MAX_VALUE);
		blkExp.getChildren().addAll(heaExp, lineExp);
		
		// Block race
		HBox lineRace = new HBox(5);		
		lineRace.getChildren().add(lblRace);
		if (mode==ViewMode.GENERATION)
			lineRace.getChildren().add(btnRace);
		lineRace.setMaxWidth(Double.MAX_VALUE);
		lblRace.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lblRace, Priority.ALWAYS);
		VBox blkRace = new VBox();
		blkRace.getChildren().addAll(heaRace, lineRace);
		
		// Block culture
		HBox lineCult = new HBox(5);		
		lineCult.getChildren().add(lblCulture);
		if (mode==ViewMode.GENERATION)
			lineCult.getChildren().add(btnCult);
		lineCult.setAlignment(Pos.CENTER_RIGHT);
		lineCult.setMaxWidth(Double.MAX_VALUE);
		lblCulture.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lblCulture, Priority.ALWAYS);
		VBox blkCult = new VBox();
		blkCult.getChildren().addAll(heaCulture, lineCult);
		
		// Block background
		HBox lineBack = new HBox(5);		
		lineBack.getChildren().add(lblBackground);
		if (mode==ViewMode.GENERATION)
			lineBack.getChildren().add(btnBack);
		lineBack.setAlignment(Pos.CENTER_RIGHT);
		lineBack.setMaxWidth(Double.MAX_VALUE);
		lblBackground.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lblBackground, Priority.ALWAYS);
		VBox blkBack = new VBox();
		blkBack.getChildren().addAll(heaBackground, lineBack);
		
		// Block education
		HBox lineEduc = new HBox(5);		
		lineEduc.getChildren().add(lblEducation);
		if (mode==ViewMode.GENERATION)
			lineEduc.getChildren().add(btnEduc);
		lineEduc.setMaxWidth(Double.MAX_VALUE);
		lineEduc.setAlignment(Pos.CENTER_RIGHT);
		lblEducation.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lblEducation, Priority.ALWAYS);
		VBox blkEduc = new VBox();
		blkEduc.getChildren().addAll(heaEducation, lineEduc);

		
		// Align text
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(5);
		grid.add(blkExp  , 0, 0, 2,1);
		grid.add(heaLevel, 0, 1);
		grid.add(lblLevel, 1, 1);
		grid.add(blkRace , 0, 2, 2,1);
		grid.add(blkCult , 0, 3, 2,1);
		grid.add(blkBack , 0, 4, 2,1);
		grid.add(blkEduc , 0, 5, 2,1);
		
		grid.add(heaSize, 0, 6);
		grid.add(lblSize, 1, 6);
		grid.add(heaWeight, 0, 7);
		grid.add(lblWeight, 1, 7);
		grid.add(heaHair, 0, 8);
		grid.add(lblHair, 1, 8);
		grid.add(heaEyes, 0, 9);
		grid.add(lblEyes, 1, 9);
		grid.add(heaSkin, 0, 10);
		grid.add(lblSkin, 1, 10);
		grid.add(heaBirthPlace, 0, 11);
		grid.add(lblBirthPlace, 1, 11);
		grid.add(heaGender, 0, 12);
		grid.add(lblGender, 1, 12);
		grid.add(heaMoonsign, 0, 13);
		grid.add(lblMoonsign, 1, 13);
		
		GridPane.setConstraints(lineRace, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		
		grid.getColumnConstraints().add(new ColumnConstraints(80, 120, 140));
		grid.getColumnConstraints().add(new ColumnConstraints(160, 180, 220));
		scrollable.getChildren().add(grid);
		
		// Spacing
		Region spacing = new Region();
		spacing.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(spacing, Priority.ALWAYS);
		scrollable.getChildren().add(spacing);
		
		// View
		VBox bxView = new VBox(5);
		bxView.getChildren().addAll(btnTiles, btnDocument);
		scrollable.getChildren().add(bxView);
		
		// Icons
		HBox icons = new HBox(20);
		icons.setAlignment(Pos.CENTER_RIGHT);
		icons.setMaxWidth(Double.MAX_VALUE);
		icons.getChildren().addAll(btnEditBase);
		VBox.setMargin(icons, new Insets(0, +25, 0, -25));
		scrollable.getChildren().add(icons);
		
		ScrollPane scroll = new ScrollPane(scrollable);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setPannable(false);
		getChildren().add(scroll);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnEditBase.setOnMouseClicked(event -> openAppearance() );
		
		btnRace.setOnAction(event -> {
			SinglePageWizard<WizardPageRace> wizard = new SinglePageWizard<WizardPageRace>(model, (SpliMoCharacterGenerator)control, WizardPageRace.class, true);
			manager.show(wizard);
		});
		btnCult.setOnAction(event -> {
			SinglePageWizard<WizardPageCulture> wizard = new SinglePageWizard<WizardPageCulture>(model, (SpliMoCharacterGenerator)control, WizardPageCulture.class, true);
			manager.show(wizard);
		});
		btnBack.setOnAction(event -> {
			SinglePageWizard<WizardPageBackground> wizard = new SinglePageWizard<WizardPageBackground>(model, (SpliMoCharacterGenerator)control, WizardPageBackground.class, true);
			manager.show(wizard);
		});
		btnEduc.setOnAction(event -> {
			SinglePageWizard<WizardPageEducation> wizard = new SinglePageWizard<WizardPageEducation>(model, (SpliMoCharacterGenerator)control, WizardPageEducation.class, true);
			manager.show(wizard);
		});
		
		
		if (mode==ViewMode.GENERATION) {
			((TextField)lblCulture   ).setOnKeyReleased(event -> {
				Culture own = model.getOwnCulture();
				if (own==null) {
					own = new Culture();
					own.setKey("custom");
					model.setOwnCulture(own);
				}
				own.setName(((TextField)lblCulture   ).getText());
			});
			((TextField)lblBackground).setOnKeyReleased(event -> {
				Background own = model.getOwnBackground();
				if (own==null) {
					own = new Background();
					own.setKey("custom");
					model.setOwnBackground(own);
				}
				own.setName(((TextField)lblBackground   ).getText());
			});
			((TextField)lblEducation).setOnKeyReleased(event -> { 
				Education own = model.getOwnEducation();
				if (own==null) {
					own = new Education();
					own.setKey("custom");
					model.setOwnEducation(own);
				}
				own.setName(((TextField)lblEducation   ).getText());
			});
		}
		
		toggleView.selectedToggleProperty().addListener( (ov,o,n) -> {
			if (n==btnDocument)
				viewProperty.set(View.DOCUMENT);
			if (n==btnTiles)
				viewProperty.set(View.TILES);
		});
		
		viewProperty.addListener( (ov,o,n) -> {
			if (n==View.DOCUMENT && toggleView.getSelectedToggle()!=btnDocument)
				toggleView.selectToggle(btnDocument);
			if (n==View.TILES && toggleView.getSelectedToggle()!=btnTiles)
				toggleView.selectToggle(btnTiles);
		});
	}

	//-------------------------------------------------------------------
	public ObjectProperty<View> viewProperty() {
		return viewProperty;
	}

	//-------------------------------------------------------------------
	public void setManager(ScreenManager manager) {
		if (manager==null)
			throw new NullPointerException("Manager may not be null");
		this.manager = manager;
	}

	//-------------------------------------------------------------------
	private void openAppearance() {
		AppearanceScreen toShow = new AppearanceScreen(control, mode);
		toShow.setData(model);
		manager.show(toShow);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter charac) {
		this.model = charac;
		
		if (model.getImage()!=null) {
			logger.debug("Found image with "+model.getImage().length+" bytes");
			ivPortrait.setImage(new Image(new ByteArrayInputStream(model.getImage())));
		} else {
			ivPortrait.setImage(new Image(ClassLoader.getSystemResourceAsStream(SpliMoCharGenJFXConstants.PREFIX+"/images/guest-256.png")));
		}
		lblExpFree.setText(String.valueOf(model.getExperienceFree()));
		lblExpInv.setText(String.valueOf(model.getExperienceInvested()));
		lblLevel.setText(String.valueOf(model.getLevel()));
		
		if (model.getRace()!=null)
			lblRace.setText(model.getRace().getName());
		else
			lblRace.setText(null);
		
		if (mode==ViewMode.GENERATION) {
			if (model.getEducation()!=null) ((TextField)lblEducation).setText(model.getEducation().getName());
			if (model.getCulture  ()!=null) ((TextField)lblCulture).setText(model.getCulture().getName());
			if (model.getBackground()!=null) ((TextField)lblBackground).setText(model.getBackground().getName());
		} else {
			((Label)lblEducation).setText(model.getEducation().getName());
			logger.warn("culture = "+model.getCulture());
			((Label)lblCulture).setText(model.getCulture().getName());
			((Label)lblBackground).setText(model.getBackground().getName());
		}
		lblSize.setText(model.getSize()+" "+uiResources.getString("label.size.unit"));
		lblWeight.setText(model.getWeight()+" "+uiResources.getString("label.weight.unit"));
		lblHair.setText(model.getHairColor());
		lblEyes.setText(model.getEyeColor());
		lblSkin.setText(model.getFurColor());
		lblBirthPlace.setText(model.getBirthplace());
		lblGender.setText(String.valueOf(model.getGender()));
		if (model.getSplinter()!=null)
			lblMoonsign.setText(model.getSplinter().getName());
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case BASE_DATA_CHANGED:
			if (model.getImage()!=null) {
				ivPortrait.setImage(new Image(new ByteArrayInputStream(model.getImage())));
			} else {
				ivPortrait.setImage(new Image(ClassLoader.getSystemResourceAsStream(SpliMoCharGenJFXConstants.PREFIX+"/images/guest-256.png")));
			}
			lblLevel.setText(String.valueOf(model.getLevel()));
			lblSize.setText(model.getSize()+" "+uiResources.getString("label.size.unit"));
			lblWeight.setText(model.getWeight()+" "+uiResources.getString("label.weight.unit"));
			lblHair.setText(model.getHairColor());
			lblEyes.setText(model.getEyeColor());
			lblSkin.setText(model.getFurColor());
			lblBirthPlace.setText(model.getBirthplace());
			lblGender.setText(String.valueOf(model.getGender()));
			if (model.getSplinter()!=null)
				lblMoonsign.setText(model.getSplinter().getName());
			break;
		case RACE_SELECTED:
			lblRace.setText( ((Race)event.getKey()).getName() );
			break;
		case BACKGROUND_SELECTED:
			if (mode==ViewMode.GENERATION) {
				((TextField)lblBackground).setText( ((Background)event.getKey()).getName());
			} else {
				((Label)lblBackground).setText( ((Background)event.getKey()).getName());
			}
			break;
		case CULTURE_SELECTED:
			if (mode==ViewMode.GENERATION) {
				((TextField)lblCulture).setText( ((Culture)event.getKey()).getName());
			} else {
				((Label)lblCulture).setText( ((Culture)event.getKey()).getName());
			}
			break;
		case EDUCATION_SELECTED:
			logger.info("--handleGen "+event);
			if (mode==ViewMode.GENERATION) {
				((TextField)lblEducation).setText(((Education)event.getKey()).getName());
			} else {
				((Label)lblEducation).setText(((Education)event.getKey()).getName());
			}
			break;
		case EXPERIENCE_CHANGED:
			lblExpFree.setText(String.valueOf(model.getExperienceFree()));
			lblExpInv.setText(String.valueOf(model.getExperienceInvested()));
			break;
		default:
		}
		
	}

}
