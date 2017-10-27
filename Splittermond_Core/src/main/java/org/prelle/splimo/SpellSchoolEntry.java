package org.prelle.splimo;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.persist.SkillConverter;

@Root(name = "school")
public class SpellSchoolEntry implements Comparable<SpellSchoolEntry> {
	
	@Attribute
	@AttribConvert(SkillConverter.class)
	private Skill skill;
	@Attribute
	private int level;

	//-------------------------------------------------------------------
	public SpellSchoolEntry() {
	}

	//-------------------------------------------------------------------
	public SpellSchoolEntry(Skill school, int level) {
		if (school.getType()!=Skill.SkillType.MAGIC)
			throw new IllegalArgumentException(school+" is not a magic school");
		this.skill = school;
		this.level  = level;
	}
	
	//-------------------------------------------------------------------
	public String toString() {
		return skill+" "+level;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @return the school
	 */
	public Skill getSchool() {
		return skill;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @param school the school to set
	 */
	public void setSchool(Skill school) {
		this.skill = school;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SpellSchoolEntry other) {
		return Integer.compare(level, other.getLevel());
	}

}