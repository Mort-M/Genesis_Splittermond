package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.Power;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.jaxb.ReferenceException.ReferenceType;

public class PowerAdapter extends XmlAdapter<String, Power> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");

	@Override
	public Power unmarshal(String v) throws Exception {
		Power data = SplitterMondCore.getPower(v);
		if (data==null) {
			logger.debug("No such power: "+v);
			throw new ReferenceException(ReferenceType.POWER, v);
		}
		return data;
	}

	@Override
	public String marshal(Power v) throws Exception {
		return v.getId();
	}
}