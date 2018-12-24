package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Culture;

public interface CultureController extends Controller {

	//-------------------------------------------------------------------
	/**
	 * Return the list of resources that can be added
	 */
	public List<Culture> getAvailableCultures();

	//-------------------------------------------------------------------
	public void selectCulture(Culture data);

}