/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Race;
import org.prelle.splimo.persist.RaceConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "racemod")
public class RaceModification extends ModificationImpl {
	
	@Attribute
	@AttribConvert(RaceConverter.class)
	private Race ref;

	//-------------------------------------------------------------------
	/**
	 */
	public RaceModification() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public RaceModification(Race race) {
		this.ref = race;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.modifications.ModificationImpl#clone()
	 */
	@Override
	public RaceModification clone() {
		RaceModification ret = new RaceModification(ref);
		ret.cloneAdd(this);
		return ret;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "race="+ref;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Race getRace() {
		return ref;
	}

}
