package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Culture;
import org.prelle.splimo.chargen.LetUserChooseListener;

public interface CultureController extends Controller {

	//-------------------------------------------------------------------
	/**
	 * Return the list of resources that can be added
	 */
	public abstract List<Culture> getAvailableBackgrounds();

	//-------------------------------------------------------------------
	public abstract void select(Culture data, LetUserChooseListener callback);

}