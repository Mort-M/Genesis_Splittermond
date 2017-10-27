/**
 * 
 */
package org.prelle.splimo.requirements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="requires")
@ElementListUnion({
    @ElementList(entry="selreq"     , type=AnyRequirement.class),
    @ElementList(entry="attrreq"    , type=AttributeRequirement.class),
    @ElementList(entry="creatmodreq", type=CreatureModuleRequirement.class),
    @ElementList(entry="creatfeatreq", type=CreatureFeatureRequirement.class),
    @ElementList(entry="creattypereq", type=CreatureTypeRequirement.class),
    @ElementList(entry="damagereq"  , type=DamageRequirement.class),
    @ElementList(entry="favskillreq", type=FavoredSkillRequirement.class),
    @ElementList(entry="itemfeatreq", type=ItemFeatureRequirement.class),
    @ElementList(entry="masterreq"  , type=MastershipRequirement.class),
    @ElementList(entry="powerreq"   , type=PowerRequirement.class),
    @ElementList(entry="resourcereq", type=ResourceRequirement.class),
    @ElementList(entry="skillreq"   , type=SkillRequirement.class),
    @ElementList(entry="specialreq" , type=SpecialRequirement.class),
    @ElementList(entry="spellreq"   , type=SpellRequirement.class),
 })
public class RequirementList extends ArrayList<Requirement> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public RequirementList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public RequirementList(Collection<? extends Requirement> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<Requirement> getRequirements() {
		return this;
	}
}
