/**
 * 
 */
package org.prelle.splimo.chargen;

import org.prelle.splimo.Skill;

/**
 * @author prelle
 *
 */
public interface SkillValueChangeListener {

	//-------------------------------------------------------------------
	/**
	 * The amount of points distributed on a skill changed
	 */
	public void skillValueChanged(Skill skill, int newValue);
	
}
