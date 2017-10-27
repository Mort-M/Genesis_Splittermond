/**
 * 
 */
package org.prelle.splimo.jaxb;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
public class ReferenceException extends RuntimeException {
	
	public enum ReferenceType {
		ITEM,
		MASTERSHIP,
		SKILL_SPECIAL,
		POWER,
		SKILL,
		SPELL,
		SPELLTYPE_SPECIAL,
	}
	
	private ReferenceType type;
	private String reference;
	private Object context;

	//--------------------------------------------------------------------
	public ReferenceException(ReferenceType type, String ref) {
		super("Invalid reference to "+type+" '"+ref+"'");
		this.type = type;
		this.reference = ref;
	}

	//--------------------------------------------------------------------
	public ReferenceException(ReferenceType type, String ref, Object context) {
		super("Invalid reference to "+type+" '"+ref+"' in context "+context);
		this.type = type;
		this.reference = ref;
		this.context = context;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public ReferenceType getType() {
		return type;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the context
	 */
	public Object getContext() {
		return context;
	}

}
