/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.DamageType;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureList;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.persist.ItemConverter;
import org.prelle.splimo.persist.SkillConverter;
import org.prelle.splimo.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
@Root(name = "cweapon")
public class CreatureWeapon {

	private static Logger logger = LogManager.getLogger("splittermond");
	
	private static PropertyResourceBundle CORE = SplitterMondCore.getI18nResources();
	
	@Attribute(required=true)
	private String id;
	@Attribute(name="name")
	private String customName;
	@Attribute(name="val",required=true)
	private int value;
	@Attribute(name="skill",required=false)
	@AttribConvert(SkillConverter.class)
	private Skill skill;
	@Attribute(name="item",required=false)
	@AttribConvert(ItemConverter.class)
	private ItemTemplate item;
	@Attribute(name="dmg",required=true)
	@AttribConvert(WeaponDamageConverter.class)
	private int damage;
	@Attribute(name="dmgtype")
	private DamageType damageType;
	@Attribute(name="spd",required=true)
	private int speed;
	@Attribute(name="ini",required=true)
	private int ini;
	@Element
	protected FeatureList features;
	

	//-------------------------------------------------------------------
	public CreatureWeapon() {
		features = new FeatureList();
	}

	//-------------------------------------------------------------------
	public CreatureWeapon(String id) {
		this.id = id;
		features = new FeatureList();
	}

	//-------------------------------------------------------------------
	public CreatureWeapon(CreatureWeapon toClone) {
		this.id = toClone.id;
		this.customName = toClone.customName;
		this.value = toClone.value;
		this.skill = toClone.skill;
		this.item  = toClone.item;
		this.damage = toClone.damage;
		this.damageType = toClone.damageType;
		this.speed = toClone.speed;
		this.ini = toClone.ini;
		this.features = new FeatureList(toClone.features);
	}

	//--------------------------------------------------------------------
	public String getName() {
		String name = null;
		if (skill!=null && skill.getId().equals("melee"))
			name = CORE.getString("creature.weapon.melee");
		if (item!=null)
			name = item.getName();
		if (customName!=null)
			name = customName;
		
		if (name==null)
			name = toString();
		
		return name;
	}

	//--------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	//--------------------------------------------------------------------
	public Skill getSkill() {
		if (skill!=null)
			return skill;
		if (item!=null) {
			return item.getType(Weapon.class).getSkill();
		}
		return null;
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

	//-------------------------------------------------------------------
	/**
	 * @return the item
	 */
	public ItemTemplate getItem() {
		return item;
	}

	//-------------------------------------------------------------------
	/**
	 * @param item the item to set
	 */
	public void setItem(ItemTemplate item) {
		this.item = item;
		logger.info("Set item to "+item);
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
	 * @return the ini
	 */
	public int getInitiative() {
		return ini;
	}

	//-------------------------------------------------------------------
	/**
	 * @param ini the ini to set
	 */
	public void setInitiative(int ini) {
		this.ini = ini;
		logger.info("Set initiative to "+ini);
	}

	//--------------------------------------------------------------------
	/**
	 * @return the requires
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the requires
	 */
	public Feature getFeature(FeatureType type) {
		for (Feature feat : features) {
			if (feat.getType()==type)
				return feat;
		}
		return null;
	}

	//--------------------------------------------------------------------
	/**
	 * @param requires the requires to set
	 */
	public void addFeature(Feature feat) {
		features.add(feat);
	}

	//-------------------------------------------------------------------
	public Feature addFeature(FeatureType key) {
		for (Feature feat : features) {
			if (feat.getType()==key)
				return feat;
		}
		Feature ret = new Feature(key);
		features.add(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	public void removeFeature(FeatureType key) {
		for (Feature feat : features) {
			if (feat.getType()==key) {
				features.remove(feat);
				return;
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @return the customName
	 */
	public String getCustomName() {
		return customName;
	}

	//-------------------------------------------------------------------
	/**
	 * @param customName the customName to set
	 */
	public void setCustomName(String customName) {
		this.customName = customName;
		logger.info("Set name to "+customName);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damageType
	 */
	public DamageType getDamageType() {
		return damageType;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damageType the damageType to set
	 */
	public void setDamageType(DamageType damageType) {
		this.damageType = damageType;
	}

}
