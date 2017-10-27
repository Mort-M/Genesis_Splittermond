/**
 * 
 */
package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Education;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

/**
 * @author prelle
 *
 */
public class EducationConverter implements StringValueConverter<Education> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Education value) throws Exception {
		return value.getKey();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Education read(String idref) throws Exception {
		Education skill = SplitterMondCore.getEducation(idref);
		if (skill==null)
			throw new ReferenceException(ReferenceType.EDUCATION, idref);

		return skill;
	}

}
