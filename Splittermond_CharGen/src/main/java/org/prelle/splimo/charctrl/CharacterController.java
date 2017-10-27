/**
 * 
 */
package org.prelle.splimo.charctrl;


/**
 * Common interface for creating and leveling a character
 * 
 * @author prelle
 *
 */
public interface CharacterController extends Controller {
	
	//-------------------------------------------------------------------
	public AttributeController getAttributeController();
	
	//-------------------------------------------------------------------
	public PowerController getPowerController();
	
	//-------------------------------------------------------------------
	public CultureLoreController getCultureLoreController();
	
	//-------------------------------------------------------------------
	public LanguageController getLanguageController();
	
	//-------------------------------------------------------------------
	public ResourceController getResourceController();
	
	//-------------------------------------------------------------------
	public SkillController getSkillController();
	
	//-------------------------------------------------------------------
	public MastershipController getMastershipController();
	
	//-------------------------------------------------------------------
	public SpellController getSpellController();

	
}
