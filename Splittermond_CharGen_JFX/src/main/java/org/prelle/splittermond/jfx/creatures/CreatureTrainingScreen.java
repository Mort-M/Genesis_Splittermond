/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.ArrayList;
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
import org.prelle.rpgframework.jfx.ThreeColumnPane;
import org.prelle.splimo.LetUserChooseAdapter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CommonCreatureController;
import org.prelle.splimo.charctrl.CreatureTrainerController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.creature.CreatureTypeValue;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Stefan
 *
 */
public class CreatureTrainingScreen extends ManagedScreen implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CreatureTrainerController control;

	private Label lbPointsLeft;
	private Label lbDescr;
	private ListView<CreatureModule> lvOptions;
	private CreatureModuleReferenceListView lvSelected;
	private LifeformPane resultPane;

	private NecessaryChoicesPane choicePane;
	
	private NavigButtonControl btnControl;

	//--------------------------------------------------------------------
	public CreatureTrainingScreen(CreatureTrainerController ctrl) {
		this.control = ctrl;
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		setTitle(UI.getString("screen.creature.train.title"));
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

		// Square UR
		lbDescr = new Label();
		lbDescr.setWrapText(true);
		lbDescr.setStyle("-fx-pref-width: 30em");

		lvOptions = new ListView<CreatureModule>();
		lvOptions.setCellFactory(param -> new CreatureModuleListCell(control));
		lvOptions.getItems().addAll(control.getAvailableOptions());
		lvSelected= new CreatureModuleReferenceListView(control, new LetUserChooseAdapter(this));
		
		choicePane = new NecessaryChoicesPane(control, (ScreenManagerProvider)this);
		
		resultPane = new LifeformPane();
		resultPane.setData(control.getCreature());
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label hdPointsLeft = new Label(UI.getString("screen.creature.train.pointspane.left"));
		Label lbPaneDescr = new Label(UI.getString("screen.creature.train.pointspane.descr"));
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



		ScrollPane choiceScroll = new ScrollPane(choicePane);
		choiceScroll.setFitToHeight(true);
//		choiceScroll.setPrefHeight(Double.MAX_VALUE);
		
		VBox column3 = new VBox(20);
		column3.getChildren().addAll(resultPane, lbDescr, choiceScroll);
		
		ThreeColumnPane columns = new ThreeColumnPane();
		columns.setColumn1Node(lvOptions);
		columns.setColumn2Node(lvSelected);
		columns.setColumn3Node(column3);
		
		lvOptions.setMaxHeight(Double.MAX_VALUE);
		lvOptions.setStyle("-fx-min-width: 19em");
		
		HBox layout = new HBox();
		layout.setSpacing(20);
		layout.getChildren().addAll(pointsPane, columns);
		HBox.setMargin(pointsPane, new Insets(0,0,20,0));
		HBox.setMargin(columns, new Insets(0,0,20,0));
		setContent(layout);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		lvOptions.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> describe(n));
		lvSelected.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) describe(n.getModule());
		});
		
		// Events for dragging back
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}

	//--------------------------------------------------------------------
	CommonCreatureController getCreatureController() {
		return control;
	}

	//-------------------------------------------------------------------
	private void describe(CreatureModule n) {
		if (n!=null) {
			String text = n.getProductName()+" "+n.getPage()+"\n\n"+n.getHelpText();
			lbDescr.setText(text);
		}
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.CloseType)
//	 */
//	@Override
//	public boolean close(CloseType closeType) {
//		if (closeType==CloseType.CANCEL)
//			return true;
//		
//		boolean canBeFinished = control.canBeFinished();
//		if (canBeFinished)
//			GenerationEventDispatcher.removeListener(this);
//		
//		return canBeFinished;
//	}
	
	//--------------------------------------------------------------------
	public void update() {
		logger.debug("update()");
		
		resultPane.refresh();
		choicePane.refresh();
//		Parent real = SpliMoCharGenJFXUtil.getDisplayNode(control.getCreature());
//		viewPane.getChildren().clear();
//		viewPane.getChildren().add(real);
		int avail = control.getAvailablePotential();
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

}
