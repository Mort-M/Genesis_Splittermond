/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="attributes")
@ElementList(entry="attr", type=AttributeValue.class)
public class Attributes extends ArrayList<AttributeValue> {

	private final static Logger logger = Logger.getLogger("splittermond");

	private transient Map<Attribute, AttributeValue> secondary;

	//-------------------------------------------------------------------
	/**
	 */
	public Attributes() {
		secondary = new HashMap<Attribute, AttributeValue>();
		for (Attribute attr : Attribute.primaryValues()) {
			AttributeValue toAdd = new AttributeValue(attr, 0);
			add(toAdd);
		}
		add(new AttributeValue(Attribute.SPLINTER, 0));
		for (Attribute attr : Attribute.secondaryValues()) {
			AttributeValue toAdd = new AttributeValue(attr, 0);
			secondary.put(attr, toAdd);
		}
	}

	//-------------------------------------------------------------------
	public List<AttributeValue> getAttributes() {
		java.util.Collections.sort(this);
		return this;
	}

	//-------------------------------------------------------------------
	public boolean add(AttributeValue pair) {
		if (pair.getAttribute().isPrimary()) {
			for (AttributeValue check : this) {
				if (check.getAttribute()==pair.getAttribute()) {
					check.setDistributed(pair.getDistributed());
					check.setStart(pair.getStart());
//					check.setGenerationModifier(pair.getGenerationModifier());
//					check.setUnmodifiedValue(pair.getUnmodifiedValue());
					return true;
				}
			}
			super.add(pair);			
		} else
			secondary.put(pair.getAttribute(), pair);
		
		return true;
	}

	//-------------------------------------------------------------------
	public void set(Attribute key, int value) {
		if (key.isPrimary()) {
			// Find matching attribute
			for (AttributeValue pair : this) {
				if (pair.getAttribute()==key) {
					pair.setDistributed(value);
					return;
				}
			}

			AttributeValue toAdd = new AttributeValue(key, value);
			super.add(toAdd);
		} else {
			AttributeValue pair = secondary.get(key);
			if (pair!=null) {
				pair.setDistributed(value);
				return;
			}
			AttributeValue toAdd = new AttributeValue(key, value);
			secondary.put(key, toAdd);
		}
	}

	//-------------------------------------------------------------------
	public int getValue(Attribute key) {
		return get(key).getValue();
	}

	//-------------------------------------------------------------------
	public AttributeValue get(Attribute key) {
		if (key==null) 
			throw new NullPointerException("Attribute may not be null");
		for (AttributeValue pair : this) {
			if (pair.getAttribute()==key) {				
				return pair;
			}
		}

		AttributeValue pair = secondary.get(key);
		if (pair!=null) {
			return pair;
		}
		logger.error("Something accessed an unset attribute: "+key+"\nprimary = "+this+"\nsecondary="+secondary);
		throw new NoSuchElementException(key.name());
	}

}
