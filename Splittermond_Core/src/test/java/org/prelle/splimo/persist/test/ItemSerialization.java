/**
 * 
 */
package org.prelle.splimo.persist.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.Personalization;
import org.prelle.splimo.items.PersonalizationReference;
import org.prelle.splimo.modifications.AttributeChangeModification;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
public class ItemSerialization {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD
			+SEP+"<itemref count=\"1\" customName=\"Stich\" location=\"SOMEWHEREELSE\" ref=\"dagger\" uniqueid=\"8f9be81b-a1c0-4caa-9cd3-488cd74788e4\">"
			+SEP+"   <enhancements>"
			+SEP+"      <enhancement ref=\"load\"/>"
			+SEP+"      <enhancement ref=\"speed\"/>"
			+SEP+"   </enhancements>"
			+SEP+"   <personalizations>"
			+SEP+"      <personalization ref=\"foo\">"
			+SEP+"         <modifications>"
			+SEP+"            <attrchgmod from=\"AGILITY\" to=\"CONSTITUTION\"/>"
			+SEP+"         </modifications>"
			+SEP+"      </personalization>"
			+SEP+"   </personalizations>"
			+SEP+"</itemref>"+SEP;
	final static String OLDDATA = HEAD
			+SEP+"<itemref location=\"SOMEWHEREELSE\" ref=\"dagger\" uniqueid=\"8f9be81b-a1c0-4caa-9cd3-488cd74788e4\">"
			+SEP+"   <modifications>"
			+SEP+"      <attrmod type=\"rel\" attr=\"MINDRESIST\" val=\"1\" cond=\"false\"/>"
			+SEP+"   </modifications>"
			+SEP+"</itemref>"+SEP;
	static CarriedItem REAL = new CarriedItem();
	static CarriedItem OLD  = new CarriedItem();
	
	private Serializer m;

	//-------------------------------------------------------------------
	static {
		Personalization perso = new Personalization("foo", ItemType.WEAPON);
		PersonalizationReference persoRef = new PersonalizationReference(perso);
		persoRef.addModification(new AttributeChangeModification(Attribute.AGILITY, Attribute.CONSTITUTION));
		
//		SplitterMondCore.initialize(new DummyRulePlugin<>());
		RPGFrameworkLoader.getInstance();
		SplitterMondCore.addPersonalizations(perso);
		ItemTemplate template = SplitterMondCore.getItem("dagger");
		
		REAL.setItem(template);
		REAL.setUniqueId(UUID.fromString("8f9be81b-a1c0-4caa-9cd3-488cd74788e4"));
		REAL.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("load")));
		REAL.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("speed")));
		REAL.setCustomName("Stich");
		
		
		REAL.addPersonalization(persoRef);
		
		OLD.setItem(template);
//		OLD.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("mindresist")));
	}
	
	//-------------------------------------------------------------------
	public ItemSerialization() throws Exception {
		m = new Persister();
	}

	//-------------------------------------------------------------------
	@Test
	public void serialize() {
		CarriedItem data = REAL;
//		Logger.getLogger("xml").setLevel(Level.DEBUG);
		
		try {
			StringWriter out = new StringWriter();
			m.write(data, out);
			System.out.println(out.toString());
	
			assertEquals(DATA, out.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	//-------------------------------------------------------------------
	@Test
	public void deserialize() {
		try {
			CarriedItem compare = REAL;
			CarriedItem result = m.read(CarriedItem.class, new StringReader(DATA));
			EquipmentTools.determineEnhancementReferenceModifications(result);
			
			assertEquals(compare, result);
		} catch (Exception e) {
			fail(e.toString());
		}
	}

}
