/**
 * 
 */
package org.prelle.splimo;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.persist.SkillConverter;
import org.prelle.splimo.persist.SpellConverter;

/**
 * @author prelle
 *
 */
@Root(name = "spellval")
public class SpellValue implements Comparable<SpellValue> {

	
	@Attribute(name="spell")
	@AttribConvert(SpellConverter.class)
	private Spell spell;
	@Attribute(name="school")
	@AttribConvert(SkillConverter.class)
	private Skill skill;
	/** Spell was selected by reaching skill threshold for reaching this level */
	@Attribute(required=false)
	private int free;

	//-------------------------------------------------------------------
	public SpellValue() {
		free = 0;
	}

	//-------------------------------------------------------------------
	public SpellValue(Spell spell, Skill skill) {
		this.spell = spell;
		this.skill = skill;
		free = -1;
	}

	//-------------------------------------------------------------------
	public SpellValue clone() {
		SpellValue ret = new SpellValue(spell, skill);
		ret.setFreeLevel(free);
		return ret;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return spell+" ("+skill+")("+free+")";
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof SpellValue) {
			SpellValue other = (SpellValue)o;
			if (spell!=other.getSpell()) return false;
			return skill==other.getSkill();
		}
		return false;
	}

	//-------------------------------------------------------------------
	public Spell getSpell() {
		return spell;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SpellValue other) {
		return spell.compareTo(other.getSpell());
	}

	//--------------------------------------------------------------------
	public boolean wasFree() {
		return free>-1;
	}

	//--------------------------------------------------------------------
	/**
	 * Get skill level threshold reached which free selection was used
	 * for this spell
	 */
	public int getFreeLevel() {
		return free;
	}

	//--------------------------------------------------------------------
	public void setFreeLevel(int free) {
		this.free = free;
	}

	//--------------------------------------------------------------------
	public int getSpellLevel() {
		return spell.getLevelInSchool(skill);
	}

}
