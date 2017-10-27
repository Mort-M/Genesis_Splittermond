/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Skill;
import org.prelle.splimo.persist.SkillConverter;

/**
 * @author prelle
 *
 */
@Root(name = "projectile")
public class Projectile extends ItemTypeData {

	@org.prelle.simplepersist.Attribute
	@AttribConvert(SkillConverter.class)
	protected Skill skill;
	
	//--------------------------------------------------------------------
	public Projectile() {
		super(ItemType.PROJECTILE);
	}
	
	//--------------------------------------------------------------------
	public String toString() {
		return "Projectile(skill="+skill+")";
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Projectile) {
			Projectile other = (Projectile)o;
			if (!super.equals(other)) return false;
            if (skill != other.getSkill()) return false;
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	//--------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

}
