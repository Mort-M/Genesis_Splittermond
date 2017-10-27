/**
 *
 */
package org.prelle.splimo.gamemaster;

import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.core.Filter;
import de.rpgframework.worldinfo.InformationLevel;

/**
 * @author Stefan
 *
 */
public class GenderFilter implements Filter {

	private static PropertyResourceBundle RES = SplitterMondCore.getI18nResources();

	//--------------------------------------------------------------------
	public GenderFilter() {
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.Filter#getInfoLevel()
	 */
	@Override
	public InformationLevel getInfoLevel() {
		return InformationLevel.ROLEPLAYING_SYSTEM;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.Filter#getName()
	 */
	@Override
	public String getName() {
		return RES.getString("label.gender");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.Filter#getChoices()
	 */
	@Override
	public List<Object> getChoices() {
		return Arrays.asList((Object[])Gender.values());
	}

}
