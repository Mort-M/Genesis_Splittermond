/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.jfx.powers.PowerPane;
import org.prelle.splittermond.jfx.resources.ResourcePane;

/**
 * @author prelle
 *
 */
public class PowerResourceBlock extends HBox {
	
	private PowerPane    powers;
	private ResourcePane resources;

	//--------------------------------------------------------------------
	public PowerResourceBlock() {
		super(10);
	}

	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter model, CharacterController control) {
		powers    = new PowerPane(control.getPowerController(), true);
		resources = new ResourcePane(control.getResourceController(), true, false);
		getChildren().add(powers);
		getChildren().add(resources);
		HBox.setHgrow(resources, Priority.SOMETIMES);
		HBox.setHgrow(powers, Priority.SOMETIMES);

		powers.setData(model);
		resources.setData(model);
	}

}
