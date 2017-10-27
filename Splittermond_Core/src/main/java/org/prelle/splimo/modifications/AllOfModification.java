/**
 * 
 */
package org.prelle.splimo.modifications;

import java.util.Collection;
import java.util.Date;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name = "allofmod")
@ElementListUnion({
    @ElementList(entry="attrchgmod", type=AttributeChangeModification.class),
    @ElementList(entry="attrmod", type=AttributeModification.class),
    @ElementList(entry="attitudemod", type=AttitudeModification.class),
    @ElementList(entry="backmod", type=BackgroundModification.class),
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
    @ElementList(entry="skillmod", type=SkillModification.class),
    @ElementList(entry="spellmod", type=SpellModification.class),
    @ElementList(entry="itemmod", type=ItemModification.class),
    @ElementList(entry="itemfeaturemod", type=ItemFeatureModification.class),
    @ElementList(entry="featuremod", type=FeatureModification.class),
    @ElementList(entry="moneymod", type=MoneyModification.class),
 })
public class AllOfModification extends ModificationList implements Modification {
    
	@Attribute(required=false)
	protected Integer expCost;
	@Attribute(required=false)
	protected Date date;
	@Attribute(name="cond",required=false)
    private boolean conditional = false;
	/**
	 * If the modification belongs to a module, this keeps the reference
	 */
	protected transient Object source;

	//-------------------------------------------------------------------
	public AllOfModification() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public AllOfModification(Collection<? extends Modification> c) {
		super(c);
	}

	//-------------------------------------------------------------------
    public Modification clone() {
    	throw new InternalError("Clone not implemented");
    }

    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification arg0) {
        return 0;
    }

	//-------------------------------------------------------------------
	/**
	 * @return the expCost
	 */
	public int getExpCost() {
		if (expCost==null)
			return 0;
		return expCost;
	}

	//-------------------------------------------------------------------
	/**
	 * @param expCost the expCost to set
	 */
	public void setExpCost(int expCost) {
		if (expCost==0)
			this.expCost = null;
		else
		this.expCost = expCost;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Datable#getDate()
	 */
	@Override
	public Date getDate() {
		return date;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Datable#setDate(java.util.Date)
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.Modification#getSource()
	 */
	public Object getSource() {
		return source;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.Modification#setSource(java.lang.Object)
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the conditional
	 */
	public boolean isConditional() {
		return conditional;
	}

	//--------------------------------------------------------------------
	/**
	 * @param conditional the conditional to set
	 */
	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}

}
