package org.prelle.splimo.jaxb;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DistributeAdapter extends XmlAdapter<String, Integer[]> {
	@Override
	public Integer[] unmarshal(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v);
		Integer[] ret = new Integer[tok.countTokens()];
		int pos=0;
		try {
			while (tok.hasMoreTokens()) {
				ret[pos++] = Integer.parseInt(tok.nextToken());
			}
		} catch (NumberFormatException e) {
			System.err.println("Non-integer values in distmod: "+v);
		}
		return ret;
	}

	@Override
	public String marshal(Integer[] v) throws Exception {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<v.length; i++) {
			buf.append(v[i]+"");
			if ((i+1)<v.length)
				buf.append(" ");
		}
		return buf.toString();
	}
}