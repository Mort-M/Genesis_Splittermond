package org.prelle.splimo.persist;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.simplepersist.XMLElementConverter;
import org.prelle.simplepersist.marshaller.XmlNode;
import org.prelle.simplepersist.unmarshal.XMLTreeItem;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Personalization;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class PersonalizationConverter implements StringValueConverter<Personalization>, XMLElementConverter<Personalization> {
	
	private final static Logger logger = Logger.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Personalization read(String v) throws Exception {
		Personalization data = SplitterMondCore.getPersonalization(v);
		if (data==null) {
			logger.error("No such Personalization: "+v);
			throw new ReferenceException(ReferenceType.PERSONALIZATION, v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Personalization v) throws Exception {
		return v.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#write(org.prelle.simplepersist.marshaller.XmlNode, java.lang.Object)
	 */
	@Override
	public void write(XmlNode node, Personalization value) throws Exception {
		node.setAttribute("ref", write(value));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#read(org.prelle.simplepersist.unmarshal.XMLTreeItem, javax.xml.stream.events.StartElement, javax.xml.stream.XMLEventReader)
	 */
	@Override
	public Personalization read(XMLTreeItem node, StartElement ev, XMLEventReader evRd) throws Exception {
		return read(ev.getAttributeByName(new QName("ref")).getValue());
	}
	
}