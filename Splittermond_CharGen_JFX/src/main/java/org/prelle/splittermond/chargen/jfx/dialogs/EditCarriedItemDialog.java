package org.prelle.splittermond.chargen.jfx.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.rpgframework.jfx.OptionalDescriptionPane;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellSchoolEntry;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.ItemTypeData;
import org.prelle.splimo.items.Weapon;
import org.prelle.splittermond.chargen.jfx.DescriptionBox;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.equip.ArmorDataPane;
import org.prelle.splittermond.chargen.jfx.equip.BasicItemDataPane;
import org.prelle.splittermond.chargen.jfx.equip.OtherDataPane;
import org.prelle.splittermond.chargen.jfx.equip.RangeWeaponDataPane;
import org.prelle.splittermond.chargen.jfx.equip.ShieldDataPane;
import org.prelle.splittermond.chargen.jfx.equip.WeaponDataPane;
import org.prelle.splittermond.chargen.jfx.listcells.EnhancementListCell;
import org.prelle.splittermond.chargen.jfx.listcells.EnhancementReferenceListCell;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan Prelle
 *
 */
public class EditCarriedItemDialog extends ManagedDialog implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditCarriedItemDialog.class.getName());

	//-------------------------------------------------------------------
	class BasicItemDataVBox extends VBox {
		BasicItemDataPane pane;
		public BasicItemDataVBox(NewItemController ctrl) {
			super(5);
			pane = new BasicItemDataPane(ctrl);
			pane.setStyle("-fx-padding: 0.3em"); 
			Label title = new Label(UI.getString("pane.basic"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
			VBox.setVgrow(pane, Priority.ALWAYS);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class WeaponDataVBox extends VBox {
		WeaponDataPane pane;
		public WeaponDataVBox() {
			super(5);
			pane = new WeaponDataPane();
			pane.setStyle("-fx-padding: 0.3em"); 
			Label title = new Label(UI.getString("pane.weapon"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
			VBox.setVgrow(pane, Priority.ALWAYS);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class RangeWeaponDataVBox extends VBox {
		RangeWeaponDataPane pane;
		public RangeWeaponDataVBox() {
			super(5);
			pane = new RangeWeaponDataPane();
			pane.setStyle("-fx-padding: 0.3em"); 
			Label title = new Label(UI.getString("pane.rangeweapon"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
			VBox.setVgrow(pane, Priority.ALWAYS);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class ArmorDataVBox extends VBox {
		ArmorDataPane pane;
		public ArmorDataVBox() {
			super(5);
			pane = new ArmorDataPane();
			pane.setStyle("-fx-padding: 0.3em"); 
			Label title = new Label(UI.getString("pane.armor"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
			VBox.setVgrow(pane, Priority.ALWAYS);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class ShieldDataVBox extends VBox {
		ShieldDataPane pane;
		public ShieldDataVBox() {
			super(5);
			pane = new ShieldDataPane();
			pane.setStyle("-fx-padding: 0.3em"); 
			Label title = new Label(UI.getString("pane.shield"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
			VBox.setVgrow(pane, Priority.ALWAYS);
		}
		public void setData(CarriedItem value) {
			pane.setData(value);
		}
	}

	//-------------------------------------------------------------------
	class OtherDataVBox extends VBox {
		OtherDataPane pane;
		public OtherDataVBox() {
			super(5);
			pane = new OtherDataPane();
			pane.setStyle("-fx-padding: 0.3em"); 
			Label title = new Label(UI.getString("pane.other"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
			getChildren().addAll(title, pane);
			VBox.setVgrow(pane, Priority.ALWAYS);
		}
		public void setData(CarriedItem value, NewItemController ctrl) {
			pane.setData(value, ctrl);
		}
	}

	//-------------------------------------------------------------------
	class DescriptionVBox extends VBox {
		TextArea pane;
		TextField name;
		public DescriptionVBox() {
			super(5);
			pane = new TextArea();
			pane.setStyle("-fx-pref-height: 5em; -fx-pref-width: 30em;");
			Label title = new Label(UI.getString("pane.descr"));
			title.setStyle("-fx-font-size: 150%; -fx-font-weight: bold");
			name = new TextField();
			name.setPrefColumnCount(20);
			name.setPromptText(UI.getString("field.name.prompt"));
			Label subtitle = new Label(UI.getString("label.name"));
			subtitle.getStyleClass().add("base");
			getChildren().addAll(title, pane, subtitle, name);
		}
		public void setData(CarriedItem value) {
			pane.setText(value.getDescription());
			name.setText(value.getName());
		}
		public ReadOnlyStringProperty textProperty() {
			return pane.textProperty();
		}
	}

	private CharacterController charGen;
	private NewItemController itemCtrl;

	private FlowPane flow;
	private BasicItemDataVBox paneBasic;
	private WeaponDataVBox 	  paneWeapon;
	private RangeWeaponDataVBox paneRange;
	private ShieldDataVBox 	  paneShield;
	private ArmorDataVBox 	  paneArmor;
	private OtherDataVBox 	  paneOther;
	private DescriptionVBox   paneDescr;
	
	private ChoiceBox<EnhancementType> cbType;
	private ListView<Enhancement> lvAvailable;
	private ListView<EnhancementReference> lvSelected;
	private OptionalDescriptionPane pane;
	private DescriptionBox descr;
	
	//-------------------------------------------------------------------
	/**
	 * @param title
	 * @param content
	 * @param buttons
	 */
	public EditCarriedItemDialog(CharacterController ctrl, NewItemController itemCtrl) {
		super(ctrl.getModel().getName(), null, CloseType.OK);
		this.charGen = ctrl;
		this.itemCtrl= itemCtrl;
		setTitle(String.format(UI.getString("dialog.editcarrieditem.title"), itemCtrl.getItem().getItem().getName()));
		
		initComponents();
		initLayout();
		initInteractivity();
		
		cbType.setValue(EnhancementType.NORMAL);
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		paneBasic = new BasicItemDataVBox(itemCtrl);
		paneWeapon= new WeaponDataVBox();
		paneRange = new RangeWeaponDataVBox();
		paneArmor = new ArmorDataVBox();
		paneShield= new ShieldDataVBox();
		paneOther = new OtherDataVBox();
		paneDescr = new DescriptionVBox();
		
		cbType = new ChoiceBox<>();
		cbType.getItems().addAll(EnhancementType.values());
		cbType.setConverter(new StringConverter<Enhancement.EnhancementType>() {
			public String toString(EnhancementType value) { return value.getName();}
			public EnhancementType fromString(String string) { return null; }
		});
		
		lvAvailable = new ListView<Enhancement>();
		lvSelected  = new ListView<EnhancementReference>();
		
		lvAvailable.setCellFactory(lv -> new EnhancementListCell(itemCtrl));
		lvSelected .setCellFactory(lv -> new EnhancementReferenceListCell(itemCtrl));
		
		Label ph = new Label(UI.getString("enhancements.selected.placeholder"));
		ph.setWrapText(true);
		lvSelected.setPlaceholder(ph);
		
		descr = new DescriptionBox();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		lvAvailable.setStyle("-fx-pref-width: 25em");
		lvSelected.setStyle("-fx-pref-width: 25em");
		
		flow = new FlowPane(paneBasic, paneDescr);
		flow.setStyle("-fx-hgap: 0.5em");
		
		Label hdAvailable = new Label(UI.getString("label.available"));
		Label hdSelected = new Label(UI.getString("label.selected"));
		Label hdDescript = new Label(UI.getString("label.description"));
		hdAvailable.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
		hdSelected.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
		hdDescript.setStyle("-fx-font-size: 150%; -fx-font-weight: bold"); 
		VBox col1 = new VBox(5, hdAvailable, cbType, lvAvailable);
		VBox col2 = new VBox(5, hdSelected, lvSelected);
		VBox col3 = new VBox(5, hdDescript, descr);
		VBox.setVgrow(lvAvailable, Priority.ALWAYS);
		VBox.setVgrow(lvSelected, Priority.ALWAYS);
		VBox.setVgrow(descr, Priority.ALWAYS);
		
		HBox bxLists = new HBox(col1, col2);
		bxLists.setStyle("-fx-spacing: 1em");
		
		pane = new OptionalDescriptionPane(bxLists, col3);
		VBox layout = new VBox(flow, pane);
		layout.setStyle("-fx-spacing: 1em");
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		lvAvailable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> descr.showData(n));
		lvSelected.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null)
				descr.showData(n.getEnhancement());
			else
				descr.showData(null);
			});
		lvSelected.setOnDragDropped(ev -> dragDropped(ev));
		lvSelected.setOnDragOver(event -> dragOver(event));
		
		cbType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			lvAvailable.getItems().clear();
			if (n!=null) {
				lvAvailable.getItems().addAll(itemCtrl.getAvailableEnhancements(n));
			}
		});
		
		paneDescr.textProperty().addListener( (ov,o,n) -> itemCtrl.getItem().setDescription(n));
	}

	//-------------------------------------------------------------------
	public void refresh() {
		CarriedItem value = itemCtrl.getItem();
		paneBasic.setData(value);
		paneDescr.setData(value);
		paneWeapon.setData(value);
		paneRange.setData(value);
		paneArmor.setData(value);
		paneShield.setData(value);
		paneOther.setData(value, itemCtrl);
		paneDescr.setData(value);
		
		flow.getChildren().retainAll(paneBasic);
		if (value.isType(ItemType.WEAPON))
			flow.getChildren().add(paneWeapon);
		if (value.isType(ItemType.LONG_RANGE_WEAPON))
			flow.getChildren().add(paneRange);
		if (value.isType(ItemType.ARMOR))
			flow.getChildren().add(paneArmor);
		if (value.isType(ItemType.SHIELD))
			flow.getChildren().add(paneShield);
		flow.getChildren().add(paneOther);
		flow.getChildren().add(paneDescr);
		
		lvSelected.getItems().clear();
		lvSelected.getItems().addAll(value.getEnhancements());
	}
	
	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString();
        	logger.info("Dropped "+enhanceID);
        	Enhancement res = SplitterMondCore.getEnhancement(enhanceID);
        	if (res!=null && itemCtrl.canBeAdded(res)) {
        		if (res.getId().equals("specialization")) {
        			// run later to prevent dragged object to be still visible in following screen
        			Platform.runLater(new Runnable(){
        				public void run() {
        					SkillSpecialization spec = selectSpecialization();
        					if (spec!=null) {
        						itemCtrl.addEnhancement(res, spec);
        						refresh();
       					} else {
        						logger.error("Cannot add specialization");
        						getScreenManager().showAlertAndCall(AlertType.ERROR, UI.getString("label.hint"), UI.getString("error.not-possible"));
        					}
        				}
        			});
        		} else if (res.getId().startsWith("uncommonspecialization")) {
        			// run later to prevent dragged object to be still visible in following screen
        			Platform.runLater(new Runnable(){
        				public void run() {
        					SkillSpecialization spec = selectSkillAndSpecialization();
        					if (spec!=null) {
        						itemCtrl.addEnhancement(res, spec);
        						refresh();
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
        						itemCtrl.addEnhancement(res, spec);
        						refresh();
        					} else {
        						logger.error("Cannot add spell");
        						getScreenManager().showAlertAndCall(AlertType.ERROR, UI.getString("label.hint"), UI.getString("error.not-possible"));
        					}
        				}
        			});
        		} else {
        			logger.info("Add direct");
        			EnhancementReference ref = itemCtrl.addEnhancement(res);
					refresh();
        			if (ref==null) {
               			getScreenManager().showAlertAndCall(AlertType.NOTIFICATION, UI.getString("label.hint"), UI.getString("error.not-possible"));
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

	//-------------------------------------------------------------------
	private SkillSpecialization selectSkillAndSpecialization() {
		logger.debug("request skill and specialization");
		ItemTemplate template = itemCtrl.getItem().getItem();

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
		CloseType close = getScreenManager().showAlertAndCall(AlertType.QUESTION, UI.getString("dialog.selectSpecializationEnhancement"), pane);
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
		ItemTemplate template = itemCtrl.getItem().getItem();
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
			CloseType close = getScreenManager().showAlertAndCall(AlertType.QUESTION, UI.getString("dialog.selectSpecializationEnhancement"), pane);
			if (close!=CloseType.OK)
				return null;
			return cbSpecs.getValue();
		}

		logger.warn("Don't know how to select specialization for "+template);
		getScreenManager().showAlertAndCall(AlertType.NOTIFICATION, UI.getString("error.not-possible"), UI.getString("error.no-skill-assigned"));

		return null;
	}

	//-------------------------------------------------------------------
	private SpellValue selectSpell(int level) {
		logger.debug("select spell");
		ItemTemplate template = itemCtrl.getItem().getItem();

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
		CloseType close = getScreenManager().showAlertAndCall(AlertType.QUESTION, UI.getString("dialog.selectSpellEnhancement"), pane);
		if (close!=CloseType.OK)
			return null;
		return cbSpecs.getValue();
	}

	//-------------------------------------------------------------------
	/**
	 * @param event
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.ITEM_CHANGED) {
			refresh();
		}
	}

}
