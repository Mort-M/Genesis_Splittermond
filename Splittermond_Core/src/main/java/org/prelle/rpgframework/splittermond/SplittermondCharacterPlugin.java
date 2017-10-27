/**
 * 
 */
package org.prelle.rpgframework.splittermond;

import java.io.IOException;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.character.DecodeEncodeException;

/**
 * @author prelle
 *
 */
public class SplittermondCharacterPlugin {

	//-------------------------------------------------------------------
	/**
	 */
	public SplittermondCharacterPlugin() {
	}

	//-------------------------------------------------------------------
	public byte[] getImageBytes(SpliMoCharacter charac) {
		return charac.getImage();
	}

	//-------------------------------------------------------------------
	public String getName(SpliMoCharacter data) {
		return data.getName();
	}

	//-------------------------------------------------------------------
	public byte[] marshal(SpliMoCharacter charac) throws DecodeEncodeException {
		return SplitterMondCore.save(charac);
	}

	//-------------------------------------------------------------------
	public SpliMoCharacter unmarshal(byte[] data) throws DecodeEncodeException {
		try {
			return SplitterMondCore.load(data);
		} catch (IOException e) {
			throw new DecodeEncodeException("IO-Error decoding", e);
		}
	}

}
