package org.prelle.splimo.persist;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.simplepersist.XMLElementConverter;
import org.prelle.simplepersist.marshaller.XmlNode;
import org.prelle.simplepersist.unmarshal.XMLTreeItem;
import org.prelle.splimo.Aspect;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class AspectConverter implements StringValueConverter<Aspect>, XMLElementConverter<Aspect> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Aspect read(String v) throws Exception {
		Aspect data = SplitterMondCore.getAspect(v);
		if (data==null) {
			System.err.println("No such aspect: "+v);
			throw new ReferenceException(ReferenceType.ASPECT,v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Aspect v) throws Exception {
		return v.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#write(org.prelle.simplepersist.marshaller.XmlNode, java.lang.Object)
	 */
	@Override
	public void write(XmlNode node, Aspect value) throws Exception {
		node.setAttribute("ref", write(value));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#read(org.prelle.simplepersist.unmarshal.XMLTreeItem, javax.xml.stream.events.StartElement, javax.xml.stream.XMLEventReader)
	 */
	@Override
	public Aspect read(XMLTreeItem node, StartElement ev, XMLEventReader evRd) throws Exception {
		return read(ev.getAttributeByName(new QName("ref")).getValue());
	}
	
}