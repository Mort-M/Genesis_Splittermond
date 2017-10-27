package org.prelle.splimo.charctrl;

import org.prelle.splimo.Attribute;

public interface AttributeController extends Generator {

	//-------------------------------------------------------------------
	public boolean canBeDecreased(Attribute key);

	//-------------------------------------------------------------------
	public boolean canBeIncreased(Attribute key);

	//-------------------------------------------------------------------
	public int getIncreaseCost(Attribute key);

	//-------------------------------------------------------------------
	public boolean increase(Attribute key);

	//-------------------------------------------------------------------
	public boolean decrease(Attribute key);

}