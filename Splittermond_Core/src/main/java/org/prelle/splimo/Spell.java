/**
 *
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.persist.SpellCastDurationConverter;
import org.prelle.splimo.persist.SpellCostConverter;
import org.prelle.splimo.persist.SpellDifficultyConverter;
import org.prelle.splimo.persist.SpellDurationConverter;
import org.prelle.splimo.persist.SpellEnhancementConverter;
import org.prelle.splimo.persist.SpellRangeConverter;
import org.prelle.splimo.requirements.RequirementList;

/**
 * @author prelle
 *
 */
@Root(name = "spell")
public class Spell extends BasePluginData implements Comparable<Spell> {

	public final static int DIFF_DEFENSE = -1;
	public final static int DIFF_MINDRESIST = -2;
	public final static int DIFF_BODYRESIST = -3;

	public final static int RANGE_CASTER = 0;
	public final static int RANGE_TOUCH = -1;

	public final static int DURATION_TICK = 0;
	public final static int DURATION_SECOND = 1;
	public final static int DURATION_MINUTE = 2;
	public final static int DURATION_HOUR   = 3;
	public final static int DURATION_DAY    = 4;
	public final static int DURATION_MONTH  = 5;
	public final static int DURATION_YEAR   = 7;
	public final static int DURATION_CHANNELLED = 9;

	@Attribute
	private String    id;
	@ElementList(entry="school",type=SpellSchoolEntry.class,inline=true)
	private List<SpellSchoolEntry> school;
	@ElementList(entry="type",type=SpellType.class,inline=true)
	private List<SpellType> type;
	@ElementList(entry="enhancementtype",type=SpellEnhancementType.class,inline=true)
	private List<SpellEnhancementType> enhancementtype;
	@Attribute(name="diff")
	@AttribConvert(SpellDifficultyConverter.class)
	private Integer difficulty;
	@Attribute(name="range")
	@AttribConvert(SpellRangeConverter.class)
	private Integer range;
	@Attribute(name="effectRange")
	private Integer effectRange;
	@Attribute(name="castdur")
	@AttribConvert(SpellCastDurationConverter.class)
	private Integer castDur;
	@Attribute(name="spelldur",required=false)
	@AttribConvert(SpellDurationConverter.class)
	private Integer spellDur;
	@Attribute(name="cost")
	@AttribConvert(SpellCostConverter.class)
	private SpellCost cost;
	@Attribute(name="enh")
	@AttribConvert(SpellEnhancementConverter.class)
	private SpellEnhancement enh;
	@Element
	private RequirementList requires;

	//-------------------------------------------------------------------
	/**
	 */
	public Spell() {
		school = new ArrayList<SpellSchoolEntry>();
		type = new ArrayList<SpellType>();
		enhancementtype = new ArrayList<>();
		requires = new RequirementList();
	}

	//-------------------------------------------------------------------
	/**
	 */
	public Spell(String id) {
		this();
		this.id = id;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "spell."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "spell."+id+".descr";
	}

	//-------------------------------------------------------------------
	public int getPage() {
		try {
			return Integer.parseInt(i18n.getString(getPageI18NKey()));
		} catch (MissingResourceException e) {
			logger.error("Missing key "+e.getKey()+" in "+i18n.getBaseBundleName());
		} catch (NumberFormatException e) {
			logger.error("Not a number in key "+getPageI18NKey()+" in "+i18n.getBaseBundleName());
		}
		return 0;
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("spell."+id);
		} catch (MissingResourceException e) {
			logger.error("Missing resource '"+e.getKey()+"' in "+i18nHelp.getBaseBundleName());
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	public String getDescription() {
		try {
			return i18nHelp.getString("spell."+id+".descr");
		} catch (MissingResourceException e) {
			logger.error("Missing resource '"+e.getKey()+"' in "+i18nHelp.getBaseBundleName());
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	public String getEnhancementDescription() {
		try {
			return i18nHelp.getString("spell."+id+".enhancedescr");
		} catch (MissingResourceException e) {
			logger.error("Missing resource '"+e.getKey()+"' in "+i18nHelp.getBaseBundleName());
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	public String getSchoolName() {
		StringBuffer buf = new StringBuffer();
		Iterator<SpellSchoolEntry> it = school.iterator();
		while (it.hasNext()) {
			SpellSchoolEntry school = it.next();
			buf.append(SplitterMondCore.getI18nResources().getString("school."+school.getSchool().getId()));
			buf.append(" "+school.getLevel());

			if (it.hasNext())
				buf.append(", ");
		}
		return buf.toString();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String dump() {
		try {
			return String.format("%s (%s) Typus: %s  Reichw. %s", getName(), school+"", type+"", getCastRangeString());
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Error in spell: "+id);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Spell o) {
		return Collator.getInstance().compare(getName(), o.getName());

	}

	//-------------------------------------------------------------------
	public List<SpellType> getTypes() {
		return type;
	}

	//-------------------------------------------------------------------
	public List<SpellSchoolEntry> getSchools() {
		Collections.sort(school);
		return school;
	}

	//-------------------------------------------------------------------
	/**
	 * @return -1 if not available in that school, spell level otherwise
	 */
	public int getLevelInSchool(Skill skill) {
		for (SpellSchoolEntry entry : school)
			if (entry.getSchool()==skill) return entry.getLevel();
		return -1;
	}

	//-------------------------------------------------------------------
	public void addSchool(Skill school, int level) {
		SpellSchoolEntry toAdd = new SpellSchoolEntry(school, level);
		if (!this.school.contains(toAdd)) {
			this.school.add(toAdd);
		}
	}

	//-------------------------------------------------------------------
	public void addType(SpellType toAdd) {
		if (!type.contains(toAdd))
			type.add(toAdd);
	}

	//-------------------------------------------------------------------
	public void addAllTypes(SpellType... toAdd) {
		for (SpellType tmp : toAdd)
		if (!type.contains(tmp))
			type.add(tmp);
	}

	//-------------------------------------------------------------------
	public String getDifficultyString() {
		if (difficulty==null)
			return "";
		if (difficulty>0)
			return String.valueOf(difficulty);
		else if (difficulty==DIFF_BODYRESIST)
			return SplitterMondCore.getI18nResources().getString("spell.difficulty.bodyresist");
		else if (difficulty==DIFF_MINDRESIST)
			return SplitterMondCore.getI18nResources().getString("spell.difficulty.mindresist");
		else if (difficulty==DIFF_DEFENSE)
			return SplitterMondCore.getI18nResources().getString("spell.difficulty.defense");
		return "?"+difficulty+"?";
	}

	//-------------------------------------------------------------------
	public SpellCost getCost() {
		return cost;
	}

	//-------------------------------------------------------------------
	public void setCost(SpellCost cost) {
		this.cost = cost;
	}

	//-------------------------------------------------------------------
	public void setCastDuration(int castDur) {
		this.castDur = castDur;
	}

	//-------------------------------------------------------------------
	public String getCastDurationString() {
		int type = castDur%10;
		int count= castDur/10;
		String suffix = SplitterMondCore.getI18nResources().getString("spell.duration.suffix."+type);
		return count+suffix;
	}

	//-------------------------------------------------------------------
	public int getCastRange() {
		return range;
	}

	//-------------------------------------------------------------------
	public String getCastRangeString() {
		switch (range) {
		case RANGE_TOUCH:
			return SplitterMondCore.getI18nResources().getString("spell.range.touch.short");
		case RANGE_CASTER:
			return SplitterMondCore.getI18nResources().getString("spell.range.caster.short");
		default:
			return range+"m";
		}
	}

	//-------------------------------------------------------------------
	public void setSpellDuration(int spellDur) {
		this.spellDur = spellDur;
	}

	//-------------------------------------------------------------------
	public String getSpellDuration() {
		return "";
	}

	//-------------------------------------------------------------------
	public boolean hasSpellDuration() {
		return spellDur!=null;
	}

	//-------------------------------------------------------------------
	public boolean isChannelized() {
		if (spellDur==null) return false;

		int type = spellDur%10;
		switch (type) {
		case DURATION_CHANNELLED: return true;
		default:
			return false;
		}
	}

	//-------------------------------------------------------------------
	public String getSpellDurationString() {
		if (spellDur==null) return "";

		int type = spellDur%10;
		int count= spellDur/10;
		String suffix = SplitterMondCore.getI18nResources().getString("spell.duration.suffix."+type);
		switch (type) {
		case DURATION_CHANNELLED: return suffix;
		default:
			return count+" "+suffix;
		}
	}

	//-------------------------------------------------------------------
	public String getEnhancementString() {
		if (enh==null)
			return "";
		return enh.toString();
	}

	//--------------------------------------------------------------------
	/**
	 * @return the difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

	//--------------------------------------------------------------------
	/**
	 * @param difficulty the difficulty to set
	 */
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}


	//--------------------------------------------------------------------

	/**
	 *
	 * @param enhancementtype
     */
	public void setEnhancementtype(List<SpellEnhancementType> enhancementtype) {
		this.enhancementtype = enhancementtype;
	}

	//--------------------------------------------------------------------

	/**
	 *
	 * @return effectRange the range of the effect
     */
	public Integer getEffectRange() {
		return effectRange;
	}

	//--------------------------------------------------------------------
	/**
	 *
	 * @param effectRange
     */
	public void setEffectRange(Integer effectRange) {
		this.effectRange = effectRange;
	}

	//--------------------------------------------------------------------
	/**
	 *
	 * @return enhancementtype type of the spell enhancements
     */
	public List<SpellEnhancementType> getEnhancementtype() {
		return enhancementtype;
	}

	//-------------------------------------------------------------------
	/**
	 * @return List of requirements to learn this spell
	 */
	public RequirementList getRequirements() {
		return requires;
	}

}