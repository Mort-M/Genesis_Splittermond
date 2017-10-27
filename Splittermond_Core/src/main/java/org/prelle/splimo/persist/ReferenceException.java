/**
 * 
 */
package org.prelle.splimo.persist;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
public class ReferenceException extends RuntimeException {
	
	public enum ReferenceType {
		ASPECT,
		CREATURE,
		CREATURE_FEATURE_TYPE,
		CREATURE_MODULE,
		CREATURE_TYPE,
		CULTURE,
		CULTURELORE,
		DEITY,
		DEITY_TYPE,
		EDUCATION,
		ENHANCEMENT,
		FEATURE_TYPE,
		ITEM,
		MASTERSHIP,
		MATERIAL,
		PERSONALIZATION,
		POWER,
		RESOURCE,
		SKILL_SPECIAL,
		SKILL,
		SPELL,
	}
	
	private ReferenceType type;
	private String reference;

	//--------------------------------------------------------------------
	public ReferenceException(ReferenceType type, String ref) {
		super("Invalid reference to "+type+" '"+ref+"'");
		this.type = type;
		this.reference = ref;
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

}
