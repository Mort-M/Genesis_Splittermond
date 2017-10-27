/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "promos")
@ElementList(entry="promo",type=PromoData.class,inline=true)
public class PromoDataList extends ArrayList<PromoData> {

	private static final long serialVersionUID = 1L;

	@org.prelle.simplepersist.Attribute
	private String secret;
	
	//-------------------------------------------------------------------
	public PromoDataList() {
	}

	//-------------------------------------------------------------------
	public PromoDataList(Collection<? extends PromoData> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	//-------------------------------------------------------------------
	/**
	 * @param secret the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

}
