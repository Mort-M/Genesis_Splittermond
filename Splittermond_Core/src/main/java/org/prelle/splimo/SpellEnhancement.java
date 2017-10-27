package org.prelle.splimo;

public class SpellEnhancement {

	private int grades;
	private SpellCost cost;

	//-------------------------------------------------------------------
	public SpellEnhancement(int gr, SpellCost cost) {
		grades    = gr;
		this.cost = cost;
	}
	//-------------------------------------------------------------------
	public int getSuccessGrades()  { return grades; }
	public SpellCost getExtraCost()   { return cost; }

	//-------------------------------------------------------------------
	public String toString() {
		String eg   = SplitterMondCore.getI18nResources().getString("spell.success.short");

		StringBuffer buf = new StringBuffer(grades+" "+eg+"/+");
		buf.append(SplitterTools.getFocusString(cost));

		return buf.toString();

	}

}