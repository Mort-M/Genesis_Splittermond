package org.prelle.splimo.persist;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Material;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class MaterialConverter implements StringValueConverter<Material> {
	
	private final static Logger logger = Logger.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Material read(String v) throws Exception {
		Material data = SplitterMondCore.getMaterial(v);
		if (data==null) {
			logger.error("No such material: "+v);
			throw new ReferenceException(ReferenceType.MATERIAL,v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Material v) throws Exception {
		return v.getId();
	}
	
}