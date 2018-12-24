/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.List;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public interface Controller {
	
	//-------------------------------------------------------------------
	/**
	 * Returns a list of steps to do in this controller
	 */
	public List<ToDoElement> getToDos();
	
	//-------------------------------------------------------------------
	public List<DecisionToMake> getDecisionsToMake();
	
	//-------------------------------------------------------------------
	public void decide(DecisionToMake choice, List<Modification> choosen);

}
