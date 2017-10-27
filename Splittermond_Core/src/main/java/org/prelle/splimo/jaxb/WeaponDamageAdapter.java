package org.prelle.splimo.jaxb;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class WeaponDamageAdapter extends XmlAdapter<String, Integer> {
	@Override
	public Integer unmarshal(String v) throws Exception {
		int sum = 0;
		StringTokenizer tok = new StringTokenizer(v,"wWdD+");
		int diceNum = Integer.parseInt(tok.nextToken());
		if (v.contains("-")) {
			int diceType= Integer.parseInt(tok.nextToken("wWdD-"));
			sum = diceNum*10000 + diceType*100;
			sum += 100-Integer.parseInt(tok.nextToken());
		} else {
			int diceType= Integer.parseInt(tok.nextToken());
			sum = diceNum*10000 + diceType*100;
			if (tok.hasMoreTokens())
				sum += Integer.parseInt(tok.nextToken());
		}
		return sum;
	}

	@Override
	public String marshal(Integer v) throws Exception {
		if (v==null)
			return null;
		StringBuffer buf = new StringBuffer();
		buf.append(String.valueOf(v/10000));
		buf.append("W");
		buf.append(String.valueOf((v%10000)/100));
		if ((v%100)>90)
			buf.append("-"+(100 - v%100));
		else if ((v%100)>0)
			buf.append("+"+(v%100));
		return buf.toString();
	}
}