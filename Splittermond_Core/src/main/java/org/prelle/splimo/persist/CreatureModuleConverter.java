package org.prelle.splimo.persist;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.simplepersist.XMLElementConverter;
import org.prelle.simplepersist.marshaller.XmlNode;
import org.prelle.simplepersist.unmarshal.XMLTreeItem;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class CreatureModuleConverter implements StringValueConverter<CreatureModule>, XMLElementConverter<CreatureModule> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public CreatureModule read(String v) throws Exception {
		CreatureModule data = SplitterMondCore.getCreatureModule(v);
		if (data==null) {
			System.err.println("No such creature module: "+v);
			throw new ReferenceException(ReferenceType.CREATURE_MODULE, v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(CreatureModule v) throws Exception {
		return v.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#write(org.prelle.simplepersist.marshaller.XmlNode, java.lang.Object)
	 */
	@Override
	public void write(XmlNode node, CreatureModule value) throws Exception {
		node.setAttribute("ref", write(value));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#read(org.prelle.simplepersist.unmarshal.XMLTreeItem, javax.xml.stream.events.StartElement, javax.xml.stream.XMLEventReader)
	 */
	@Override
	public CreatureModule read(XMLTreeItem node, StartElement ev, XMLEventReader evRd) throws Exception {
		return read(ev.getAttributeByName(new QName("ref")).getValue());
	}
	
}