/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="languages")
@ElementList(entry="language",type=Language.class,inline=true)
public class LanguageList extends ArrayList<Language> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public LanguageList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public LanguageList(Collection<? extends Language> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<Language> getData() {
		return this;
	}
}
