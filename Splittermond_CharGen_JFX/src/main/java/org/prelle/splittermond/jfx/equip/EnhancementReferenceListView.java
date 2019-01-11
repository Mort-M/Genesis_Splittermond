/**
 * 
 */
package org.prelle.splittermond.jfx.equip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellSchoolEntry;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.ItemTypeData;
import org.prelle.splimo.items.Weapon;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class EnhancementReferenceListView extends ListView<EnhancementReference> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private NewItemController control;
	private ScreenManagerProvider managerProvider;
	
	//--------------------------------------------------------------------
	public EnhancementReferenceListView(NewItemController control, ScreenManagerProvider provider) {
		this.control = control;
		this.managerProvider = provider;
		
		initComponents();
		initValueFactories();
		initInteractivity();
	}
	
	//--------------------------------------------------------------------
	public void updateItemController(NewItemController control) {
		this.control = control;
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setPlaceholder(new Text(UI.getString("placeholder.enhancement")));
        setStyle("-fx-pref-width: 20em; -fx-background-color: transparent; -fx-border-width: 1px; -fx-border-color: black;");
	}

	//-------------------------------------------------------------------
	private void initValueFactories() {
		setCellFactory(new Callback<ListView<EnhancementReference>, ListCell<EnhancementReference>>() {
			public ListCell<EnhancementReference> call(ListView<EnhancementReference> p) {
				return new EnhancementReferenceListCell(control);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		setOnDragDropped(event -> dragDropped(event));
		setOnDragOver(event -> dragOver(event));
	}
	
	//-------------------------------------------------------------------
	private SkillSpecialization selectSkillAndSpecialization() {
		logger.debug("request skill and specialization");
		ItemTemplate template = control.getItem().getItem();

		ChoiceBox<Skill> cbSkills = new ChoiceBox<>();
		cbSkills.setConverter(new StringConverter<Skill>() {
			public String toString(Skill object) { return object.getName(); }
			public Skill fromString(String string) { return null; }
		});
		cbSkills.getItems().addAll(SplitterMondCore.getSkills(SkillType.NORMAL));
		cbSkills.getItems().addAll(SplitterMondCore.getSkills(SkillType.MAGIC));
		ChoiceBox<SkillSpecialization> cbSpecs = new ChoiceBox<>();
		cbSpecs.setConverter(new StringConverter<SkillSpecialization>() {
			public String toString(SkillSpecialization object) { return object.getName(); }
			public SkillSpecialization fromString(String string) { return null; }
		});
		cbSkills.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			cbSpecs.getItems().clear();
			cbSpecs.getItems().addAll(n.getSpecializations());
			Collections.sort(cbSpecs.getItems());
			cbSpecs.getSelectionModel().select(0);
		});
		// If there already is a skill assigned, use it
		if (template.getSkill()!=null) {
			cbSkills.getSelectionModel().select(template.getSkill());				
		} else {
			cbSkills.getSelectionModel().select(0);
		}



		Label lbSkill   = new Label(UI.getString("label.skill"));
		Label lbSpecial = new Label(UI.getString("label.specialization"));

		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(5);
		pane.add(lbSkill  , 0, 0);
		pane.add(cbSkills , 1, 0);
		pane.add(lbSpecial, 0, 1);
		pane.add(cbSpecs  , 1, 1);
		ScreenManager manager = managerProvider.getScreenManager();
		CloseType close = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("dialog.selectSpecializationEnhancement"), pane);
		if (close!=CloseType.OK)
			return null;
		
		if (cbSpecs.getValue().getType()==SkillSpecializationType.SPELLTYPE) {
			logger.debug("Special treatment for spell types");
		  return new SkillSpecialization(cbSkills.getValue(), cbSpecs.getValue().getType(), cbSkills.getValue().getId()+"/"+cbSpecs.getValue().getId());
		}
		return cbSpecs.getValue();
	}
	
	//-------------------------------------------------------------------
	private SkillSpecialization selectSpecialization() {
		logger.debug("selectSpecialization");
		ItemTemplate template = control.getItem().getItem();
		Skill skill = null;
		for (ItemTypeData data : template.getTypeData()) {
			if (data.getType() == ItemType.WEAPON || data.getType() == ItemType.LONG_RANGE_WEAPON) {
				skill = ((Weapon)data).getSkill();
				break;
			}
		}
		if (skill!=null) {
			SkillSpecialization spec = skill.getSpecialization(template.getID());
			if (spec==null) {
				logger.error("Item "+template.getID()+" is a "+template.getTypeData()+" with skill "+skill+", but there is no matching specialization in that skill");
				return null;
			}
			return spec;
		}
		
		/*
		 * Check if the item itself has a specialization
		 */
		if (template.getSpecialization()!=null) {
			return template.getSpecialization();
		}
		
		/*
		 * If there is a skill given, let user choose
		 */
		if (template.getSkill()!=null) {
			ChoiceBox<SkillSpecialization> cbSpecs = new ChoiceBox<>();
			cbSpecs.getItems().addAll(template.getSkill().getSpecializations());
			cbSpecs.setConverter(new StringConverter<SkillSpecialization>() {
				public String toString(SkillSpecialization object) { return object.getName(); }
				public SkillSpecialization fromString(String string) { return null; }
			});
			cbSpecs.getSelectionModel().select(0);
			
			Label lbSpecial = new Label(String.format(UI.getString("dialog.selectSpecializationEnhancement.desc"), template.getSkill().getName()));
			
			VBox pane = new VBox(10);
			pane.getChildren().addAll(lbSpecial, cbSpecs);
			ScreenManager manager = managerProvider.getScreenManager();
			CloseType close = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("dialog.selectSpecializationEnhancement"), pane);
			if (close!=CloseType.OK)
				return null;
			return cbSpecs.getValue();
		}		
		
		logger.warn("Don't know how to select specialization for "+template);
		ScreenManager manager = managerProvider.getScreenManager();
		manager.showAlertAndCall(AlertType.NOTIFICATION, UI.getString("error.not-possible"), UI.getString("error.no-skill-assigned"));

		return null;
	}
	
	//-------------------------------------------------------------------
	private SpellValue selectSpell(int level) {
		logger.debug("select spell");
		ItemTemplate template = control.getItem().getItem();
		
		/*
		 * Build a list of all spells that are of the requested level in at least one school
		 */
		Skill arcaneLore = SplitterMondCore.getSkill("arcanelore");
		List<SpellValue> options = new ArrayList<>();
		outer:
		for (Spell spell : SplitterMondCore.getSpells()) {
			for (SpellSchoolEntry entry : spell.getSchools()) {
				if (entry.getLevel()==level) {
					options.add(new SpellValue(spell, arcaneLore));
					continue outer;
				}
			}
		}
		// Sort that list
		Collections.sort(options);
		
		
		/*
		 * If there is a skill given, let user choose
		 */
		ChoiceBox<SpellValue> cbSpecs = new ChoiceBox<>();
		cbSpecs.getItems().addAll(options);
		cbSpecs.setConverter(new StringConverter<SpellValue>() {
			public String toString(SpellValue object) { return object.getSpell().getName(); }
			public SpellValue fromString(String string) { return null; }
		});
		cbSpecs.getSelectionModel().select(0);

		Label lbSpecial = new Label(UI.getString("dialog.selectSpellEnhancement.desc"));

		VBox pane = new VBox(10);
		pane.getChildren().addAll(lbSpecial, cbSpecs);
		ScreenManager manager = managerProvider.getScreenManager();
		CloseType close = manager.showAlertAndCall(AlertType.QUESTION, UI.getString("dialog.selectSpellEnhancement"), pane);
		if (close!=CloseType.OK)
			return null;
		return cbSpecs.getValue();
	}
	
	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString();
        	logger.debug("Dropped "+enhanceID);
        	Enhancement res = SplitterMondCore.getEnhancement(enhanceID);
        	if (res!=null && control.canBeAdded(res)) {
        		if (res.getId().equals("specialization")) {
        			// run later to prevent dragged object to be still visible in following screen
        			Platform.runLater(new Runnable(){
        				public void run() {
        					SkillSpecialization spec = selectSpecialization();
        					if (spec!=null) {
        						control.addEnhancement(res, spec);
        					} else {
        						logger.error("Cannot add specialization");
        						managerProvider.getScreenManager().showAlertAndCall(AlertType.ERROR, UI.getString("label.hint"), UI.getString("error.not-possible"));
        					}
        				}
        			});
        		} else if (res.getId().startsWith("uncommonspecialization")) {
        			// run later to prevent dragged object to be still visible in following screen
        			Platform.runLater(new Runnable(){
        				public void run() {
        					SkillSpecialization spec = selectSkillAndSpecialization();
        					if (spec!=null) {
        						control.addEnhancement(res, spec);
//        					} else {
//        						logger.error("Cannot add specialization");
//        						managerProvider.getScreenManager().showAlertAndCall(AlertType.ERROR, UI.getString("label.hint"), UI.getString("error.not-possible"));
        					}
        				}
        			});
        		} else if (res.getId().startsWith("embedspell")) {
        			int spellLevel = res.getSize()-1;
        			// run later to prevent dragged object to be still visible in following screen
        			Platform.runLater(new Runnable(){
        				public void run() {
        					SpellValue spec = selectSpell(spellLevel);
        					if (spec!=null) {
        						control.addEnhancement(res, spec);
        					} else {
        						logger.error("Cannot add spell");
        						managerProvider.getScreenManager().showAlertAndCall(AlertType.ERROR, UI.getString("label.hint"), UI.getString("error.not-possible"));
        					}
        				}
        			});
        		} else {
        			logger.info("Add direct");
        			EnhancementReference ref = control.addEnhancement(res);
        			if (ref==null) {
               			managerProvider.getScreenManager().showAlertAndCall(AlertType.NOTIFICATION, UI.getString("label.hint"), UI.getString("error.not-possible"));
        			}
        		}
        	} else
        		logger.debug("Cannot add "+res);
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

}

class EnhancementReferenceListCell extends ListCell<EnhancementReference> {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private transient EnhancementReference data;
	
	private NewItemController charGen;
	private CheckBox checkBox;
	private HBox layout;
	private Label name;
	
	//-------------------------------------------------------------------
	public EnhancementReferenceListCell(NewItemController charGen) {
		this.charGen = charGen;
		
		layout  = new HBox();
		checkBox= new CheckBox();
		name    = new Label();
		layout.getChildren().addAll(checkBox, name);
		layout.getStyleClass().add("content");
		
		name.getStyleClass().add("base");
		checkBox.getStyleClass().add("text-subheader");
		

		setPrefWidth(250);
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				EnhancementReferenceListCell.this.changed(arg0, arg1, arg2);
			}});
		

		setAlignment(Pos.CENTER);
		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
//		if (!charGen.canBeDeselected(data))
//			return;
		if (data==null)
			return;
		
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(data.getID());
        db.setContent(content);
        
        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);
        
        event.consume();	
    }

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(EnhancementReference item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			name.setText(null);
//			setStyle("-fx-border: 0px; -fx-border-color: transparent; -fx-padding: 2px; -fx-background-color: transparent");
			return;
		} else {
			data = item;
			checkBox.setSelected(true);
			checkBox.setDisable(!charGen.canBeRemoved(data));
			
			setGraphic(layout);
			name.setText(item.getName());
			if (item.getSkillSpecialization()!=null) 
				name.setText(item.getSkillSpecialization().getSkill().getName()+"/"+item.getSkillSpecialization().getName()+" +1");
			if (item.getSpellValue()!=null) 
				name.setText(String.format(UI.getString("screen.enhancements.embedspell.cell"), item.getSpellValue().getSpell().getName()));
//			if (charGen.canBeDeselected(item)) {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("selectable-list-item");
//			} else {
//				layout.getStyleClass().clear();
//				layout.getStyleClass().add("unselectable-list-item");
//			}
		}

	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			charGen.removeEnhancement(data);
		}
	}

}

