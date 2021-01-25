package de.rpgframework.splittermond.export.foundry;

import org.prelle.splimo.Attribute;

/**
 * @author prelle
 *
 */
public class Util {

	//-------------------------------------------------------------------
	public static String translateAttribute(Attribute attr) {
		switch (attr) {
		case STRENGTH: return "sta";
		case INTUITION: return "inn";
		default:
			return attr.getShortName().toLowerCase();
		}
	}

}
