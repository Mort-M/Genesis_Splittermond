/**
 *
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.UniqueObject;
import org.prelle.splimo.persist.CreatureConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "creatinst")
public class CreatureReference extends UniqueObject implements Lifeform, Comparable<CreatureReference> {

	@Element
	private String name;
	@Attribute(name="ref")
	@AttribConvert(CreatureConverter.class)
	private Creature ref;
	@Element(name="modcreature")
	private ModuleBasedCreature modBasedCreature;
	@Element
	private Entourage entourage;;
	@ElementList(entry="creatmodref", type=CreatureModuleReference.class) //,convert=CreatureModuleConverter.class)
	protected List<CreatureModuleReference> trainings;
	
	private transient ResourceReference resourceRef;
	private transient List<Modification> trainingMods;

	//-------------------------------------------------------------------
	public CreatureReference() {
		trainings = new ArrayList<CreatureModuleReference>();
		trainingMods = new ArrayList<Modification>();
	}

	//-------------------------------------------------------------------
	public CreatureReference(Creature ref) {
		trainings = new ArrayList<CreatureModuleReference>();
		this.ref = ref;
		trainingMods = new ArrayList<Modification>();
	}

	//-------------------------------------------------------------------
	public CreatureReference(ModuleBasedCreature mod) {
		trainings = new ArrayList<CreatureModuleReference>();
		this.modBasedCreature = mod;
		trainingMods = new ArrayList<Modification>();
	}
	
	//-------------------------------------------------------------------
	public Creature getTemplate() {
		return ref;
	}

	//-------------------------------------------------------------------
	public ModuleBasedCreature getModuleBasedCreature() {
		return modBasedCreature;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CreatureReference other) {
		return ref.compareTo(other.getTemplate());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the name
	 */
	public String getName() {
    	if (name!=null)
    		return name;
    	if (ref!=null)
    		return ref.getName();
    	return "Unnamed Creature";
	}

	//-------------------------------------------------------------------
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	//-------------------------------------------------------------------
	public boolean hasAttribute(org.prelle.splimo.Attribute key) {
		if (modBasedCreature!=null)
			return modBasedCreature.getAttribute(key)!=null;
		else
			return ref.getAttribute(key)!=null;
	}

	//-------------------------------------------------------------------
	public AttributeValue getAttribute(org.prelle.splimo.Attribute key) {
		AttributeValue ret = null;
		if (modBasedCreature!=null) {
			AttributeValue unmod = modBasedCreature.getAttribute(key);
			if (unmod==null)
				throw new NullPointerException("No attribute "+key+" in creature template");
			ret = new AttributeValue(key, unmod.getValue());
		} else {
			AttributeValue unmod = ref.getAttribute(key);
			if (unmod==null)
				throw new NullPointerException("No attribute "+key+" in creature template");
			ret = new AttributeValue(key, unmod.getValue());
		}
		// Add trainings
		for (Modification mod : trainingMods) {
			if (mod instanceof AttributeModification && ((AttributeModification)mod).getAttribute()==key)
				ret.addModification((AttributeModification)mod);
		}
		
		return ret;
	}

//	//-------------------------------------------------------------------
//	public List<CreatureWeapon> getCreatureWeapons() {
//		// TODO Auto-generated method stub
//		return ref.getCreatureWeapons();
//	}


    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.creature.Lifeform#getSkills()
     */
	@Override
    public List<SkillValue> getSkills() {
		if (modBasedCreature!=null)
			return modBasedCreature.getSkills();
		else
			return ref.getSkills();
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#hasSkill(org.prelle.splimo.Skill)
	 */
	@Override
	public boolean hasSkill(Skill skill) {
		if (modBasedCreature!=null)
			return modBasedCreature.hasSkill(skill);
		else
			return ref.hasSkill(skill);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSkills(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<SkillValue> getSkills(SkillType type) {
		List<SkillValue> ret = new ArrayList<>();
		for (SkillValue tmp : ((modBasedCreature!=null)?modBasedCreature.getSkills(type):ref.getSkills(type))) {
			ret.add(getSkillValue(tmp.getSkill()));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSkillValue(org.prelle.splimo.Skill)
	 */
	@Override
	public SkillValue getSkillValue(Skill skill) {
		SkillValue val = null;
		if (modBasedCreature!=null)
			val =  modBasedCreature.getSkillValue(skill);
		else
			val = ref.getSkillValue(skill);
		
		SkillValue ret = new SkillValue(val);
		for (Modification mod : trainingMods) {
			if (mod instanceof SkillModification && ((SkillModification)mod).getSkill()==skill)
				ret.addModification((SkillModification)mod);
			if (mod instanceof MastershipModification && ((SkillModification)mod).getSkill()==skill) {
				MastershipModification mmod = (MastershipModification)mod;
				if (mmod.getMastership()!=null)
					ret.addMastership(new MastershipReference(mmod.getMastership()));
			}
		}
		
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSpells()
	 */
	@Override
	public List<SpellValue> getSpells() {
		if (modBasedCreature!=null)
			return modBasedCreature.getSpells();
		else
			return ref.getSpells();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#hasSpell(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean hasSpell(SpellValue spell) {
		if (modBasedCreature!=null)
			return modBasedCreature.hasSpell(spell);
		else
			return ref.hasSpell(spell);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getSpellValueFor(org.prelle.splimo.SpellValue)
	 */
	@Override
	public int getSpellValueFor(SpellValue spellVal) {
		if (modBasedCreature!=null)
			return modBasedCreature.getSpellValueFor(spellVal);
		else
			return ref.getSpellValueFor(spellVal);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getCreatureTypes()
	 */
	@Override
	public List<CreatureTypeValue> getCreatureTypes() {
		if (modBasedCreature!=null)
			return modBasedCreature.getCreatureTypes();
		if (ref!=null)
			return ref.getCreatureTypes();
		else
			return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getCreatureWeapons()
	 */
	@Override
	public List<CreatureWeapon> getCreatureWeapons() {
		List<CreatureWeapon> ret = new ArrayList<>();
		List<CreatureWeapon> unmod ;
		if (modBasedCreature!=null)
			unmod = modBasedCreature.getCreatureWeapons();
		else
			unmod = ref.getCreatureWeapons();
		
		for (CreatureWeapon tmp : unmod) {
			CreatureWeapon weap = new CreatureWeapon(tmp);
			for (Modification mod : trainingMods) {
				System.out.println("Consider "+mod);
				if (mod instanceof SkillModification && ((SkillModification)mod).getSkill().getId().equals("melee")) {
					System.out.println(".. apply "+((SkillModification)mod).getValue());
					weap.setValue(tmp.getValue() + ((SkillModification)mod).getValue() );
				}
			}
			ret.add(weap);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.creature.Lifeform#getFeatures()
	 */
	@Override
	public List<CreatureFeature> getFeatures() {
		if (modBasedCreature!=null)
			return modBasedCreature.getFeatures();
		else
			return ref.getFeatures();
	}

	//-------------------------------------------------------------------
	public CreatureFeature getCreatureFeature(CreatureFeatureType feature) {
		for (CreatureFeature feat : getFeatures()) {
			if (feat.getType()==feature)
				return feat;
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the entourage
	 */
	public Entourage getEntourage() {
		return entourage;
	}

	//-------------------------------------------------------------------
	/**
	 * @param entourage the entourage to set
	 */
	public void setEntourage(Entourage entourage) {
		this.entourage = entourage;
	}

	//-------------------------------------------------------------------
	public ResourceReference getResource() {
		return resourceRef;
	}

	//-------------------------------------------------------------------
	/**
	 * @param resrc the relic to set
	 */
	public void setResource(ResourceReference resrc) {
		this.resourceRef = resrc;
	}

	//-------------------------------------------------------------------
	public List<CreatureModuleReference> getTrainings() {
		return trainings;
	}

	//-------------------------------------------------------------------
	public boolean hasTraining(CreatureModule mod) {
		return trainings.stream().anyMatch(t -> t.getModule()==mod);
	}

	//-------------------------------------------------------------------
	public void addTraining(CreatureModuleReference value) {
		trainings.add(value);
	}

	//-------------------------------------------------------------------
	public void removeTraining(CreatureModuleReference value) {
		trainings.remove(value);
	}

	//-------------------------------------------------------------------
	public int getInvestedPotential() {
		int ret = 0;
		for (CreatureModuleReference tmp : trainings) {
			ret += tmp.getModule().getCost();
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public CreatureTypeValue getCreatureType(CreatureType key) {
		for (CreatureTypeValue tmp : getCreatureTypes()) {
			if (tmp.getType()==key)
				return tmp;
		}
		return null;
	}

	//-------------------------------------------------------------------
	public void clearTrainingModifications() {
		trainingMods.clear();
	}

	//-------------------------------------------------------------------
	public void addTrainingModifications(Modification mod) {
		System.out.println("CreatureReference.addTrainingModification("+mod+")");
		trainingMods.add(mod);
	}
	
}
