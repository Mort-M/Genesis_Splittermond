/**
 *
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Random;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.persist.CultureConverter;

/**
 * @author Stefan
 *
 */
@Root(name="nametable")
public class SpliMoNameTable {

	private static PropertyResourceBundle RES = SplitterMondCore.getI18nResources();

	public enum OPTIONS {
		TOWN,
		RURAL
		;
		public String toString() {
			return RES.getString("nametable.option."+name().toLowerCase());
		}
	}

	@Attribute
	@AttribConvert(CultureConverter.class)
	private Culture culture;
	@ElementList(entry="entry",required=true,type=String.class)
	private List<String> femaleFirstNames;
	@ElementList(entry="entry",required=true,type=String.class)
	private List<String> maleFirstNames;
	@ElementList(entry="entry",required=true,type=String.class)
	private List<String> lastNames;
	@ElementList(entry="entry",type=String.class)
	private List<String> villageNames;



	//--------------------------------------------------------------------
	public SpliMoNameTable() {
		femaleFirstNames = new ArrayList<String>();
		maleFirstNames = new ArrayList<String>();
		lastNames = new ArrayList<String>();
		villageNames = new ArrayList<String>();
	}

	//--------------------------------------------------------------------
	/**
	 * @return the culture
	 */
	public Culture getCulture() {
		return culture;
	}

	//--------------------------------------------------------------------
	public String generateName(Gender gender, OPTIONS option) {
		Random random = new Random();
		List<String> firstNameList = (gender==Gender.FEMALE)?femaleFirstNames:maleFirstNames;
		switch (option) {
		case TOWN: // Town
			return firstNameList.get(random.nextInt(firstNameList.size()))+
					" "+lastNames.get(random.nextInt(lastNames.size()));
		case RURAL: // Rural
			return firstNameList.get(random.nextInt(firstNameList.size()))+
					" "+RES.getString("nametable.label.from")+" "+villageNames.get(random.nextInt(villageNames.size()));
		}

		return null;
	}

}
