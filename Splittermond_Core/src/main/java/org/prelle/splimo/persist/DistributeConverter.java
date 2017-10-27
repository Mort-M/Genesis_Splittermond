package org.prelle.splimo.persist;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;

public class DistributeConverter implements StringValueConverter<Integer[]> {
	
	private final static Logger logger = Logger.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Integer[] v) throws Exception {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<v.length; i++) {
			buf.append(v[i]+"");
			if ((i+1)<v.length)
				buf.append(" ");
		}
		return buf.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Integer[] read(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v);
		Integer[] ret = new Integer[tok.countTokens()];
		int pos=0;
		try {
			while (tok.hasMoreTokens()) {
				ret[pos++] = Integer.parseInt(tok.nextToken());
			}
		} catch (NumberFormatException e) {
			logger.error("Non-integer values in distmod: "+v);
		}
		return ret;
	}
}