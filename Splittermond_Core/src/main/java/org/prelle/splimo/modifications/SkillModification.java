/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.persist.SkillConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "skillmod")
public class SkillModification extends ModificationImpl {

	public enum RestrictionType {
		ANY,
		// One of the existing
		MUST_EXIST,
		// One of the not existing
		MUST_NOT_EXIST,
		// All existing
		ANY_EXIST
	}
	
	@Attribute(required=false)
	private ModificationValueType type;
	@Attribute
	@AttribConvert(SkillConverter.class)
	private Skill ref;
	@Attribute(required=false)
	private int value;
	@org.prelle.simplepersist.Attribute(name="modsrc")
    private ModificationSource modSource;
	@Attribute(required=false,name="choice")
	private SkillType choiceType;
	@Attribute(required=false,name="restrict")
	private RestrictionType restrictionType;

	//-------------------------------------------------------------------
	/**
	 */
	public SkillModification() {
        type = ModificationValueType.RELATIVE;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SkillModification(Skill data, int value) {
        type = ModificationValueType.RELATIVE;
		this.ref = data;
		this.value = value;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SkillModification(ModificationValueType type, Skill data, int value) {
        this.type  = type;
		this.ref   = data;
		this.value = value;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SkillModification(String skillID, int value) {
        type = ModificationValueType.RELATIVE;
		this.ref = SplitterMondCore.getSkill(skillID);
		this.value = value;
 	}

	//-------------------------------------------------------------------
	/**
	 */
	public SkillModification(String skillID, int value, Object src) {
		this(skillID, value);
		this.source = src;
	}

	//-------------------------------------------------------------------
	public String dump() {
		return String.format("SkillMod(type=%s, ref=%s, value=%d, modSrc=%s, choiceType=%s, restr=%s)", 
				String.valueOf(type), ref, value, modSource, choiceType, restrictionType);
	}

	//-------------------------------------------------------------------
	public String toString() {
		String sourceString = source != null ? " (" + source + ")" : "";
		
		StringBuffer buf = new StringBuffer();
		if (ref==null) {
			buf.append("Select ");
			if (choiceType==null) 
				buf.append((restrictionType==RestrictionType.ANY_EXIST)?"ALL":"ANY");
			else {
				buf.append((restrictionType==RestrictionType.ANY_EXIST)?"all":"any "+choiceType);
			}
			buf.append(" skill");
			if (restrictionType!=null) {
				switch (restrictionType) {
				case ANY:
					break;
				case MUST_EXIST:
				case ANY_EXIST:
					buf.append(" that has been selected before"); break;
				case MUST_NOT_EXIST:
					buf.append(" that has not been selected before"); break;
				}
			}
				
		} else {
			buf.append("For skill "+ref.getName());
		}
		
		
		if (type==ModificationValueType.ABSOLUT) {
			buf.append(" and set it to "+value);
		} else {
			buf.append(" and add "+value);
		}
		buf.append(sourceString);
		return buf.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		if (other instanceof SkillModification) {
			if (((SkillModification)other).getSkill()!=null)
				return ref.compareTo( ((SkillModification)other).getSkill() );
			return 0;
		}
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.modifications.ModificationImpl#clone()
	 */
	@Override
	public SkillModification clone() {
		SkillModification ret = new SkillModification(type,ref, value);
		ret.setModificationSource(modSource);
    	ret.cloneAdd(this);
    	return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Skill getSkill() {
		return ref;
	}

	//-------------------------------------------------------------------
	public String getSkillName() {
		return ref.getName();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	//-------------------------------------------------------------------
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public ModificationValueType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(ModificationValueType type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the modSource
	 */
	public ModificationSource getModificationSource() {
		return modSource;
	}

	//-------------------------------------------------------------------
	/**
	 * @param modSource the modSource to set
	 */
	public void setModificationSource(ModificationSource modSource) {
		this.modSource = modSource;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the choiceType
	 */
	public SkillType getChoiceType() {
		return choiceType;
	}

	//-------------------------------------------------------------------
	/**
	 * @param choiceType the choiceType to set
	 */
	public void setChoiceType(SkillType choiceType) {
		this.choiceType = choiceType;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the restrictionType
	 */
	public RestrictionType getRestrictionType() {
		if (restrictionType==null)
			return RestrictionType.ANY;
		return restrictionType;
	}

	//-------------------------------------------------------------------
	/**
	 * @param restrictionType the restrictionType to set
	 */
	public void setRestrictionType(RestrictionType restrictionType) {
		this.restrictionType = restrictionType;
	}

}
