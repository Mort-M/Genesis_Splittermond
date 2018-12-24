/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.persist.ResourceConverter;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modifyable;
import de.rpgframework.genericrpg.modification.ModifyableImpl;

/**
 * @author prelle
 *
 */
public class ResourceReference extends ModifyableImpl implements Comparable<ResourceReference> {

	
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
		modifications = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public ResourceReference(Resource resource, int value) {
		this();
		if (resource==null)
			throw new NullPointerException("Resource may not be null");
		this.resource = resource;
		this.value = value;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.valueOf(resource)+" "+getModifiedValue();
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
	 * @return the value
	 */
	public int getModifiedValue() {
		int sum = value;
		for (Modification mod : modifications) {
			if (mod instanceof ResourceModification && ((ResourceModification)mod).getResource()==resource)
				sum+= ((ResourceModification)mod).getValue();
		}
		return sum;
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
