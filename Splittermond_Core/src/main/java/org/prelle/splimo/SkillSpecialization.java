/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.ResourceI18N;

/**
 * @author prelle
 *
 */
public class SkillSpecialization implements MastershipOrSpecialization {
	
	public enum SkillSpecializationType {
		NORMAL,
		WEAPON,
		SPELLTYPE
		;
	}

	@Attribute
	private String id;
	@Attribute(required=false)
	private SkillSpecializationType type = SkillSpecializationType.NORMAL;
	private transient Skill skill;

	//-------------------------------------------------------------------
	public SkillSpecialization() {
		
	}

	//-------------------------------------------------------------------
	public SkillSpecialization(Skill skill, SkillSpecializationType type, String id) {
		this.skill = skill;
		this.type  = type;
		this.id    = id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		if (type==SkillSpecializationType.SPELLTYPE) {
			if (id.indexOf("/")>0)
				return SplitterMondCore.getI18nResources().getString("spell.type."+id.toLowerCase().substring(id.indexOf("/")+1));
			return SplitterMondCore.getI18nResources().getString("spell.type."+id.toLowerCase());
		}
		if (type==SkillSpecializationType.WEAPON) {
			if (SplitterMondCore.getI18nResources().containsKey("skillspecial."+id.toLowerCase()))
				return SplitterMondCore.getI18nResources().getString("skillspecial."+id.toLowerCase());
			if (SplitterMondCore.getItem(id)!=null)
				return SplitterMondCore.getItem(id).getName();
			if (SplittermondCustomDataCore.getItem(id)!=null)
				return SplittermondCustomDataCore.getItem(id).getName();
			return "SkillSpecialization.getName()";
		}
		if (id!=null)
			return ResourceI18N.get(SplitterMondCore.getI18nResources(),"skillspecial."+id);
		return "any "+type;
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof SkillSpecialization) {
			SkillSpecialization other = (SkillSpecialization)o;
			if (type!=other.getType()) return false;
			return id.equals(other.getId());
		}
		return false;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return type+": "+skill+"("+id+")";
	}

	//-------------------------------------------------------------------
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public SkillSpecializationType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(SkillSpecializationType type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MastershipOrSpecialization other) {
		return Collator.getInstance().compare(this.getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.MastershipOrSpecialization#getLevel()
	 */
	@Override
	public int getLevel() {
		return 1;
	}

}
