package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Deity;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class DeityConverter implements StringValueConverter<Deity> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Deity read(String v) throws Exception {
		Deity skill = SplitterMondCore.getDeity(v);
		if (skill==null) {
			System.err.println("No such deity : "+v);
			throw new ReferenceException(ReferenceType.DEITY_TYPE, v);
		}
		return skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Deity v) throws Exception {
		return v.getId();
	}
	
}