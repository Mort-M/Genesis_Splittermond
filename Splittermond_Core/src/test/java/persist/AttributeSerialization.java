/**
 * 
 */
package persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Attributes;

/**
 * @author prelle
 *
 */
public class AttributeSerialization {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD
			+SEP+"<attr id=\"AGILITY\" start=\"3\" value=\"3\"/>"+SEP;
	final static String DATA2 = HEAD
			+SEP+"<attributes>"
			+SEP+"   <attr id=\"CHARISMA\" value=\"0\"/>"
			+SEP+"   <attr id=\"AGILITY\" start=\"3\" value=\"3\"/>"
			+SEP+"   <attr id=\"INTUITION\" value=\"0\"/>"
			+SEP+"   <attr id=\"CONSTITUTION\" value=\"0\"/>"
			+SEP+"   <attr id=\"MYSTIC\" value=\"0\"/>"
			+SEP+"   <attr id=\"STRENGTH\" value=\"0\"/>"
			+SEP+"   <attr id=\"MIND\" value=\"0\"/>"
			+SEP+"   <attr id=\"WILLPOWER\" value=\"0\"/>"
			+SEP+"</attributes>"+SEP;
	
	private Serializer m;

	//-------------------------------------------------------------------
	static {
		Logger.getRootLogger().setLevel(Level.WARN);
//		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("log4j.properties"));
	}

	//-------------------------------------------------------------------
	public AttributeSerialization() throws Exception {
		m = new Persister();
	}

	//-------------------------------------------------------------------
	@Test
	public void serialize() {
		AttributeValue data = new AttributeValue(Attribute.AGILITY, 3);
		
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
			AttributeValue result = m.read(AttributeValue.class, new StringReader(DATA));
			
			assertEquals(Attribute.AGILITY, result.getAttribute());
			assertEquals(3, result.getValue());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	//-------------------------------------------------------------------
	@Test
	public void serializeList() {
		AttributeValue data2 = new AttributeValue(Attribute.AGILITY, 3);
		Attributes data = new Attributes();
		data.add(data2);
		
		try {
			StringWriter out = new StringWriter();
			m.write(data, out);
			
			assertEquals(DATA2, out.toString());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	//-------------------------------------------------------------------
	@Test
	public void deserializeList() {
		try {
			Attributes result = m.read(Attributes.class, new StringReader(DATA2));

			assertEquals(0, result.get(Attribute.CHARISMA).getValue());
			assertEquals(3, result.get(Attribute.AGILITY).getValue());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

}
