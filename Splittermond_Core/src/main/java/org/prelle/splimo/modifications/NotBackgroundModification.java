/**
 * 
 */
package org.prelle.splimo.modifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Background;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "notbackmod")
public class NotBackgroundModification extends ModificationImpl {
	
	@Attribute
	private String ids;

	//-------------------------------------------------------------------
	/**
	 */
	public NotBackgroundModification() {
		ids="";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "backgrounds not="+ids;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public List<Background> getBackgrounds() {
		Collection<String> tmp = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(ids, ", ");
		while (tok.hasMoreTokens())
			tmp.add(tok.nextToken());
		
		List<Background> ret = new ArrayList<Background>(SplitterMondCore.getBackgrounds());
		for (String ref : tmp) {
			Background data = SplitterMondCore.getBackground(ref);
			if (ret.contains(data))
				ret.remove(data);
		}
		
		return ret;
	}

}
