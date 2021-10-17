package org.prelle.splittermond.chargen.jfx.creatures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.OptionalDescriptionPane;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CreatureController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModule.Type;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureModuleReference.NecessaryChoice;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.creature.CreatureTypeValue;
import org.prelle.splittermond.chargen.jfx.LetUserChooseAdapter;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.listcells.CreatureModuleListCell;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;

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
import javafx.scene.control.TextField;
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
public class CreatureEditPane extends OptionalDescriptionPane implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(CompanionSection.class.getName());

	private CreatureController control;
	private ScreenManagerProvider provider;

	private Label lbPointsLeft;
	private ChoiceBox<CreatureModule> cbBase;
	private ChoiceBox<CreatureModule> cbRole;
	private Button btnTypus;
	private Label  lblTypus;
	private ListView<CreatureModule> lvOptions;
	private CreatureModuleReferenceListView lvSelected;

	private TextField tfName;
	private LifeformPane resultPane;
	private VBox optionalColumn;

	//--------------------------------------------------------------------
	public CreatureEditPane(CreatureController ctrl, ScreenManagerProvider provider) {
		this.control = ctrl;
		this.provider= provider;
		initComponents();
		initLayout();
		initInteractivity();

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
			public String toString(CreatureModule val) { return (val!=null)?val.getName():"";}
			public CreatureModule fromString(String string) {return null;}
		});

		cbRole = new ChoiceBox<CreatureModule>();
		cbRole.getItems().addAll(control.getRoles());
		cbRole.setConverter(new StringConverter<CreatureModule>() {
			public String toString(CreatureModule val) { return (val!=null)?val.getName():"";}
			public CreatureModule fromString(String string) {return null;}
		});

		lvOptions = new ListView<CreatureModule>();
		lvOptions.setCellFactory(param -> new CreatureModuleListCell(control));
		lvOptions.getItems().addAll(control.getAvailableOptions());

		lvSelected= new CreatureModuleReferenceListView(control, new LetUserChooseAdapter(provider), provider);
		
		resultPane = new LifeformPane();
		resultPane.setData(control.getCreature());
		
		tfName = new TextField(control.getCreature().getName());
		tfName.setPrefColumnCount(30);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label hdPointsLeft = new Label(UI.getString("dialog.creature.create.cost"));
		hdPointsLeft.setWrapText(true);
		VBox pointsPane = new VBox();
		pointsPane.getChildren().addAll(hdPointsLeft, lbPointsLeft);
		pointsPane.setAlignment(Pos.TOP_CENTER);



		Label hdType = new Label(UI.getString("dialog.creature.create.type"));
		Label hdBase = new Label(UI.getString("dialog.creature.create.base"));
		Label hdRole = new Label(UI.getString("dialog.creature.create.role"));
		hdType.getStyleClass().add("base");
		hdBase.getStyleClass().add("base");
		hdRole.getStyleClass().add("base");

		GridPane grid = new GridPane();
		grid.setStyle("-fx-hgap: 0.3em; -fx-vgap: 0.5em");
		grid.add(hdType  , 0, 0);
		grid.add(btnTypus, 1, 0);
		grid.add(lblTypus, 2, 0, 2,1);
		
		grid.add(hdBase, 0, 1);
		grid.add(cbBase, 1, 1);
		grid.add(hdRole, 2, 1);
		grid.add(cbRole, 3, 1);
		
		grid.add(pointsPane, 4,0, 1,2);
		
		GridPane.setMargin(hdRole,  new Insets(0, 0, 0, 20));
		GridPane.setMargin(pointsPane,  new Insets(0, 0, 0, 50));
		
		
		GridPane content = new GridPane();
//		content.setGridLinesVisible(true);
		content.setVgap(20);
		content.setHgap(10);
		content.add(grid   , 0,	0, 2,1);
		content.add(lvOptions , 0,	1, 1,1);
		content.add(lvSelected, 1,	1);
		content.setMaxHeight(Double.MAX_VALUE);
		lvOptions.setMaxHeight(Double.MAX_VALUE);
		lvOptions.setStyle("-fx-min-width: 19em");
        lvOptions.setStyle("-fx-pref-width: 20em;");
		lvSelected.setStyle("-fx-pref-width: 25em;");
		GridPane.setConstraints(lvOptions, 0,	1, 1,1, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
		
		// Optional
		Label hdName = new Label(UI.getString("dialog.creature.create.name"));
		HBox nameLine = new HBox(10, hdName,  tfName);
		optionalColumn = new VBox(20, nameLine, resultPane);
		
		setChildren(content, optionalColumn);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		cbBase.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> control.selectBase(n));
		cbRole.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> control.selectRole(n));
//		lvOptions.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> describe(n));
//		lvSelected.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
//			if (n!=null) describe(n.getModule());
//		});
		btnTypus.setOnAction(event -> openCreatureTypeDialog());
		
		// Events for dragging back
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
//		setCanBeLeftCallback( (dialog, closeType) -> (closeType==CloseType.CANCEL) || control.canBeFinished());
		
		tfName.textProperty().addListener( (ov,o,n) -> control.getCreature().setName(n));
	}

	//--------------------------------------------------------------------
	CreatureController getCreatureController() {
		return control;
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
	private List<NecessaryChoice> getChoicesOfRole() {
		List<NecessaryChoice> ret = new ArrayList<>();
		for (NecessaryChoice tmp : control.getChoicesToMake()) {
			if (tmp.originModule.getModule().getType()==Type.ROLE) {
				ret.add(tmp);
			}
		}
		return ret;
	}
	
	//--------------------------------------------------------------------
	public void update() {
		lblTypus.setText(getCreatureTypesString());
		resultPane.refresh();
		int avail = control.getAvailableCreaturePoints();
		lbPointsLeft.setText(String.valueOf(avail));

		lvOptions.getItems().clear();
		lvOptions.getItems().addAll(control.getAvailableOptions());
		
		lvSelected.getItems().clear();
		lvSelected.getItems().addAll(control.getSelectedOptions());
		
		/*
		 * If role has necessary choices, inject a virtual creature module
		 */
		List<NecessaryChoice> choices = getChoicesOfRole();
		if (choices!=null && !choices.isEmpty()) {
			lvSelected.getItems().add(0, control.getSelectedRole());
		}
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
		
		CloseType close = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, UI.getString("dialog.creature.create.typedialog.title"), scroll);
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