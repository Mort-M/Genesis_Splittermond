/**
 * 
 */
package org.prelle.splittermond.chargen.lvl.jfx;

import javafx.beans.property.BooleanProperty;

/**
 * @author prelle
 *
 */
public interface MyPopUpContent<T> {

	public BooleanProperty getReadyProperty();

	public T getSelected();
	
}
