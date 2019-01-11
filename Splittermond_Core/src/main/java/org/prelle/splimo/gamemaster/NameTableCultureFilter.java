/**
 *
 */
package org.prelle.splimo.gamemaster;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Culture;
import org.prelle.splimo.SpliMoNameTable;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.core.Filter;
import de.rpgframework.worldinfo.InformationLevel;

/**
 * @author Stefan
 *
 */
public class NameTableCultureFilter implements Filter {

	private static Logger logger = LogManager.getLogger("splittermond");

	private static PropertyResourceBundle RES = SplitterMondCore.getI18nResources();

	//--------------------------------------------------------------------
	public NameTableCultureFilter() {
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
		return RES.getString("label.culture");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.Filter#getChoices()
	 */
	@Override
	public List<?> getChoices() {
		List<Culture> ret = new ArrayList<>();
		for (SpliMoNameTable table : SplitterMondCore.getNameTables()) {
			if (!ret.contains(table.getCulture())) {
				logger.warn("Add table for culture "+table.getCulture());
				ret.add(table.getCulture());
			}
		}
		return ret;
	}

}
