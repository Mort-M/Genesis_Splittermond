/**
 *
 */
package org.prelle.splimo.npc;

import org.prelle.splimo.creature.Creature;

/**
 * @author Stefan
 *
 */
public class NPCGenerator {

	private Creature model;
	private CreatureTypeController ctrlCreatureType;
	private NPCAttributeGenerator ctrlAttributes;
	private NPCWeaponController ctrlCreatureWeapon;

	//--------------------------------------------------------------------
	/**
	 */
	public NPCGenerator(Creature model) {
		this.model = model;

		ctrlCreatureType = new CreatureTypeController(model);
		ctrlAttributes   = new NPCAttributeGenerator(model);
		ctrlCreatureWeapon = new NPCWeaponController(model);
	}

	//--------------------------------------------------------------------
	public Creature getCreature() {
		return model;
	}

	//--------------------------------------------------------------------
	public CreatureTypeController getCreatureTypeController() {
		return ctrlCreatureType;
	}

	//--------------------------------------------------------------------
	public NPCAttributeGenerator getAttributeController() {
		return ctrlAttributes;
	}

	//--------------------------------------------------------------------
	public NPCWeaponController getWeaponController() {
		return ctrlCreatureWeapon;
	}

}
