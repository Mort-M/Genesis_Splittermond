/**
 * 
 */
package org.prelle.splimo;

/**
 * @author prelle
 *
 */
public interface MastershipOrSpecialization extends Comparable<MastershipOrSpecialization> {

	//-------------------------------------------------------------------
	public String getName();
	
	//-------------------------------------------------------------------
	public Skill getSkill();
	
	//-------------------------------------------------------------------
	public int getLevel();
	
}
