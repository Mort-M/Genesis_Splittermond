/**
 * 
 */
package org.prelle.splimo.requirements;

import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "favskillreq")
public class FavoredSkillRequirement extends Requirement {

	//-------------------------------------------------------------------
	/**
	 */
	public FavoredSkillRequirement() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
		// TODO Auto-generated method stub
		return true;
	}

}
