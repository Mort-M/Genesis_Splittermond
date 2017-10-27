/**
 * 
 */
package org.prelle.splimo.persist;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.simplepersist.XMLElementConverter;
import org.prelle.simplepersist.marshaller.XmlNode;
import org.prelle.simplepersist.unmarshal.XMLTreeItem;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

/**
 * @author prelle
 *
 */
public class SkillConverter implements StringValueConverter<Skill>, XMLElementConverter<Skill> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Skill value) throws Exception {
		return value.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Skill read(String idref) throws Exception {
		Skill skill = SplitterMondCore.getSkill(idref);
		if (skill==null)
			throw new ReferenceException(ReferenceType.SKILL, idref);

		return skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#write(org.prelle.simplepersist.marshaller.XmlNode, java.lang.Object)
	 */
	@Override
	public void write(XmlNode node, Skill value) throws Exception {
		node.setAttribute("ref", write(value));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.XMLElementConverter#read(org.prelle.simplepersist.unmarshal.XMLTreeItem, javax.xml.stream.events.StartElement, javax.xml.stream.XMLEventReader)
	 */
	@Override
	public Skill read(XMLTreeItem node, StartElement ev, XMLEventReader evRd) throws Exception {
		return read(ev.getAttributeByName(new QName("ref")).getValue());
	}

}
