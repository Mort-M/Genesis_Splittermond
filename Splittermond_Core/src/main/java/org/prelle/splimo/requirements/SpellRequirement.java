/**
 * 
 */
package org.prelle.splimo.requirements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
@Root(name = "spellreq")
public class SpellRequirement extends Requirement {

	private static Logger logger = LogManager.getLogger("splittermond.req");

	@Attribute(name="ref")
	private String ref;
	private transient Spell resolved; 
//	@Attribute
//	private int value;

	//-------------------------------------------------------------------
	public SpellRequirement() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SpellRequirement(Spell data, int value) {
		this.ref = data.getId();
		resolved = data;
//		this.value = value;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SpellRequirement(String spellID, int value) {
		this.ref   = spellID;
//		this.value = value;
		resolved   = SplitterMondCore.getSpell(spellID);
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ref; //+"+"+value;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Spell getSpell() {
		if (resolved!=null)
			return resolved;

		if (resolve())
			return resolved;

		return null;
	}

	//-------------------------------------------------------------------
	public String getSpellName() {
		return getSpell().getName();
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @return the value
//	 */
//	public int getValue() {
//		return value;
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @param value the value to set
//	 */
//	public void setValue(int value) {
//		this.value = value;
//	}

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
			resolved =SplitterMondCore.getSpell(ref);
			return true;
		} catch (Exception e) {
		} 
		logger.error("Resolution of spell reference '"+ref+"' failed");
		return false;
	}

}
