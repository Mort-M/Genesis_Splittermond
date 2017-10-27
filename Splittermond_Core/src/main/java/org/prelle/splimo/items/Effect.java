/**
 * 
 */
package org.prelle.splimo.items;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.modifications.SpellModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * Wrapper for magic modifcations by the item.
 * 
 * @author prelle
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Effect {
	
	@XmlAttribute
	private int size;
	@XmlElementWrapper
	@XmlElements({ 
	    @XmlElement(name="attrmod", type=AttributeModification.class),
	    @XmlElement(name="spellmod", type=SpellModification.class),
	    @XmlElement(name="skillmod", type=SkillModification.class),
	})
	private List<Modification> modifications;

	//--------------------------------------------------------------------
	public Effect() {
		modifications = new ModificationList();
	}

	//--------------------------------------------------------------------
	public Effect(int size, Modification mod) {
		this.size = size;
		modifications = new ModificationList();
		modifications.add(mod);
	}

	//--------------------------------------------------------------------
	/**
	 * When creating an item how many item quality slots does the effect
	 * need.
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public List<Modification> getModifications() {
		return modifications;
	}

	//--------------------------------------------------------------------
	public void setModifications(List<Modification> modifications) {
		this.modifications = modifications;
	}

	//--------------------------------------------------------------------
	public void addModification(Modification modification) {
		modifications.add(modification);
	}
	
}
