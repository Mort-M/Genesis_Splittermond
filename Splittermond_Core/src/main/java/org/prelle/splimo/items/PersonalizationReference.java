/**
 * 
 */
package org.prelle.splimo.items;

import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.AttributeChangeModification;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.persist.PersonalizationConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name="personalref")
public class PersonalizationReference {

	@Attribute(name="ref", required=true)
	@AttribConvert(PersonalizationConverter.class)
	private Personalization personalization;
	@Element
	private ModificationList modifications;
	
	//-------------------------------------------------------------------
	public PersonalizationReference() {
		modifications = new ModificationList();
	}
	
	//-------------------------------------------------------------------
	/**
	 */
	public PersonalizationReference(Personalization enhance) {
		this.personalization = enhance;
		modifications = new ModificationList();
	}
	
	//-------------------------------------------------------------------
	public String toString() {
		if (personalization==null)
			return personalization+" (mods="+modifications+")";
		else
			return personalization.getId()+" (mods="+modifications+")";
	}
	
	//-------------------------------------------------------------------
	public String getName() {
		if (!modifications.isEmpty() && modifications.get(0) instanceof AttributeChangeModification) {
			AttributeChangeModification mod = (AttributeChangeModification)modifications.get(0);
			return mod.getFrom().getShortName()+" -> "+mod.getTo().getShortName();
		}
		return personalization.getName();
	}
	
	//-------------------------------------------------------------------
	public Personalization getPersonalization() {
		return personalization;
	}
	
	//-------------------------------------------------------------------
	public String getID() {
		return super.toString();
	}

	//--------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public List<Modification> getModifications() {
		return modifications;
	}

	//--------------------------------------------------------------------
	/**
	 * @param modifications the modifications to set
	 */
	public void addModification(Modification mod) {
		modifications.add(mod);
	}

}
