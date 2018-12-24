package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;

public interface MastershipController extends Controller {

	public class FreeMastershipSelection implements Comparable<FreeMastershipSelection> {
		private Skill skill;
		private int   level;
		private MastershipReference usedFor;
		public FreeMastershipSelection(Skill school, int lvl) {
			this.skill = school;
			this.level  = lvl;
		}
		//--------------------------------------------------------------------
		public Skill getSkill() {return skill;}
		public void setSkill(Skill skill) {this.skill = skill;}
		//--------------------------------------------------------------------
		public int getLevel() {	return level;}
		//--------------------------------------------------------------------
		public MastershipReference getUsedFor() {return usedFor;}
		public void setUsedFor(MastershipReference used) { usedFor = used; }
		//--------------------------------------------------------------------
		public String toString() {
			return "Level "+level+" mastership in "+skill;
		}
		//--------------------------------------------------------------------
		@Override
		public int compareTo(FreeMastershipSelection other) {
			int cmp = skill.compareTo(other.getSkill());
			if (cmp!=0) return cmp;
			return ((Integer)level).compareTo(other.getLevel());
		}
	}

	
	//-------------------------------------------------------------------
	public int getFreeMasterships();

	//-------------------------------------------------------------------
	public int getFreeMasterships(Skill skill);
	
	//-------------------------------------------------------------------
	/**
	 * Returns a list of steps to do in this controller
	 */
	public List<String> getToDos(Skill skill);

	//-------------------------------------------------------------------
	public int getAssignedFreeMasterships(Skill skill);

	//-------------------------------------------------------------------
	public boolean isEditable(SkillSpecialization special, int level);

	//-------------------------------------------------------------------
	public boolean isEditable(Mastership master);

	//-------------------------------------------------------------------
	public boolean select(SkillSpecialization special, int level);

	//-------------------------------------------------------------------
	public boolean deselect(SkillSpecialization special, int level);

	//-------------------------------------------------------------------
	public boolean select(Mastership master);

	//-------------------------------------------------------------------
	public boolean deselect(Mastership master);

	//-------------------------------------------------------------------
	public boolean canBeSelected(Mastership master);

	//-------------------------------------------------------------------
	public List<String> getUnfulfilledRequirements(Mastership master);

	//-------------------------------------------------------------------
	public boolean canBeSelected(SkillSpecialization special, int level);

	//-------------------------------------------------------------------
	public boolean canBeDeselected(Mastership master);

	//-------------------------------------------------------------------
	public boolean canBeDeselected(SkillSpecialization special, int level);

}