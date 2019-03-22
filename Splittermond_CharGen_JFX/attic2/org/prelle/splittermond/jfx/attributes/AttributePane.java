/**
 * 
 */
package org.prelle.splittermond.jfx.attributes;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splittermond.chargen.jfx.ViewMode;

import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class AttributePane extends HBox {

	private AttributeController control;
	private ViewMode            mode;
	
	private AttributePanePrimary primary;
	private AttributePaneSecondary secondary;

	//--------------------------------------------------------------------
	public AttributePane(AttributeController cntrl, ViewMode mode) {
		this.control = cntrl;
		this.mode    = mode;
		assert control!=null;
		assert mode   !=null;
		
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		primary = new AttributePanePrimary(control, mode);
		secondary = new AttributePaneSecondary(control, mode);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setSpacing(5);
		getChildren().addAll(primary, secondary);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		primary.setData(model);
		secondary.setData(model);
	}

}
