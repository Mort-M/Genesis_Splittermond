package org.prelle.splimo.requirements;

/**
 * @author prelle
 *
 */
public abstract class Requirement {

	//--------------------------------------------------------------------
	public Requirement() {
	}

	//--------------------------------------------------------------------
	/**
	 * Called when loading data in a second pass to resolve references
	 * to other data just loaded
	 * @return TRUE, when resolution was successfull
	 */
	public abstract boolean resolve();

}
