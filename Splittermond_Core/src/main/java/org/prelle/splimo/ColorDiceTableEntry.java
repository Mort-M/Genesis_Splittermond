package org.prelle.splimo;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;


@Root(name = "color")
public class ColorDiceTableEntry {

	@Attribute
    int from;
	@Attribute
    int to;
	@Attribute(name="value")
    String color;

    //------------------------------------------------
    public ColorDiceTableEntry() {    	
    }

    //------------------------------------------------
    public ColorDiceTableEntry(int from, int to, String val) {
    	this.from = from;
    	this.to   = to;
    	color = val;
    }

    //------------------------------------------------
    public String toString() {
    	return String.format("%d - %d = %s", from, to, color);
    }

    //------------------------------------------------
    /**
     * @return Returns the color.
     */
    public String getColor() {
        return color;
    }

    //------------------------------------------------
    /**
     * @param color The color to set.
     */
    public void setColor(String color) {
        this.color = color;
    }

    //------------------------------------------------
    /**
     * @return Returns the from.
     */
    public int getFrom() {
        return from;
    }

    //------------------------------------------------
    /**
     * @param from The from to set.
     */
    public void setFrom(int from) {
        this.from = from;
    }

    //------------------------------------------------
    /**
     * @return Returns the to.
     */
    public int getTo() {
        return to;
    }

    //------------------------------------------------
    /**
     * @param to The to to set.
     */
    public void setTo(int to) {
        this.to = to;
    }

}
