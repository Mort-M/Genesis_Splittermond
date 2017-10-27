package org.prelle.splimo.modifications;

import java.util.PropertyResourceBundle;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecializationValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.MastershipConverter;
import org.prelle.splimo.persist.SkillConverter;
import org.prelle.splimo.persist.SkillSpecializationValueConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "mastermod")
public class MastershipModification extends ModificationImpl {

	private static PropertyResourceBundle res = SplitterMondCore.getI18nResources();

	@Attribute(name="ref",required=false)
	@AttribConvert(MastershipConverter.class)
    private Mastership id;
	@Attribute(name="spec",required=false)
	@AttribConvert(SkillSpecializationValueConverter.class)
	private SkillSpecializationValue spec;
	@Attribute(required=false)
	@AttribConvert(SkillConverter.class)
	/** Required for ANY mastership modifications */
	private Skill skill;
	@Attribute(name="skilltype",required=false)
	/** Required for ANY mastership modifications */
	private SkillType skillType;

	/*
	 * Eigentlich überflüssig
	 */
	@Attribute(required=false)
    private int level;

    //-----------------------------------------------------------------------
    public MastershipModification() {
    	level = 1;
    }

    //-----------------------------------------------------------------------
    public MastershipModification(Mastership master) {
        this.skill = master.getSkill();
        this.id    = master;
    }

    //-----------------------------------------------------------------------
    public MastershipModification(SkillSpecialization special, int level) {
        this.skill = special.getSkill();
        this.spec  = new SkillSpecializationValue(special, level);
    }

    //-----------------------------------------------------------------------
    public MastershipModification(Skill skill, int level) {
        this.skill = skill;
        this.level = level;
    }

    //-----------------------------------------------------------------------
    public MastershipModification clone() {
    	MastershipModification ret = null;
    	if (id==null)
    		ret = new MastershipModification(skill, level);
    	else
    		ret = new MastershipModification(id);
    	((MastershipModification)ret).setSpecialization(spec);
    	ret.cloneAdd(this);
    	return ret;
    }

    //-----------------------------------------------------------------------
    public String toString() {
    	if (id==null && spec==null) {
    		if (skill!=null)
//        		return "Any level "+level+" mastership of "+skill;
    			return String.format(res.getString("mastermod.anystring.skill"), level, skill.getName());
    		if (skillType!=null)
    			return String.format(res.getString("mastermod.anystring.skilltype"), level, skillType.getName());
    		if (level>0)
    			return String.format(res.getString("mastermod.anystring.level"), level);
    		return "MastershipModification.ERROR";
    	}
    	if (spec!=null)
  			return SplitterMondCore.getI18nResources().getString("label.specialization")+" "+spec.getSpecial().getName();

    	if (id!=null)
   			return SplitterMondCore.getI18nResources().getString("label.mastership")+" "+id.getName();

        return String.valueOf(id);
    }

    //-----------------------------------------------------------------------
    public boolean equals(Object o) {
    	if (o instanceof MastershipModification) {
    		MastershipModification other = (MastershipModification)o;
    		if (spec!=null && other.getSpecialization()!=null)
    			return spec.equals(other.getSpecialization());
    		if (id  !=null && other.getMastership()!=null)
    			return id.equals(other.getMastership());
    		return super.equals(o);
    	}
    	return false;
    }

    //-----------------------------------------------------------------------
    public Skill getSkill() {
    	if (skill!=null)
    		return skill;
    	if (id!=null)
    		return id.getSkill();
    	if (spec!=null)
    		return spec.getSpecial().getSkill();

    	return null;
    }

    //-----------------------------------------------------------------------
    public Mastership getMastership() {
        return id;
    }

    //-----------------------------------------------------------------------
    public int getLevel() {
    	if (spec!=null)
    		return spec.getLevel();
        return level;
    }

    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Modification obj) {
        if (!(obj instanceof MastershipModification))
            return toString().compareTo(obj.toString());
        MastershipModification other = (MastershipModification)obj;
        if (skill==null || other.getSkill()==null) return 0;
        int tmp = skill.compareTo(other.getSkill());
        if (tmp==0) return 0;
        if (id==null)
        	return ((Integer)level).compareTo(other.getLevel());
        return id.getName().compareTo(other.getMastership().getName());
    }

	//-------------------------------------------------------------------
	/**
	 * @return the spec
	 */
	public SkillSpecializationValue getSpecialization() {
		return spec;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the skillType
	 */
	public SkillType getSkillType() {
		return skillType;
	}

	//--------------------------------------------------------------------
	/**
	 * @param skillType the skillType to set
	 */
	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}

	//--------------------------------------------------------------------
	public void setSpecialization(SkillSpecializationValue skillSpec) {
		this.spec = skillSpec;
	}

}// AttributeModification
