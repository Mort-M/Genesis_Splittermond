package org.prelle.splimo.charctrl;

import java.util.List;

import org.prelle.splimo.Background;
import org.prelle.splimo.chargen.LetUserChooseListener;

public interface BackgroundController extends Controller {

	//-------------------------------------------------------------------
	public abstract void setIncludeUncommonBackgrounds(boolean uncommon);

	//-------------------------------------------------------------------
	/**
	 * Return the list of resources that can be added
	 */
	public abstract List<Background> getAvailableBackgrounds();

	//-------------------------------------------------------------------
	public abstract Background getSelected();

	//-------------------------------------------------------------------
	public abstract void select(Background data, LetUserChooseListener callback);

}