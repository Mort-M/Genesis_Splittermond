package org.prelle.splimo.persist;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class CreatureConverter implements StringValueConverter<Creature> {
	
	private final static Logger logger = Logger.getLogger("splimo.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Creature read(String v) throws Exception {
		Creature item = SplitterMondCore.getCreature(v);
		if (item==null) {
			logger.error("Unknown item reference: '"+v+"'");
			throw new ReferenceException(ReferenceType.CREATURE, v);
		}
		return item;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Creature v) throws Exception {
		return v.getId();
	}
	
}