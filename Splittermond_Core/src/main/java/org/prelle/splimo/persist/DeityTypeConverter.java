package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.DeityType;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class DeityTypeConverter implements StringValueConverter<DeityType> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public DeityType read(String v) throws Exception {
		DeityType skill = SplitterMondCore.getDeityType(v);
		if (skill==null) {
			System.err.println("No such deity type: "+v);
			throw new ReferenceException(ReferenceType.DEITY_TYPE, v);
		}
		return skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(DeityType v) throws Exception {
		return v.getId();
	}
	
}