/**
 * 
 */
package org.prelle.splimo.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "features")
@ElementList(entry="feature",type=Feature.class,inline=true)
public class FeatureList extends ArrayList<Feature> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public FeatureList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public FeatureList(Collection<? extends Feature> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	@XmlElements({ 
	    @XmlElement(name="feature", type=Feature.class),
	})
	public List<Feature> getData() {
		return this;
	}
}
