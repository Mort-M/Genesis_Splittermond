package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.prelle.simplepersist.ElementList;


public class ColorDiceTable {
    
	@ElementList(entry="color",type=ColorDiceTableEntry.class)
    private List<ColorDiceTableEntry> entries;
    
    //-----------------------------------------------------------------------
    public ColorDiceTable() {
        entries = new ArrayList<ColorDiceTableEntry>();
    }
   
    //-----------------------------------------------------------------------
    public void add(ColorDiceTableEntry entry) {
    	entries.add(entry);
    }
    
    //-----------------------------------------------------------------------
    public void setTable(List<ColorDiceTableEntry> entries) {
    	this.entries = entries;
    }
    
    //-----------------------------------------------------------------------
    public Iterator<ColorDiceTableEntry> getEntries() {
    	return entries.iterator();
    }

    //-----------------------------------------------------------------------
    public String getColor(int d20) {
    	for (ColorDiceTableEntry entry : entries) {
    		if (entry.getFrom()<=d20 && entry.getTo()>=d20) {
    			System.out.println("   returns "+entry.getColor());
    			return entry.getColor();
    		}
    	}
    	return null;
    }
    
//    //-----------------------------------------------------------------------
//    public boolean equals(Object o) {
//        if (!(o instanceof ColorDiceTable)) return false;
//        ColorDiceTable other = (ColorDiceTable)o;
//        for (int i=0; i<color.length; i++) {
//            if (color[i]==null || other.getColor(i+1)==null) {
//                if (other.getColor(i+1)!=color[i]) return false;
//            }
//        }
//        return true;
//    }
    
    //-----------------------------------------------------------------------
    public void dump(StringBuffer buf) {
         StringBuffer line2 = new StringBuffer();
        for (ColorDiceTableEntry entry : entries)
        	buf.append(entry.toString());
        buf.append("\n"+line2+"\n");
    }
    
//    //-----------------------------------------------------------------------
//    /**
//     * Count the amount of different result ranges within the table.
//     */
//    public Object[][] getDifferentRanges() {
//        List<Object[]> list = new ArrayList<Object[]>();
//        int from = 1;
//        String old = color[0];
//        
//        for (int i=1; i<20; i++) {
//            if (color[i]==null) {
//                if (old!=null) {
//                    // Store current data
//                    list.add(new Object[]{new Integer(from), new Integer(i), old});
//                    old = color[i];
//                    from = i+1;
//                } 
//            } else {
//                if (!color[i].equals(old)) {
//                    // Store current data
//                    list.add(new Object[]{new Integer(from), new Integer(i), old});
//                    old = color[i];
//                    from = i+1;
//                }
//            }
//            
//        }
//        list.add(new Object[]{new Integer(from), new Integer(20), old});
//        
//        // Convert list to array
//        Object[][] data2 = new Object[list.size()][];
//        data2 = (Object[][])list.toArray(data2);
//        return data2;
//    }
    
}
