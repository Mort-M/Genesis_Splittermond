/**
 *
 */
package org.prelle.splimo.gamemaster;

import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SpliMoNameTable.OPTIONS;

import de.rpgframework.core.Filter;
import de.rpgframework.worldinfo.InformationLevel;

/**
 * @author Stefan
 *
 */
public class TownOrRuralFilter implements Filter {

	private static PropertyResourceBundle RES = SplitterMondCore.getI18nResources();

	//--------------------------------------------------------------------
	/**
	 */
	public TownOrRuralFilter() {
		// TODO Auto-generated constructor stub
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
		return RES.getString("label.type");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.Filter#getChoices()
	 */
	@Override
	public List<Object> getChoices() {
		return Arrays.asList(OPTIONS.TOWN, OPTIONS.RURAL);
	}

}
