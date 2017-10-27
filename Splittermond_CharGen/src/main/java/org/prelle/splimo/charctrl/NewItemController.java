/**
 * 
 */
package org.prelle.splimo.charctrl;

import java.util.List;

import org.prelle.splimo.DeityType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.Material;
import org.prelle.splimo.items.PersonalizationReference;

/**
 * @author prelle
 *
 */
public interface NewItemController extends Generator {

	//-------------------------------------------------------------------
	public CarriedItem getItem();
	
	//-------------------------------------------------------------------
	public List<Enhancement> getAvailableEnhancements();
	
	//-------------------------------------------------------------------
	public List<Enhancement> getAvailableEnhancements(EnhancementType type);
	
	//-------------------------------------------------------------------
	public List<Enhancement> getAvailableEnhancements(DeityType type);
	
//	//-------------------------------------------------------------------
//	public boolean needsSelection(Enhancement data);
//	
//	//-------------------------------------------------------------------
//	public Object[] getSelectionOptions(Enhancement data);
	
	//-------------------------------------------------------------------
	public boolean canBeAdded(Enhancement toAdd);
	
	//-------------------------------------------------------------------
	public boolean canBeRemoved(EnhancementReference toRemove);
	
	//-------------------------------------------------------------------
	public EnhancementReference addEnhancement(Enhancement toAdd);
	
	//-------------------------------------------------------------------
	public EnhancementReference addEnhancement(Enhancement toAdd, SkillSpecialization skillSpec);
	
	//-------------------------------------------------------------------
	public EnhancementReference addEnhancement(Enhancement toAdd, SpellValue spell);
	
	//-------------------------------------------------------------------
	public void removeEnhancement(EnhancementReference toRemove);
	
	//-------------------------------------------------------------------
	public List<Material> getAvailableMaterials();
	
	//-------------------------------------------------------------------
	public void setMaterial(Material material);

	//-------------------------------------------------------------------
	public int getPrice();
	
	//-------------------------------------------------------------------
	public List<PersonalizationReference> getAvailableFirstPersonalizations();
	
	//-------------------------------------------------------------------
	public void setFirstPersonalization(PersonalizationReference person);
	
	//-------------------------------------------------------------------
	public boolean isSecondPersonalizationAllowed();
	
	//-------------------------------------------------------------------
	public List<PersonalizationReference> getAvailableSecondPersonalizations();
	
	//-------------------------------------------------------------------
	public void setSecondPersonalization(PersonalizationReference person);

	List<PersonalizationReference> makeCompartibleList(PersonalizationReference template,
			List<PersonalizationReference> values);
	
}
