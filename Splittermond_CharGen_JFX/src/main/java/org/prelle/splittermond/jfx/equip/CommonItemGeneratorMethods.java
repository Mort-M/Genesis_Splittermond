/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import org.prelle.javafx.ScreenManager;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * Wird nur verwendet, um mittels DeveloperMode zwischen alter und neuer
 * Ansicht umschalten zu können.
 *
 * @author Stefan
 *
 */
public interface CommonItemGeneratorMethods extends GenerationEventListener {

	public void setScreenManager(ScreenManager manager);

	public void setData(NewItemController ctrl);

	//--------------------------------------------------------------------
	public void updateContent();

}
