/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Language;
import org.prelle.splimo.LanguageReference;

/**
 * @author prelle
 *
 */
public interface LanguageController extends Controller {
	
	//-------------------------------------------------------------------
	/**
	 * Return those resources that can be added/opened
	 */
	public List<Language> getAvailableLanguages();
	
	//-------------------------------------------------------------------
	/**
	 * Add the resource to the list of existing resources.
	 * Some resources may be added multiple times.
	 */
	public LanguageReference select(Language res);
	
	//-------------------------------------------------------------------
	public boolean canBeDeselected(LanguageReference ref);
	
	//-------------------------------------------------------------------
	public boolean canBeSelected(Language lore);

	//-------------------------------------------------------------------
	public boolean deselect(LanguageReference ref);
	
}
