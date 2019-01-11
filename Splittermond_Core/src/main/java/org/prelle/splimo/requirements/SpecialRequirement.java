package org.prelle.splimo.requirements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecializationValue;
import org.prelle.splimo.persist.SkillSpecializationValueConverter;

/**
 * @author prelle
 *
 */
@Root(name = "specialreq")
public class SpecialRequirement extends Requirement {

	private static Logger logger = LogManager.getLogger("splittermond.req");

	@Attribute(name="ref")
	private String id;
	private transient SkillSpecializationValue resolved;
	private transient Skill skill;
    
    //-----------------------------------------------------------------------
    public SpecialRequirement() {
     }
    
    //-----------------------------------------------------------------------
    public SpecialRequirement(SkillSpecialization special, int level) {
        this.skill = special.getSkill();
        this.resolved = new SkillSpecializationValue(special, level);
    }
    
    //-----------------------------------------------------------------------
    public String toString() {
    	if (resolved!=null)
    		return resolved.toString();
        return String.valueOf(id);
    }
    
    //-----------------------------------------------------------------------
    public boolean equals(Object o) {
    	if (o instanceof SpecialRequirement) {
    		SpecialRequirement other = (SpecialRequirement)o;
    		if (resolved!=null && other.getSpecialization()!=null) 
    			return resolved.equals(other.getSpecialization());
//    		if (id  !=null && other.getMastership()!=null) 
//    			return id.equals(other.getMastership());
    		return false;
    	}
    	return false;
    }
    
    //-----------------------------------------------------------------------
    public Skill getSkill() {
    	if (skill!=null)
    		return skill;
    	if (resolved!=null)
    		return resolved.getSpecial().getSkill();
    	
    	return null;
    }
    
    //-----------------------------------------------------------------------
    public SkillSpecializationValue getSpecialization() {
    	if (resolved!=null)
    		return resolved;
    	
    	return null;
    }

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
    	/*
    	 * Try to resolve now.
    	 * If format is x/y, use adapter class, otherwise search in skill
    	 */
    	if (id.indexOf('/')>0) {
    		try {
    			resolved = (new SkillSpecializationValueConverter()).read(id);
    			return true;
    		} catch (Exception e) {
    		} 
    	} else {
    		if (skill!=null) {
//    			logger.warn(String.format("In skill '%s' is a mastership with a specialreq '%s' with no skill assigned", skill.getId(), id));        		
        		SkillSpecialization spec = skill.getSpecialization(id);
    			if (spec!=null) {
    				resolved = new SkillSpecializationValue(spec, 1);
    				return true;
    			}
    			logger.error("Unresolvable specialization reference '"+id+"' in skill '"+skill+"'");
    		}    		
    	}

    	logger.error("Resolution of skill specialization reference '"+id+"' failed");
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
    
}
