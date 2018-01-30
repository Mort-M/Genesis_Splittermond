/**
 *
 */
package foo;

import org.prelle.rpgframework.splittermond.merchant.Shop;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Availability;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Complexity;
import org.prelle.splimo.items.ItemType;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
public class TestMerchant {

	//-------------------------------------------------------------------
	public static void main(String[] args) {
		RPGFrameworkLoader.getInstance();
//		SplitterMondCore.initialize(new DummyRulePlugin<>());

		Shop shop = new Shop(Availability.SMALL_TOWN, Complexity.MASTER, ItemType.ARMOR);
		for (CarriedItem item : shop.generateItems()) {
			System.out.println("Sells "+item.getName()+"    \t"+item);
		}
	}

}
