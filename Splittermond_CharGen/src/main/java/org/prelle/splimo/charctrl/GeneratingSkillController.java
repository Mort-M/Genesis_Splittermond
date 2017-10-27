/**
 * 
 */
package org.prelle.splimo.charctrl;

import org.prelle.splimo.Skill.SkillType;

/**
 * @author prelle
 *
 */
public interface GeneratingSkillController extends SkillController, Generator {

	//--------------------------------------------------------------------
	public int getFreeMastershipsLeft(SkillType type);

}
