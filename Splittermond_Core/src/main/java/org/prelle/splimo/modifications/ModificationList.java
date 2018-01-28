/**
 * 
 */
package org.prelle.splimo.modifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name="modifications")
@ElementListUnion({
    @ElementList(entry="attrchgmod", type=AttributeChangeModification.class),
    @ElementList(entry="attrmod", type=AttributeModification.class),
    @ElementList(entry="allofmod", type=AllOfModification.class),
    @ElementList(entry="attitudemod", type=AttitudeModification.class),
    @ElementList(entry="backmod", type=BackgroundModification.class),
    @ElementList(entry="condmod", type=ConditionalModification.class),
    @ElementList(entry="cultureloremod", type=CultureLoreModification.class),
    @ElementList(entry="creaturefeaturemod", type=CreatureFeatureModification.class),
    @ElementList(entry="creaturetypemod", type=CreatureTypeModification.class),
    @ElementList(entry="countmod", type=CountModification.class),
    @ElementList(entry="damagemod", type=DamageModification.class),
    @ElementList(entry="edumod", type=EducationModification.class),
    @ElementList(entry="languagemod", type=LanguageModification.class),
    @ElementList(entry="mastermod", type=MastershipModification.class),
    @ElementList(entry="notbackmod", type=NotBackgroundModification.class),
    @ElementList(entry="pointsmod", type=PointsModification.class),
    @ElementList(entry="powermod", type=PowerModification.class),
    @ElementList(entry="reqmod", type=RequirementModification.class),
    @ElementList(entry="resourcemod", type=ResourceModification.class),
    @ElementList(entry="racemod", type=RaceModification.class),
    @ElementList(entry="selmod", type=ModificationChoice.class),
    @ElementList(entry="skillmod", type=SkillModification.class),
    @ElementList(entry="spellmod", type=SpellModification.class),
    @ElementList(entry="itemmod", type=ItemModification.class),
    @ElementList(entry="itemfeaturemod", type=ItemFeatureModification.class),
    @ElementList(entry="featuremod", type=FeatureModification.class),
    @ElementList(entry="moneymod", type=MoneyModification.class),
//    @ElementList(entry="subselmod", type=SubModificationChoice.class),
 })
public class ModificationList extends ArrayList<Modification> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public ModificationList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public ModificationList(Collection<? extends Modification> c) {
		super(c);
		}

	//-------------------------------------------------------------------
	public List<Modification> getModificiations() {
		return this;
	}
}
