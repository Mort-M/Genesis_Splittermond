/**
 * 
 */
package org.prelle.splimo.persist.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.Culture;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.CultureLoreModification;
import org.prelle.splimo.modifications.LanguageModification;

/**
 * @author prelle
 *
 */
public class CultureSerialization {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD
			+SEP+"<culture id=\"common\">"
			+SEP+"   <modifications>"
			+SEP+"      <cultureloremod ref=\"zwingard\"/>"
			+SEP+"      <languagemod ref=\"zwingardisch\"/>"
			+SEP+"   </modifications>"
			+SEP+"</culture>"+SEP;
	static Culture REAL = new Culture();
	
	private Serializer m;

	//-------------------------------------------------------------------
	static {
		
		REAL.setKey("common");
		REAL.addModification(new CultureLoreModification(SplitterMondCore.getCultureLore("zwingard")));
		REAL.addModification(new LanguageModification(SplitterMondCore.getLanguage("zwingardisch")));
	}
	
	//-------------------------------------------------------------------
	public CultureSerialization() throws Exception {
		m = new Persister();
	}

	//-------------------------------------------------------------------
	@Test
	public void serialize() {
		Culture data = REAL;
		
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
			Culture compare = REAL;
			Culture result = m.read(Culture.class, new StringReader(DATA));
			
			assertEquals(compare.getKey(), result.getKey());
//			assertEquals(3, result.getValue());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

}
