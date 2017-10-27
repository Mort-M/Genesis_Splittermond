/**
 * 
 */
package org.prelle.splimo.chargen.event;

/**
 * @author prelle
 *
 */
public class GenerationEvent {
	
	private GenerationEventType type;
	private Object key;
	private Object value;

	//-------------------------------------------------------------------
	public GenerationEvent(GenerationEventType type, Object key) {
		this.type = type;
		this.key  = key;
	}

	//-------------------------------------------------------------------
	public GenerationEvent(GenerationEventType type, Object key, Object val) {
		this.type = type;
		this.key  = key;
		this.value= val;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return type+"(key="+key+", value="+value+")";
	}
	
	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public GenerationEventType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

}
