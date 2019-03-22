package org.prelle.splimo.charctrl;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;

import de.rpgframework.genericrpg.NumericalValueController;

public interface AttributeController extends Generator, NumericalValueController<Attribute, AttributeValue> {

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