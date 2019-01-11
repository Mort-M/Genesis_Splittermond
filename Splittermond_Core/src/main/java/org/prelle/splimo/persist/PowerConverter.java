package org.prelle.splimo.persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Power;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class PowerConverter implements StringValueConverter<Power> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.AttributeConverter#read(java.lang.String)
	 */
	@Override
	public Power read(String v) throws Exception {
		Power data = SplitterMondCore.getPower(v);
		if (data==null) {
			System.err.println("No such power: "+v);
			logger.error("No such power: "+v);
			throw new ReferenceException(ReferenceType.POWER, v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.AttributeConverter#write(java.lang.Object)
	 */
	@Override
	public String write(Power v) throws Exception {
		return v.getId();
	}
}