package org.prelle.splittermond.chargen.jfx.sections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AttentionPane;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.SingleSection;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class SpellSchoolSection extends SingleSection {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private CharacterController control;
	private ToggleGroup tgSpellSchools;
	private FlowPane spellSchools;
	private Map<Skill, AttentionPane > attentionsBySchool;
	
	private ObjectProperty<Skill> selectedSchool = new SimpleObjectProperty<>();

	//-------------------------------------------------------------------
	public SpellSchoolSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(provider, title, null);
		this.control = ctrl;
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tgSpellSchools  = new ToggleGroup();
		spellSchools    = new FlowPane();
		attentionsBySchool = new HashMap<>();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		// Spell schools
		VBox.setVgrow(spellSchools, Priority.NEVER);
		spellSchools.setVgap(15);
		spellSchools.setHgap(20);
		spellSchools.setMaxWidth(Double.MAX_VALUE);
		spellSchools.setMaxWidth(Double.MAX_VALUE);
		spellSchools.setStyle("-fx-max-height: 3em");
		
		setContent(spellSchools);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tgSpellSchools.selectedToggleProperty().addListener( (ov,o,n) -> {
			logger.debug("spell school set from "+n);
			if (n!=null)
				selectedSchool.set((Skill)n.getUserData());
			else
				selectedSchool.set(null);
			});
	}
	
	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<Skill> selectedSchoolProperty() { return selectedSchool; }
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		Skill oldSkill = null;
		if (tgSpellSchools.getSelectedToggle()!=null)
			oldSkill = (Skill)tgSpellSchools.getSelectedToggle().getUserData();
		
		spellSchools.getChildren().clear();
		tgSpellSchools.getToggles().clear();
		attentionsBySchool.clear();
		for (SkillValue check : control.getModel().getSkills(SkillType.MAGIC)) {
			if (check.getValue()==0)
				continue;
			ToggleButton but = new ToggleButton(check.getSkill().getName()+" "+check.getValue());
			but.getStyleClass().add("bordered");
			but.setUserData(check.getSkill());
			tgSpellSchools.getToggles().add(but);
			AttentionPane attention = new AttentionPane(but, Pos.BOTTOM_RIGHT);
			spellSchools.getChildren().add(attention);
			attentionsBySchool.put(check.getSkill(), attention);
			
			if (oldSkill!=null && oldSkill==check.getSkill() && tgSpellSchools.getSelectedToggle()==null) {
				tgSpellSchools.selectToggle(but);
			}

		}
		
		if (!tgSpellSchools.getToggles().isEmpty()  && tgSpellSchools.getSelectedToggle()==null)
			tgSpellSchools.selectToggle(tgSpellSchools.getToggles().get(0));
		
		updateAttentionFlags();
	}

	//-------------------------------------------------------------------
	public void updateAttentionFlags() {
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
	}

}
