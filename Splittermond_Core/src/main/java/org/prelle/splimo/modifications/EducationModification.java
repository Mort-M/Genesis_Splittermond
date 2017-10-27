/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Education;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.EducationConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "edumod")
public class EducationModification extends ModificationImpl {
	
	@Attribute
	@AttribConvert(EducationConverter.class)
	private Education ref;

	//-------------------------------------------------------------------
	/**
	 */
	public EducationModification() {
 	}

	//-------------------------------------------------------------------
	/**
	 */
	public EducationModification(Education data) {
		this.ref = data;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public EducationModification(String id) {
		this.ref = SplitterMondCore.getEducation(id);
	}

	//-------------------------------------------------------------------
	/**
	 */
	public EducationModification(String skillID, Object src) {
		this(skillID);
		this.source = src;
	}

	//-------------------------------------------------------------------
	public String toString() {
		String sourceString = source != null ? " (" + source + ")" : "";
		if (ref!=null) {
			return ref.getName()+ " " + sourceString;
		}
		return "(no skill.2) +"+sourceString;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		if (other instanceof EducationModification) {
			return ref.compareTo( ((EducationModification)other).getEducation() );
		}
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.modifications.ModificationImpl#clone()
	 */
	@Override
	public EducationModification clone() {
		EducationModification ret = new EducationModification(ref);
    	ret.cloneAdd(this);
    	return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Education getEducation() {
		return ref;
	}

}
