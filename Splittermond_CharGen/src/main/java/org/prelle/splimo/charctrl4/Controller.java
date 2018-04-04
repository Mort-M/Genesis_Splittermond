/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.List;

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

}
