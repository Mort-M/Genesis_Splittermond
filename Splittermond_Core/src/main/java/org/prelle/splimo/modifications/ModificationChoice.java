package org.prelle.splimo.modifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.persist.DistributeConverter;

import de.rpgframework.genericrpg.modification.Modification;


@Root(name = "selmod")
public class ModificationChoice extends ModificationImpl {
    
	@ElementListUnion({
	    @ElementList(entry="allofmod", type=AllOfModification.class),
	    @ElementList(entry="attrmod", type=AttributeModification.class),
	    @ElementList(entry="backmod", type=BackgroundModification.class),
	    @ElementList(entry="countmod", type=CountModification.class),
	    @ElementList(entry="cultureloremod", type=CultureLoreModification.class),
	    @ElementList(entry="damagemod", type=DamageModification.class),
	    @ElementList(entry="languagemod", type=LanguageModification.class),
	    @ElementList(entry="mastermod", type=MastershipModification.class),
	    @ElementList(entry="notbackmod", type=NotBackgroundModification.class),
	    @ElementList(entry="pointsmod", type=PointsModification.class),
	    @ElementList(entry="powermod", type=PowerModification.class),
	    @ElementList(entry="reqmod", type=RequirementModification.class),
	    @ElementList(entry="resourcemod", type=ResourceModification.class),
	    @ElementList(entry="racemod", type=RaceModification.class),
	    @ElementList(entry="skillmod", type=SkillModification.class),
	    @ElementList(entry="itemmod", type=ItemModification.class),
	    @ElementList(entry="itemfeaturemod", type=ItemFeatureModification.class),
	    @ElementList(entry="featuremod", type=FeatureModification.class),
	    @ElementList(entry="creaturefeaturemod", type=CreatureFeatureModification.class),
	    @ElementList(entry="subselmod", type=SubModificationChoice.class),
	 })
    protected List<Modification> optionList;
	@Attribute(name="num",required=false)
    protected int numberOfChoices; 
	@Attribute(name="dist",required=false)
    protected int distribute; 
	@Attribute(required=false)
	@AttribConvert(DistributeConverter.class)
    protected Integer[] values; 
    
    //-----------------------------------------------------------------------
    public ModificationChoice() {
        optionList   = new ArrayList<Modification>();
        numberOfChoices = 1;
        values = new Integer[0];
    }
    
    //-----------------------------------------------------------------------
    public ModificationChoice(int noOfChoices) {
        optionList   = new ArrayList<Modification>();
        this.numberOfChoices = noOfChoices;
        values = new Integer[0];
   }
    
    //-----------------------------------------------------------------------
    public ModificationChoice(List<Modification> mods, int val) {
        this.optionList   = mods;
        this.numberOfChoices = val;
        values = new Integer[0];
   }
    
    //-----------------------------------------------------------------------
    public ModificationChoice(Modification... mods) {
        this.optionList   = new ArrayList<Modification>();
        for (Modification tmp : mods)
        	optionList.add(tmp);
        this.numberOfChoices = 1;
        values = new Integer[0];
    }
    
    //-----------------------------------------------------------------------
    public void add(Modification mod) {
        if (!optionList.contains(mod)) {
            optionList.add(mod);
            Collections.sort(optionList, new Comparator<Modification>(){
                public int compare(Modification arg0, Modification arg1) {
                    if (arg0.getClass()!=arg1.getClass())
                        return arg0.getClass().getName().compareTo(arg1.getClass().getName());
                    return arg0.compareTo(arg1);
                }});
        }
    }
    
    //-----------------------------------------------------------------------
    public void add(Object mod) {
    	add( (Modification)mod );
    }
    
    //-----------------------------------------------------------------------
    public void remove(Modification mod) {
        optionList.remove(mod);
    }
    
    //-----------------------------------------------------------------------
    public boolean equals(Object o) {
        if (o instanceof ModificationChoice) {
            ModificationChoice mc = (ModificationChoice)o;
            if (numberOfChoices!=mc.getNumberOfChoices()) return false;
//            Modification[] otherOpt = mc.getOptions();
//            if (otherOpt.length!=mods.size())
//                return false;
//            for (int i=0; i<mods.size(); i++)
//                if (!mods.get(i).equals(otherOpt[i]))
//                    return false;
//            return true;
            return optionList.equals(mc.getOptionList());
        }
        return false;
    }
    
    //-----------------------------------------------------------------------
    public Modification[] getOptions() {
        Modification[] modArray = new Modification[optionList.size()];
        modArray = (Modification[]) optionList.toArray(modArray);
        return modArray;
    }
    
    //-----------------------------------------------------------------------
    public List<Modification> getOptionList() {
    	return new ArrayList<Modification>(optionList);
    }
    
    //-----------------------------------------------------------------------
    public String toString() {
    	StringBuffer buf = null;
    	if (distribute>0) {
    		buf = new StringBuffer("Distribute "+distribute+" on (");
    		buf = new StringBuffer("Select "+numberOfChoices+" of (");

    		Iterator<Modification> it = optionList.iterator();
    		while (it.hasNext()) {
    			buf.append(it.next().toString());
    			if (it.hasNext())
    				buf.append("|");
    		}
    	} else {  	
    		buf = new StringBuffer("Select "+numberOfChoices+" of (");

    		Iterator<Modification> it = optionList.iterator();
    		while (it.hasNext()) {
    			buf.append(it.next().toString());
    			if (it.hasNext())
    				buf.append("|");
    		}
    	}

        buf.append(")");
        return buf.toString();
    }
    
    //------------------------------------------------
    /* 
     * @see org.prelle.dsatool.modifications.Modification#clone()
     */
    @SuppressWarnings("unchecked")
    public ModificationChoice clone() {
    	List<Modification> newMods = (ArrayList<Modification>) ((ArrayList<Modification>)optionList).clone();
    	ModificationChoice ret = new ModificationChoice(newMods, numberOfChoices);
    	ret.cloneAdd(this);
    	return ret;
    }

    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification arg0) {
        return 0;
    }

    //-------------------------------------------------------
    /**
     * @return Returns the numberOfChoices.
     */
   public int getNumberOfChoices() {
        return numberOfChoices;
    }

    //--------------------------------------------------------
    /**
     * @param numberOfChoices The numberOfChoices to set.
     */
    public void setNumberOfChoices(int numberOfChoices) {
        this.numberOfChoices = numberOfChoices;
    }

    //-------------------------------------------------------
   public Integer[] getValues() {
        return values;
    }

    //--------------------------------------------------------
    public void setValues(int[] values) {
        this.values = new Integer[values.length];
        for (int i=0; i<values.length; i++)
        	this.values[i] = values[i];
    }

    //--------------------------------------------------------
    public void setValues(Integer[] values) {
        this.values = new Integer[values.length];
        for (int i=0; i<values.length; i++)
        	this.values[i] = values[i];
    }

	//-------------------------------------------------------------------
	/**
	 * @return the distribute
	 */
	public int getDistribute() {
		return distribute;
	}

	//-------------------------------------------------------------------
	/**
	 * @param distribute the distribute to set
	 */
	public void setDistribute(int distribute) {
		this.distribute = distribute;
	}
    
}// ModificationChoice
