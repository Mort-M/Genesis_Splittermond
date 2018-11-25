/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.requirements.MastershipRequirement;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.RequirementList;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class Mastership extends BasePluginData implements MastershipOrSpecialization {
	
	private final Collator COLLATOR = Collator.getInstance();

	@Attribute
	private String key;
	@Attribute
	private int level;	
	@Attribute(required=false)
	private boolean multiple;	
	private transient Skill skill;
	@Element(name="requires")
	private RequirementList requires;
	@Element
	private ModificationList modifications= new ModificationList();
	@Attribute
	private boolean common;	
	
	private transient SkillSpecialization focus;
	
	//-------------------------------------------------------------------
	public Mastership() {
		requires = new RequirementList();
	}
	
	//-------------------------------------------------------------------
	public Mastership(String id, int level) {
		requires = new RequirementList();
		this.level = level;
		this.key   = id;
	}
	
	//-------------------------------------------------------------------
	public Mastership(Mastership toClone, SkillSpecialization focus) {
		this.key   = toClone.getKey();
		this.level = toClone.getLevel();
		this.multiple = toClone.isMultiple();
		requires = new RequirementList(toClone.getPrerequisites());
		modifications = new ModificationList(toClone.getModifications());
		this.common = toClone.isCommon();
		this.skill  = toClone.getSkill();
		super.i18n = toClone.getResourceBundle();
		super.i18nHelp = toClone.getHelpResourceBundle();
		super.plugin = toClone.getPlugin();
		this.focus = focus;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.PluginData#getId()
	 */
	@Override
	public String getId() {
		if (focus!=null)
			return key+"_"+focus.getId();
		return key;
	}
	
	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof Mastership) {
			Mastership other = (Mastership)o;
			if (!getKey().equals(other.getKey())) return false;
			if (level!=other.getLevel()) return false;
			if (focus!=other.getFocus()) return false;
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "mastership."+key+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		if (common)
			return "mastership."+key+".desc";
		return "mastership."+skill.getId()+"."+key+".desc";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.MastershipOrSpecialization#getName()
	 */
	@Override
	public String getName() {
		String mastershipKey = "mastership."+key;
		try {
			if (focus!=null)
				return i18n.getString(mastershipKey)+" ("+focus.getName()+")";
			return i18n.getString(mastershipKey);
		} catch (MissingResourceException e) {
			if (!reportedKeys.contains(e.getKey())) {
				reportedKeys.add(e.getKey());
				logger.warn(String.format("key missing:    %s   %s", i18n.getBaseBundleName(), e.getKey()));
				if (MISSING!=null) {
					MISSING.println(e.getKey()+"=");
				}
			}
			return key;
		}
	}

	//-------------------------------------------------------------------
	public String getShortDescription() {
		String mastershipDescriptionKey = String.format("mastership.%s.shortdesc", key);
		try{
			String shortDescription = i18n.getString(mastershipDescriptionKey);
			String page = String.valueOf(getPage());
			return  String.format("%s (%s %s)", shortDescription, getProductNameShort(), page);
		} catch (MissingResourceException e){
			if (!reportedKeys.contains(mastershipDescriptionKey)) {
				reportedKeys.add(mastershipDescriptionKey);
				logger.warn(String.format("key missing:   %s   %s", i18n.getBaseBundleName(), mastershipDescriptionKey));
				if (MISSING!=null) {
					MISSING.println(mastershipDescriptionKey+"=");
				}
			}
			return " ";
		}
	}

	//-------------------------------------------------------------------
	public String toString() {
//		if (modifications!=null && !modifications.isEmpty())
//			return key+" "+modifications;
		return getKey();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the key
	 */
	public String getKey() {
		if (focus!=null)
			return key+"_"+focus.getId();
		return key;
	}

	//-------------------------------------------------------------------
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	//-------------------------------------------------------------------
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.MastershipOrSpecialization#getSkill()
	 */
	@Override
	public Skill getSkill() {
		return skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	//-------------------------------------------------------------------
	public List<Requirement> getPrerequisites() {
		return new ArrayList<Requirement>(requires);
//		List<Mastership> ret = new ArrayList<Mastership>();
//		logger.debug(key+"  req="+req);
//		if (req==null) return ret;
//		if (skill==null) {
//			logger.error("No skill resolved in mastership "+key);
//			return ret;
//		}
//		
//		Collection<String> tmp = new ArrayList<String>();
//		StringTokenizer tok = new StringTokenizer(req, ", ");
//		while (tok.hasMoreTokens())
//			tmp.add(tok.nextToken());
//		
//		for (String ref : tmp) {
//			Mastership data = skill.getMastership(ref);
//			if (data==null)
//				logger.error("Cannot resolve required mastership '"+ref+"' in mastership "+this);
//			else if (!ret.contains(data))
//				ret.add(data);
//		}
//		
//		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MastershipOrSpecialization other) {
		int comp = skill.compareTo(other.getSkill());
		if (comp!=0) return comp;
		comp = ((Integer)level).compareTo(other.getLevel());
		if (comp!=0) return comp;
		return COLLATOR.compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the attrmod
	 */
	public Collection<Modification> getModifications() {
		if (modifications==null) return new ArrayList<Modification>();
		return modifications;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the common
	 */
	public boolean isCommon() {
		return common;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the multiple
	 */
	public boolean isMultiple() {
		return multiple;
	}

	//-------------------------------------------------------------------
	/**
	 * @param mReq
	 * @param mastershipRequirement
	 */
	public void replaceRequirement(MastershipRequirement mReq, MastershipRequirement mastershipRequirement) {
		requires.remove(mReq);
		requires.add(mastershipRequirement);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the focus
	 */
	public SkillSpecialization getFocus() {
		return focus;
	}

}
