/**
 * 
 */
package org.prelle.splittermond.jfx.spells;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ResponsiveVBox;
import org.prelle.javafx.TriStateCheckBox;
import org.prelle.javafx.TriStateCheckBox.State;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.fluent.CommandBar;
import org.prelle.javafx.fluent.NodeWithTitle;
import org.prelle.rpgframework.jfx.AttentionPane;
import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.rpgframework.jfx.SettingsAndCommandBar;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.charctrl.SpellController.FreeSelection;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.jfx.skills.ListElemSpecialization;
import org.prelle.splittermond.jfx.skills.SkillSpecListCell;
import org.prelle.splittermond.jfx.spells.SpellScreen.SelectionOption;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SpellScreen extends ManagedScreen implements GenerationEventListener, NodeWithTitle {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;
	
	// Used as user data for slider
	class SelectionOption {
		Spell spell;
		SpellValue spellVal;
		FreeSelection token;
	}

	private CharacterController control;
	
	private Label lbExpTotal;
	private Label lbExpInvested;
	private FreePointsNode freePoints;
	private Label lbLevel;
	private CommandBar commands;
	private SettingsAndCommandBar firstLine;
	
	private SpliMoCharacter model;
	private Skill selectedSkill;
	private int selectedSpellRow = -1;
	
	private ToggleGroup tgSpellSchools;
	private Map<Spell, TriStateCheckBox> slidersBySpell;
	private Map<Spell, Label         > labelsBySpell;
	private Map<Skill, AttentionPane > attentionsBySchool;
	private FlowPane spellSchools;
	private AttentionPane attentionSpells;
	private AttentionPane attentionSpecs;
	private GridPane spellGrid;
	private Map<Integer, List<Node>> spellNodesByLine;
	private VBox descrPane;
	private Label lbSpellRef;
	private ListView<ListElemSpecialization> specializations;
	
	private boolean isRefreshing;
	
	//-------------------------------------------------------------------
	/**
	 */
	public SpellScreen(CharacterController control) {
		this.control = control;
		
		initComponents();
		initLayout();
		initInteractivity();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setTitle(UI.getString("screen.spells.title"));
		
		tgSpellSchools  = new ToggleGroup();
		spellSchools    = new FlowPane();
		spellGrid       = new GridPane();
		spellNodesByLine= new HashMap<>();
		descrPane       = new VBox();
		specializations     = new ListView<ListElemSpecialization>();
		slidersBySpell  = new HashMap<>();
		labelsBySpell   = new HashMap<>();
		attentionsBySchool = new HashMap<>();
		lbSpellRef      = new Label();
		lbSpellRef.getStyleClass().add("base");

		spellSchools.setOrientation(Orientation.HORIZONTAL);
		specializations.setCellFactory(new Callback<ListView<ListElemSpecialization>, ListCell<ListElemSpecialization>>() {
			public ListCell<ListElemSpecialization> call(ListView<ListElemSpecialization> param) {
				return new SkillSpecListCell(control);
			}
		});
		
		/*
		 * Exp & Co.
		 */
		freePoints = new FreePointsNode();
		freePoints.setStyle("-fx-max-height: 3em; -fx-max-width: 3em");
		freePoints.setPoints(control.getModel().getExperienceFree());
		freePoints.setName(UI.getString("label.ep.free"));
		Label hdExpTotal    = new Label(SpliMoCharGenConstants.RES.getString("label.ep.total")+": ");
		Label hdExpInvested = new Label(SpliMoCharGenConstants.RES.getString("label.ep.used")+": ");
		Label hdLevel       = new Label(SpliMoCharGenConstants.RES.getString("label.level")+": ");
		lbExpTotal    = new Label("?");
		lbExpInvested = new Label("?");
		lbLevel       = new Label("?");
		lbExpTotal.getStyleClass().add("base");
		lbExpInvested.getStyleClass().add("base");
		lbLevel.getStyleClass().add("base");
		lbExpTotal.setText(String.valueOf(control.getModel().getExperienceInvested()+control.getModel().getExperienceFree()));
		lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
		lbLevel.setText(control.getModel().getLevel()+"");
		
		commands = new CommandBar();
		commands.getItems().add(new MenuItem("Drucken", new Label("\uD83D\uDDB6")));
		
		HBox expLine = new HBox(5);
		expLine.getChildren().addAll(hdExpTotal, lbExpTotal, hdExpInvested, lbExpInvested, hdLevel, lbLevel);
		HBox.setMargin(hdLevel, new Insets(0,0,0,20));
		expLine.getStyleClass().add("character-document-view-firstline");
		
		firstLine = new SettingsAndCommandBar();
		firstLine.setSettings(expLine);
//		firstLine.setCommandBar(commands);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		// Spell schools
		VBox.setVgrow(spellSchools, Priority.NEVER);
		spellSchools.setVgap(20);
		spellSchools.setHgap(20);
		spellSchools.setMaxWidth(Double.MAX_VALUE);
		spellSchools.setMaxWidth(Double.MAX_VALUE);
		
		// Spells
		ScrollPane spellGridFlow = new ScrollPane(spellGrid);
		spellGridFlow.setMaxWidth(Double.MAX_VALUE);
		spellGridFlow.setFitToWidth(false);
		spellGridFlow.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		Label lblSpells = new Label(UI.getString("label.spells"));
		lblSpells.getStyleClass().add("text-subheader");
		VBox bxSpells   = new VBox(20);
		bxSpells.getStyleClass().add("content");
		bxSpells.getChildren().addAll(lblSpells, spellGridFlow);
//		VBox.setVgrow(spellSchools, Priority.ALWAYS);
		spellGrid.setVgap(5);
//		spellGrid.setHgap(5);
		spellGridFlow.setMaxWidth(Double.MAX_VALUE);
		spellGridFlow.setMinWidth(300);
		attentionSpells = new AttentionPane(bxSpells, Pos.TOP_RIGHT);
		
		// Description
		Label lblDescr = new Label(UI.getString("label.spell.effect"));
		lblDescr.getStyleClass().add("text-subheader");
		VBox bxDescr   = new VBox(20);
		bxDescr.getStyleClass().add("content");
		bxDescr.setStyle("-fx-padding: 0.5em");
		bxDescr.getChildren().addAll(lblDescr, lbSpellRef, descrPane);
		bxDescr.setPrefWidth(300);
		
		// Masteries
		VBox bxMaster = new VBox(20);
		bxMaster.setStyle("-fx-min-width: 21em;");
		Label lblMaster = new Label(UI.getString("label.specializations"));
		bxMaster.getChildren().addAll(lblMaster, specializations);
		VBox.setVgrow(specializations, Priority.ALWAYS);
		lblMaster.getStyleClass().add("text-subheader");
		bxMaster.getStyleClass().add("content");
		attentionSpecs = new AttentionPane(bxMaster, Pos.TOP_RIGHT);
		
		// Flow
		HBox lowerLine = new HBox(20);
		lowerLine.getChildren().addAll(freePoints, attentionSpells, bxDescr, attentionSpecs);
		lowerLine.setMaxHeight(Double.MAX_VALUE);
		lowerLine.setStyle("-fx-spacing: 1em");
		HBox.setHgrow(bxDescr, Priority.NEVER);
		VBox flow = new VBox(20);
		flow.getChildren().addAll(spellSchools, lowerLine);
		flow.setMaxWidth(Double.MAX_VALUE);
		VBox.setVgrow(spellSchools, Priority.NEVER);
		VBox.setVgrow(lowerLine, Priority.ALWAYS);
		
		
		VBox content = new ResponsiveVBox() {
			@Override
			public void setResponsiveMode(WindowMode value) {
				bxDescr.setManaged(value==WindowMode.EXPANDED);
				lblDescr.setManaged(value==WindowMode.EXPANDED);
				bxDescr.setVisible(value==WindowMode.EXPANDED);
				lblDescr.setVisible(value==WindowMode.EXPANDED);
			}
		};
		content.setSpacing(20);
		content.getChildren().addAll(firstLine, flow);
		VBox.setVgrow(flow, Priority.ALWAYS);
		content.getStyleClass().add("spell-screen");
		
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tgSpellSchools.selectedToggleProperty().addListener(
				(ov,o,n) -> {
					if (n!=null)
					schoolChanged((Skill)n.getUserData());
				});
	}

	//-------------------------------------------------------------------
	private SpellValue createSpellValue(Spell spell, Skill school) {
		// Replace with value used in model
		for (SpellValue chk : model.getSpells()) {
			if (chk.getSkill()==school && chk.getSpell()==spell)
				return chk;
		}
		return new SpellValue(spell, school);
	}

	//-------------------------------------------------------------------
	private void schoolChanged(Skill school) {
		logger.debug("School changed to "+school);
		selectedSkill = school;
		
		List<Spell> spells = SplitterMondCore.getSpells(school);
		// Sort spells by level and name
		Collections.sort(spells, new Comparator<Spell>() {
			public int compare(Spell spell1, Spell spell2) {
				int cmp = ((Integer)spell1.getLevelInSchool(school)).compareTo(spell2.getLevelInSchool(school));
				if (cmp!=0)
					return cmp;
				return Collator.getInstance().compare(spell1.getName(), spell2.getName());
			}
		});
		
		SpellController spellCtrl = control.getSpellController();
		attentionSpells.setAttentionFlag(spellCtrl.getUnusedFreeSelections(school).isEmpty());
		attentionSpecs.setAttentionFlag(control.getMastershipController().getFreeMasterships(school)>0);
		
		// Build table
		spellGrid.getChildren().clear();
		slidersBySpell.clear();
		spellNodesByLine.clear();
		int y=0;
		for (Spell spell : spells) {
			SelectionOption option = new SelectionOption();
			option.spell = spell;
			option.spellVal = createSpellValue(spell, school);
			
			Label lblName = new Label(spell.getName());
			lblName.setMaxWidth(Double.MAX_VALUE);
			Label lblLevel= new Label(String.valueOf(spell.getLevelInSchool(school)));
			
			TriStateCheckBox slider = new TriStateCheckBox();
			slider.setUserData(option);
			slider.setStateFormatter(new StringConverter<TriStateCheckBox.State>(){
				public String toString(State state) {
					switch (state) {
					case SELECTION1: return UI.getString("spellslider.state1");
					case SELECTION2: return UI.getString("spellslider.state2");
					case SELECTION3: return UI.getString("spellslider.state3");
					}
					return "??";
				}

				@Override
				public State fromString(String string) { return null;}
			});
			slidersBySpell.put(spell, slider);

			Label lblValue= new Label("   ");
			labelsBySpell.put(spell, lblValue);
			
			spellGrid.add(lblLevel, 0, y);
			spellGrid.add(lblName , 1, y);
			spellGrid.add(slider  , 2, y);
			spellGrid.add(lblValue, 3, y);
			lblLevel.setStyle("-fx-padding: 0 5 0 3");
			lblName .setStyle("-fx-padding: 0 5 0 5");
			lblValue.setStyle("-fx-padding: 0 5 0 5");
			GridPane.setFillWidth(lblName, true);
			GridPane.setHgrow(lblName, Priority.ALWAYS);
			List<Node> nodeList = new ArrayList<>(Arrays.asList(lblLevel, lblName, slider, lblValue));
			spellNodesByLine.put(y, nodeList);
			
			/*
			 * Set data for spell
			 */
			SpellValue value = createSpellValue(spell, school);
			
//			// Buy with exp
//			if (spellCtrl.canBeFreeSelected(value)!=null) {
//				slider.getAllowedStates().add(TriStateCheckBox.State.SELECTION3);
//			} else
//				slider.getAllowedStates().remove(TriStateCheckBox.State.SELECTION3);
//			
//			// Buy with free spell
//			if (spellCtrl.canBeSelected(value)) {
//				slider.getAllowedStates().add(TriStateCheckBox.State.SELECTION2);
//			} else
//				slider.getAllowedStates().remove(TriStateCheckBox.State.SELECTION2);
			
//			btnFree  .setDisable(! (spellCtrl.canBeFreeSelected(value)!=null) );
//			btnBought.setDisable(!spellCtrl.canBeSelected(value));
			int intVal = model.getSpellValueFor(value);
			lblValue.setText(String.valueOf(intVal));
			
			/*
			 *  Interactivity
			 */
			// Mouse
			lblName.setOnMouseEntered( event -> highlightSpellRow(GridPane.getRowIndex((Node)event.getSource()), true));
			lblName.setOnMouseExited( event -> highlightSpellRow(GridPane.getRowIndex((Node)event.getSource()), false));
			lblName.setOnMouseClicked( event -> selectSpellRow(GridPane.getRowIndex((Node)event.getSource()), true));
			// Selection
			slider.stateProperty().addListener( (ov,o,n) -> {
				changeSpellSelection((SelectionOption)slider.getUserData(), o, n);
			});
			
			y++;
		}
		
		refreshSpells();
		refreshSpecializations();
	}

	//-------------------------------------------------------------------
	private void changeSpellSelection(SelectionOption option, State oldState, State newState) {
		logger.info(String.format("Slider for %s changed from %s to %s   refreshing=%s", option.spell, oldState, newState, isRefreshing));
		if (isRefreshing)
			return;
		if (oldState==newState)
			return;
		
		SpellController spellCtrl = control.getSpellController();
		
		switch (oldState) {
		case SELECTION1:
			// Has previously been inactive
			switch (newState) {
			case SELECTION2:
				FreeSelection free = spellCtrl.canBeFreeSelected(option.spellVal);
				if (free!=null)
					spellCtrl.select(free, option.spellVal);
				else
					logger.error("Selection failed");
				break;
			case SELECTION3:
				// Change from not selected to bought
				spellCtrl.select(option.spellVal);
				break;
			case SELECTION1:
			}
			break;
		case SELECTION2:
			// Has previously been free selected
			if (option.spellVal.getFreeLevel()<0)
				throw new IllegalStateException("Spell "+option.spellVal+" was free selected in GUI, but has a FreeLevel of "+option.spellVal.getFreeLevel());
			switch (newState) {
			case SELECTION1:
				spellCtrl.deselect(option.spellVal);
				break;
			case SELECTION3:
				// Switch from free selection to EXP
				spellCtrl.deselect(option.spellVal);
				if (spellCtrl.canBeSelected(option.spellVal))
					spellCtrl.select(option.spellVal);
				else {
					// Was able to remove free selection, but not buying it.
					// Return to free selection
					FreeSelection free = spellCtrl.canBeFreeSelected(option.spellVal);
					if (free!=null)
						spellCtrl.select(free, option.spellVal);
				}
			case SELECTION2:
			}
			break;
		case SELECTION3:
			// Has previously been bought
			spellCtrl.deselect(option.spellVal); // Effectivly selection1 now
			if (newState==State.SELECTION2) {
				FreeSelection token = spellCtrl.canBeFreeSelected(option.spellVal);
				logger.debug("Select free with token "+token);
				spellCtrl.select(token, option.spellVal);
				option.token = token;
			}
			break;
		}
		
		refreshSpells();
	}

	//-------------------------------------------------------------------
	private void highlightSpellRow(int row, boolean highlight) {
		List<Node> list = spellNodesByLine.get(row);
		if (list==null)
			return;
		for (Node node : list) {
			if (highlight)
				node.getStyleClass().add("highlighted");
			else
				node.getStyleClass().remove("highlighted");
		}
	}

	//-------------------------------------------------------------------
	private void selectSpellRow(int row, boolean select) {
		if (select) {
			// Deselect old
			selectSpellRow(selectedSpellRow, false);
			selectedSpellRow = row;
		} else {
			selectedSpellRow = -1;
		}
		
		List<Node> list = spellNodesByLine.get(row);
		if (list==null)
			return;
		for (Node node : list) {
			if (select)
				node.getStyleClass().add("selected");
			else
				node.getStyleClass().remove("selected");
			// Update description (Spell is assigned as UserObject to slider
			if (node.getUserData()!=null && node.getUserData() instanceof SelectionOption)
				updateDescription( ((SelectionOption)node.getUserData()).spell );
		}
		
	}

	//-------------------------------------------------------------------
	private void updateDescription(Spell spell) {
		descrPane.getChildren().clear();
		
		lbSpellRef.setText(spell.getProductName()+" "+spell.getPage());
		
		StringBuffer buf;
		
		// Type
		Label headType = new Label(UI.getString("label.spell.type")+": ");
		Label lblType  = new Label();
		headType.getStyleClass().add("base");
		buf = new StringBuffer();
		for (Iterator<SpellType> it=spell.getTypes().iterator(); it.hasNext(); ) {
			buf.append(it.next().getName());
			if (it.hasNext())
				buf.append(", ");
		}
		lblType.setText(buf.toString());
		
		// Difficulty
		Label headDiff = new Label(UI.getString("label.spell.difficulty")+": ");
		Label lblDiff  = new Label();
		headDiff.getStyleClass().add("base");
		lblDiff.setText(String.valueOf(spell.getDifficultyString()));
		
		// Cost
		Label headCost = new Label(UI.getString("label.spell.cost")+": ");
		Label lblCost  = new Label();
		headCost.getStyleClass().add("base");
		lblCost.setText(SplitterTools.getFocusString(spell.getCost()));
		
		// Cast duration
		Label headDur  = new Label(UI.getString("label.spell.castduration")+": ");
		Label lblDur   = new Label();
		headDur.getStyleClass().add("base");
		lblDur.setText(spell.getCastDurationString());

		// Range
		Label headRange= new Label(UI.getString("label.spell.castrange")+": ");
		Label lblRange = new Label();
		headRange.getStyleClass().add("base");
		lblRange.setText(spell.getCastRangeString());

		// Spell Duration
		Label headSpDur= new Label(UI.getString("label.spell.duration")+": ");
		Label lblSpDur = new Label();
		headSpDur.getStyleClass().add("base");
		lblSpDur.setText(spell.getSpellDurationString());
		
		// Effect
		Label lblDescr = new Label(spell.getDescription());
		lblDescr.setWrapText(true);
		
		// Enhancement
		Label headEnhan= new Label(UI.getString("label.spell.enhanced")+": ");
		Label lblEnhCost = new Label(spell.getEnhancementString());
		Label lblEnhan = new Label(spell.getEnhancementDescription());
		lblEnhan.setWrapText(true);
		headEnhan.getStyleClass().add("base");

		
		HBox boxEnhan = new HBox(headEnhan, lblEnhCost);
		descrPane.getChildren().addAll(
				new HBox(headType, lblType), 
				new HBox(headDiff, lblDiff), 
				new HBox(headCost, lblCost), 
				new HBox(headDur , lblDur ), 
				new HBox(headRange, lblRange), 
				new HBox(headSpDur, lblSpDur), 
				lblDescr, 
				boxEnhan, 
				lblEnhan);
		VBox.setMargin(lblDescr, new Insets(20,0,0,0));
		VBox.setMargin(boxEnhan, new Insets(20,0,0,0));

	}

	//-------------------------------------------------------------------
	private void refreshSpells() {
		logger.debug("refreshSpells");
		if (tgSpellSchools.getSelectedToggle()==null)
			return;
		isRefreshing = true;
		Skill school = (Skill)tgSpellSchools.getSelectedToggle().getUserData();
		SpellController spellCtrl = control.getSpellController();

		// update attention flag
		attentionSpells.setAttentionFlag(!spellCtrl.getUnusedFreeSelections(school).isEmpty());
		
		outer:
		for (Spell spell : SplitterMondCore.getSpells(school)) {
			TriStateCheckBox buttons = slidersBySpell.get(spell);
			
			SpellValue spellVal = createSpellValue(spell, school);
			boolean isFreeSelected = spellVal.wasFree();
			boolean isExpSelected  = !isFreeSelected && model.getSpells().contains(spellVal);
//			logger.debug("Spell "+spellVal
//					+"  canBeDeselected="+spellCtrl.canBeDeSelected(spellVal)
//					+"  canBeFreeSelected="+spellCtrl.canBeFreeSelected(spellVal)
//					+"  canBeSelected="+spellCtrl.canBeSelected(spellVal)
//					+"  isFree="+isFreeSelected
//					+"  isExp="+isExpSelected
//					);
			
			// Check state SELECTION1	
			if (spellCtrl.canBeDeSelected(spellVal) || !(isFreeSelected || isExpSelected)) {
				if (!buttons.getAllowedStates().contains(State.SELECTION1))
					buttons.getAllowedStates().add(State.SELECTION1);
			} else {
				buttons.getAllowedStates().remove(State.SELECTION1);
			}
			
			// Check state SELECTION2	
			if (spellCtrl.canBeFreeSelected(spellVal)!=null || isFreeSelected) {
				if (!buttons.getAllowedStates().contains(State.SELECTION2))
					buttons.getAllowedStates().add(State.SELECTION2);
			} else if (buttons.getAllowedStates().contains(State.SELECTION2)) {
				buttons.getAllowedStates().remove(State.SELECTION2);
			}
			
			// Check state SELECTION3	
			if (spellCtrl.canBeSelected(spellVal) || isExpSelected) {
				if (!buttons.getAllowedStates().contains(State.SELECTION3))
					buttons.getAllowedStates().add(State.SELECTION3);
			} else {
				buttons.getAllowedStates().remove(State.SELECTION3);
			}
			
			buttons.setDisable(buttons.getAllowedStates().size()<=1);
			

			// Value
			Label lblVal = labelsBySpell.get(spell);
			lblVal.setText(String.valueOf(model.getSpellValueFor(spellVal)));
			
			// If spell has been bought with free selections
			for (FreeSelection free : spellCtrl.getFreeSelections()) {
				if (free.getSchool()!=school)
					continue;
				if (free.getUsedFor()==null || !free.getUsedFor().equals(spellVal))
					continue;
				// Yes, it has
				if (logger.isTraceEnabled())
					logger.trace(spellVal+" was free selected");
				buttons.select(State.SELECTION2);
				continue outer;
			}
			
			// If spell has been bought with exp
			if (isExpSelected) {
				buttons.select(State.SELECTION3);
				continue outer;
			}
		}
		
		
		updateAttentionFlags();
		isRefreshing = false;
	}

	//-------------------------------------------------------------------
	private void refreshSpecializations() {
		logger.debug("refreshSpecializations");
		specializations.getItems().clear();
		
		Skill skill = (Skill)tgSpellSchools.getSelectedToggle().getUserData();
		for (SkillSpecialization spec : skill.getSpecializations()) {
			ListElemSpecialization item = new ListElemSpecialization();
			item.skill = skill;
			item.data  = spec;
			item.level = model.getSkillSpecializationLevel(spec);
			// Count how often the spec. is used by spells
			for (SpellValue spell : model.getSpells()) {
				if (spell.getSkill()!=skill)
					continue;
				for (SpellType type : spell.getSpell().getTypes()) {
					if (spec.getId().equalsIgnoreCase(type.name()))
						item.count++;
				}
				
			}
			specializations.getItems().add(item);
		}
		
		final Collator collat = Collator.getInstance();
		Collections.sort(specializations.getItems(), new Comparator<ListElemSpecialization>() {
			@Override
			public int compare(ListElemSpecialization o1, ListElemSpecialization o2) {
				int cmp = ((Integer)o1.count).compareTo(o2.count);
				if (cmp!=0)
					return -cmp;
				return collat.compare(o1.data.getName(), o2.data.getName());
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			lbExpTotal.setText(String.valueOf(control.getModel().getExperienceInvested()+control.getModel().getExperienceFree()));
			lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
			lbLevel.setText(control.getModel().getLevel()+"");
			freePoints.setPoints(control.getModel().getExperienceFree());
			break;
		case SKILL_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			logger.debug(" changed: "+event.getKey()+"  selected="+selectedSkill);
			if (((Skill)event.getKey())==selectedSkill) {
				refreshSpecializations();
			}
			lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
			lbLevel.setText(control.getModel().getLevel()+"");
			break;
		case SPELL_FREESELECTION_CHANGED:
			lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
			lbLevel.setText(control.getModel().getLevel()+"");
		case SPELL_ADDED:
		case SPELL_CHANGED:
		case SPELL_OFFER_CHANGED:
		case SPELL_REMOVED:
			logger.debug("rcv "+event.getType()+"  "+event.getKey()+"   val="+event.getValue());
			refreshSpells();
			break;
		case MASTERSHIP_ADDED:
		case MASTERSHIP_CHANGED:
		case MASTERSHIP_REMOVED:
			logger.debug("rcv "+event);
			logger.debug(" changed: "+event.getKey()+"  selected="+selectedSkill);
			refreshSpells();
			if (((Skill)event.getKey())==selectedSkill) {
				refreshSpecializations();
			}			
			break;
		case POINTS_LEFT_MASTERSHIPS:
			updateAttentionFlags();		
			break;
		default:
			break;
		}		
	}

	//-------------------------------------------------------------------
	private void updateAttentionFlags() {
		logger.debug("updateAttentionFlags");
		
		/*
		 * Top school list
		 */
		for (Entry<Skill, AttentionPane> entry : attentionsBySchool.entrySet()) {
			List<String> todo = control.getSpellController().getToDos(entry.getKey());
			todo.addAll(control.getMastershipController().getToDos(entry.getKey()));
			AttentionPane pane = entry.getValue();
			if (todo.isEmpty()) {
				pane.setAttentionFlag(false);
				pane.setAttentionToolTip(null);
			} else {
				pane.setAttentionFlag(true);
				pane.setAttentionToolTip(todo);
			}
		}
		
		/*
		 * Spell list
		 */
		List<String> todo = control.getSpellController().getToDos(selectedSkill);
		if (todo.isEmpty()) {
			attentionSpells.setAttentionFlag(false);
			attentionSpells.setAttentionToolTip(null);
		} else {
			attentionSpells.setAttentionFlag(true);
			attentionSpells.setAttentionToolTip(todo);
		}
		

		/*
		 * Specializations
		 */
		todo = control.getMastershipController().getToDos(selectedSkill);
		if (todo.isEmpty()) {
			attentionSpecs.setAttentionFlag(false);
			attentionSpecs.setAttentionToolTip(null);
		} else {
			attentionSpecs.setAttentionFlag(true);
			attentionSpecs.setAttentionToolTip(todo);
		}
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		
		lbExpInvested.setText(model.getExperienceInvested()+"");
		lbLevel.setText(model.getLevel()+"");
		spellSchools.getChildren().clear();
		tgSpellSchools.getToggles().clear();
		attentionsBySchool.clear();
		for (SkillValue check : model.getSkills(SkillType.MAGIC)) {
			if (check.getValue()==0)
				continue;
			ToggleButton but = new ToggleButton(check.getSkill().getName()+" "+check.getValue());
			but.getStyleClass().add("bordered");
			but.setUserData(check.getSkill());
			tgSpellSchools.getToggles().add(but);
			AttentionPane attention = new AttentionPane(but, Pos.BOTTOM_RIGHT);
			spellSchools.getChildren().add(attention);
			attentionsBySchool.put(check.getSkill(), attention);
		}
		
//		skills.setContent(model);
		specializations.getItems().clear();
//		focusAttributeName.setText(Attribute.CHARISMA.getName());
//		focusAttributeDescription.setText(uiResources.getString("descr.attribute.charisma"));
		
		if (!tgSpellSchools.getToggles().isEmpty())
			tgSpellSchools.selectToggle(tgSpellSchools.getToggles().get(0));
		
		updateAttentionFlags();
	}

}

class SpellsSpecListCell extends  ListCell<ListElemSpecialization> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private CheckBox check1;
	private CheckBox check2;
	private CheckBox check3;
	private CheckBox check4;
	private Label label;
	private Label count;
	private HBox box;
	private MastershipController control;
	
	//--------------------------------------------------------------------
	public SpellsSpecListCell(CharacterController ctrl) {
		this.control = ctrl.getMastershipController();
		check1 = new CheckBox();
		check2 = new CheckBox();
		check3 = new CheckBox();
		check4 = new CheckBox();
		label  = new Label();
		count  = new Label();
		box = new HBox(5);
		box.getChildren().addAll(check1, check2, check3, label, count);
		HBox.setHgrow(label, Priority.ALWAYS);
		label.setMaxWidth(Double.MAX_VALUE);
		this.setPrefWidth(0);
		box.prefWidthProperty().bind(this.widthProperty());
		
		check1.setOnAction(event -> changed(1, check1.selectedProperty().get()));
		check2.setOnAction(event -> changed(2, check2.selectedProperty().get()));
		check3.setOnAction(event -> changed(3, check3.selectedProperty().get()));
		check4.setOnAction(event -> changed(4, check4.selectedProperty().get()));
	}
	
	//--------------------------------------------------------------------
	private void changed(int level, boolean selected) {
		logger.debug("SkillSpecListCell.changed("+level+", "+selected+")");
		if (selected)
			control.select(getItem().data, level);
		else
			control.deselect(getItem().data, level);
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(ListElemSpecialization item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			setText(null);
		} else {
			label.setText(item.data.getName());
			count.setText(item.count+"x");
			setGraphic(box);
			check1.setSelected(item.level>0);
			check2.setSelected(item.level>1);
			check3.setSelected(item.level>2);
			check4.setSelected(item.level>3);
			check1.setDisable(!control.isEditable(item.data, 1));
			check2.setDisable(!control.isEditable(item.data, 2));
			check3.setDisable(!control.isEditable(item.data, 3));
			check4.setDisable(!control.isEditable(item.data, 4));
		}
		
	}
}