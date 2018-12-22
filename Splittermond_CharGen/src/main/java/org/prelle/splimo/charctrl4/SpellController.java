/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.Collection;
import java.util.List;

import org.prelle.splimo.Skill;
import org.prelle.splimo.SpellValue;

import de.rpgframework.genericrpg.ToDoElement;

/**
 * @author prelle
 *
 */
public interface SpellController extends Controller, Generator {

	public class FreeSelection implements Comparable<FreeSelection> {
		private Skill school;
		private int   level;
		private SpellValue usedFor;
		public FreeSelection(Skill school, int lvl) {
			this.school = school;
			this.level  = lvl;
		}
		//--------------------------------------------------------------------
		public Skill getSchool() {return school;}
		//--------------------------------------------------------------------
		public int getLevel() {	return level;}
		//--------------------------------------------------------------------
		public SpellValue getUsedFor() {return usedFor;}
		public void setUsedFor(SpellValue used) { usedFor = used; }
		//--------------------------------------------------------------------
		public String toString() {
			return school+" up to level "+level;
		}
		//--------------------------------------------------------------------
		@Override
		public int compareTo(FreeSelection other) {
			int cmp = school.compareTo(other.getSchool());
			if (cmp!=0) return cmp;
			return ((Integer)level).compareTo(other.getLevel());
		}
	}
	
	//-------------------------------------------------------------------
	/**
	 * Returns a list of steps to do in this controller
	 */
	public List<ToDoElement> getToDos(Skill skill);
	
	//--------------------------------------------------------------------
	/**
	 * Returns a list of free selections that were granted when the level
	 * in a spell school reached a threshold.
	 */
	public Collection<FreeSelection> getFreeSelections();
	
	//--------------------------------------------------------------------
	/**
	 * Returns a list of free selections that were granted when the level
	 * in a spell school reached a threshold.
	 */
	public Collection<FreeSelection> getUnusedFreeSelections();
	
	//--------------------------------------------------------------------
	public Collection<FreeSelection> getUnusedFreeSelections(Skill school);
	
	//--------------------------------------------------------------------
	public List<SpellValue> getPossibleSpells(FreeSelection token);
	
	//--------------------------------------------------------------------
	/**
	 * Check if it is possible to select the spell for free.
	 * @return If it is possible, return a token to use for the
	 *   <code>select</code> operation. Otherwise return NULL.
	 */
	public FreeSelection canBeFreeSelected(SpellValue spell);
	
	//--------------------------------------------------------------------
	public void select(FreeSelection token, SpellValue spell);
	
	//--------------------------------------------------------------------
	/**
	 * Obtain a list of spell schools with at least one point
	 */
	public List<Skill> getAvailableSpellSchools();
	
	//--------------------------------------------------------------------
	/**
	 * Obtain a list of spells in the given school that can be bought 
	 * by experience points (depending on threshold) that haven't been 
	 * bought yet
	 */
	public List<SpellValue> getAvailableSpells(Skill school);
	
//	//--------------------------------------------------------------------
//	/**
//	 * Obtain a list of spells that can be bought by experience points
//	 * (depending on threshold in spell schools) in all schools that 
//	 * haven't been bought yet
//	 */
//	public List<SpellValue> getAvailableSpells();
	
	//--------------------------------------------------------------------
	/**
	 * Check if all requirements are met to buy this spell.
	 */
	public boolean canBeSelected(SpellValue spell);
	
	//--------------------------------------------------------------------
	/**
	 * Check if all requirements are met to undo buying this spell.
	 */
	public boolean canBeDeSelected(SpellValue spell);
	
	//--------------------------------------------------------------------
	/**
	 * Add the spell paying with experience points
	 * @return TRUE when adding spell was successful
	 */
	public boolean select(SpellValue spell);
	
	//--------------------------------------------------------------------
	/**
	 * Remove the spell and get experience points back
	 * @return TRUE when removing spell was successful
	 */
	public boolean deselect(SpellValue spell);
	
}
