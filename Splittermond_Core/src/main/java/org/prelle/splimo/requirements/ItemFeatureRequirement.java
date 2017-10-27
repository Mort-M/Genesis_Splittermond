/**
 * 
 */
package org.prelle.splimo.requirements;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.persist.FeatureTypeConverter;

/**
 * @author prelle
 *
 */
@Root(name = "itemfeatreq")
public class ItemFeatureRequirement extends Requirement {
	
	@Attribute
	private boolean not;
	@Attribute(name="ref",required=true)
	@AttribConvert(FeatureTypeConverter.class)
	private FeatureType feature;

	//-------------------------------------------------------------------
	public ItemFeatureRequirement() {
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ((not)?"not ":"")+feature;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
//		if (feature==null) {
//			feature = SplitterMondCore.getCreatureFeatureType(ref);
//			
//		}
//
		return feature!=null;
	}

	//-------------------------------------------------------------------
	public boolean isNegated() {
		return not;
	}

	//-------------------------------------------------------------------
	public void setNegated(boolean not) {
		this.not = not;
	}

	//-------------------------------------------------------------------
	public FeatureType getFeature() {
		return feature;
	}

	//-------------------------------------------------------------------
	public void setFeature(FeatureType ref) {
		this.feature = ref;
//		this.ref = feature.getId();
	}

}
