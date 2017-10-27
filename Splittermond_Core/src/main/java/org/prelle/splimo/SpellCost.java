package org.prelle.splimo;

public class SpellCost {
	
	private int channelled;
	private int exhausted;
	private int consumed;
	
	//-------------------------------------------------------------------
	public SpellCost(int ch, int ex, int co) {
		channelled = ch;
		exhausted  = ex;
		consumed   = co;
	}
	//-------------------------------------------------------------------
	public int getChannelled() { return channelled; }
	public int getExhausted()  { return exhausted; }
	public int getConsumed()   { return consumed; }
	
}