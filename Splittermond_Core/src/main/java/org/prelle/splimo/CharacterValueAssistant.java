/**
 * 
 */
package org.prelle.splimo;

import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ModificationSource;

/**
 * @author prelle
 *
 */
public class CharacterValueAssistant {

	//--------------------------------------------------------------------
	public static AttributeValue getDefense(SpliMoCharacter model) {
		AttributeValue unmodified = model.getAttribute(Attribute.DEFENSE);
		
		AttributeValue value = new AttributeValue(unmodified);
		for (CarriedItem item : model.getItems()) {
			if (item.getLocation()!=ItemLocationType.BODY)
				continue;
			if (item.getItem().isType(ItemType.ARMOR)) {
				AttributeModification mod = new AttributeModification(Attribute.DEFENSE, item.getDefense(ItemType.ARMOR), ModificationSource.EQUIPMENT);
				mod.setSource(item);
				value.addModification(mod);
			} else
			if (item.getItem().isType(ItemType.SHIELD)) {
				AttributeModification mod = new AttributeModification(Attribute.DEFENSE, item.getDefense(ItemType.SHIELD), ModificationSource.EQUIPMENT);
				mod.setSource(item);
				value.addModification(mod);
			}
		}
		
		return value;
	}

	//--------------------------------------------------------------------
	/**
	 * When the model is read from XML, some modifications (e.g. Resistances
	 * and defense depending on level) must be calculated
	 */
	public static void calculateModifications(SpliMoCharacter model) {
		
	}

}
