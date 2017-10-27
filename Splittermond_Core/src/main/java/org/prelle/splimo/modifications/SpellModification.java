/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.SpellValue;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "spellmod")
public class SpellModification extends ModificationImpl {

	@Element
	private SpellValue spell;
	/**
	 * If set to X>0 , a spell of level (X-1) can be selected from existing magic schools
	 */
	@Attribute(name="levelplusone")
	private int level;
	@Attribute
	private boolean remove;
	
	//--------------------------------------------------------------------
	/**
	 */
	public SpellModification() {
	}
	
	//--------------------------------------------------------------------
	/**
	 */
	public SpellModification(SpellValue spell) {
		this.spell = spell;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.modifications.ModificationImpl#clone()
	 */
	@Override
	public SpellModification clone() {
		SpellModification ret = new SpellModification(spell.clone());
    	ret.cloneAdd(this);
    	return ret;
	}

	//--------------------------------------------------------------------
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (remove) buf.append("Remove ");
		else buf.append("Add ");
		
		if (spell!=null)
			buf.append("spell "+spell.getSpell().getId()+" in school "+spell.getSkill().getId());
		else {
			if (level>0)
				buf.append("spell of lvl "+level);
			else
				buf.append("spell with highest existing level");
		}
		return buf.toString();
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification o) {
		if (o instanceof SpellModification)
			return spell.getSpell().compareTo(((SpellModification)o).getSpell().getSpell());
		return 0;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the spell
	 */
	public SpellValue getSpell() {
		return spell;
	}

	//--------------------------------------------------------------------
	public void setSpell(SpellValue spellVal) {
		this.spell = spellVal;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	//-------------------------------------------------------------------
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the remove
	 */
	public boolean isRemove() {
		return remove;
	}

	//-------------------------------------------------------------------
	/**
	 * @param remove the remove to set
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}

}
