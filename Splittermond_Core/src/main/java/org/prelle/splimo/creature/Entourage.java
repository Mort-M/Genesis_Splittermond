/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Attributes;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SpellValue;

/**
 * @author prelle
 *
 */
public class Entourage implements Lifeform {

	private static Logger logger = LogManager.getLogger("splittermond");
	
	@org.prelle.simplepersist.Attribute(name="race")
	private String race;
	@org.prelle.simplepersist.Attribute(name="culture")
	private String culture;
	@Element
	private Attributes attributes;
	@ElementList(entry="skillval", type=SkillValue.class)
	private List<SkillValue> skillvals;
	@ElementList(entry="spellval", type=SpellValue.class)
	private List<SpellValue> spellvals;
	@Element
	private String description;
	
	private Map<String, Object> choicesByOptionID;

	//-------------------------------------------------------------------
	public Entourage() {
		attributes   = new Attributes();
		skillvals    = new ArrayList<SkillValue>();
		spellvals    = new ArrayList<SpellValue>();
		choicesByOptionID = new HashMap<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getAttribute(org.prelle.splimo.Attribute)
	 */
	@Override
	public AttributeValue getAttribute(Attribute key) {
		return attributes.get(key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSkills()
	 */
	@Override
	public List<SkillValue> getSkills() {
		List<SkillValue> ret = new ArrayList<SkillValue>(skillvals);
		// Sort alphabetically
		Collections.sort(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#hasSkill(org.prelle.splimo.Skill)
	 */
	@Override
    public boolean hasSkill(Skill skill) {
		for (SkillValue tmp : skillvals)
			if (tmp.getSkill()==skill && tmp.getModifiedValue()>0)
				return true;
    	return false;
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSkills(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<SkillValue> getSkills(SkillType type) {
		List<SkillValue> ret = new ArrayList<SkillValue>();
		for (SkillValue tmp : new ArrayList<SkillValue>(skillvals)) {
			if (tmp.getSkill()==null) {
				logger.warn("Remove unset skill from character: "+tmp);
				skillvals.remove(tmp);
				continue;
			}
			if (tmp.getSkill().getType()==type)
				ret.add(tmp);
		}
		// Sort alphabetically
		Collections.sort(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSkillValue(org.prelle.splimo.Skill)
	 */
	@Override
	public SkillValue getSkillValue(Skill skill) {
		for (SkillValue tmp : skillvals)
			if (tmp.getSkill()==skill)
				return tmp;

		SkillValue sVal = new SkillValue(skill, 0);
		skillvals.add(sVal);
		return sVal;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSpells()
	 */
	@Override
	public List<SpellValue> getSpells() {
		List<SpellValue> ret = new ArrayList<SpellValue>(spellvals);
		Collections.sort(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#hasSpell(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean hasSpell(SpellValue spell) {
		return getSpells().contains(spell);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSpellValueFor(org.prelle.splimo.SpellValue)
	 */
	@Override
	public int getSpellValueFor(SpellValue spellVal) {
		SkillValue skillVal = getSkillValue(spellVal.getSkill());
		// Calculate value for skill, including attributes
		int val = skillVal.getValue();
		val += getAttribute(skillVal.getSkill().getAttribute1()).getValue();
		val += getAttribute(skillVal.getSkill().getAttribute2()).getValue();

		for (SpellType type : spellVal.getSpell().getTypes()) {
			SkillSpecialization spec = new SkillSpecialization();
			spec.setType(SkillSpecializationType.SPELLTYPE);
			spec.setId(type.name());
			val += skillVal.getSpecializationLevel(spec);
		}

		return val;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	//-------------------------------------------------------------------
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getCreatureTypes()
	 */
	@Override
	public List<CreatureTypeValue> getCreatureTypes() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getCreatureWeapons()
	 */
	@Override
	public List<CreatureWeapon> getCreatureWeapons() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getFeatures()
	 */
	@Override
	public List<CreatureFeature> getFeatures() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

}
