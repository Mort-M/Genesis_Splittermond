/**
 * 
 */
package org.prelle.splittermond.jfx.skills;

import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.charctrl.SkillController;

import javafx.scene.control.Control;

/**
 * @author prelle
 *
 */
public class ToDoSkillValueListView extends Control {
	
	private CharacterController ctrl;
	private SkillType type;

	//-------------------------------------------------------------------
	public ToDoSkillValueListView(CharacterController charGen, SkillType type) {
		this.ctrl = charGen;
		this.type = type;
		setSkin(new ToDoSkillValueListViewGridPaneSkin(this));
	}

	//-------------------------------------------------------------------
	public SpliMoCharacter getModel() {
		return ctrl.getModel();
	}

	//-------------------------------------------------------------------
	public SkillController getSkillController() {
		return ctrl.getSkillController();
	}

	//-------------------------------------------------------------------
	public MastershipController getMastershipController() {
		return ctrl.getMastershipController();
	}

	//-------------------------------------------------------------------
	public SkillType getSkillType() {
		return type;
	}
}
