/**
 * 
 */
package org.prelle.splimo.requirements;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.CreatureModule;

/**
 * @author prelle
 *
 */
@Root(name = "creatmodreq")
public class CreatureModuleRequirement extends Requirement {
	
	@Attribute
	private boolean not;
	@Attribute(required=true)
	private String ref;
	private transient CreatureModule module;

	//-------------------------------------------------------------------
	public CreatureModuleRequirement() {
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ((not)?"not ":"")+ref;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
		if (module==null) {
			module = SplitterMondCore.getCreatureModule(ref);
		}

		return module!=null;
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
	public CreatureModule getModule() {
		return module;
	}

	//-------------------------------------------------------------------
	public void setModule(CreatureModule ref) {
		this.module = ref;
		this.ref = module.getId();
	}

}
