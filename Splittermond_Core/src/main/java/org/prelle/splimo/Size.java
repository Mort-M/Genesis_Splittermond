package org.prelle.splimo;

import org.prelle.simplepersist.Attribute;


public class Size {
    
    /** Basic size */
    @Attribute(name="base")
    private int    size_base;
    /** Amounts of D20 to add to siz */
    @Attribute(name="d10",required=false)
    private int    d10;
    /** Amounts of D6 to add to size */
    @Attribute(name="d6",required=false)
    private int    d6;
    /** To substract from size to get weight */
    @Attribute
    private int    wmin;
    @Attribute
    private int    wmax;
    
    //-----------------------------------------------------------------------
    public Size() {
     }
    
    //-----------------------------------------------------------------------
    public Size(int szBase, int szD20, int szD6, int wmin, int wmax) {
        this.size_base = szBase;
        this.d10 = szD20;
        this.d6 = szD6;
        this.wmin = wmin;
        this.wmax = wmax;
    }
    
    //-----------------------------------------------------------------------
    public void setSizeBase(int base) {size_base = base;}
    public void setSizeD10(int d10) {this.d10 = d10;}
    public void setSizeD6(int d6) {this.d6 = d6;}
    
    //-----------------------------------------------------------------------
    public int getSizeBase() {return size_base;}
    public int getSizeD10() {return d10;}
    public int getSizeD6() {return d6;}
    
    //-----------------------------------------------------------------------
    public void setWeightMin(int weight) {this.wmin = weight;}
    public int getWeightMin() {return wmin;}
    
    //-----------------------------------------------------------------------
    public void setWeightMax(int weight) {this.wmax = weight;}
    public int getWeightMax() {return wmax;}
    
    //-----------------------------------------------------------------------
    public boolean equals(Object o) {
        if (!(o instanceof Size))
            return false;
        Size other = (Size)o;
        if (size_base!=other.getSizeBase()) return false;
        if (d10!=other.getSizeD10()) return false;
        if (d6!=other.getSizeD6()) return false;
        if (wmin!=other.getWeightMin()) return false;
        if (wmax!=other.getWeightMax()) return false;
        return true;
    }
}
