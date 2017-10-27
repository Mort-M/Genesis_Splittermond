/**
 * 
 */
package org.prelle.splimo.requirements;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Resource;
import org.prelle.splimo.persist.ResourceConverter;

/**
 * @author prelle
 *
 */
@Root(name = "resourcereq")
public class ResourceRequirement extends Requirement {

	@Attribute(required=true)
	@AttribConvert(ResourceConverter.class)
	private Resource ref;
	@Attribute
	private int value;

	//-------------------------------------------------------------------
	public ResourceRequirement() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public ResourceRequirement(Resource data, int value) {
		this.ref = data;
		this.value = value;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ref+"+"+value;
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

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
		return true;
	}

}
