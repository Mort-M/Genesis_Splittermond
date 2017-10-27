package org.prelle.splimo.requirements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.Root;


@Root(name = "selreq")
public class AnyRequirement extends Requirement {
    
	@ElementListUnion({
	    @ElementList(entry="attrreq"   , type=AttributeRequirement.class),
	    @ElementList(entry="creatmodreq", type=CreatureModuleRequirement.class),
	    @ElementList(entry="creatfeatreq", type=CreatureFeatureRequirement.class),
	    @ElementList(entry="masterreq", type=MastershipRequirement.class),
	    @ElementList(entry="powerreq" , type=PowerRequirement.class),
	    @ElementList(entry="resourcereq", type=ResourceRequirement.class),
	    @ElementList(entry="skillreq", type=SkillRequirement.class),
	    @ElementList(entry="specialreq" , type=SpecialRequirement.class),
	    @ElementList(entry="spellreq", type=SpellRequirement.class),
	 })
    protected List<Requirement> optionList;
    
    //-----------------------------------------------------------------------
    public AnyRequirement() {
        optionList   = new ArrayList<Requirement>();
    }
    
    //-----------------------------------------------------------------------
    public AnyRequirement(List<Requirement> mods) {
        this.optionList   = mods;
   }
    
    //-----------------------------------------------------------------------
    public AnyRequirement(Requirement... mods) {
        this.optionList   = new ArrayList<Requirement>();
        for (Requirement tmp : mods)
        	optionList.add(tmp);
    }
    
    //-----------------------------------------------------------------------
    public void add(Requirement mod) {
        if (!optionList.contains(mod)) {
            optionList.add(mod);
        }
    }
    
    //-----------------------------------------------------------------------
    public void add(Object mod) {
        if (!optionList.contains(mod) && mod instanceof Requirement) {
            optionList.add((Requirement)mod);
        }
    }
   
    //-----------------------------------------------------------------------
    public void remove(Requirement mod) {
        optionList.remove(mod);
    }
    
    //-----------------------------------------------------------------------
    public boolean equals(Object o) {
        if (o instanceof AnyRequirement) {
            AnyRequirement mc = (AnyRequirement)o;
            return optionList.equals(mc.getOptionList());
        }
        return false;
    }
    
    //-----------------------------------------------------------------------
    public Requirement[] getOptions() {
        Requirement[] modArray = new Requirement[optionList.size()];
        modArray = (Requirement[]) optionList.toArray(modArray);
        return modArray;
    }
    
    //-----------------------------------------------------------------------
    public List<Requirement> getOptionList() {
    	return new ArrayList<Requirement>(optionList);
    }
    
    //-----------------------------------------------------------------------
    public String toString() {
        StringBuffer buf = new StringBuffer("Require any of (");
        
        Iterator<Requirement> it = optionList.iterator();
        while (it.hasNext()) {
            buf.append(it.next().toString());
            if (it.hasNext())
                buf.append("|");
        }
        
        buf.append(")");
        return buf.toString();
    }

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
		for (Requirement req : optionList)
			if (!req.resolve())
				return false;
		return true;
	}
    
}
