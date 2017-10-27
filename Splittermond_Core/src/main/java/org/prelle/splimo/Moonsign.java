/**
 * 
 */
package org.prelle.splimo;

/**
 * @author prelle
 *
 */
public enum Moonsign {

	OMEN,
	SIGHT,
	TRABANT,
	GAMBLER,
	RICHMAN,
	MOONPOWER,
	FLASH,
	BLOODMOON,
	GHOSTTHOUGHT,
	ROCK
	;

    //-------------------------------------------------------------------
    public String getName() {
        return SplitterMondCore.getI18nResources().getString("moonsplinter."+this.name().toLowerCase());
    }

	//-------------------------------------------------------------------
	public String getShortName() {
		return SplitterMondCore.getI18nResources().getString("moonsplinter."+this.name().toLowerCase()+".short");
	}

	//-------------------------------------------------------------------
	public String getLevelText(int level){
		switch (level){
			case 1:
				return SplitterMondCore.getI18nResources().getString("moonsplinter." + this.name().toLowerCase() + ".level1");
			case 2:
				return SplitterMondCore.getI18nResources().getString("moonsplinter." + this.name().toLowerCase() + ".level2");
			case 4:
				return SplitterMondCore.getI18nResources().getString("moonsplinter." + this.name().toLowerCase() + ".level4");
			default:
				return " ";
		}
	}

    public String toString() {
    	return getName();
    }

}
