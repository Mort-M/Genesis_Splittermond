package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SplitterMondCore;

public class SpellConverter implements StringValueConverter<Spell> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Spell read(String v) throws Exception {
		Spell skill = SplitterMondCore.getSpell(v);
		if (skill==null) {
			System.err.println("No such spell: "+v);
			throw new IllegalArgumentException("No such spell: "+v);
		}
		return skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Spell v) throws Exception {
		return v.getId();
	}
	
}