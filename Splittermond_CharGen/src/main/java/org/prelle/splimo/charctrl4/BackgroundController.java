package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Background;

public interface BackgroundController extends Controller {

	//-------------------------------------------------------------------
	public abstract void setIncludeUncommonBackgrounds(boolean uncommon);

	//-------------------------------------------------------------------
	/**
	 * Return the list of resources that can be added
	 */
	public List<Background> getAvailableBackgrounds();

	//-------------------------------------------------------------------
	public void select(Background value);

}