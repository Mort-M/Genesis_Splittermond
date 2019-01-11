/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SelectMastershipDialog2 extends HBox implements ChangeListener<Boolean>, GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private static PropertyResourceBundle RULES = SplitterMondCore.getI18nResources();

	private MastershipController control;
	private SpliMoCharacter model;
	private Skill           skill;
	
	private Map<SkillSpecialization, CheckBox[]> focusCheck;
	private Map<Mastership, CheckBox> masteryCheck;
	private Label pointsLeft, expLeft;
	private VBox context;

	//-------------------------------------------------------------------
	public SelectMastershipDialog2(MastershipController chGen, SpliMoCharacter model, Skill skill) {
		super(0);
		control = chGen;
		this.model = model;
		this.skill = skill;
		
		focusCheck   = new HashMap<SkillSpecialization, CheckBox[]>();
		masteryCheck = new HashMap<Mastership, CheckBox>();
		
		doInit();
		doLayout();
		updateContent();
		// Do this after setting all checkbox states
		doInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void doInit() {
		/*
		 * Focus areas
		 */
		for (SkillSpecialization special : skill.getSpecializations()) {
			CheckBox[] boxes = new CheckBox[4];
			for (int i=0; i<boxes.length; i++) {
				boxes[i] = new CheckBox();
//				boxes[i].selectedProperty().addListener(this);
			}
			
			focusCheck.put(special, boxes);
		}
		
		// Masteries
		for (Mastership tmp : skill.getMasterships()) {
			CheckBox box = new CheckBox();
//			box.selectedProperty().addListener(this);
			masteryCheck.put(tmp, box);
		}
		
		context = getContextMenu();
		
	}

	//-------------------------------------------------------------------
	private void doLayout() {
		this.setAlignment(Pos.TOP_CENTER);
		GridPane msGrid = new GridPane();
		GridPane foGrid = new GridPane();
		msGrid.setHgap(2);
		msGrid.setVgap(1);
		foGrid.setHgap(2);
		foGrid.setVgap(1);
		int y=0;
		
		foGrid.setPadding(new Insets(3));
		msGrid.setPadding(new Insets(3));
		
		/*
		 * Focus areas
		 */
		logger.debug("Add focus areas");		
		Label f = new Label(RULES.getString("mastership.focus"));
		f.getStyleClass().add("wizard-heading2");
		f.setMaxWidth(Double.MAX_VALUE);
		foGrid.add(f, 0, y++, 5,1);
		for (SkillSpecialization special : skill.getSpecializations()) {
			Label      label = new Label(special.getName());
			CheckBox[] boxes = focusCheck.get(special);
			
			foGrid.add(label, 0, y);
			for (int i=0; i<boxes.length; i++)
				foGrid.add(boxes[i], 1+i, y);
			
			y++;
		}
		VBox leftBox = new VBox(0);
		leftBox.getStyleClass().add("wizard-bordered");
		leftBox.getChildren().addAll(f, foGrid);
		
		/*
		 * Level 1
		 */
		Label m = new Label(RULES.getString("mastership.label"));
		m.getStyleClass().add("wizard-heading2");
		m.setMaxWidth(Double.MAX_VALUE);
		y=0;
		Label l1 = new Label(RULES.getString("mastership.level1"));
		l1.getStyleClass().add("title");
		msGrid.add(l1, 0, y++, 5,1);
		for (Mastership tmp : skill.getMasterships()) {
			if (tmp.getLevel()!=1) continue;
			Label    label = new Label(tmp.getName());
			CheckBox box   = masteryCheck.get(tmp);
			
			msGrid.add(label, 0, y);
			msGrid.add(box  , 1, y);			
			y++;
		}
		// Add a separator
		msGrid.add(new Separator(), 0, y++, 5,1);
		
		/*
		 * Level 2
		 */
		Label l2 = new Label(RULES.getString("mastership.level2"));
		l2.getStyleClass().add("title");
		msGrid.add(l2, 0, y++, 5,1);
		for (Mastership tmp : skill.getMasterships()) {
			if (tmp.getLevel()!=2) continue;
			Label    label = new Label(tmp.getName());
			CheckBox box   = masteryCheck.get(tmp);
			
			msGrid.add(label, 0, y);
			msGrid.add(box  , 1, y);			
			y++;
		}
		// Add a separator
		msGrid.add(new Separator(), 0, y++, 5,1);
		
		/*
		 * Level 3
		 */
		Label l3 = new Label(RULES.getString("mastership.level3"));
		l3.getStyleClass().add("title");
		msGrid.add(l3, 0, y++, 5,1);
		for (Mastership tmp : skill.getMasterships()) {
			if (tmp.getLevel()!=3) continue;
			Label    label = new Label(tmp.getName());
			CheckBox box   = masteryCheck.get(tmp);
			
			msGrid.add(label, 0, y);
			msGrid.add(box  , 1, y);			
			y++;
		}
		// Add a separator
		msGrid.add(new Separator(), 0, y++, 5,1);
		
		/*
		 * Level 4
		 */
		Label l4 = new Label(RULES.getString("mastership.level4"));
		l4.getStyleClass().add("title");
		msGrid.add(l4, 0, y++, 5,1);
		for (Mastership tmp : skill.getMasterships()) {
			if (tmp.getLevel()!=4) continue;
			Label    label = new Label(tmp.getName());
			CheckBox box   = masteryCheck.get(tmp);
			
			msGrid.add(label, 0, y);
			msGrid.add(box  , 1, y);			
			y++;
		}
		VBox rightBox = new VBox(0);
		rightBox.getStyleClass().add("wizard-bordered");
		rightBox.getChildren().addAll(m, msGrid);

		/* Center */
		HBox sideBySide = new HBox(10);
		sideBySide.getStyleClass().add("wizard-content");
//		HBox.setHgrow(mastPane, Priority.NEVER);
		sideBySide.getChildren().addAll(leftBox, rightBox);
		
		
		
		getChildren().addAll(sideBySide, context);
	}

	//-------------------------------------------------------------------
	private void doInteractivity() {
		
		/*
		 * Focus areas
		 */
		for (SkillSpecialization special : skill.getSpecializations()) {
			CheckBox[] boxes = focusCheck.get(special);
			for (int i=0; i<boxes.length; i++) {
				boxes[i].selectedProperty().addListener(this);
			}
		}
		
		// Masteries
		for (Mastership tmp : skill.getMasterships()) {
			CheckBox box = masteryCheck.get(tmp);
			box.selectedProperty().addListener(this);
		}
	}
	
	private VBox getContextMenu() {
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");

		// Line that reflects remaining points to distribute
		pointsLeft = new Label(String.valueOf(control.getFreeMasterships(skill)));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		Label pointsLeft_f = new Label(UI.getString("wizard.distrMaster.pointsLeft")+" ");
		pointsLeft_f.setWrapText(true);
		pointsLeft_f.getStyleClass().add("wizard-context");

		// Line that reflects remaining points to distribute
		expLeft = new Label("?");
		if (model!=null)
			expLeft.setText(String.valueOf(model.getExperienceFree()));
		expLeft.setStyle("-fx-font-size: 400%");
		expLeft.getStyleClass().add("wizard-context");
		Label expLeft_f = new Label(UI.getString("wizard.distrMaster.expLeft")+" ");
		expLeft_f.setWrapText(true);
		expLeft_f.getStyleClass().add("wizard-context");


		context = new VBox(15);
		context.getStyleClass().add("wizard-context");
		context.setAlignment(Pos.TOP_CENTER);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, pointsLeft, pointsLeft_f, expLeft, expLeft_f);
		return context;
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		SkillValue value = model.getSkillValue(skill);
		
		// Set specializations
		for (SkillSpecialization special : skill.getSpecializations()) {
			int level = value.getSpecializationLevel(special);
			CheckBox[] boxes = focusCheck.get(special);
			for (int i=0; i<boxes.length; i++) {
				// Set value
				boolean newValue = level>=(i+1);
				if (boxes[i].selectedProperty().get()!=newValue)
					boxes[i].selectedProperty().setValue(newValue);
				// Enable or disable boxes
				boxes[i].setDisable(!control.isEditable(special, i+1));
			}
			
		}
			
		List<MastershipReference> list = model.getSkillValue(skill).getMasterships();
		for (MastershipReference ref : list) {
			if (ref.getMastership()!=null) {
				if (!masteryCheck.get(ref.getMastership()).selectedProperty().getValue())
					masteryCheck.get(ref.getMastership()).selectedProperty().setValue(true);
			}
		}
		
		for (Mastership master : skill.getMasterships()) {
			if (logger.isTraceEnabled())
				logger.trace("isEditable("+master+") = "+control.isEditable(master));
			masteryCheck.get(master).setDisable(!control.isEditable(master));			
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Boolean> box, Boolean arg1,
			Boolean newVal) {
		SkillSpecialization special = null;
		int level = 0;
		Mastership master = null;
		logger.debug("changed("+box+", "+newVal+")");
		
		// Find checkbox
		outer:
		for (Entry<SkillSpecialization, CheckBox[]> pair : focusCheck.entrySet()) {
			for (int i=0; i<pair.getValue().length; i++) {
				if (pair.getValue()[i].selectedProperty()==box) {
					level = i+1;
					special = pair.getKey();
					logger.debug("Clicked specialization "+special+" for level "+level+": "+newVal);
					if (newVal)
						control.select(special, level);
					else
						control.deselect(special, level);
					updateContent();
					break outer;
				}
			}
		}
		
		outer:
			for (Entry<Mastership, CheckBox> pair : masteryCheck.entrySet()) {
				if (pair.getValue().selectedProperty()==box) {
					master = pair.getKey();
					logger.debug("Clicked mastership "+master+": "+newVal);
					if (newVal)
						control.select(master);
					else
						control.deselect(master);
					updateContent();
					break outer;
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
		case POINTS_LEFT_MASTERSHIPS:
			Skill changedSkill = (Skill)event.getKey();
			if (changedSkill!=skill)
				return;
			logger.debug("process "+event);
			pointsLeft.setText(String.valueOf(event.getValue()));
			updateContent();
			break;
		case EXPERIENCE_CHANGED:
			logger.debug("process "+event);
			int[] pair = (int[]) event.getValue();
			expLeft.setText(String.valueOf(pair[0]));
			break;
		default:
		}
	}

}
