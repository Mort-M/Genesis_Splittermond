package org.prelle.splittermond.chargen.jfx.dialogs;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AttentionPane;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.CommandBar;
import org.prelle.javafx.ManagedDialog;
import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.rpgframework.jfx.HelpTextPane;
import org.prelle.rpgframework.jfx.ThreeColumnPane;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipOrSpecialization;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.persist.MastershipConverter;
import org.prelle.splimo.persist.SpecializationConverter;
import org.prelle.splittermond.chargen.jfx.ExpLine;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.listcells.MastershipListCell;
import org.prelle.splittermond.chargen.jfx.listcells.MastershipReferenceListCell;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.core.RoleplayingSystem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MastershipDialog extends ManagedDialog implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(MastershipDialog.class.getName());

	private final static MastershipConverter CONVERT_MASTER = new MastershipConverter();
	private final static SpecializationConverter CONVERT_SPECIAL = new SpecializationConverter();

	private MastershipController control;
	private CharacterController charGen;
	private SkillValue sVal;

	private RadioButton rbMaster;
	private RadioButton rbSpecial;
	private ToggleGroup toggle;
	private TextField tfSearch;
	private ListView<MastershipOrSpecialization> lvAvailable;
	private ListView<MastershipReference> lvSelected;
	private AttentionPane attention;
	private HelpTextPane description;
//	private Label lblName;
//	private Label lblProduct;
//	private Label lblDescr;

	private FreePointsNode freePoints;
	private CommandBar commands;
	private ExpLine firstLine;

	//-------------------------------------------------------------------
	public MastershipDialog(CharacterController ctrl) {
		super(ctrl.getModel().getName(), null, CloseType.OK);
		this.charGen = ctrl;
		this.control = ctrl.getMastershipController();

		initComponents();
		initLayout();
		initInteractivity();
		setTitle(UI.getString("dialog.masterships.title"));

		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
//		setTitle(type.getName());

		lvAvailable = new ListView<MastershipOrSpecialization>();
		lvAvailable.setCellFactory( listView -> new MastershipListCell(control));
		lvSelected = new ListView<MastershipReference>();
		lvSelected.setCellFactory( listView -> new MastershipReferenceListCell(control));

		description = new HelpTextPane();
//		lblName = new Label();
//		lblName.getStyleClass().add("text-subheader");
//		lblProduct = new Label();
//		lblDescr = new Label();
//		lblDescr.setWrapText(true);

		toggle = new ToggleGroup();
		rbMaster = new RadioButton(UI.getString("label.masterships"));
		rbMaster.setToggleGroup(toggle);
		rbSpecial = new RadioButton(UI.getString("label.specializations"));
		rbSpecial.setToggleGroup(toggle);
		toggle.selectToggle(rbMaster);

		tfSearch = new TextField();
		tfSearch.setStyle("-fx-pref-width: 20em");

		/*
		 * Exp & Co.
		 */
		freePoints = new FreePointsNode();
		freePoints.setStyle("-fx-max-height: 3em; -fx-max-width: 3em");
		freePoints.setPoints(charGen.getModel().getExperienceFree());
		freePoints.setName(UI.getString("label.ep.free"));
		
		firstLine = new ExpLine();
		firstLine.setData(charGen.getModel());

		commands = new CommandBar();
		commands.setContent(firstLine);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label lblIntro = new Label(UI.getString("screen.masterships.master_or_special"));
		HBox lineQuestion = new HBox(10);
		lineQuestion.getChildren().addAll(lblIntro, rbMaster, rbSpecial);

		Label lblSearch = new Label(UI.getString("screen.masterships.search"));
		HBox lineSearch = new HBox(10);
		lineSearch.getChildren().addAll(lblSearch, tfSearch);


//		VBox bxDescr = new VBox(lblName, lblProduct, lblDescr);
//		VBox.setMargin(lblDescr, new Insets(20, 20, 0, 0));
		VBox.setMargin(description, new Insets(20, 20, 0, 0));
		attention  = new AttentionPane(lvAvailable, Pos.TOP_RIGHT);

		lvAvailable.setStyle("-fx-pref-width: 25em");
//		bxDescr.setStyle("-fx-pref-width: 25em");
//		lblDescr.setStyle("-fx-pref-width: 25em");
		description.setStyle("-fx-pref-width: 25em");

		ThreeColumnPane threeCol = new ThreeColumnPane();

		threeCol.setHeadersVisible(true);
		threeCol.setColumn1Header(UI.getString("label.available"));
		threeCol.setColumn2Header(UI.getString("label.selected"));
		threeCol.setColumn3Header(UI.getString("label.description"));

		threeCol.setColumn1Node(attention);
		threeCol.setColumn2Node(lvSelected);
//		threeCol.setColumn3Node(bxDescr);
		threeCol.setColumn3Node(description);
		threeCol.setMaxHeight(Double.MAX_VALUE);

		VBox box = new VBox();
		box.setStyle("-fx-spacing: 1em");
		box.getChildren().addAll(lineQuestion, lineSearch, threeCol);
		VBox.setVgrow(threeCol, Priority.ALWAYS);

		HBox flow = new HBox();
		flow.setSpacing(20);
		flow.getChildren().addAll(freePoints, box);
		HBox.setMargin(freePoints, new Insets(0,0,20,0));
		HBox.setMargin(box, new Insets(0,0,20,0));
		HBox.setHgrow(threeCol, Priority.ALWAYS);
		flow.getStyleClass().add("text-body");

		VBox content = new VBox();
		content.setSpacing(20);
		content.getChildren().addAll(firstLine, flow);
		VBox.setVgrow(flow, Priority.ALWAYS);
		VBox.setMargin(flow  , new Insets(0,0,20,0));
		setContent(content);
		content.getStyleClass().add("mastership-screen");

		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		lvAvailable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> mastershipSelected(n));
		lvSelected.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null)
				mastershipSelected(n.getMastership());
			else
				mastershipSelected(null);
		});
		toggle.selectedToggleProperty().addListener( (ov,o,n) -> refresh());

		lvAvailable.setOnDragDropped(event -> dragDroppedSelected(event));
		lvAvailable.setOnDragOver   (event -> dragOverSelected(event));
		lvSelected.setOnDragDropped (event -> dragDroppedAvailable(event));
		lvSelected.setOnDragOver    (event -> dragOverAvailable(event));

		tfSearch.textProperty().addListener((observable, oldValue, newValue) -> refresh());
	}

	//-------------------------------------------------------------------
	private void mastershipSelected(MastershipOrSpecialization selected) {
		logger.debug("mastershipSelected: "+selected);
		if (selected==null) {
			description.setData(null);
//			lblName.setText(null);
//			lblProduct.setText(null);
//			lblDescr.setText(null);
			return;
		}

		if (selected instanceof Mastership) {
			Mastership master = (Mastership)selected;
			boolean hasLicense = RPGFrameworkLoader.getInstance().getLicenseManager().hasLicense(RoleplayingSystem.SPLITTERMOND, master.getPlugin().getID());
			description.setData(master);
//			lblName.setText(master.getName());
//			lblProduct.setText(master.getProductName()+" "+master.getPage());
//			if (hasLicense)
//				lblDescr.setText(master.getHelpText());
//			else
//				lblDescr.setText(UI.getString("warning.license"));
		} else {
			SkillSpecialization special = (SkillSpecialization)selected;
			description.setData(special.getName(), selected.getSkill().getProductName()+" "+selected.getSkill().getPage(), null);
//			lblName.setText(special.getName());
//			lblProduct.setText(selected.getSkill().getProductName()+" "+selected.getSkill().getPage());
//			lblDescr.setText(null);
			if (special.getType()==SkillSpecializationType.SPELLTYPE) {
				Skill skill = special.getSkill();
				// Build list of spelly with that type in that school
				SpellType sType = SpellType.valueOf(special.getId());
				List<Spell> spells = new ArrayList<Spell>();
				for (Spell spell : SplitterMondCore.getSpells(skill)) {
					if (spell.getTypes().contains(sType))
						spells.add(spell);
				}
				// Sort spells
				Collections.sort(spells, new Comparator<Spell>() {
					public int compare(Spell o1, Spell o2) {
						int cmp = ((Integer)o1.getLevelInSchool(skill)).compareTo(o2.getLevelInSchool(skill));
						if (cmp!=0)
							return cmp;
						return Collator.getInstance().compare(o1.getName(), o2.getName());
					}
				});
				// Build text
				StringBuffer buf = new StringBuffer();
				spells.forEach( spell -> buf.append(spell.getLevelInSchool(skill)+" "+spell.getName()+"\n"));
//				lblDescr.setText(buf.toString());
				description.setData(special.getName(), selected.getSkill().getProductName()+" "+selected.getSkill().getPage(), buf.toString());
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case MASTERSHIP_ADDED:
		case MASTERSHIP_CHANGED:
		case MASTERSHIP_REMOVED:
			refresh();
			break;
		case EXPERIENCE_CHANGED:
			firstLine.setData(charGen.getModel());
			freePoints.setPoints(charGen.getModel().getExperienceFree());
			break;
		case POINTS_LEFT_MASTERSHIPS:
		default:
		}
	}

	//-------------------------------------------------------------------
	private void refresh() {
		rbSpecial.setDisable(sVal.getSkill().isGrouped());

		lvAvailable.getItems().clear();
		lvSelected.getItems().clear();

		// Available
		List<MastershipOrSpecialization> toAdd = new ArrayList<MastershipOrSpecialization>() ;
		String normalizedFilter = MastershipSearchFilter.normalize(tfSearch.getText());
		if (rbMaster.isSelected()) {
			for (Mastership tmp : sVal.getSkill().getMasterships()) {
				// For grouped skills, ignore "journeyman", "expert" and "master"
				if (sVal.getSkill().isGrouped()) {
					if (tmp.getId().equals("journeyman") || tmp.getId().equals("expert"))
						continue;
				}
				if ((!sVal.hasMastership(tmp) || tmp.isMultiple())
						&& MastershipSearchFilter.matches(tmp, normalizedFilter))
					toAdd.add(tmp);
			}
		}
		if (rbSpecial.isSelected()) {
			for (SkillSpecialization tmp : sVal.getSkill().getSpecializations()) {
				if (sVal.getSpecializationLevel(tmp)==0
						&& MastershipSearchFilter.matches(tmp, normalizedFilter))
					toAdd.add(tmp);
			}
		}
		Collections.sort(toAdd);
		lvAvailable.getItems().addAll(toAdd);

		// Selected
		lvSelected.getItems().addAll(sVal.getMasterships());

		List<String> toDos = control.getToDos(sVal.getSkill());
		logger.debug("TODOs for "+sVal+" = "+toDos);
		attention.setAttentionFlag(toDos.size()>0);
		attention.setAttentionToolTip(toDos);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model, SkillValue sVal) {
//		this.model = model;
		this.sVal  = sVal;

		refresh();
		firstLine.setData(charGen.getModel());
		freePoints.setPoints(charGen.getModel().getExperienceFree());
		setTitle(sVal.getSkill().getName()+"/"+UI.getString("dialog.masterships.title"));
	}

	//-------------------------------------------------------------------
	public boolean close(CloseType close) {
		GenerationEventDispatcher.removeListener(this);
		return true;
	}

	//-------------------------------------------------------------------
	/*
	 * Select
	 */
	private void dragDroppedAvailable(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
//        logger.warn("dragDroppedAvailable "+event);
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString();
        	logger.debug("Dropped "+enhanceID);
        	// Get reference for ID
        	if (enhanceID.startsWith("master:select:")) {
        		String id = enhanceID.substring("master:select:".length());
        		Mastership master = CONVERT_MASTER.read(id);
         		if (master!=null) {
        			logger.info("Select mastership "+master);
        			control.select(master);
        		} else {
        			logger.warn("Cannot select unknown mastership: "+enhanceID);
        		}
        	} else
        	if (enhanceID.startsWith("special:select:")) {
        		String id = enhanceID.substring("special:select:".length());
        		SkillSpecialization special = CONVERT_SPECIAL.read(id);
         		if (special!=null) {
        			logger.info("Select skill specialization "+special);
        			control.select(special, 1);
        		} else {
        			logger.warn("Cannot select unknown skill specialization: "+enhanceID);
        		}
        	}
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
	}

	//-------------------------------------------------------------------
	/*
	 * Select
	 */
	private void dragOverAvailable(DragEvent event) {
//        logger.warn("dragOverAvailable "+event);
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            String enhanceID = event.getDragboard().getString();
        	// Get reference for ID
        	if (enhanceID.startsWith("master:select:")) {
        		String id = enhanceID.substring("master:select:".length());
        		Mastership master = CONVERT_MASTER.read(id);
         		if (master!=null) {
        			if (control.canBeSelected(master))
        	            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        			else
            			logger.warn("Dragged unselectable mastership "+master);
        		}

        	} else
        	if (enhanceID.startsWith("special:select:")) {
        		String id = enhanceID.substring("special:select:".length());
        		SkillSpecialization special = CONVERT_SPECIAL.read(id);
         		if (special!=null) {
        			if (control.isEditable(special, 1))
        	            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        			else
            			logger.warn("Dragged unselectable skill specialization "+special);
        		}
        	}
        }
	}

	//-------------------------------------------------------------------
	/*
	 * Deselect
	 */
	private void dragDroppedSelected(DragEvent event) {
       /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
//        logger.warn("dragDroppedAvailable "+event);
        boolean success = false;
        if (db.hasString()) {
            String enhanceID = db.getString();
        	logger.debug("Dropped "+enhanceID);
        	// Get reference for ID
        	if (enhanceID.startsWith("master:deselect:")) {
        		String id = enhanceID.substring("master:deselect:".length());
        		Mastership master = CONVERT_MASTER.read(id);
         		if (master!=null) {
        			logger.info("Deselect mastership "+master);
        			control.deselect(master);
        		} else {
        			logger.warn("Cannot deselect unknown mastership: "+enhanceID);
        		}
        	} else
        	if (enhanceID.startsWith("special:deselect:")) {
        		String id = enhanceID.substring("special:deselect:".length());
        		SkillSpecialization special = CONVERT_SPECIAL.read(id);
        		int level = Integer.parseInt(id.substring(id.lastIndexOf("/")+1));
         		if (special!=null) {
        			logger.info("Deselect mastership "+special);
        			control.deselect(special,level);
        		} else {
        			logger.warn("Cannot deselect unknown mastership: "+enhanceID);
        		}
        	}
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
	}

	//-------------------------------------------------------------------
	/*
	 * Deselect
	 */
	private void dragOverSelected(DragEvent event) {
        logger.warn("dragOverSelected "+event);
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            String enhanceID = event.getDragboard().getString();
        	// Get reference for ID
        	if (enhanceID.startsWith("master:deselect:")) {
        		String id = enhanceID.substring("master:deselect:".length());
        		Mastership master = CONVERT_MASTER.read(id);
         		if (master!=null) {
        			if (control.canBeDeselected(master))
        	            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        		}

        	} else
        	if (enhanceID.startsWith("special:deselect:")) {
        		String id = enhanceID.substring("special:deselect:".length());
        		SkillSpecialization master = CONVERT_SPECIAL.read(id);
        		int level = Integer.parseInt(id.substring(id.lastIndexOf("/")+1));

         		if (master!=null) {
        			if (control.isEditable(master, level))
        	            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        		}

        	}
        }
	}

}
