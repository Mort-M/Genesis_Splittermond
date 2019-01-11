/**
 * 
 */
package org.prelle.splimo.items;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.Skill;
import org.prelle.splimo.persist.SkillConverter;
import org.prelle.splimo.persist.WeaponDamageConverter;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.RequirementList;

/**
 * @author prelle
 *
 */
@Root(name = "weapon")
public class Weapon extends ItemTypeData {

	private final static Logger logger = LogManager.getLogger("splittermond.items");
	
	@org.prelle.simplepersist.Attribute
	@AttribConvert(SkillConverter.class)
	protected Skill skill;
	@org.prelle.simplepersist.Attribute
	protected int speed;
	@org.prelle.simplepersist.Attribute(name="att1")
	protected Attribute attribute1;
	@org.prelle.simplepersist.Attribute(name="att2")
	protected Attribute attribute2;
	/**
	 * Coded XXYYZZ
	 * XX = Number of dice
	 * YY = Dice type
	 * ZZ = Modifier
	 */
	@org.prelle.simplepersist.Attribute
	@AttribConvert(WeaponDamageConverter.class)
	protected int damage;
	@Element
	protected RequirementList requires;
	@Element
	protected FeatureList features;
	
	//--------------------------------------------------------------------
	public Weapon() {
		super(ItemType.WEAPON);
		attribute1 = Attribute.AGILITY;
		requires = new RequirementList();
		features = new FeatureList();
		damage   = 600;
	}
	
	//--------------------------------------------------------------------
	public String toString() {
		return "Weapon(dmg="+damage+",spd="+speed+",a1="+attribute1+",a2="+attribute2+")";
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Weapon) {
            Weapon other = (Weapon) o;
            try {
                if (skill != other.getSkill()) return false;
                if (attribute1 != other.getAttribute1()) return false;
                if (attribute2 != other.getAttribute2()) return false;
                if (speed != other.getSpeed()) return false;
                if (damage     != other.getDamage()) return false;
                if (!requires.equals(other.getRequirements())) return false;
                if (!features.equals(other.deliverFeaturesAsDeepClone())) return false;
                return true;
            } catch (Exception e) {
                logger.error("Error comparing items",e);
            }
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
	 * @return the attribute1
	 */
	public Attribute getAttribute1() {
		return attribute1;
	}

	//--------------------------------------------------------------------
	/**
	 * @param attribute1 the attribute1 to set
	 */
	public void setAttribute1(Attribute attribute1) {
		this.attribute1 = attribute1;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the attribute2
	 */
	public Attribute getAttribute2() {
		return attribute2;
	}

	//--------------------------------------------------------------------
	/**
	 * @param attribute2 the attribute2 to set
	 */
	public void setAttribute2(Attribute attribute2) {
		this.attribute2 = attribute2;
	}

	//--------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	//--------------------------------------------------------------------
	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	//--------------------------------------------------------------------
	/**
	 * @param damage the damage to set
	 */
	public void setDamage(Integer damage) {
		this.damage = damage;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the requires
	 */
	public RequirementList getRequirements() {
		return requires;
	}

	//--------------------------------------------------------------------
	/**
	 * @param req the requires to set
	 */
	public void addRequirement(Requirement req) {
		requires.add(req);
	}

	//--------------------------------------------------------------------
	/**
	 * @return the {@link Feature} entries as deep copied {@link List}
	 */
	public List<Feature> deliverFeaturesAsDeepClone() {
		// make deep copy 		
		return features.stream().map(Feature::clone).collect(Collectors.toList());
	} 
	
	// TODO Pastore 10.04.2017: It's not a good solution. CarriedItem should copy the member features and Weapon should always return its member features without shallow or deep cloning.
	
	/** 
	 * @return the {@link Feature} entries of the member list
	 */
	public List<Feature> getFeatures() { 
		return features;
	}

	//--------------------------------------------------------------------
	/**
	 * @param feat the Feature to set
	 */
	public void addFeature(Feature feat) {
		features.add(feat);
	}
	
	//--------------------------------------------------------------------
	public static int getDamageDiceCount(Weapon data) {
		return data.getDamage()/10000;
	}
	
	//--------------------------------------------------------------------
	public static int getDamageDiceType(Weapon data) {
		return (data.getDamage()/100)%100;
	}
	
	//--------------------------------------------------------------------
	public static int getDamageModifier(Weapon data) {
		int v = data.getDamage();
		if ((v%100)>90)
			return -(100 - v%100);
		return data.getDamage()%100;
	}

}
