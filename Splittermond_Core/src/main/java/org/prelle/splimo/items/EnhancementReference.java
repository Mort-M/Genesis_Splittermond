/**
 * 
 */
package org.prelle.splimo.items;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.persist.EnhancementConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name="enhanceref")
public class EnhancementReference {

	@Attribute(name="ref", required=true)
	@AttribConvert(EnhancementConverter.class)
	private Enhancement enhancement;
	@Element(name="special", required=false)
	private SkillSpecialization skillSpecialization;
	@Element(name="spellval", required=false)
	private SpellValue spell;
	@Element(name="power", required=false)
	private PowerReference powerRef;
	
	/*
	 * Can be used to distinguish otherwise identical modifications
	 */
	private transient String uniqueID;
	private transient List<Modification> modifications;
	
	//-------------------------------------------------------------------
	public EnhancementReference() {
		modifications = new ArrayList<Modification>();
	}
	
	//-------------------------------------------------------------------
	@Override
	public boolean equals(Object o) {
		if (o instanceof EnhancementReference) {
			EnhancementReference other = (EnhancementReference)o;
			if (enhancement!=other.getEnhancement()) return false;
			if (skillSpecialization!=other.getSkillSpecialization()) return false;
			if (uniqueID==null) {
				if (other.getUniqueID()!=null) return false;
			} else {
				if (other.getUniqueID()==null) return false;
				if (!uniqueID.equals(other.getUniqueID())) return false;
			}
			return true;
		}
		return false;
	}
	
	//-------------------------------------------------------------------
	/**
	 */
	public EnhancementReference(Enhancement enhance) {
		this();
		this.enhancement = enhance;
	}
	
	//-------------------------------------------------------------------
	public String toString() {
		return "("+enhancement+" or "+skillSpecialization+" or "+spell+")("+uniqueID+")";
	}
	
//	//-------------------------------------------------------------------
//	@Override
//	public boolean equals(Object o) {
//		if (o instanceof EnhancementReference) {
//			EnhancementReference other = (EnhancementReference)o;
//			if (enhancement!=other.getEnhancement()) return false;
//			return true;
//		}
//		return false;
//	}
	
	//-------------------------------------------------------------------
	public String getName() {
		if (skillSpecialization!=null)
			return enhancement.getName()+" ("+skillSpecialization.getName()+")";
		if (spell!=null)
			return enhancement.getName()+" ("+spell.getSpell().getName()+")";
		if (powerRef!=null)
			return enhancement.getName()+" ("+powerRef.toString()+")";
		return enhancement.getName();
	}
	
	//-------------------------------------------------------------------
	public Enhancement getEnhancement() {
		return enhancement;
	}
	
	//-------------------------------------------------------------------
	public String getID() {
		return super.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skillSpecialization
	 */
	public SkillSpecialization getSkillSpecialization() {
		return skillSpecialization;
	}

	//-------------------------------------------------------------------
	/**
	 * @param skillSpecialization the skillSpecialization to set
	 */
	public void setSkillSpecialization(SkillSpecialization skillSpecialization) {
		this.skillSpecialization = skillSpecialization;
	}

	//-------------------------------------------------------------------
	public SpellValue getSpellValue() {
		return spell;
	}

	//-------------------------------------------------------------------
	public void setSpellValue(SpellValue spell) {
		this.spell = spell;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the uniqueID
	 */
	public String getUniqueID() {
		return uniqueID;
	}

	//-------------------------------------------------------------------
	/**
	 * @param uniqueID the uniqueID to set
	 */
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	//-------------------------------------------------------------------
	public PowerReference getPower() {
		return powerRef;
	}

	//-------------------------------------------------------------------
	public void setPower(PowerReference powerRef) {
		this.powerRef = powerRef;
	}

	//-------------------------------------------------------------------
	public List<Modification> getModifications() {
		return modifications;
	}

}
