/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import javafx.beans.property.BooleanProperty;

/**
 * @author prelle
 *
 */
public interface MyPopUpContent<T> {

	public BooleanProperty getReadyProperty();

	public T getSelected();
	
}
