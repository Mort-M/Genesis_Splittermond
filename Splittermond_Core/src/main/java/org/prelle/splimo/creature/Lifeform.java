/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.List;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splimo.requirements.CreatureFeatureRequirement;
import org.prelle.splimo.requirements.CreatureModuleRequirement;
import org.prelle.splimo.requirements.CreatureTypeRequirement;
import org.prelle.splimo.requirements.DamageRequirement;
import org.prelle.splimo.requirements.ItemFeatureRequirement;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.SkillRequirement;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellValue;

/**
 * @author prelle
 *
 */
public interface Lifeform {

	//-------------------------------------------------------------------
	public List<CreatureTypeValue> getCreatureTypes();
	
//	//-------------------------------------------------------------------
//	public void setAttribute(Attribute key, int val);

	//-------------------------------------------------------------------
	public AttributeValue getAttribute(Attribute key);

//	//-------------------------------------------------------------------
//	public void setSkill(SkillValue skillVal);
//
//	//-------------------------------------------------------------------
//	public int getSkillPoints(Skill key);

	//-------------------------------------------------------------------
    public List<SkillValue> getSkills();

	//-------------------------------------------------------------------
    /**
     * Returns a list of skills of a given type.
     * @param type SkillType
     * @return List of skills of the given type, sorted alphabetically
     */
    public List<SkillValue> getSkills(SkillType type);

	//-------------------------------------------------------------------
	public SkillValue getSkillValue(Skill skill);

	//-------------------------------------------------------------------
	public boolean hasSkill(Skill skill);
		   
//	//-------------------------------------------------------------------
//	public void addSpell(SpellValue tmp);
//
//	//--------------------------------------------------------------------
//	public void removeSpell(SpellValue spell);
	
	//-------------------------------------------------------------------
	public List<SpellValue> getSpells();

	//--------------------------------------------------------------------
	public boolean hasSpell(SpellValue spell);

//	//-------------------------------------------------------------------
//	public void clearSpells();

	//-------------------------------------------------------------------
	/**
	 * Get the value for a specific spell, including skill value and
	 * eventually existing spell type specializations
	 */
	public int getSpellValueFor(SpellValue spellVal);

	//-------------------------------------------------------------------
	public List<CreatureWeapon> getCreatureWeapons();

	//-------------------------------------------------------------------
	public List<CreatureFeature> getFeatures();
	
	//-------------------------------------------------------------------
//	public boolean isRequirementMet(Requirement req);

}
