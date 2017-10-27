/**
 *
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.ElementList;

/**
 * @author prelle
 *
 */
public class Skill extends BasePluginData implements Comparable<Skill> {

	public enum SkillType {
		COMBAT,
		NORMAL,
		MAGIC;

		public String getName() {
			return SplitterMondCore.getI18nResources().getString("skill.type."+name().toLowerCase());
		}
	}

	@org.prelle.simplepersist.Attribute
	private String    id;
	@org.prelle.simplepersist.Attribute(required=false)
	private Attribute attr1;
	@org.prelle.simplepersist.Attribute(required=false)
	private Attribute attr2;
	@org.prelle.simplepersist.Attribute(required=false)
	private SkillType type;
	@org.prelle.simplepersist.Attribute(required=false)
	private boolean grouped;
	@ElementList(entry="specialization",type=SkillSpecialization.class,inline=true)
	private List<SkillSpecialization> specialization;
	@ElementList(entry="mastership",type=Mastership.class,inline=true)
	private List<Mastership> mastership;

	//-------------------------------------------------------------------
	/**
	 */
	public Skill() {
		type = SkillType.NORMAL;
		mastership = new ArrayList<Mastership>();
		specialization = new ArrayList<SkillSpecialization>();
	}

	//-------------------------------------------------------------------
	/**
	 */
	public Skill(String id, SkillType type, Attribute attr1, Attribute attr2) {
		this.id = id;
		this.type = type;
		this.attr1 = attr1;
		this.attr2 = attr2;
		mastership = new ArrayList<Mastership>();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "skill."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "skill."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String getName() {
		return i18n.getString("skill."+id);
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String dump() {
		return SplitterMondCore.getI18nResources().getString("skill."+id)+"  ("+attr1+" + "+attr2+")  "+type+" ... "+mastership+" ... "+specialization;
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
	 * @return the attr1
	 */
	public Attribute getAttribute1() {
		return attr1;
	}

	//-------------------------------------------------------------------
	/**
	 * @param attr1 the attr1 to set
	 */
	public void setAttribute1(Attribute attr1) {
		this.attr1 = attr1;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the attr2
	 */
	public Attribute getAttribute2() {
		return attr2;
	}

	//-------------------------------------------------------------------
	/**
	 * @param attr2 the attr2 to set
	 */
	public void setAttribute2(Attribute attr2) {
		this.attr2 = attr2;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Skill o) {
		int foo = type.compareTo(o.getType());
		if (foo!=0)
			return foo;
		return Collator.getInstance().compare(getName(), o.getName());

	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public SkillType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(SkillType type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the masterships
	 */
	public Mastership getMastership(String id) {
		for (Mastership tmp : mastership)
			if (tmp.getKey().equals(id))
				return tmp;
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the masterships
	 */
	public List<Mastership> getMasterships() {
		return mastership;
	}

	//-------------------------------------------------------------------
	public void addMastership(Mastership toAdd) {
		if (!mastership.contains(toAdd)) {
			mastership.add(toAdd);
			toAdd.setSkill(this);
		}
	}

	//-------------------------------------------------------------------
	public void removeMastership(Mastership toRemove) {
		mastership.remove(toRemove);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the masterships
	 */
	public List<SkillSpecialization> getSpecializations() {
		return specialization;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the masterships
	 */
	public SkillSpecialization getSpecialization(String id) {
		for (SkillSpecialization tmp : specialization) {
			if (tmp.getId().equals(id))
				return tmp;
			if (id.startsWith(tmp.getId()))
				return tmp;
		}
		return null;
	}

	//-------------------------------------------------------------------
	public void addSpecialization(SkillSpecialization spec) {
		specialization.add(spec);
		spec.setSkill(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the grouped
	 */
	public boolean isGrouped() {
		return grouped;
	}

}
