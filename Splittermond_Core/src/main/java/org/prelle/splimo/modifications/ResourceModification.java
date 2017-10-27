/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Resource;
import org.prelle.splimo.persist.ResourceConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "resourcemod")
public class ResourceModification extends ModificationImpl {
	
	@Attribute(required=false)
	private ModificationValueType type;
	@Attribute
	@AttribConvert(ResourceConverter.class)
	private Resource ref;
	@Attribute(required=false)
	private int value;
	@Element(required=false)
	private String comment;

	//-------------------------------------------------------------------
	public ResourceModification() {
		value = 1;
	}

	//-------------------------------------------------------------------
	public ResourceModification(Resource race, int val) {
		this.ref = race;
		this.value = val;
	}

	//-------------------------------------------------------------------
	public ResourceModification(ModificationValueType type, Resource race, int val) {
		this.type = type;
		this.ref = race;
		this.value = val;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.modifications.ModificationImpl#clone()
	 */
	@Override
	public ResourceModification clone() {
		ResourceModification ret = new ResourceModification(ref, value);
    	ret.cloneAdd(this);
    	return ret;
	}

	//-------------------------------------------------------------------
	public String toString() {
		if (ref!=null)
			return getResourceName()+" +"+value;
		return String.valueOf(ref)+" "+value;
	}

	//-------------------------------------------------------------------
	public String getResourceName() {
		return ref.getName();
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		if (other instanceof ResourceModification)
			return ref.compareTo(((ResourceModification)other).getResource());
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Resource getResource() {
		return ref;
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

	//-------------------------------------------------------------------
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	//-------------------------------------------------------------------
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

}
