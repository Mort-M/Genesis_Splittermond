/**
 * 
 */
package org.prelle.splimo;

import java.util.UUID;

import org.prelle.simplepersist.Attribute;

/**
 * @author prelle
 *
 */
public abstract class UniqueObject {

	@Attribute(name="uniqueid")
	private UUID uniqueId;
	
	//-------------------------------------------------------------------
	public UniqueObject() {
		uniqueId = UUID.randomUUID();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the uniqueId
	 */
	public UUID getUniqueId() {
		return uniqueId;
	}

	//-------------------------------------------------------------------
	/**
	 * @param uniqueId the uniqueId to set
	 */
	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}
	
}
