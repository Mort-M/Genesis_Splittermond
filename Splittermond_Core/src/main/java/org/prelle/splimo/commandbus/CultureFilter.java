/**
 *
 */
package org.prelle.splimo.commandbus;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.prelle.splimo.Culture;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.core.Filter;
import de.rpgframework.worldinfo.InformationLevel;

/**
 * @author Stefan
 *
 */
public class CultureFilter implements Filter {

	private static PropertyResourceBundle RES = SplitterMondCore.getI18nResources();

	private List<Culture> choices;

	//--------------------------------------------------------------------
	/**
	 */
	public CultureFilter(List<Culture> options) {
		this.choices = new ArrayList<>(options);
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
		return RES.getString("filter.culture");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.Filter#getChoices()
	 */
	@Override
	public List<Object> getChoices() {
		List<Object> ret = new ArrayList<Object>();
		for (Culture tmp : choices) {
			ret.add(tmp.getName());
		}
		return ret;
	}

}
