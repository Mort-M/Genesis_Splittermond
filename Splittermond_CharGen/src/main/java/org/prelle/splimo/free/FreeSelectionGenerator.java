/**
 *
 */
package org.prelle.splimo.free;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Background;
import org.prelle.splimo.Culture;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.Education;
import org.prelle.splimo.Language;
import org.prelle.splimo.Power;
import org.prelle.splimo.Resource;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl4.SpliMoCharGenConstants;
import org.prelle.splimo.modifications.CultureLoreModification;
import org.prelle.splimo.modifications.LanguageModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.NotBackgroundModification;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class FreeSelectionGenerator {

	private static Logger logger = LogManager.getLogger("splittermond.chargen.free");

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;

	private static List<Resource> BASE_RESOURCES = new ArrayList<Resource>(Arrays.asList(new Resource[]{
			SplitterMondCore.getResource("reputation"),
			SplitterMondCore.getResource("status"),
			SplitterMondCore.getResource("contacts"),
			SplitterMondCore.getResource("wealth")
	}));

	private SpliMoCharacter model;

	private String name;

	private boolean selectCultureLore;
	private CultureLore cultLore;

	private boolean selectLanguage;
	private Language language;

	// Points available for powers
	private int maxPointsPower;
	// Unspent points for powers
	private int availPowers;
	// Selected powers
	private List<Power> selectPower;

	// Points available for skills
	private int maxPointsSkills;
	// Unspent points for skills
	private int availSkills;
	// Highest bonus for a skill
	private int maxSkillValue;
	// Highest bonus for a magic skill
	private int maxMagicSkillValue;
	// How many magic skills may be chosen
	private int maxMagicSkills;
	// Selected skills
	private Map<Skill, Integer> selectedSkills;

	// Points for masteries
	private int maxMasteries;
	// Unspent points for masteries
	private int availMasteries;
	// Selected masterships
	private List<MastershipModification> masterships;

	// Points available for resources
	private int maxPointsResources;
	// Unspent points for skills
	private int availResources;
	// Selected resources
	private Map<Resource, Integer> selectedResources;


	//-------------------------------------------------------------------
	public FreeSelectionGenerator(SpliMoCharacter model, boolean selCultLore, boolean selLang, int powers, int skills, int magSchools, int maxSkill, int maxMagic,
			int masteries, int resources) {
		this.model = model;

		selectCultureLore = selCultLore;
		selectLanguage  = selLang;
		maxPointsPower  = powers;
		availPowers     = powers;
		selectPower     = new ArrayList<Power>();
		maxPointsSkills = skills;
		availSkills     = skills;
		maxMagicSkills  = magSchools;
		maxSkillValue   = maxSkill;
		maxMagicSkillValue = maxMagic;
		selectedSkills  = new HashMap<Skill, Integer>();

		maxMasteries    = masteries;
		availMasteries  = masteries;
		masterships     = new ArrayList<MastershipModification>();

		maxPointsResources = resources;
		availResources  = resources;
		this.selectedResources  = new HashMap<Resource, Integer>();
	}

	//-------------------------------------------------------------------
	public boolean isDone() {
		if (canSelectCultureLore() && cultLore==null) return false;
		if (canSelectLanguage()    && language==null) return false;
		if (canSelectPowers()      && getPointsPowers()>0) return false;
		if (canSelectSkills()      && getPointsSkills()>0) return false;
		if (canSelectMasterships() && getPointsMasterships()>0) return false;
		if (canSelectResources()   && getPointsResources()!=0) return false;

		return true;
	}

	//-------------------------------------------------------------------
	public boolean canSelectCultureLore() {
		return selectCultureLore;
	}

	//-------------------------------------------------------------------
	public void selectCultureLore(CultureLore data) {
		cultLore = data;
	}

	//-------------------------------------------------------------------
	public boolean canSelectLanguage() {
		return selectLanguage;
	}

	//-------------------------------------------------------------------
	public void selectLanguage(Language data) {
		language = data;
	}

	//-------------------------------------------------------------------
	public boolean canSelectPowers() {
		return maxPointsPower>0;
	}

	//-------------------------------------------------------------------
	public int getPointsPowers() {
		return availPowers;
	}

	//-------------------------------------------------------------------
	public List<Power> getAvailablePowers() {
		List<Power> ret = new ArrayList<Power>();
		for (Power tmp : SplitterMondCore.getPowers()) {
			if (tmp.getCost()>availPowers)
				continue;
			ret.add(tmp);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public List<Power> getAvailablePowersWithoutPrefill() {
		List<Power> ret = new ArrayList<Power>();
		for (Power tmp : SplitterMondCore.getPowers()) {
			if (tmp.getCost()>maxPointsPower)
				continue;
			switch (tmp.getSelectable()) {
			case ALWAYS:
			case GENERATION:
			case LEVEL:
				if (model.hasPower(tmp))
					continue;
			default:
			}
			ret.add(tmp);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public void selectPower(Power selection, boolean setSelected) {
		if (setSelected) {
			// Already selected ?
			if (selectPower.contains(selection))
					return;
			// Not affordable ?
			if (availPowers<selection.getCost())
				return;
			if (!selectPower.contains(selection))
				selectPower.add(selection);
		} else {
			// Not selected?
			// De-select
			selectPower.remove(selection);
		}

		// Recalculate points
		availPowers = maxPointsPower;
		for (Power power : selectPower)
			availPowers -= power.getCost();
	}

	//-------------------------------------------------------------------
	public List<Power> getSelectedPowers() {
		return new ArrayList<Power>(selectPower);
	}

	//-------------------------------------------------------------------
	public boolean canSelectSkills() {
		return maxPointsSkills>0;
	}

	//-------------------------------------------------------------------
	public int getPointsSkills() {
		return availSkills;
	}

	//-------------------------------------------------------------------
	public List<Skill> getAvailableSkills() {
		int magicCount = 0;
		for (Skill tmp : selectedSkills.keySet())
			if (tmp.getType()==SkillType.MAGIC) magicCount++;


		List<Skill> ret = new ArrayList<Skill>();
		for (Skill tmp : SplitterMondCore.getSkills()) {
			// Don't show those that are already selected
			if (selectedSkills.containsKey(tmp))
				continue;
			// Only show magic skill when allowed
			if (maxMagicSkillValue==0 && tmp.getType()==SkillType.MAGIC)
				continue;
			// Only show magic skill when maximum not yet reached
			if (magicCount>=maxMagicSkills && tmp.getType()==SkillType.MAGIC)
				continue;
			// Only show non-magic skill when allowed
			if (maxSkillValue==0 && tmp.getType()!=SkillType.MAGIC)
				continue;

			ret.add(tmp);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @return TRUE if selection was allowed;
	 */
	public boolean set(Skill selection, int value) {
//		logger.debug("set skill "+selection+" to "+value);

		if (value<0) return false;
		if (selection.getType()!=SkillType.MAGIC && value>maxSkillValue) return false;
		if (selection.getType()==SkillType.MAGIC && value>maxMagicSkillValue) return false;

		if (selectedSkills.containsKey(selection)) {
			// Already selected
			int oldVal = selectedSkills.get(selection);
			if (oldVal<value) {
				// CASE: Trying to increase skill
				int diff = value - oldVal;
				if (diff>availSkills)
					// Not enough points left
					return false;

				selectedSkills.put(selection, Integer.valueOf(value));
				availSkills -= diff;
			} else if (oldVal>value) {
				// CASE: Trying to decrease skill
				int diff = oldVal - value;
				availSkills += diff;
				if (value==0)
					selectedSkills.remove(selection);
				else
					selectedSkills.put(selection, Integer.valueOf(value));
			}
		} else {
			// Not selected yet
			if (value>availSkills)
				return false;
			selectedSkills.put(selection, Integer.valueOf(value));
			availSkills -= value;
		}
		return true;
	}

	//-------------------------------------------------------------------
	public List<SkillModification> getSelectedSkills() {
		List<SkillModification> ret = new ArrayList<SkillModification>();
		for (Entry<Skill, Integer> entry : selectedSkills.entrySet()) {
			ret.add( new SkillModification(entry.getKey(), entry.getValue()));
		}
		Collections.sort(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	public boolean canBeIncreased(Skill skill) {
		Integer current = selectedSkills.get(skill);
		if (current!=null) {
			if (skill.getType()==SkillType.MAGIC) return current<maxMagicSkillValue;
			return current<maxSkillValue;
		}
		return false;
	}

	//-------------------------------------------------------------------
	public boolean canBeDecreased(Skill skill) {
		Integer current = selectedSkills.get(skill);
		return (current!=null && current>0);
	}

	//-------------------------------------------------------------------
	public int getMaxSkillValue() {
		return maxSkillValue;
	}

	//-------------------------------------------------------------------
	public int getMaxMagicSkillValue() {
		return maxMagicSkillValue;
	}

	//-------------------------------------------------------------------
	public boolean canSelectMasterships() {
		return maxMasteries>0;
	}

	//-------------------------------------------------------------------
	public List<Skill> getAvailableMastershipSkills() {
		// TODO: only return those skills that are selected - assuming
		// that to have to receive at least one points in a skill before
		// you can select a mastership

		List<Skill> ret = new ArrayList<Skill>();
		for (Skill tmp : SplitterMondCore.getSkills()) {
			// Don't show those skills where not at least point is distributed
			if (!selectedSkills.containsKey(tmp))
				continue;
			ret.add(tmp);
		}
		return ret;
//		return getAvailableSkills();
	}

	//-------------------------------------------------------------------
	public int getPointsMasterships() {
		return availMasteries;
	}

	//-------------------------------------------------------------------
	public List<MastershipModification> getMasterships() {
		return masterships;
	}

	//-------------------------------------------------------------------
	public void setMastership(MastershipModification master, boolean add) {
		// Remove existing mastership
		masterships.remove(master);

		availMasteries = maxMasteries - masterships.size();


		if (availMasteries>0 && add) {
			masterships.add(master);
			availMasteries--;
		}
	}

	//-------------------------------------------------------------------
	public Culture getAsCulture() {
		Culture ret = new Culture();
		ret.setName(name);
		ret.setKey("custom");
		ret.addModification(new NotBackgroundModification());

		// Culture Lore
		if (cultLore!=null)
			ret.addModification(new CultureLoreModification(cultLore));

		// Language
		if (language!=null)
			ret.addModification(new LanguageModification(language));

		// Powers
		for (Power power : selectPower)
			ret.addModification(new PowerModification(power));

		// Skills
		for (Entry<Skill, Integer> entry : selectedSkills.entrySet())
			ret.addModification(new SkillModification(entry.getKey(), entry.getValue()));

		// Masterships
		for (MastershipModification mod : masterships)
			ret.addModification(mod);

		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * Prefill will data from a culture
	 */
	public void setFrom(Culture data) {
		logger.info("setFrom(Culture) "+data);
		if (data.getKey()!=null)
			name = data.getName()+"*";
		for (Modification mod : data.getModifications()) {
			logger.debug(" now "+mod);
			if (mod instanceof LanguageModification) {
				LanguageModification lMod = (LanguageModification)mod;
				language = lMod.getData();
			} else if (mod instanceof CultureLoreModification) {
				CultureLoreModification cMod = (CultureLoreModification)mod;
				cultLore = cMod.getData();
			} else if (mod instanceof PowerModification) {
				PowerModification pMod = (PowerModification)mod;
				selectPower.add(pMod.getPower());
				availPowers -= pMod.getPower().getCost();
			} else if (mod instanceof SkillModification) {
				SkillModification sMod = (SkillModification)mod;
				selectedSkills.put(sMod.getSkill(), sMod.getValue());
				availSkills -= sMod.getValue();
			} else if (mod instanceof MastershipModification) {
				MastershipModification mMod = (MastershipModification)mod;
				masterships.add(mMod);
				availMasteries--;
			}
		}
	}

	//-------------------------------------------------------------------
	public Background getAsBackground() {
		Background ret = new Background();
		ret.setName(name);
		ret.setKey("custom");

		// Resources
		for (Entry<Resource, Integer> res : selectedResources.entrySet())
			ret.addModifications(new ResourceModification(res.getKey(), res.getValue()));

		// Skills
		for (Entry<Skill, Integer> entry : selectedSkills.entrySet())
			ret.addModifications(new SkillModification(entry.getKey(), entry.getValue()));

		logger.debug("Return as background: "+ret.getModifications());
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * Prefill will data from a background
	 */
	public void setFrom(Background data) {
		logger.debug("setFrom(Background) "+data);
		this.name = data.getName()+"*";
		for (Modification mod : data.getModifications()) {
			logger.debug("  now "+mod);
			if (mod instanceof ResourceModification) {
				ResourceModification rMod = (ResourceModification)mod;
				selectedResources.put(rMod.getResource(), rMod.getValue());
				logger.debug("  result "+selectedResources);
				availResources -= rMod.getValue();
			} else if (mod instanceof SkillModification) {
				SkillModification sMod = (SkillModification)mod;
				selectedSkills.put(sMod.getSkill(), sMod.getValue());
				availSkills -= sMod.getValue();
			}
		}
		logger.debug("  resources final "+selectedResources);
	}

	//-------------------------------------------------------------------
	public Education getAsEducation() {
		Education ret = new Education();
		ret.setName(name);
		ret.setKey("custom");

		// Resources
		for (Entry<Resource, Integer> res : selectedResources.entrySet())
			ret.addModifications(new ResourceModification(res.getKey(), res.getValue()));

		// Powers
		for (Power power : selectPower)
			ret.addModifications(new PowerModification(power));

		// Skills
		for (Entry<Skill, Integer> entry : selectedSkills.entrySet())
			ret.addModifications(new SkillModification(entry.getKey(), entry.getValue()));

		// Masterships
		for (MastershipModification mod : masterships)
			ret.addModifications(mod);

		logger.debug("Modifications of selected = "+ret.getModifications());
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * Prefill will data from an education
	 */
	public void setFrom(Education data) {
		logger.debug("setFrom(Education) called with "+data.getModifications());
		name = data.getName()+"*";
		for (Modification mod : data.getModifications()) {
			logger.debug("  now "+mod);
			if (mod instanceof ResourceModification) {
				ResourceModification rMod = (ResourceModification)mod;
				selectedResources.put(rMod.getResource(), rMod.getValue());
				availResources -= rMod.getValue();
			} else if (mod instanceof PowerModification) {
				PowerModification pMod = (PowerModification)mod;
				selectPower.add(pMod.getPower());
				availPowers -= pMod.getPower().getCost();
			} else if (mod instanceof SkillModification) {
				SkillModification sMod = (SkillModification)mod;
				if (sMod.getValue()<1) {
					logger.debug("Do not prefill negative skill modifier");
					continue;
				}
				selectedSkills.put(sMod.getSkill(), sMod.getValue());
				availSkills -= sMod.getValue();
			} else if (mod instanceof MastershipModification) {
				MastershipModification mMod = (MastershipModification)mod;
				masterships.add(mMod);
				availMasteries--;
			}
		}
		logger.debug("Skills = "+selectedSkills);
		logger.debug("Master = "+masterships);
	}

	//-------------------------------------------------------------------
	public boolean canSelectResources() {
		return maxPointsResources>0;
	}

	//-------------------------------------------------------------------
	public int getPointsResources() {
		return availResources;
	}

	//-------------------------------------------------------------------
	public List<Resource> getAvailableResources() {
		List<Resource> ret = new ArrayList<Resource>();
		for (Resource tmp : SplitterMondCore.getResources()) {
			// Don't show those that are already selected
			if (selectedResources.containsKey(tmp))
				continue;

			ret.add(tmp);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @return TRUE if selection was allowed;
	 */
	public boolean set(Resource selection, int value) {
		logger.debug("set resource "+selection+" to "+value);

		/*
		 * Check if there are enough available points to increase
		 * the resource
		 */
		if (selectedResources.get(selection)==null || selectedResources.get(selection)<value) {
			// This is an increase or new resource
			logger.debug("Increase value - availabe = "+availResources);
			if (availResources<1)
				return false;
		}


		/*
		 * While in theory there is no limit on how high a resource
		 * can be raised, we bind the upper limit to the maximum
		 * points allowed to spend. Although with negative base
		 * resources higher values may be reached.
		 */
		if (value>maxPointsResources)
			return false;

		/*
		 * A value of 0 indicates the request to remove the resource,
		 * unless it was a basic resource, which also may have negative
		 * values
		 */
		if (value==0 && !BASE_RESOURCES.contains(selection)) {
			selectedResources.remove(selection);
		} else {
			selectedResources.put(selection, Integer.valueOf(value));
		}

		/*
		 * Calculate current resource cost
		 */
		int cost = 0;
		for (Entry<Resource,Integer> entry : selectedResources.entrySet()) {
			cost += entry.getValue();
		}
		availResources = maxPointsResources - cost;

		return true;
	}

	//-------------------------------------------------------------------
	public List<ResourceModification> getSelectedResources() {
		List<ResourceModification> ret = new ArrayList<ResourceModification>();
		for (Entry<Resource, Integer> entry : selectedResources.entrySet()) {
			if (entry.getValue()==0 && !BASE_RESOURCES.contains(entry.getKey()))
				continue;

			ret.add( new ResourceModification(entry.getKey(), entry.getValue()));
		}
		Collections.sort(ret);
		return ret;
	}

	//--------------------------------------------------------------------
	public CultureLore getCultureLore() {
		return cultLore;
	}

	//--------------------------------------------------------------------
	public Language getLanguage() {
		return language;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	//-------------------------------------------------------------------
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
