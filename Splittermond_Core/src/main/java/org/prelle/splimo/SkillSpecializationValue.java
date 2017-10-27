/**
 * 
 */
package org.prelle.splimo;


/**
 * @author prelle
 *
 */
public class SkillSpecializationValue {

	private SkillSpecialization special;
	private int level;

	//-------------------------------------------------------------------
	public SkillSpecializationValue(SkillSpecialization special, int level) {
		this.special = special;
		this.level   = level;
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof SkillSpecializationValue) {
			SkillSpecializationValue other = (SkillSpecializationValue)o;
			if (level!=other.getLevel()) return false;
			return special.equals(other.getSpecial());
		}
		return false;
	}

	//-------------------------------------------------------------------
	public String getName() {
		return special.getName()+" "+level;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return special+" "+level;
	}

	//-------------------------------------------------------------------
	public int getLevel() {
		return level;
	}

	//-------------------------------------------------------------------
	public void setLevel(int level) {
		this.level = level;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the special
	 */
	public SkillSpecialization getSpecial() {
		return special;
	}

	//-------------------------------------------------------------------
	/**
	 * @param special the special to set
	 */
	public void setSpecial(SkillSpecialization special) {
		this.special = special;
	}

}
