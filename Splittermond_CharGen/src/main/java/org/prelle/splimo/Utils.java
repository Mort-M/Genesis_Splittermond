/**
 * 
 */
package org.prelle.splimo;

import de.rpgframework.LicenseManager;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class Utils {

	
	//-------------------------------------------------------------------
	public static boolean supports(Feature feature) {
		return true;
//		return LicenseManager.hasLicense(RoleplayingSystem.SPLITTERMOND, feature.name());
	}
	
}
