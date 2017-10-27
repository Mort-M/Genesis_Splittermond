/**
 * 
 */
package org.prelle.splimo.creature;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ConditionalModification;
import org.prelle.splimo.modifications.CreatureFeatureModification;
import org.prelle.splimo.modifications.CreatureTypeModification;
import org.prelle.splimo.modifications.DamageModification;
import org.prelle.splimo.modifications.FeatureModification;
import org.prelle.splimo.modifications.ItemFeatureModification;
import org.prelle.splimo.modifications.ItemModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.modifications.CountModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.modifications.SpellModification;
import org.prelle.splimo.requirements.RequirementList;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CreatureModule extends BasePluginData implements Comparable<CreatureModule>{

	public enum Type {
		BASE,
		ROLE,
		OPTION,
		TRAINING
	}
	
	@Attribute(required=true)
	private String id;
	@Attribute(required=true)
	private Type type;
	@Attribute
	private int cost;
	@Attribute
	private boolean multiple;
	
	@ElementListUnion(inline=true,value={
	    @ElementList(entry="attrmod", type=AttributeModification.class),
	    @ElementList(entry="mastermod", type=MastershipModification.class),
	    @ElementList(entry="powermod", type=PowerModification.class),
	    @ElementList(entry="selmod", type=ModificationChoice.class),
	    @ElementList(entry="skillmod", type=SkillModification.class),
	    @ElementList(entry="spellmod", type=SpellModification.class),
	    @ElementList(entry="featuremod", type=FeatureModification.class),
	    @ElementList(entry="creaturefeaturemod", type=CreatureFeatureModification.class),
	    @ElementList(entry="creaturetypemod", type=CreatureTypeModification.class),
	    @ElementList(entry="damagemod", type=DamageModification.class),
	    @ElementList(entry="countmod", type=CountModification.class),
	    @ElementList(entry="itemmod", type=ItemModification.class),
	    @ElementList(entry="itemfeaturemod", type=ItemFeatureModification.class),
	    @ElementList(entry="condmod", type=ConditionalModification.class),
	 })
	private List<Modification> modifications;
	@Element(name="requires")
	private RequirementList requirements;

	//-------------------------------------------------------------------
	/**
	 */
	public CreatureModule() {
		modifications = new ArrayList<Modification>();
		requirements  = new RequirementList();
	}

	//-------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CreatureModule other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("creaturemodule."+id);
		} catch (MissingResourceException mre) {
			logger.error("Missing property '"+mre.getKey()+"' in "+i18n.getBaseBundleName());
		}
		return "creaturemodule."+id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "creaturemodule."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "creaturemodule."+id+".desc";
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
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
	 * @return the requirements
	 */
	public RequirementList getRequirements() {
		return requirements;
	}

	//-------------------------------------------------------------------
	/**
	 * @param requirements the requirements to set
	 */
	public void setRequirements(RequirementList requirements) {
		this.requirements = requirements;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public List<Modification> getModifications() {
		return new ArrayList<Modification>(modifications);
	}

	//-------------------------------------------------------------------
	/**
	 * @param modifications the modifications to set
	 */
	public void setModifications(List<Modification> modifications) {
		this.modifications = modifications;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the multiple
	 */
	public boolean canBeUsedMultiple() {
		return multiple;
	}

	//-------------------------------------------------------------------
	/**
	 * @param multiple the multiple to set
	 */
	public void setUsedMultiple(boolean multiple) {
		this.multiple = multiple;
	}


}
