/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="creatures")
@ElementList(entry="creature",type=Creature.class,inline=true)
public class CreatureList extends ArrayList<Creature> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public CreatureList() {
	}

	//-------------------------------------------------------------------
	public CreatureList(Collection<? extends Creature> c) {
		super(c);
	}

}
