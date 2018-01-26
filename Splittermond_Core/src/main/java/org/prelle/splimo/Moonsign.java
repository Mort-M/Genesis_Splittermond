/**
 * 
 */
package org.prelle.splimo;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.core.RoleplayingSystem;

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

	//-------------------------------------------------------------------
	private String getPage(){
		return SplitterMondCore.getI18nResources().getString("moonsplinter."+this.name().toLowerCase()+".page");
	}

	// full description for Selection during Character Generation
	public String getDescription() {
		String description = this.getName() + "\n" + this.getPage();
		boolean hasLicense= RPGFrameworkLoader.getInstance().getLicenseManager().hasLicense(RoleplayingSystem.SPLITTERMOND, "CORE_ALL");
		if (hasLicense) {
			description = description + "\n\n" +
					"Grad 1: " + this.getLevelText(1)+ "\n"+
					"Grad 2: " + this.getLevelText(2)+ "\n"+
					"Grad 4: " + this.getLevelText(4)+ "\n";
		}
		return description;

	}

    public String toString() {
    	return getName();
    }

}
