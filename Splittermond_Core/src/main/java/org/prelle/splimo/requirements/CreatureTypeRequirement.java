/**
 * 
 */
package org.prelle.splimo.requirements;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.persist.CreatureTypeConverter;

/**
 * @author prelle
 *
 */
@Root(name = "creattypereq")
public class CreatureTypeRequirement extends Requirement {
	
	@Attribute
	private boolean not;
	@Attribute(required=true)
	@AttribConvert(CreatureTypeConverter.class)
    private CreatureType type;

	//-------------------------------------------------------------------
	public CreatureTypeRequirement() {
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ((not)?"not ":"")+type;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
//		if (type==null) {
//			type = SplitterMondCore.getCreatureFeatureType(ref);
//			
//		}

		return type!=null;
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
	public CreatureType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	public void setFeature(CreatureType ref) {
		this.type = ref;
//		this.ref = feature.getId();
	}

}
