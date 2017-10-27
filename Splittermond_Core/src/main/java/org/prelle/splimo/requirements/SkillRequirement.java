/**
 * 
 */
package org.prelle.splimo.requirements;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.jaxb.SkillAdapter;

/**
 * @author prelle
 *
 */
@Root(name = "skillreq")
public class SkillRequirement extends Requirement {

	private final static Logger logger = Logger.getLogger("splittermond.req");

	@Attribute(name="ref")
	private String ref;
	private transient Skill resolved; 
	private transient SkillSpecialization focus; 
	@Attribute
	private int value;

	//-------------------------------------------------------------------
	public SkillRequirement() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SkillRequirement(Skill data, int value) {
		this.ref = data.getId();
		resolved = data;
		this.value = value;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SkillRequirement(String skillID, int value) {
		this.ref   = skillID;
		this.value = value;
		resolved   = SplitterMondCore.getSkill(skillID);
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ref+"+"+value;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Skill getSkill() {
		if (resolved!=null)
			return resolved;

		if (resolve())
			return resolved;

		return null;
	}

	//-------------------------------------------------------------------
	public String getSkillName() {
		return getSkill().getName();
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

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
		if (resolved!=null)
			return true;

		/*
		 * Try to resolve now.
		 */
		try {
			resolved = (new SkillAdapter()).unmarshal(ref);
			return true;
		} catch (Exception e) {
		} 
		logger.error("Resolution of skill reference '"+ref+"' failed");
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the focus
	 */
	public SkillSpecialization getFocus() {
		return focus;
	}

	//-------------------------------------------------------------------
	/**
	 * @param focus the focus to set
	 */
	public void setFocus(SkillSpecialization focus) {
		this.focus = focus;
	}

}
