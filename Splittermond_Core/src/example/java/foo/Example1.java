/**
 * 
 */
package foo;

import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
public class Example1 {

	//-------------------------------------------------------------------
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SplitterMondCore.initialize(new SplittermondRules());
		
		SpliMoCharacter charac = new SpliMoCharacter();
		charac.setName("Tiai Schimmersee");
		charac.setRace("wird-noch-ersetzt");
		charac.setAttribute(Attribute.CHARISMA, 3);
	}

}
