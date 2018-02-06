package org.prelle.splimo.requirements;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.persist.MastershipConverter;
import org.prelle.splimo.persist.ReferenceException;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;
import org.prelle.splimo.persist.SkillSpecializationConverter;

/**
 * @author prelle
 *
 */
@Root(name = "masterreq")
public class MastershipRequirement extends Requirement {

	private final static Logger logger = Logger.getLogger("splittermond.req");

	@Attribute(name="ref")
	private String id;
	@Attribute(name="focus")
	private String focus;
	private transient Mastership resolved;
	private transient Skill skill;
	private transient SkillSpecialization specialization;

    //-----------------------------------------------------------------------
    public MastershipRequirement() {
    }

    //-----------------------------------------------------------------------
    public MastershipRequirement(Mastership master) {
        this.skill = master.getSkill();
        this.id    = master.getKey();
        resolved   = master;
    }

    //-----------------------------------------------------------------------
    public String toString() {
        return id;
    }

    //-----------------------------------------------------------------------
    public boolean equals(Object o) {
    	if (o instanceof MastershipRequirement) {
    		MastershipRequirement other = (MastershipRequirement)o;
    		if (id  !=null && other.getMastership()!=null)
    			return id.equals(other.getMastership().getId());
    		return false;
    	}
    	return false;
    }

    //-----------------------------------------------------------------------
    public Skill getSkill() {
    	if (skill!=null)
    		return skill;
    	if (resolved!=null)
    		return resolved.getSkill();

    	return null;
    }

    //-----------------------------------------------------------------------
    public Mastership getMastership() {
    	if (resolved!=null)
    		return resolved;

    	if (resolve())
    		return resolved;
    	return null;
    }

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
		if (resolved!=null)
			return true;

		try {
			if (focus!=null)
				specialization = (new SkillSpecializationConverter()).read(focus);
		} catch (Exception e1) {
			logger.error("Failed resolving SkillSpecialization '"+focus+"' in masterreq",e1);
		}

    	/*
    	 * Try to resolve now.
    	 * If format is x/y, use adapter class, otherwise search in skill
    	 */
    	if (id.indexOf('/')>0) {
    		try {
    			resolved = (new MastershipConverter()).read(id);
    			return true;
    		} catch (Exception e) {
    			logger.error(e);
    		}
    	} else {
    		if (skill!=null) {
//    			logger.debug(String.format("In skill '%s' is a mastership with a masterreq '%s' with no skill assigned", skill.getId(), id));
    			resolved = skill.getMastership(id);
    			if (resolved!=null)
    				return true;
    			logger.warn(String.format("In skill '%s' there is no mastership '%s' ", skill.getId(), id));
    		}
    	}

    	logger.error("Resolution of mastership reference '"+id+"' failed. Skill="+skill);
    	logger.error("  skill="+skill);
		throw new ReferenceException(ReferenceType.MASTERSHIP, id);
//		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the specialization
	 */
	public SkillSpecialization getFokus() {
		return specialization;
	}

}
