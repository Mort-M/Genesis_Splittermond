/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.javafx.skin.NavigButtonControl;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.creature.CreatureGenerator;
import org.prelle.splimo.chargen.creature.CreatureTrainer;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splimo.creature.ModuleBasedCreature;
import org.prelle.splimo.npc.NPCGenerator;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * @author Stefan
 *
 */
public class CreatureScreen extends ManagedScreen implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private CharacterController control;
	private SpliMoCharacter model;

	private TilePane flow;
	
	private ObjectProperty<CreatureReference> selected;
	
	private ContextMenu ctxMenuAdd;
	private MenuItem menuAddCreature;
	private MenuItem menuCreateCreature;
	private MenuItem menuCreateEntourage;

	
	//--------------------------------------------------------------------
	/**
	 */
	public CreatureScreen(CharacterController control) {
		this.control = control;
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
		
		setData(control.getModel());
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		selected = new SimpleObjectProperty<>();
		
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("screen.creatures.title"));
		
		
		flow = new TilePane(Orientation.HORIZONTAL);
		flow.setPrefRows(1);
		flow.setPrefColumns(3);
		flow.setHgap(20);
		flow.setVgap(20);

		/*
		 * Add-Button context menu
		 * List all available ruleplugins
		 */
		ctxMenuAdd = new ContextMenu();
		menuAddCreature    = new MenuItem(UI.getString("screen.creature.add.add_creature"));
		menuCreateCreature = new MenuItem(UI.getString("screen.creature.add.create_creature"));
		menuCreateEntourage= new MenuItem(UI.getString("screen.creature.add.create_entourage"));
		getStaticButtons().addAll(menuAddCreature, menuCreateCreature);
		menuAddCreature.setOnAction( event -> addCreatureClicked());
		menuCreateCreature.setOnAction( event -> createCreatureClicked());
//		ctxMenuAdd.getItems().addAll(menuAddCreature);
		ctxMenuAdd.getItems().addAll(menuAddCreature, menuCreateCreature);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");
		
		Label warning = new Label("Es gibt einen bekannten Bug, der dafür sorgt dass die Auswirkungen von Abrichtungen nicht (korrekt) berücksichtigt werden.");
		warning.setWrapText(true);
		warning.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 4px; -fx-font-weight: bold");
		
		ScrollPane scroll = new ScrollPane(flow);
		
		HBox.setMargin(flow, new Insets(0, 0, 20, 0));
		setContent(new VBox(20, warning, scroll));
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
	}

	//-------------------------------------------------------------------
	private VBox getBox(CreatureReference ref) {
		for (Node node : flow.getChildren()) {
			if (node.getUserData()==ref) {
				return (VBox)node;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	private void select(CreatureReference ref) {
		if (selected!=null) {
			// Find old selection
			VBox box = getBox(selected.get());
			if (box!=null) {
//				CreaturePane pane = (CreaturePane)box.getChildren().get(1);
				box.getStyleClass().remove("selectedcreaturebox");
			}
		}
		
		selected.set(ref);
		VBox box = getBox(selected.get());
		if (box!=null) {
//			CreaturePane pane = (CreaturePane)box.getChildren().get(1);
			box.getStyleClass().add("selectedcreaturebox");
		}
		
	}

	//-------------------------------------------------------------------
	private void add(CreatureReference ref) {
		VBox col = new VBox(20);
		col.setUserData(ref);
		Label heading = new Label(ref.getName());
		heading.getStyleClass().add("text-subheader");
		Button btnDel = new Button(null, new FontIcon("\uE107"));
		btnDel.setTooltip(new Tooltip(UI.getString("tooltip.creature.delete")));
		btnDel.setOnAction(event -> {remove(ref); update();});
		Button btnEdit = new Button(null, new FontIcon("\uE0D8"));
		btnEdit.setOnAction(event -> {askName(ref); update();});
		
		HBox headline = new HBox(10);
		headline.getChildren().addAll(heading, btnDel, btnEdit);
		HBox.setMargin(btnDel, new Insets(0,0,0,10));
		HBox.setHgrow(heading, Priority.ALWAYS);
		heading.setMaxWidth(Double.MAX_VALUE);
		headline.setMaxWidth(Double.MAX_VALUE);
		headline.setAlignment(Pos.CENTER_LEFT);
		
		VBox lifeAndTrain = new VBox(20);
		// Companion data
		LifeformPane pane = new LifeformPane();
		if (ref.getModuleBasedCreature()!=null)
			pane.setData(ref.getModuleBasedCreature());
		else if (ref.getEntourage()!=null)
			pane.setData(ref.getEntourage());
		else if (ref.getTemplate()!=null)
			pane.setData(ref.getTemplate());

		lifeAndTrain.getChildren().addAll(pane);

		// Training data
		if (ref.getEntourage()==null) {
			TrainingPane training = new TrainingPane();
			training.setData(new CreatureTrainer(model, ref), model, (ScreenManagerProvider)this);
			lifeAndTrain.getChildren().addAll(training);
		}
		
		lifeAndTrain.getStyleClass().addAll("bordered","content");
		lifeAndTrain.setStyle("-fx-pref-width: 30em");
		
		col.getChildren().addAll(headline, lifeAndTrain);
		flow.getChildren().add(col);
		
		pane.setOnMouseClicked(event -> select(ref));
	}

	//-------------------------------------------------------------------
	private void remove(CreatureReference ref) {
		if (ref!=null) {
			logger.info("Remove creature/follower "+ref.getName());
			model.removeCreature(ref);
			VBox col = getBox(ref);
			flow.getChildren().remove(col);
			selected.set(null);
		}
	}


	//-------------------------------------------------------------------
	private void addCreatureClicked() {
		logger.debug("addCreatureClicked");
		String heading = UI.getString("screen.creatures.addcreaturedialog.title"); 

		CreatureListView list = new CreatureListView();
		list.getItems().addAll(SplitterMondCore.getCreatures(SplitterMondCore.getCreatureFeatureType("CREATURE")));
		
		NavigButtonControl control = new NavigButtonControl();
		
		CloseType close = manager.showAlertAndCall(AlertType.QUESTION, heading, list);
		if (close==CloseType.OK) {
			Creature selected = list.getSelectionModel().getSelectedItem();
			if (selected==null) {
				logger.warn("Clicked OK but selected nothing");
			} else {
				CreatureReference ref = new CreatureReference(selected);
				model.addCreature(ref);
				add(ref);
			}
		}
		
//		dia = new SelectItemDialogScreen();
//		this.location = location;
//		dia.startListenForEvents();
//		manager.show(dia);
	}

	//-------------------------------------------------------------------
	private void createCreatureClicked() {
		logger.debug("createCreatureClicked");
//		ResourceReference resource = new ResourceReference(SplitterMondCore.getResource("creature"),2);
		CreatureGenerator creatGen = new CreatureGenerator((ResourceReference)null);
		CreatureCreateScreen screen = new CreatureCreateScreen(creatGen);
		manager.show(screen);
	}
	
	//-------------------------------------------------------------------
	/**
	 * In t
	 */
	public void childClosed(ManagedScreen child, CloseType type) {
		child.setCloseType(type);
		
		logger.debug("childClosed("+child+") via "+type);
		if (child instanceof CreatureCreateScreen) {
			ModuleBasedCreature toAdd = ((CreatureCreateScreen)child).getCreatureController().getCreature().getModuleBasedCreature();
			logger.info("Created "+toAdd);
			CreatureReference ref = new CreatureReference(toAdd);
			askName(ref);
			model.addCreature(ref);
			add(ref);
		}
		if (child instanceof CreatureTrainingScreen) {
			update();
		}
	}

	//-------------------------------------------------------------------
	private void askName(CreatureReference ref) {
		Label lbInput = new Label(UI.getString("screen.creatures.namedialog.mess"));
		TextField tfInput = new TextField();
		tfInput.setStyle("-fx-pref-width: 30em");
		
		VBox layout = new VBox(5, lbInput, tfInput);
		CloseType close = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("screen.creatures.namedialog.title"), layout);
		if (close==CloseType.OK) {
			String name = tfInput.getText();
			logger.info("Rename creature "+ref+" to \""+name+"\"");
			ref.setName(name);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Show menu with options what to add
	 */
	private void creatureCreateClicked() {
		Creature data = new Creature();
		NPCGenerator ctrl = new NPCGenerator(data);
		CreatureWizardSpliMo wizard = new CreatureWizardSpliMo(ctrl);
		logger.debug("creatureCreateClicked");
		manager.show(wizard);
	}

	//-------------------------------------------------------------------
	private void update() {
		flow.getChildren().clear();
		for (CreatureReference ref : model.getCreatures()) {
			add(ref);
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		logger.debug("setData");
		this.model = model;
		setTitle(model.getName()+"/"+UI.getString("screen.creatures.title"));
		update();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CREATURE_CHANGED:
			logger.debug("RCV "+event);
			// WARNING: When calling update here, we have an recursive loop
//			update();
			break;
		default:
			break;
		}		
	}

}
