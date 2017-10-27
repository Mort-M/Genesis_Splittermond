/**
 * 
 */
package org.prelle.splimo;

import java.util.UUID;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.persist.ResourceConverter;

/**
 * @author prelle
 *
 */
public class ResourceReference implements Comparable<ResourceReference> {

	
	@Attribute(name="ref")
	@AttribConvert(ResourceConverter.class)
	private Resource resource;
	@Attribute(name="val")
	private int value;
	@Attribute(required=false)
	private String description;
	/**
	 * References some other object in the character. Format is either
	 * "creature:<uniqueid>" or "item:<uniqueid>" 
	 */
	@Attribute(required=false)
	private UUID idRef;

	//-------------------------------------------------------------------
	public ResourceReference() {
	}

	//-------------------------------------------------------------------
	public ResourceReference(Resource resource, int value) {
		if (resource==null)
			throw new NullPointerException("Resource may not be null");
		this.resource = resource;
		this.value = value;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.valueOf(resource)+" "+value;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Resource getResource() {
		return resource;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	//-------------------------------------------------------------------
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ResourceReference other) {
		return resource.compareTo(other.getResource());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the title
	 */
	public String getDescription() {
		return description;
	}

	//-------------------------------------------------------------------
	/**
	 * @param title the title to set
	 */
	public void setDescription(String title) {
		this.description = title;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the idref
	 */
	public UUID getIdReference() {
		return idRef;
	}

	//-------------------------------------------------------------------
	/**
	 * @param idref the idref to set
	 */
	public void setIdReference(UUID idref) {
		this.idRef = idref;
	}

}
