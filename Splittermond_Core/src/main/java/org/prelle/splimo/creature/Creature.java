/**
 * 
 */
package org.prelle.splimo.creature;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.CreatureModuleConverter;
import org.prelle.splimo.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
@Root(name = "creature")
public class Creature extends BasePluginData implements Lifeform,Comparable<Creature> {
	
	public final static List<String> DEFAULT_SKILL_NAMES = Arrays.asList("acrobatics", "athletics", "determination",
			"stealth", "hunting","perception", "endurance");

	@org.prelle.simplepersist.Attribute
	protected String id;

	@ElementList(entry="attr", type=AttributeValue.class)
	protected List<AttributeValue> attributes;
	@ElementList(entry="creaturetype", type=CreatureTypeValue.class)
	protected List<CreatureTypeValue> creaturetypes;
	@org.prelle.simplepersist.Attribute(name="lvl1", required=false)
	protected int levelAlone;
	@org.prelle.simplepersist.Attribute(name="lvl2", required=false)
	protected int levelGroup;
	@ElementList(type=CreatureWeapon.class)
	protected List<CreatureWeapon> weapons;
	@ElementList(entry="skillval", type=SkillValue.class)
	protected List<SkillValue> skillvals;
	@ElementList(entry="spellval", type=SpellValue.class)
	protected List<SpellValue> spellvals;
	@ElementList(type=CreatureFeature.class)
	protected List<CreatureFeature> features;

	/* For self built creatures */
	@org.prelle.simplepersist.Attribute
	@AttribConvert(CreatureModuleConverter.class)
	protected CreatureModule base;
	@org.prelle.simplepersist.Attribute
	@AttribConvert(CreatureModuleConverter.class)
	protected CreatureModule role;
	@ElementList(type=CreatureModule.class,entry="option",convert=CreatureModuleConverter.class)
	protected List<CreatureModule> options;

	
	//-------------------------------------------------------------------
	public Creature() {
		attributes   = new ArrayList<AttributeValue>();
		weapons      = new ArrayList<CreatureWeapon>();
		skillvals    = new ArrayList<SkillValue>();
		spellvals    = new ArrayList<SpellValue>();
		creaturetypes= new ArrayList<CreatureTypeValue>();
		features     = new ArrayList<CreatureFeature>();
		options      = new ArrayList<CreatureModule>();
		
		for (Attribute attr : Attribute.values()) {
			if (attr==Attribute.SPLINTER)
				continue;
//			if (attr==Attribute.INITIATIVE)
//				continue;
			AttributeValue toAdd = new AttributeValue(attr, 0);
			attributes.add(toAdd);
		}
	}

	//-------------------------------------------------------------------
	public void initializeDefaultSkills() {
		for (String id : DEFAULT_SKILL_NAMES) {
			Skill skill = SplitterMondCore.getSkill(id);
			skillvals.add(new SkillValue(skill, 0));
		}
	}

	//-------------------------------------------------------------------
	public Creature(String id) {
		this();
		this.id = id;
	}

	//-------------------------------------------------------------------
	public String dump() {
		WeaponDamageConverter dmgConvert = new WeaponDamageConverter();
		StringBuffer buf = new StringBuffer();
		buf.append("Name  : "+id);
		
		for (Attribute attr : Attribute.values()) {
			buf.append("\n"+attr.getShortName()+": "+getAttribute(attr)+"  \t");
		}
		
		buf.append("\n\nWaffen:\n=============");
		for (CreatureWeapon tmp : weapons) {
			buf.append("\n "+tmp.getName()+" : "+tmp.getValue()+"  "+dmgConvert.write(tmp.getDamage())+" "+tmp.getSpeed()+"  "+tmp.getInitiative()+"  "+tmp.getFeatures());
		}
		
		buf.append("\n\nFertigkeiten:\n=============");
		for (SkillValue tmp : skillvals) {
			buf.append("\n "+tmp.getSkill().getName()+" : "+tmp.getModifiedValue());
		}
		
		buf.append("\n\nZauber:\n=============");
		for (SpellValue tmp : spellvals) {
			buf.append("\n "+tmp.getSpell().getName()+" : "+tmp);
		}
		
		buf.append("\n\nMerkmale:\n=============");
		for (CreatureFeature tmp : features) {
			buf.append("\n "+tmp.getName());
		}
		
		return buf.toString();
	}

	//-------------------------------------------------------------------
	public boolean isCustom() {
		return id.startsWith("custom");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "creature."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "creature."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String getName() {
		if (i18n==null)
			return "creature."+id;
		try {
			return i18n.getString("creature."+id);
		} catch (MissingResourceException mre) {
			logger.error("Missing property 'creature."+id+"' in "+i18n.getBaseBundleName());
		}
		return "creature."+id;
	}

	//-------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public void setAttribute(Attribute key, int value) {
		for (AttributeValue pair : attributes) {
			if (pair.getAttribute()==key) {
				pair.setDistributed(value);
				return;
			}
		}

		AttributeValue toAdd = new AttributeValue(key, value);
		attributes.add(toAdd);
	}

	//-------------------------------------------------------------------
	public AttributeValue getAttribute(Attribute key) {
		for (AttributeValue pair : attributes) {
			if (pair.getAttribute()==key) {
				return pair;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	public void setSkill(SkillValue skillVal) {
		if (!skillvals.contains(skillVal.getSkill()))
			skillvals.add(skillVal);
	}

	//-------------------------------------------------------------------
	public int getSkillPoints(Skill key) {
		for (SkillValue tmp : skillvals)
			if (tmp.getSkill()==key)
				return tmp.getModifiedValue();
		return 0;
	}

	//-------------------------------------------------------------------
    /**
     * Returns a list of skills of a given type.
     * @param type SkillType
     * @return List of skills of the given type, sorted alphabetically
     */
    public List<SkillValue> getSkills(SkillType type) {
        List<SkillValue> ret = new ArrayList<SkillValue>();
        for (SkillValue tmp : skillvals)
            if (tmp.getSkill().getType()==type)
                ret.add(tmp);
        // Sort alphabetically
        Collections.sort(ret);
        return ret;
    }

	//-------------------------------------------------------------------
    public void addSkill(SkillValue sval) {
    	for (SkillValue check : skillvals) {
    		if (check.getSkill()==sval.getSkill())
    			throw new IllegalArgumentException("Already exists");
    	}
    	skillvals.add(sval);
    }

	//-------------------------------------------------------------------
    public void removeSkill(SkillValue sval) {
    	if (skillvals.remove(sval))
    		return;
    	
    	for (SkillValue check : skillvals) {
    		if (check.getSkill()==sval.getSkill()) {
    			skillvals.remove(check);
    			return;
    		}
    			
    	}
    }

    //-------------------------------------------------------------------
    public List<SkillValue> getSkills() {
        List<SkillValue> ret = new ArrayList<SkillValue>(skillvals);
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
	public SkillValue getSkillValue(Skill skill) {
		for (SkillValue tmp : skillvals)
			if (tmp.getSkill()==skill)
				return tmp;
		
		SkillValue sVal = new SkillValue(skill, 0);
		skillvals.add(sVal);
		return sVal;
//		throw new NoSuchElementException();
	}

	//-------------------------------------------------------------------
	public void addSpell(SpellValue tmp) {
		spellvals.add(tmp);
	}

	//--------------------------------------------------------------------
	public void removeSpell(SpellValue spell) {
		spellvals.remove(spell);
	}

	//-------------------------------------------------------------------
	public List<SpellValue> getSpells() {
		return new ArrayList<SpellValue>(spellvals);
	}

	//--------------------------------------------------------------------
	public boolean hasSpell(SpellValue spell) {
		return getSpells().contains(spell);
	}

	//-------------------------------------------------------------------
	public void clearSpells() {
		spellvals.clear();
	}

	//-------------------------------------------------------------------
	/**
	 * Get the value for a specific spell, including skill value and
	 * eventually existing spell type specializations
	 */
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
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Creature other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
    public List<CreatureFeature> getFeatures() {
        return new ArrayList<>(features);
    }

	//-------------------------------------------------------------------
    /**
     * @return Value >0 if feature is present, -1 otherwise
     */
    public int getCreatureFeatureLevel() {
        for (CreatureFeature feat : features) {
        	if (feat.getType().getId().equals("CREATURE"))
        		return feat.getLevel();
        }
        return -1;
    }

	//-------------------------------------------------------------------
   public CreatureFeature getCreatureFeature(CreatureFeatureType key) {
        for (CreatureFeature feat : features) {
        	if (feat.getType()==key)
        		return feat;
        }
        return null;
    }

   //-------------------------------------------------------------------
   public CreatureFeature addCreatureFeature(CreatureFeatureType key) {
	   for (CreatureFeature feat : features) {
		   if (feat.getType()==key)
			   return feat;
	   }
	   CreatureFeature ret = new CreatureFeature(key);
	   features.add(ret);
	   return ret;
   }

   //-------------------------------------------------------------------
   public void removeCreatureFeature(CreatureFeatureType key) {
	   for (CreatureFeature feat : features) {
		   if (feat.getType()==key) {
			   features.remove(feat);
			   return;
		   }
	   }
   }

	//-------------------------------------------------------------------
    public void addCreatureType(CreatureTypeValue cType) {
    	for (CreatureTypeValue val : creaturetypes) {
    		if (val.getType()==cType.getType()) {
    			if (val.getLevel()==cType.getLevel())
    				return;    		
    			val.setLevel(Math.max(val.getLevel(), cType.getLevel()));
    			return;
    		}
    	}
    	
    	creaturetypes.add(cType);
    }

	//-------------------------------------------------------------------
    public void removeCreatureType(CreatureTypeValue cType) {
    	for (CreatureTypeValue val : creaturetypes) {
    		if (val.getType()==cType.getType()) {
    			creaturetypes.remove(val);
    			return;
    		}
    	}
    }

	//-------------------------------------------------------------------
    public List<CreatureTypeValue> getCreatureTypes() {
        return new ArrayList<>(creaturetypes);
    }

	//-------------------------------------------------------------------
    public void addWeapon(CreatureWeapon data) {
//    	for (CreatureWeapon val : weapons) {
//    		if (val.
//    			if (val.getLevel()==cType.getLevel())
//    				return;    		
//    			val.setLevel(Math.max(val.getLevel(), cType.getLevel()));
//    			return;
//    		}
//    	}
    	
    	weapons.add(data);
    }

	//-------------------------------------------------------------------
	public void removeWeapon(CreatureWeapon data) {
		weapons.remove(data);
	}

	//-------------------------------------------------------------------
    public void removeCreatureType(CreatureWeapon data) {
    	weapons.remove(data);
    }

	//-------------------------------------------------------------------
    public List<CreatureWeapon> getCreatureWeapons() {
        return new ArrayList<>(weapons);
    }

	//-------------------------------------------------------------------
	/**
	 * @return the levelAlone
	 */
	public int getLevelAlone() {
		return levelAlone;
	}

	//-------------------------------------------------------------------
	/**
	 * @param levelAlone the levelAlone to set
	 */
	public void setLevelAlone(int levelAlone) {
		this.levelAlone = levelAlone;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the levelGroup
	 */
	public int getLevelGroup() {
		return levelGroup;
	}

	//-------------------------------------------------------------------
	/**
	 * @param levelGroup the levelGroup to set
	 */
	public void setLevelGroup(int levelGroup) {
		this.levelGroup = levelGroup;
	}
    
}
