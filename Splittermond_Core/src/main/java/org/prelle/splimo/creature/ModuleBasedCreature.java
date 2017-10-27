/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
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
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
@Root(name = "modcreature")
public class ModuleBasedCreature implements Lifeform {

	@Element(required=true)
	protected CreatureModuleReference base;
	@Element(required=true)
	protected CreatureModuleReference role;
	@ElementList(type=CreatureModuleReference.class,entry="option")
	protected List<CreatureModuleReference> options;

	private transient List<AttributeValue> attributes;
	@ElementList(entry="creaturetype",type=CreatureTypeValue.class)
	private List<CreatureTypeValue> creaturetypes;
	@Attribute(name="lvl2", required=false)
	protected int levelGroup;
	
	
	private transient List<CreatureWeapon> weapons;
	private transient List<SkillValue> skillvals;
	private transient List<SpellValue> spellvals;
	private transient List<CreatureFeature> features;

	//-------------------------------------------------------------------
	public ModuleBasedCreature() {
		options   = new ArrayList<CreatureModuleReference>();
		
		attributes   = new ArrayList<AttributeValue>();
		weapons      = new ArrayList<CreatureWeapon>();
		skillvals    = new ArrayList<SkillValue>();
		skillvals.add(new SkillValue(SplitterMondCore.getSkill("melee"),0));
		spellvals    = new ArrayList<SpellValue>();
		creaturetypes= new ArrayList<CreatureTypeValue>();
		features     = new ArrayList<CreatureFeature>();
	}

	//-------------------------------------------------------------------
	public void clear() {
		attributes.clear();
		for (org.prelle.splimo.Attribute attr : org.prelle.splimo.Attribute.primaryValues()) {
			AttributeValue toAdd = new AttributeValue(attr, 0);
			attributes.add(toAdd);
		}
		for (org.prelle.splimo.Attribute attr : org.prelle.splimo.Attribute.secondaryValues()) {
			AttributeValue toAdd = new AttributeValue(attr, 0);
			attributes.add(toAdd);
		}
		skillvals.clear();
		skillvals.add(new SkillValue(SplitterMondCore.getSkill("melee"),0));
		spellvals.clear();
//		creaturetypes.clear();
		features.clear();
		weapons.clear();
	}

	//-------------------------------------------------------------------
	public String dump() {
		StringBuffer buf = new StringBuffer();
		
		for (CreatureModuleReference tmp : options) {
			buf.append("\n"+tmp.getModule());
		}
		
		return buf.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the base
	 */
	public CreatureModule getBase() {
		if (base==null)
			return null;
		return base.getModule();
	}

	//-------------------------------------------------------------------
	/**
	 * @param base the base to set
	 */
	public void setBase(CreatureModuleReference base) {
		this.base = base;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the role
	 */
	public CreatureModuleReference getRole() {
		return role;
	}

	//-------------------------------------------------------------------
	/**
	 * @param role the role to set
	 */
	public void setRole(CreatureModuleReference role) {
		this.role = role;
	}

	//-------------------------------------------------------------------
	public List<CreatureModuleReference> getOptions() {
		return new ArrayList<>(options);
	}

	//-------------------------------------------------------------------
	public void addOption(CreatureModuleReference mod) {
		options.add(mod);
	}

	//-------------------------------------------------------------------
	public void removeOption(CreatureModuleReference mod) {
		options.remove(mod);
	}

	//-------------------------------------------------------------------
	public void setAttribute(org.prelle.splimo.Attribute key, int value) {
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
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getAttribute(org.prelle.splimo.Attribute)
	 */
	@Override
	public AttributeValue getAttribute(org.prelle.splimo.Attribute key) {
		for (AttributeValue pair : attributes) {
			if (pair.getAttribute()==key) {
				return pair;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	public void setSkill(SkillValue skillVal) {
		if (!skillvals.contains(skillVal))
			skillvals.add(skillVal);
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
	 * @see org.prelle.splimo.creature.Lifeform#getSkills(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
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
	 * @see org.prelle.splimo.creature.Lifeform#getSkillValue(org.prelle.splimo.Skill)
	 */
	@Override
	public SkillValue getSkillValue(Skill skill) {
		for (SkillValue tmp : skillvals)
			if (tmp.getSkill()==skill)
				return tmp;
		
//		SkillValue sVal = new SkillValue(skill, 0);
//		skillvals.add(sVal);
//		return sVal;
		return null;
	}

	//-------------------------------------------------------------------
	public void addSpell(SpellValue tmp) {
		if (tmp==null)
			throw new NullPointerException();
		if (tmp.getSkill()==null)
			throw new NullPointerException("No skilL");
		spellvals.add(tmp);
	}

	//--------------------------------------------------------------------
	public void removeSpell(SpellValue spell) {
		spellvals.remove(spell);
	}

	//-------------------------------------------------------------------
	public void clearSpells() {
		spellvals.clear();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSpells()
	 */
	@Override
	public List<SpellValue> getSpells() {
		return new ArrayList<SpellValue>(spellvals);
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
	public boolean hasOption(CreatureModule mod) {
		return options.stream().anyMatch(opt -> opt.getModule()==mod);
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
	   if (key==null)
		   throw new NullPointerException("Key may not be null");
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
   public CreatureTypeValue getCreatureType(CreatureType key) {
	   for (CreatureTypeValue feat : creaturetypes) {
		   if (feat.getType()==key)
			   return feat;
	   }
	   return null;
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
	public void setCreatureTypes(List<CreatureTypeValue> types) {
		this.creaturetypes = types;
	}
	
	//-------------------------------------------------------------------
//	@Override
	public boolean isRequirementMet(Requirement req) {
		if (req instanceof CreatureModuleRequirement) {
			CreatureModuleRequirement foo = (CreatureModuleRequirement)req;
			CreatureModule option = foo.getModule();
			boolean found = hasOption(option);
			for (CreatureModuleReference ref : getOptions())
				if (ref.getModule()==option) found=true;
			if (foo.isNegated()) {
				return !found;
			} else
				return found;
		} else if (req instanceof AttributeRequirement) {
			AttributeRequirement foo = (AttributeRequirement)req;
			org.prelle.splimo.Attribute key = foo.getAttribute();
			AttributeValue aVal = getAttribute(key);
			return aVal.getValue()>=foo.getValue();
		} else if (req instanceof CreatureFeatureRequirement) {
			CreatureFeatureRequirement foo = (CreatureFeatureRequirement)req;
			CreatureFeature tmp = getCreatureFeature(foo.getFeature());
			boolean hasFeature = tmp!=null;
			return foo.isNegated()?!hasFeature:hasFeature;
		} else if (req instanceof SkillRequirement) {
			SkillRequirement foo = (SkillRequirement)req;
			Skill key = foo.getSkill();
			SkillValue aVal = getSkillValue(key);
			if (aVal==null)
				return foo.getValue()==0;
			return aVal.getModifiedValue()>=foo.getValue();
		} else if (req instanceof CreatureTypeRequirement) {
			CreatureTypeRequirement foo = (CreatureTypeRequirement)req;
			CreatureType key = foo.getType();
			boolean typeMatch = getCreatureType(key)!=null;
			return foo.isNegated()?!typeMatch:typeMatch;
		} else if (req instanceof ItemFeatureRequirement) {
			ItemFeatureRequirement foo = (ItemFeatureRequirement)req;
			FeatureType key = foo.getFeature();
			for (CreatureWeapon weapon : getCreatureWeapons()) {
				if (weapon.getFeature(key)!=null) {
					return !foo.isNegated();
				}
			}
			return foo.isNegated();
		} else if (req instanceof DamageRequirement) {
			DamageRequirement foo = (DamageRequirement)req;
			CreatureWeapon weapon = getCreatureWeapons().get(0);
			return weapon.getDamage()>=foo.getDamage();
		} else {
				throw new RuntimeException("Don't know how to verify this requirement: "+req.getClass());
		}
	}

}
