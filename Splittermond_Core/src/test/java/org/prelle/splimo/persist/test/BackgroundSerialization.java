/**
 * 
 */
package org.prelle.splimo.persist.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.Background;
import org.prelle.splimo.BackgroundList;

/**
 * @author prelle
 *
 */
public class BackgroundSerialization {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD
			+SEP+"<background id=\"artists\"/>"+SEP;
	final static String DATA2 = HEAD
			+SEP+"<backgrounds>"
			+SEP+"   <background id=\"artists\"/>"
			+SEP+"</backgrounds>"+SEP;
	final static Background WITHOUT = new Background("artists");
	
	private Serializer m;

	//-------------------------------------------------------------------
	static {
	}
	
	//-------------------------------------------------------------------
	public BackgroundSerialization() throws Exception {
		m = new Persister();
	}

	//-------------------------------------------------------------------
	@Test
	public void serialize() {
		Background data = WITHOUT;
		
		try {
			StringWriter out = new StringWriter();
			m.write(data, out);
	
			assertEquals(DATA, out.toString());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	//-------------------------------------------------------------------
	@Test
	public void deserialize() {
		try {
			Background compare = WITHOUT;
			Background result = m.read(Background.class, new StringReader(DATA));
			
			assertEquals(compare, result);
//			assertEquals(3, result.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	//-------------------------------------------------------------------
	@Test
	public void serializeList() {
		Background data2 = WITHOUT;
		BackgroundList data = new BackgroundList();
		data.add(data2);
		
		try {
			StringWriter out = new StringWriter();
			m.write(data, out);

			System.out.println(out.toString());
			assertEquals(DATA2, out.toString());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	//-------------------------------------------------------------------
	@Test
	public void deserializeList() {
		try {
			BackgroundList result = m.read(BackgroundList.class, new StringReader(DATA2));

			assertTrue(result.contains(WITHOUT));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
