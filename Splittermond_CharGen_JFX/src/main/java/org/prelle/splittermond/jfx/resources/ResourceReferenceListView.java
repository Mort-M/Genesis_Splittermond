/**
 *
 */
package org.prelle.splittermond.jfx.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.apache.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.skin.NavigButtonControl;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureFeature;
import org.prelle.splimo.creature.CreatureGenerator;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splimo.creature.ModuleBasedCreature;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.levelling.ResourceLeveller;
import org.prelle.splittermond.jfx.creatures.CreatureCreateScreen;
import org.prelle.splittermond.jfx.equip.EditItemScreen;
import org.prelle.splittermond.jfx.equip.SelectItemDialogScreen;

/**
 * @author prelle
 *
 */
public class ResourceReferenceListView extends ListView<ResourceReference> {

	private static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private ResourceController control;
	private SpliMoCharacter model;
	private ScreenManager manager;
	private LetUserChooseListener callback;

	//--------------------------------------------------------------------
	public ResourceReferenceListView(ResourceController control, LetUserChooseListener callback) {
		this.control = control;
		this.callback = callback;

		initComponents();
		initValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	public void updateItemController(ResourceController control) {
		this.control = control;
	}

	//--------------------------------------------------------------------
	public void setManager(ScreenManager mgr) {
		this.manager = mgr;
	}

	//--------------------------------------------------------------------
	public ScreenManager getScreenManager() {
		return manager;
	}

	//--------------------------------------------------------------------
	public SpliMoCharacter getCharacter() {
		return model;
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Label ph = new Label(UI.getString("placeholder.resources.selected"));
		ph.setWrapText(true);
        setPlaceholder(ph);
        setStyle("-fx-min-width: 15em; -fx-pref-width: 22em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black; -fx-padding: 2px");
	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<ResourceReference>, ListCell<ResourceReference>>() {
			public ListCell<ResourceReference> call(ListView<ResourceReference> p) {
				return new ResourceReferenceListCell(control, ResourceReferenceListView.this);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}

	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString();
        	logger.debug("Dropped "+enhanceID);
        	Resource res = SplitterMondCore.getResource(enhanceID);
        	if (res!=null) // && control.canBeAdded(res))
        		control.openResource(res);
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
	public ScreenManager getManager() {
		return manager;
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	SpliMoCharacter getData() {
		return model;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the callback
	 */
	public LetUserChooseListener getCallback() {
		return callback;
	}

}

class ResourceReferenceListCell extends ListCell<ResourceReference> {

	private static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private transient ResourceReference data;

	private ResourceController charGen;
	private ResourceReferenceListView parent;

	private HBox layout;
	private Label name;
	private Button btnEdit;
	private Button btnDec;
	private Label  lblVal;
	private Button btnInc;

	private Label tfDescr;
	private transient CreatureReference item = null;

	//-------------------------------------------------------------------
	public ResourceReferenceListCell(ResourceController charGen, ResourceReferenceListView parent) {
		this.charGen = charGen;
		this.parent  = parent;
		if (parent==null)
			throw new NullPointerException();

		layout  = new HBox(5);
		name    = new Label();
		tfDescr = new Label();
		btnEdit = new Button("\uE1C2");
		btnDec  = new Button("\uE738");
		lblVal  = new Label("?");
		btnInc  = new Button("\uE710");

		initStyle();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initStyle() {
		btnDec.setStyle("-fx-background-color: transparent");
		btnInc.setStyle("-fx-background-color: transparent");
		btnDec.getStyleClass().add("bordered");
		btnInc.getStyleClass().add("bordered");
		name.getStyleClass().add("text-small-subheader");
		lblVal.getStyleClass().add("text-subheader");

		btnEdit.setStyle("-fx-background-color: transparent");

		setStyle("-fx-pref-width: 15em");
		layout.getStyleClass().add("content");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		HBox line2 = new HBox(5);
		line2.getChildren().addAll(btnEdit, tfDescr);

		VBox bxCenter = new VBox(2);
		bxCenter.getChildren().addAll(name, line2);

		btnDec.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnInc.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		TilePane tiles = new TilePane(Orientation.HORIZONTAL);
		tiles.setPrefColumns(3);
		tiles.setHgap(4);
		tiles.getChildren().addAll(btnDec, lblVal, btnInc);
		tiles.setAlignment(Pos.CENTER_LEFT);
		layout.getChildren().addAll(bxCenter, tiles);

		bxCenter.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(bxCenter, Priority.ALWAYS);


		setAlignment(Pos.CENTER);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnInc .setOnAction(event -> {charGen.increase(data); updateItem(data, false); parent.refresh();});
		btnDec .setOnAction(event -> {charGen.decrease(data); updateItem(data, false); parent.refresh();});
		btnEdit.setOnAction(event -> editClicked(data));

		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.debug("drag started for "+data);
		if (data==null)
			return;
		logger.debug("canBeDeselected = "+charGen.canBeDeselected(data));
		logger.debug("canBeTrashed    = "+((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true));
		if (!charGen.canBeDeselected(data) && !((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true))
			return;

		Node source = (Node) event.getSource();
		logger.debug("drag src = "+source);

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        String id = "resource:"+data.getResource().getId()+"|desc="+data.getDescription()+"|idref="+data.getIdReference();
        content.putString(id);
        db.setContent(content);

        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);

        event.consume();
    }

	//-------------------------------------------------------------------
	private void editClickedRelic(ResourceReference ref) {
		logger.debug("edit clicked for relic");
		String heading = UI.getString("resourcelistview.relicdialog.title");
		String explain = UI.getString("resourcelistview.relicdialog.descr");

		// Description
		Label lblExplain = new Label(String.format(explain, ref.getValue()));
		lblExplain.setWrapText(true);

		// Options
		RadioButton option1 = new RadioButton(String.format(UI.getString("resourcelistview.relicdialog.option1"), ref.getValue(), ref.getValue()));
		RadioButton option2 = new RadioButton(String.format(UI.getString("resourcelistview.relicdialog.option2"), ref.getValue(), ref.getValue()));
		RadioButton option3 = new RadioButton(String.format(UI.getString("resourcelistview.relicdialog.option3"), ref.getValue(), ref.getValue()));
		ToggleGroup group = new ToggleGroup();
		group.getToggles().addAll(option1, option2, option3);

		/*
		 * List
		 */
		ListView<CarriedItem> listAvailable = new ListView<CarriedItem>();
		listAvailable.setStyle("-fx-max-width: 20em");
		listAvailable.setStyle("-fx-pref-height: 10em");
		Label placeholder = new Label(UI.getString("resourcelistview.relicdialog.placeholder"));
		placeholder.setWrapText(true);
		listAvailable.setPlaceholder(placeholder);
		listAvailable.setCellFactory(new Callback<ListView<CarriedItem>, ListCell<CarriedItem>>() {
			public ListCell<CarriedItem> call(ListView<CarriedItem> param) {
				return new ListCell<CarriedItem>() {
					@Override
					public void updateItem(CarriedItem item, boolean empty) {
						super.updateItem(item, empty);
						setText( empty?null:String.format(UI.getString("resourcelistview.relicdialog.cell"), (item.getArtifactQuality() + item.getItemQuality()), item.getName()));
					}
				};
			}
		});

		/*
		 * Option details
		 */
		HBox optionPane = new HBox();
		optionPane.setStyle("-fx-pref-width: 20em; -fx-pref-heigh: 20em");

		/*
		 * Build list of items already added to character that have
		 * a matching quality level
		 */
		List<CarriedItem> matching = new ArrayList<>();
		for (CarriedItem tmp : parent.getData().getItems()) {
			int quality = tmp.getArtifactQuality() + tmp.getItemQuality();
			if (quality==ref.getValue()) {
				matching.add(tmp);
				break;
			}
		}



		/*
		 * Button
		 */
		Button btnAdd = new Button(UI.getString("button.add"));
		btnAdd.getStyleClass().add("bordered");

		/*
		 * Interactivity
		 */
		group.selectedToggleProperty().addListener( (ov,o,n) -> {
			if (n==option1) {
				optionPane.getChildren().clear();
				optionPane.getChildren().add(listAvailable);
				listAvailable.getItems().clear();
				listAvailable.getItems().addAll(matching);
			} else if (n==option2) {
				optionPane.getChildren().clear();
				optionPane.getChildren().add(listAvailable);
				listAvailable.getItems().clear();
				for (CarriedItem item : parent.getData().getItems())
					if (item.getItemQuality()==0 && item.getArtifactQuality()==0)
						listAvailable.getItems().add(item);
			} else if (n==option3) {
				optionPane.getChildren().clear();
				optionPane.getChildren().add(btnAdd);
				listAvailable.getItems().addAll(parent.getData().getItems());
			}
		});
		NavigButtonControl control = new NavigButtonControl();
		btnAdd.setOnAction(event -> {
			SelectItemDialogScreen select = new SelectItemDialogScreen(parent.getManager(), ref.getValue(), ref);
			select.startListenForEvents();
//			CloseType closed = (CloseType) parent.getManager().showAndWait(select);
			CloseType closed = (CloseType) parent.getManager().showAlertAndCall(AlertType.QUESTION, select.getTitle(), select.getContent());
			select.stopListenForEvents();
			if (closed==CloseType.OK) {
				CarriedItem ref2 = select.getSelectedItem();
				logger.info("Selected item had Q"+ref2.getItemQuality()+"/"+ref2.getArtifactQuality()+" and was "+ref2);
				parent.getData().addItem(ref2);
				listAvailable.getItems().add(ref2);
				listAvailable.getSelectionModel().select(ref2);
				control.fireEvent(CloseType.OK, event);

			}
		});
		listAvailable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> control.setDisabled(CloseType.OK, n==null));

		// Default selection
		group.selectToggle(option2);

		/*
		 * Layout
		 */
		VBox optionLine = new VBox(5);
		optionLine.getChildren().addAll(option1, option2, option3);

		VBox content = new VBox(20);
		content.getChildren().addAll(lblExplain, optionLine, optionPane);

		CloseType close = parent.getManager().showAlertAndCall(
				AlertType.QUESTION,
				heading,
				content,
				control);
		if (close==CloseType.OK) {
			CarriedItem item = listAvailable.getSelectionModel().getSelectedItem();
			logger.debug("Closed with item "+item);
			if (item!=null) {
				if (group.getSelectedToggle()==option2) {
					// Levelling the item
					item.setResource(ref);
					EditItemScreen screen = new EditItemScreen();
					screen.setData(parent.getData(), new ItemLevellerAndGenerator(item, ref.getValue()));
					CloseType closed = (CloseType) parent.getManager().showAndWait(screen);
					logger.warn("TODO: closed with "+closed);
//					GenerationEventDispatcher.removeListener(pane);
					if (closed==CloseType.OK) {
						control.fireEvent(CloseType.OK, new ActionEvent());
					}

				}

				ref.setIdReference(item.getUniqueId());
				ref.setDescription(item.getName());
				tfDescr.setText(item.getName());

				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, parent.getCharacter()));
			}
		}

	}

	//-------------------------------------------------------------------
	private void editClickedCreature(ResourceReference ref) {
		logger.debug("editClickedCreature");
		
		/*
		 * Build list of creatures already added to character that have
		 * a matching creature feature level
		 */
		List<CreatureReference> matching = new ArrayList<>();
		for (CreatureReference tmp : parent.getData().getCreatures()) {
			for (CreatureFeature feat : tmp.getFeatures()) {
				if (feat.getType()==SplitterMondCore.getCreatureFeatureType("CREATURE") && feat.getLevel()==ref.getValue()) {
					matching.add(tmp);
					break;
				}
			}
		}

		/*
		 * Build list of new creatures that might be added
		 */
		List<CreatureReference> possible = new ArrayList<>();
		for (Creature real : SplitterMondCore.getCreatures(SplitterMondCore.getCreatureFeatureType("CREATURE"))) {
			for (CreatureFeature feat : real.getFeatures()) {
				if (feat.getType()==SplitterMondCore.getCreatureFeatureType("CREATURE") && feat.getLevel()==ref.getValue()) {
					possible.add(new CreatureReference(real));
					break;
				}
			}
		}


		String heading = UI.getString("resourcelistview.creaturedialog.title");
		String explain = UI.getString("resourcelistview.creaturedialog.descr");

		// Description
		Label lblExplain = new Label(String.format(explain, ref.getValue()));
		lblExplain.setWrapText(true);

		ToggleGroup tgOptions = new ToggleGroup();
		GridPane grid = new GridPane();

		/*
		 * Option 1: Select a predefined creature
		 */
		RadioButton option1 = new RadioButton(UI.getString("resourcelistview.creaturedialog.option.choose_mine"));
		tgOptions.getToggles().add(option1);

		ChoiceBox<CreatureReference> cbPossible = new ChoiceBox<>();
		cbPossible.setConverter(new StringConverter<CreatureReference>() {
			public String toString(CreatureReference object) { return object.getName();}
			public CreatureReference fromString(String string) { return null;}
		});
		cbPossible.getItems().addAll(matching);
		if (matching.isEmpty())
			option1.setDisable(true);
//		Button btnAdd = new Button(UI.getString("button.add"));
//		btnAdd.setDisable(true);

		/*
		 * Option 2
		 */
		RadioButton option2 = new RadioButton(UI.getString("resourcelistview.creaturedialog.option.choose_predefined"));
		tgOptions.getToggles().add(option2);

		ChoiceBox<CreatureReference> listAvailable = new ChoiceBox<CreatureReference>();
		listAvailable.getItems().addAll(matching);
//		listAvailable.setStyle("-fx-max-width: 20em");
//		listAvailable.setStyle("-fx-pref-height: 10em");
		listAvailable.setConverter(new StringConverter<CreatureReference>() {
			public String toString(CreatureReference object) { return object.getName();}
			public CreatureReference fromString(String string) { return null;}
		});
		listAvailable.getItems().addAll(possible);
		if (possible.isEmpty())
			option2.setDisable(true);
///		listAvailable.setCellFactory(new Callback<ListView<CreatureReference>, ListCell<CreatureReference>>() {
//			public ListCell<CreatureReference> call(ListView<CreatureReference> param) {
//				return new ListCell<CreatureReference>() {
//					@Override
//					public void updateItem(CreatureReference item, boolean empty) {
//						super.updateItem(item, empty);
//						setText( empty?null:item.getTemplate().getName());
//					}
//				};
//			}
//		});

		/*
		 * Option 3
		 */
		RadioButton option3 = new RadioButton(UI.getString("resourcelistview.creaturedialog.option.build_new"));
		tgOptions.getToggles().add(option3);
		if (ref.getValue()<2)
			option3.setDisable(true);
		Label lblNameBuilt = new Label();
		Button btnBuild = new Button(String.format(UI.getString("resourcelistview.creaturedialog.option.build_new.button"), ref.getValue()));
		btnBuild.getStyleClass().add("bordered");
		HBox line3 = new HBox(10, lblNameBuilt, btnBuild);
		line3.setAlignment(Pos.CENTER_LEFT);



		/*
		 * Interactivity
		 */
		tgOptions.selectedToggleProperty().addListener( (ov,o,n) -> {
			cbPossible.setDisable(true);
			listAvailable.setDisable(true);
			btnBuild.setDisable(true);
			if (n==option1) {
				cbPossible.setDisable(false);
			} else if (n==option2) {
				listAvailable.setDisable(false);
			} else if (n==option3) {
				btnBuild.setDisable(false);
			}
		});
		tgOptions.selectToggle(option1);
		cbPossible.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				item = n;
			}
		});
		listAvailable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				item = n;
			}
		});
		
		btnBuild.setOnAction(event -> {
			logger.debug("Build new creature");
			CreatureGenerator ctrl = new CreatureGenerator(ref);
			CreatureCreateScreen screen = new CreatureCreateScreen(ctrl);
			CloseType result = (CloseType) parent.getScreenManager().showAndWait(screen);
			logger.debug("Result = "+result);
			if (result==CloseType.APPLY) {
				ModuleBasedCreature toAdd = ctrl.getCreature().getModuleBasedCreature();
				logger.info("Created "+toAdd);
				item = ctrl.getCreature();
				item.setResource(data);
				item.setUniqueId(ref.getIdReference());
				askName(item);
				parent.getCharacter().addCreature(item);

				lblNameBuilt.setText(item.getName());
				data.setDescription(item.getName());
			}
		});
		
//		cbPossible.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnAdd.setDisable(n==null));
//		btnAdd.setOnAction(event -> {
//			CreatureReference ref2 = cbPossible.getSelectionModel().getSelectedItem();
//			parent.getData().addCreature(ref2);
//			listAvailable.getItems().add(ref2);
//			listAvailable.getSelectionModel().select(ref2);
//		});

		/*
		 * Layout
		 */
		grid.setVgap(10);
		grid.setHgap(10);
		grid.add(option1   , 0, 0);
		grid.add(cbPossible, 1, 0);
		grid.add(option2   , 0, 1);
		grid.add(listAvailable, 1, 1);
		grid.add(option3   , 0, 2);
		grid.add(line3     , 1, 2);

		//		HBox addLine = new HBox(5);
//		addLine.getChildren().addAll(cbPossible, btnAdd);

		VBox content = new VBox(20);
		content.getChildren().addAll(lblExplain, grid);
		content.setStyle("-fx-max-width: 40em");

		CloseType close = parent.getManager().showAlertAndCall(
				AlertType.QUESTION,
				heading,
				content);
		if (close==CloseType.OK) {
			if (item!=null) {

				logger.info("Selected creature "+item);
				ref.setIdReference(item.getUniqueId());
				ref.setDescription(item.getName());
				item.setResource(data);
				tfDescr.setText(item.getName());

				logger.info("Add creature "+item.getName()+" to character");
				parent.getCharacter().addCreature(item);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, parent.getCharacter()));
			}
		}

	}

	//-------------------------------------------------------------------
	private void askName(CreatureReference ref) {
		Label lbInput = new Label(UI.getString("screen.creatures.namedialog.mess"));
		TextField tfInput = new TextField();
		tfInput.setStyle("-fx-pref-width: 30em");
		
		VBox layout = new VBox(5, lbInput, tfInput);
		CloseType close = parent.getManager().showAlertAndCall(AlertType.QUESTION, UI.getString("screen.creatures.namedialog.title"), layout);
		if (close==CloseType.OK) {
			String name = tfInput.getText();
			logger.info("Rename creature "+ref+" to \""+name+"\"");
			ref.setName(name);
		}
	}

	//-------------------------------------------------------------------
	private void editClicked(ResourceReference ref) {
		String res = ref.getResource().getId();
		if (res.equals("creature")) {
			editClickedCreature(ref);
		} else
		if (res.equals("relic")) {
			editClickedRelic(ref);
		} else {
			TextField tf = new TextField(ref.getDescription());
			tf.textProperty().addListener( (ov,o,n) -> {
				if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); tf.setText(n); }
				if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); tf.setText(n); }
				if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); tf.setText(n); }
				if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); tf.setText(n); }
			});
			tf.setOnAction(event -> {
				ManagedScreen screen = (ManagedScreen) tf.getParent().getParent().getParent().getParent();
				logger.debug("Action on "+screen);
				screen.impl_navigClicked(CloseType.OK, event);
				});
			CloseType close = parent.getManager().showAlertAndCall(
					AlertType.QUESTION,
					UI.getString("resourcelistview.namedialog.title"),
					tf);
			if (close==CloseType.OK) {
				ref.setDescription(tf.getText());
				tfDescr.setText(tf.getText());
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(ResourceReference resRef, boolean empty) {
		super.updateItem(resRef, empty);

		if (empty) {
			setGraphic(null);
			name.setText(null);
//			setStyle("-fx-border: 0px; -fx-border-color: transparent; -fx-padding: 2px; -fx-background-color: transparent");
			return;
		} else {
			data = resRef;

			setGraphic(layout);
			name.setText(resRef.getResource().getName());
			tfDescr.setText(resRef.getDescription());
			lblVal.setText(" "+String.valueOf(resRef.getValue())+" ");
			btnDec.setDisable(!charGen.canBeDecreased(resRef));
			btnInc.setDisable(!charGen.canBeIncreased(resRef));
//			if (resRef.getSkillSpecialization()!=null)
//				name.setText(resRef.getSkillSpecialization().getSkill().getName()+"/"+resRef.getSkillSpecialization().getName()+" +1");
//			if (charGen.canBeDeselected(item)) {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("selectable-list-item");
//			} else {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("unselectable-list-item");
//			}
		}

	}

}

