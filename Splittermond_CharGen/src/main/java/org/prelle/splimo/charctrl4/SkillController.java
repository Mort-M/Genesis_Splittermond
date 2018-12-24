package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;

import de.rpgframework.genericrpg.ToDoElement;

public interface SkillController extends Controller {
	
	//-------------------------------------------------------------------
	/**
	 * Returns a list of steps to do in this controller
	 */
	public List<ToDoElement> getToDos(SkillType type);
	
	//-------------------------------------------------------------------
	/**
	 * Returns a list of steps to do in this controller
	 */
	public List<ToDoElement> getToDos(Skill skill);

	//-------------------------------------------------------------------
	public int getMaxSkill();

	//-------------------------------------------------------------------
	public boolean canBeIncreased(SkillValue skill);

	//-------------------------------------------------------------------
	public boolean canBeDecreased(SkillValue skill);

	//-------------------------------------------------------------------
	public boolean increase(SkillValue skill);

	//-------------------------------------------------------------------
	public boolean decrease(SkillValue skill);

}