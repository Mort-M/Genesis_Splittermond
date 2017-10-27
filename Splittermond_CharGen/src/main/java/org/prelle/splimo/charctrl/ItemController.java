/**
 * 
 */
package org.prelle.splimo.charctrl;

import java.util.List;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Spell;
import org.prelle.splimo.items.ItemAttribute;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public interface ItemController extends Controller {
	
	public void setIgnoreLimits(boolean ignore);
	

	public boolean canBeIncreased(ItemAttribute attrib);

	public boolean canBeDecreased(ItemAttribute attrib);

	public boolean decrease(ItemAttribute attrib);

	public boolean increase(ItemAttribute attrib);

	
//	public boolean canBeIncreased(Attribute attrib);
//
//	public boolean canBeDecreased(Attribute attrib);
//
//	public boolean decrease(Attribute attrib);
//
//	public boolean increase(Attribute attrib);
//
//	
//	public boolean canBeIncreased(Skill skill);
//
//	public boolean canBeDecreased(Skill skill);
//
//	public boolean decrease(Skill skill);
//
//	public boolean increase(Skill skill);
//	
//	
//	public boolean canEmbedSpell(SpellValue spell);
//	
//	public boolean addEmbeddedSpell(SpellValue spell);
//	
//	public boolean removeEmbeddedSpell(SpellValue spell);

	public List<Modification> getAvailableQualityModifications();

	public List<Modification> getAvailableMagicModifications();
	
	public boolean canAddModification(Modification mod);

	public boolean addModification(Modification mod);

	public boolean removeModification(Modification mod);
	
	public boolean canBeIncreased(Modification mod);

	public boolean canBeDecreased(Modification mod);
	
	public boolean decrease(Modification mod);

	public boolean increase(Modification mod);

	/**
	 * Get all spells for a given school which are low enough to fit into the artifact
	 */
	public List<Spell> getEmbeddableSpells(Skill newVal);

}
