/**
 *
 */
package org.prelle.splimo.npc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureWeapon;

/**
 * @author Stefan
 *
 */
public class NPCWeaponController {
	
	private static Logger logger = LogManager.getLogger("splittermond.npcgen");

	private Creature model;

	//--------------------------------------------------------------------
	public NPCWeaponController(Creature model) {
		this.model = model;
	}

	//--------------------------------------------------------------------
	public void addWeapon(CreatureWeapon value) {

	}

}
