/**
 * 
 */
package org.prelle.splimo.processor;

import java.util.List;

import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public interface SpliMoCharacterProcessor {

	/**
	 * @param unprocessed Unprocessed modifications from previous steps
	 * @return Unprocessed modification
	 */
	public List<Modification> process(SpliMoCharacter model, List<Modification> unprocessed);

}
