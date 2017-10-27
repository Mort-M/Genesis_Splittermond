/**
 * 
 */
package org.prelle.splimo.requirements;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Power;
import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
@Root(name = "powerreq")
public class PowerRequirement extends Requirement {

	private final static Logger logger = Logger.getLogger("splittermond.req");
	
	@Attribute(required=true)
	private String ref;
	private transient Power resolved; 

	//-------------------------------------------------------------------
	/**
	 */
	public PowerRequirement() {
	}

	//-------------------------------------------------------------------
	public PowerRequirement(String ref) {
		this.ref = ref;
	}

	//-------------------------------------------------------------------
	public PowerRequirement(Power power) {
		this.ref = power.getId();
		this.resolved = power;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.valueOf(ref);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.modifications.ModificationImpl#clone()
	 */
	@Override
	public Object clone() {
		return new PowerRequirement(ref);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Power getPower() {
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

		/*
		 * Try to resolve now.
		 */
		try {
			resolved = SplitterMondCore.getPower(ref);
			if (resolved!=null)
				return true;
		} catch (Exception e) {
		} 
		logger.error("Resolution of power reference '"+ref+"' failed");
		return false;
	}

}
