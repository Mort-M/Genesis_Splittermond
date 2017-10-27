package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.Spell;
import org.prelle.splimo.SplitterMondCore;

public class SpellAdapter extends XmlAdapter<String, Spell> {
		@Override
	public Spell unmarshal(String v) throws Exception {
			Spell skill = SplitterMondCore.getSpell(v);
			if (skill==null) {
				System.err.println("No such spell: "+v);
				throw new IllegalArgumentException("No such spell: "+v);
			}
		return skill;
	}

	@Override
	public String marshal(Spell v) throws Exception {
		if (v==null)
			return null;
		return v.getId();
	}
}