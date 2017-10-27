package org.prelle.splimo.persist;

import java.util.StringTokenizer;

import org.prelle.simplepersist.StringValueConverter;

public class WeaponDamageConverter implements StringValueConverter<Integer> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public Integer read(String v) throws Exception {
		v = v.trim();
		int sum = 0;
		StringTokenizer tok = new StringTokenizer(v,"wWdD+");
		int diceNum = Integer.parseInt(tok.nextToken());
		if (!tok.hasMoreTokens())
			return diceNum;
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(Integer v) {
		if (v==null)
			return null;
		StringBuffer buf = new StringBuffer();
		if (v>10000) {
			buf.append(String.valueOf(v/10000));
			buf.append("W");
			buf.append(String.valueOf((v%10000)/100));
		}
		if ((v%100)>90)
			buf.append("-"+(100 - v%100));
		else if ((v%100)>0)
			buf.append( ((buf.length()>0)?"+":"")+(v%100));
		return buf.toString();
	}
	
}