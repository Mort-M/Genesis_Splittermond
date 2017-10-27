package org.prelle.splimo.charctrl;

import java.util.List;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;

public interface SkillController extends Controller {
	
	//-------------------------------------------------------------------
	/**
	 * Returns a list of steps to do in this controller
	 */
	public List<String> getToDos(SkillType type);
	
	//-------------------------------------------------------------------
	/**
	 * Returns a list of steps to do in this controller
	 */
	public List<String> getToDos(Skill skill);

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