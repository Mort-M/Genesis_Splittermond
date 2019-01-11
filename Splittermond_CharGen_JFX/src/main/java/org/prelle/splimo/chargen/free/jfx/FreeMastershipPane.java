package org.prelle.splimo.chargen.free.jfx;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.free.FreeSelectionGenerator;
import org.prelle.splimo.modifications.MastershipModification;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class FreeMastershipPane extends HBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private FreeSelectionGenerator control;
	private FreeSelectionDialog parent;

	private ChoiceBox<Skill>[] master_skill_cb;
	private ChoiceBox<MastershipModification>[] master_mod_cb;
	private Label pointsLeft;
	
	//--------------------------------------------------------------------
	public FreeMastershipPane(FreeSelectionGenerator ctrl, FreeSelectionDialog parent) {
		this.parent  = parent;
		this.control = ctrl;

		doInit();
		initInteractivity();

		updateContent();
	}
	
	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		// Content
		int count = control.getPointsMasterships() + control.getMasterships().size();
		master_skill_cb = new ChoiceBox[count];
		master_mod_cb   = new ChoiceBox[count];
		
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(10);
		for (int i=0; i<master_mod_cb.length; i++) {
			master_skill_cb[i] = new ChoiceBox<Skill>(FXCollections.observableArrayList(control.getAvailableMastershipSkills()));
			master_skill_cb[i].setConverter(new StringConverter<Skill>() {
				public String toString(Skill data) { return data.getName(); }
				public Skill fromString(String data) { return null; }
			});
			master_mod_cb[i] = new ChoiceBox<MastershipModification>();
			master_mod_cb[i].setConverter(new StringConverter<MastershipModification>() {
				public String toString(MastershipModification data) {
					if (data.getMastership()!=null) return data.getMastership().getName();
					if (data.getSpecialization()!=null) return data.getSpecialization().getSpecial().getName();
					return data.toString(); 
				}
				public MastershipModification fromString(String data) { return null; }
			});
			
			grid.add(master_skill_cb[i], 0, i);
			grid.add(master_mod_cb[i]  , 1, i);
		}


		/* Sidebar */
		// Line that reflects remaining points to distribute
		pointsLeft = new Label(String.valueOf(control.getPointsPowers()));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		VBox sidebar = new VBox(15);
		sidebar.setPrefWidth(80);
		sidebar.setAlignment(Pos.TOP_CENTER);
		sidebar.getStyleClass().add("wizard-context");
		sidebar.getChildren().addAll(pointsLeft);
		sidebar.setMaxHeight(Double.MAX_VALUE);

		grid.getStyleClass().add("wizard-content");
		grid.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(grid, Priority.SOMETIMES);

		// Add to layout
		setMaxHeight(Double.MAX_VALUE);
		getChildren().addAll(grid, sidebar);
	}
	
	//-------------------------------------------------------------------
	private int getIndexOfMastership(ObservableValue<? extends Skill> box) {
		for (int i=0; i<master_skill_cb.length; i++)
			if (master_skill_cb[i].getSelectionModel().selectedItemProperty()==box)
				return i;
		
		return -1;
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		// Interactivity
		for (int i=0; i<master_skill_cb.length; i++) {
			master_skill_cb[i].getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Skill>() {
				public void changed(ObservableValue<? extends Skill> cb,
						Skill arg1, Skill newVal) {
					logger.trace("skill changed: "+newVal);
					if (newVal==null)
						return;
					int i = getIndexOfMastership(cb);
					
					// Check for old modification to remove
					MastershipModification oldMod = master_mod_cb[i].getSelectionModel().selectedItemProperty().getValue();
					if (oldMod!=null)
						control.setMastership(oldMod, false);
					
					ObservableList<MastershipModification> mods = FXCollections.observableArrayList();
					// Specializations
					for (SkillSpecialization spec : newVal.getSpecializations()) {
						mods.add(new MastershipModification(spec, 1));
					}
					// Masterships
					for (Mastership mast : newVal.getMasterships()) {
						if (mast.getLevel()==1)
							mods.add(new MastershipModification(mast));
					}
					
					master_mod_cb[i].setItems(mods);
				}
			});
			// Display data - if present
			if (control.getMasterships().size()>i) {
				MastershipModification tmp = control.getMasterships().get(i);
				if (tmp.getSkill()==null) {
					logger.debug("Ignore MastershipModification without skill: "+tmp);
					continue;
				}
				logger.debug("-----display "+tmp.getSkill()+" from "+master_skill_cb[i].getItems());
				if (!master_skill_cb[i].getItems().contains(tmp.getSkill())) {
					master_skill_cb[i].getItems().add(tmp.getSkill());
					Collections.sort(master_skill_cb[i].getItems());
				}
				master_skill_cb[i].getSelectionModel().select(tmp.getSkill());
				master_mod_cb[i].getSelectionModel().select(tmp);
			}
			
			// Selection
			master_mod_cb[i].getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MastershipModification>() {
				public void changed(ObservableValue<? extends MastershipModification> cb,
						MastershipModification oldMod, MastershipModification newMod) {
					logger.debug("Changed "+oldMod+" to "+newMod);
					if (oldMod!=null)
						control.setMastership(oldMod, false);
					
					if (newMod!=null)
						control.setMastership(newMod, true);
					
					pointsLeft.setText(Integer.toString(control.getPointsMasterships()));
					parent.updateButtons();
				}
			});
		}
	}

	//-------------------------------------------------------------------
	void updateContent() {
		logger.debug("update masterships");
		
		pointsLeft.setText(String.valueOf(control.getPointsMasterships()));
		
		parent.updateButtons();
	}

	//--------------------------------------------------------------------
	void skillAdded(Skill skill) {
		for (ChoiceBox<Skill> cb : master_skill_cb) {
			if (!cb.getItems().contains(skill)) {
				cb.getItems().add(skill);
				Collections.sort(cb.getItems());
			}			
		}
	}

	//--------------------------------------------------------------------
	void skillRemoved(Skill skill) {
		logger.debug("skillRemoved("+skill+")");
		for (int i=0; i<master_skill_cb.length; i++) {
			ChoiceBox<Skill> cb = master_skill_cb[i];
			if (cb.getItems().contains(skill)) {
				logger.debug("Is set as skill "+i);
				if (cb.getSelectionModel().getSelectedItem()==skill) {
					master_mod_cb[i].getSelectionModel().select(-1);
					master_mod_cb[i].getItems().clear();
				}
				
				cb.getItems().remove(skill);
			}			
		}
	}

}
