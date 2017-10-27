/**
 * 
 */
package org.prelle.splimo.requirements;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.creature.CreatureFeatureType;
import org.prelle.splimo.persist.CreatureFeatureTypeConverter;

/**
 * @author prelle
 *
 */
@Root(name = "creatfeatreq")
public class CreatureFeatureRequirement extends Requirement {
	
	@Attribute
	private boolean not;
	@Attribute(name="ref",required=true)
	@AttribConvert(CreatureFeatureTypeConverter.class)
	private CreatureFeatureType feature;

	//-------------------------------------------------------------------
	public CreatureFeatureRequirement() {
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
	public CreatureFeatureType getFeature() {
		return feature;
	}

	//-------------------------------------------------------------------
	public void setFeature(CreatureFeatureType ref) {
		this.feature = ref;
//		this.ref = feature.getId();
	}

}
