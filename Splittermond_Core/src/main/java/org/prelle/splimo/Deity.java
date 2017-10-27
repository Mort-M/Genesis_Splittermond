/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.splimo.modifications.EducationModification;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.persist.AspectConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class Deity extends BasePluginData implements Comparable<Deity> {

	@Attribute(name="id")
	private String id;
	@ElementList(type=Aspect.class,convert=AspectConverter.class,entry="aspect")
	private List<Aspect> aspects;
	@Element
	private ModificationList modifications;

	//-------------------------------------------------------------------
	/**
	 */
	public Deity() {
		aspects = new ArrayList<>();
		modifications = new ModificationList();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		return i18n.getString("deity."+id);
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Deity other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "deity."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "deity."+id+".desc";
	}

	//-------------------------------------------------------------------
	public List<Aspect> getAspects() {
		return new ArrayList<Aspect>(aspects);
	}

	//-------------------------------------------------------------------
	public List<Skill> getFavoredSkills() {
		List<Skill> ret = new ArrayList<>();
		for (Modification mod : modifications) {
			if (mod instanceof SkillModification) {
				SkillModification sMod = (SkillModification)mod;
				if (sMod.getValue()>=0)
					ret.add(sMod.getSkill());
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public List<Skill> getUnusualSkills() {
		List<Skill> ret = new ArrayList<>();
		for (Modification mod : modifications) {
			if (mod instanceof SkillModification) {
				SkillModification sMod = (SkillModification)mod;
				if (sMod.getValue()<0)
					ret.add(sMod.getSkill());
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public List<Education> getFavoredEducations() {
		List<Education> ret = new ArrayList<>();
		for (Modification mod : modifications) {
			if (mod instanceof EducationModification) {
				EducationModification eMod = (EducationModification)mod;
				ret.add(eMod.getEducation());
			}
		}
		return ret;
	}

}
