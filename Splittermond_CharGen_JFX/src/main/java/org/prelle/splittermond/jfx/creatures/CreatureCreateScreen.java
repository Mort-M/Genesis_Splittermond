/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.javafx.skin.NavigButtonControl;
import org.prelle.splimo.LetUserChooseAdapter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CreatureController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.creature.CreatureTypeValue;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class CreatureCreateScreen extends ManagedScreen implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CreatureController control;

	private Label lbPointsLeft;
	private VBox viewPane;
	private ChoiceBox<CreatureModule> cbBase;
	private ChoiceBox<CreatureModule> cbRole;
	private Button btnTypus;
	private Label  lblTypus;
	private Label lbDescr;
	private ListView<CreatureModule> lvOptions;
	private CreatureModuleReferenceListView lvSelected;

	private LifeformPane resultPane;
	private NecessaryChoicesPane choicePane;
	
	private NavigButtonControl btnControl;

	//--------------------------------------------------------------------
	public CreatureCreateScreen(CreatureController ctrl) {
		this.control = ctrl;
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		setTitle(UI.getString("screen.creature.create.title"));
		getNavigButtons().add(CloseType.APPLY);
		getNavigButtons().add(CloseType.CANCEL);
		
		btnControl = new NavigButtonControl();
		setButtonControl(btnControl);

		update();

		GenerationEventDispatcher.addListener(this);
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		lbPointsLeft = new Label();
		lbPointsLeft.getStyleClass().add("text-header");

		// Square OL
		btnTypus = new Button(UI.getString("button.select"));
		btnTypus.getStyleClass().add("bordered");
		lblTypus = new Label();
		
		cbBase = new ChoiceBox<CreatureModule>();
		cbBase.getItems().addAll(control.getBases());
		cbBase.setConverter(new StringConverter<CreatureModule>() {
			public String toString(CreatureModule val) { return val.getName();}
			public CreatureModule fromString(String string) {return null;}
		});

		cbRole = new ChoiceBox<CreatureModule>();
		cbRole.getItems().addAll(control.getRoles());
		cbRole.setConverter(new StringConverter<CreatureModule>() {
			public String toString(CreatureModule val) { return val.getName();}
			public CreatureModule fromString(String string) {return null;}
		});

		// Square UR
		lbDescr = new Label();
		lbDescr.setWrapText(true);
		lbDescr.setStyle("-fx-pref-width: 30em");

		lvOptions = new ListView<CreatureModule>();
		lvOptions.setCellFactory(param -> new CreatureModuleListCell(control));
		lvOptions.getItems().addAll(control.getAvailableOptions());
		lvSelected= new CreatureModuleReferenceListView(control, new LetUserChooseAdapter(this));

		viewPane = new VBox();
		
		resultPane = new LifeformPane();
		resultPane.setData(control.getCreature());
		
		choicePane = new NecessaryChoicesPane(control, (ScreenManagerProvider)this);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label hdPointsLeft = new Label(UI.getString("screen.creature.create.pointspane.left"));
		Label lbPaneDescr = new Label(UI.getString("screen.creature.create.pointspane.descr"));
		hdPointsLeft.setWrapText(true);
		lbPaneDescr.setWrapText(true);
		VBox pointsPane = new VBox();
		pointsPane.getChildren().addAll(lbPointsLeft, hdPointsLeft, lbPaneDescr);
		pointsPane.setAlignment(Pos.TOP_CENTER);
		VBox.setMargin(lbPaneDescr, new Insets(40,0,0,0));
		pointsPane.getStyleClass().add("section-bar");
		lbPaneDescr.setStyle("-fx-min-width: 10em");
		pointsPane.setStyle("-fx-min-width: 10em");
		pointsPane.setStyle("-fx-pref-width: 10em");



		Label hdType = new Label(UI.getString("screen.creature.create.type"));
		Label hdBase = new Label(UI.getString("screen.creature.create.base"));
		Label hdRole = new Label(UI.getString("screen.creature.create.role"));
		hdType.getStyleClass().add("text-subheader");
		hdBase.getStyleClass().add("text-subheader");
		hdRole.getStyleClass().add("text-subheader");


		HBox lineType = new HBox(10);
		lineType.getChildren().addAll(btnTypus, lblTypus);
		lineType.setAlignment(Pos.CENTER_LEFT);
		VBox square1 = new VBox();
		square1.getChildren().addAll(hdType, lineType, hdBase, cbBase, hdRole, cbRole);
		VBox.setMargin(hdRole, new Insets(20, 0, 0, 0));

		ScrollPane choiceScroll = new ScrollPane(choicePane);
		choiceScroll.setFitToHeight(true);
//		choiceScroll.setPrefHeight(Double.MAX_VALUE);
		
		VBox col3 = new VBox();
		col3.getChildren().addAll(resultPane, choiceScroll);
		col3.setSpacing(20);
//		col3.setStyle("-fx-background-color: red");
		VBox.setVgrow(choiceScroll, Priority.ALWAYS);
		
		
		GridPane content = new GridPane();
//		content.setGridLinesVisible(true);
		content.setVgap(20);
		content.setHgap(10);
		content.add(square1   , 0,	0);
		content.add(lbDescr   , 1,	0);
		content.add(lvOptions , 0,	1, 1,1);
		content.add(lvSelected, 1,	1);
		content.add(col3      , 2,	0, 1,2);
		content.setMaxHeight(Double.MAX_VALUE);
		lvOptions.setMaxHeight(Double.MAX_VALUE);
		lvOptions.setStyle("-fx-min-width: 19em");
		GridPane.setConstraints(lvOptions, 0,	1, 1,1, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
//		content.setStyle("-fx-background-color: cyan");

//		resultPane.setStyle("-fx-background-color: green; -fx-min-width: 4em; -fx-min-height: 4em");
//		viewPane.setStyle("-fx-background-color: red; -fx-min-width: 4em; -fx-min-height: 4em");
		
		HBox layout = new HBox();
		layout.setSpacing(20);
		layout.getChildren().addAll(pointsPane, content, viewPane);
		HBox.setMargin(pointsPane, new Insets(0,0,20,0));
		HBox.setMargin(content, new Insets(0,0,20,0));
		HBox.setMargin(viewPane, new Insets(0,0,20,0));
//		layout.setPrefHeight(Double.MAX_VALUE);
//		layout.setStyle("-fx-min-height: 50em");
//		layout.setStyle("-fx-background-color: lime");
		
		ScrollPane scroll = new ScrollPane(layout);
		scroll.setFitToHeight(true);
		scroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		setContent(scroll);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		cbBase.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> control.selectBase(n));
		cbRole.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> control.selectRole(n));
		lvOptions.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> describe(n));
		lvSelected.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) describe(n.getModule());
		});
		btnTypus.setOnAction(event -> openCreatureTypeDialog());
		
		// Events for dragging back
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}

	//--------------------------------------------------------------------
	CreatureController getCreatureController() {
		return control;
	}

	//-------------------------------------------------------------------
	private void describe(CreatureModule n) {
		if (n!=null)
			lbDescr.setText(n.getHelpText());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.CloseType)
	 */
	@Override
	public boolean close(CloseType closeType) {
		if (closeType==CloseType.CANCEL)
			return true;
		
		boolean canBeFinished = control.canBeFinished();
		if (canBeFinished)
			GenerationEventDispatcher.removeListener(this);
		
		return canBeFinished;
	}

	//--------------------------------------------------------------------
	private String getCreatureTypesString() {
		StringBuffer buf = new StringBuffer();
		for (Iterator<CreatureTypeValue> it=control.getCreatureTypes().iterator(); it.hasNext(); ) {
			CreatureTypeValue val = it.next();
			String text = val.getName();
			if (it.hasNext())
				text +=",";
			buf.append(text);
		}
		return buf.toString();
	}
	
	//--------------------------------------------------------------------
	public void update() {
		lblTypus.setText(getCreatureTypesString());
		resultPane.refresh();
		choicePane.refresh();
//		Parent real = SpliMoCharGenJFXUtil.getDisplayNode(control.getCreature());
//		viewPane.getChildren().clear();
//		viewPane.getChildren().add(real);
		int avail = control.getAvailableCreaturePoints();
		lbPointsLeft.setText(String.valueOf(avail));
		btnControl.setDisabled(CloseType.APPLY, avail!=0);

		lvOptions.getItems().clear();
		lvOptions.getItems().addAll(control.getAvailableOptions());
		
		lvSelected.getItems().clear();
		lvSelected.getItems().addAll(control.getSelectedOptions());
	}

	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString().substring(db.getString().indexOf(":")+1);
        	logger.debug("Dropped "+enhanceID);
    		for (CreatureModuleReference ref : control.getSelectedOptions()) {
    			if (ref.getUniqueId().toString().equals(enhanceID)) {
    				logger.debug("found module reference to deselect: "+ref);
    	       		Platform.runLater(new Runnable(){
    					public void run() {
    			       		control.deselectOption(ref);
    					}
            		});
    			}
    		}
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
	}

	//-------------------------------------------------------------------
	private void dragOver(DragEvent event) {
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.info("RCV "+event);
		switch (event.getType()) {
		case CREATURE_CHANGED:
			logger.debug("RCV "+event);
			update();
			break;
		default:
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @return
	 */
	private void openCreatureTypeDialog() {
		VBox list = new VBox(5);
		for (CreatureType type : SplitterMondCore.getCreatureTypes()) {
			if (type.isSpecial())
				continue;
			RadioButton radio = new RadioButton(type.getName());
			radio.setUserData(new CreatureTypeValue(type));
			if (control.hasCreatureType(type))
				radio.setSelected(true);
			list.getChildren().add(radio);
		}
		ScrollPane scroll = new ScrollPane(list);
		
		CloseType close = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("screen.creature.create.typedialog.title"), scroll);
		if (close==CloseType.OK) {
			List<CreatureTypeValue> toSet = new ArrayList<>();
			for (Node node : list.getChildren()) {
				RadioButton radio = (RadioButton)node;
				if (radio.isSelected())
					toSet.add((CreatureTypeValue) radio.getUserData());
			}
			control.setCreatureTypes(toSet);
			update();
		}
	}

}