/**
 *
 */
package org.prelle.splimo.gamemaster;

import java.util.Arrays;
import java.util.List;

import org.prelle.simplepersist.Root;
import org.prelle.splimo.Culture;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.SpliMoNameTable;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SpliMoNameTable.OPTIONS;

import de.rpgframework.core.AppliedFilter;
import de.rpgframework.core.Filter;
import de.rpgframework.worldinfo.Generator;
import de.rpgframework.worldinfo.WorldInformationType;

/**
 * @author Stefan
 *
 */
@Root(name="nametable")
public class SpliMoNameGenerator implements Generator<String> {

	private GenderFilter filterGender;
	private NameTableCultureFilter filterCulture;
	private TownOrRuralFilter filterTownOrRural;

	//--------------------------------------------------------------------
	public SpliMoNameGenerator() {
		filterGender = new GenderFilter();
		filterTownOrRural = new TownOrRuralFilter();
		filterCulture = new NameTableCultureFilter();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.worldinfo.Generator#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return SplitterMondCore.getI18nResources().getString("generator.names");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.worldinfo.Generator#getType()
	 */
	@Override
	public WorldInformationType getType() {
		return WorldInformationType.NAMES;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.worldinfo.Generator#getSupportedFilter()
	 */
	@Override
	public List<Filter> getSupportedFilter() {
		return Arrays.asList(filterGender, filterCulture, filterTownOrRural);
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.worldinfo.Generator#willWork(de.rpgframework.core.AppliedFilter[])
	 */
	@Override
	public boolean willWork(AppliedFilter... choices) {
//		for (AppliedFilter choice : choices) {
//			if (choice.getFilter()==filterCulture)
//				return true;
//		}
//
//		return false;

		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.worldinfo.Generator#generate(de.rpgframework.core.AppliedFilter[])
	 */
	@Override
	public String generate(AppliedFilter... choices) {
		Gender gender = Gender.MALE;
		SpliMoNameTable.OPTIONS option = OPTIONS.TOWN;
		SpliMoNameTable table = null;

		for (AppliedFilter choice : choices) {
			if (choice.getFilter()==filterGender)
				gender = (Gender) choice.getValue();
			else if (choice.getFilter()==filterTownOrRural)
				option = (OPTIONS) choice.getValue();
			else if (choice.getFilter()==filterCulture)
				table = SplitterMondCore.getNameTable( (Culture) choice.getValue() );
		}

		if (table==null)
			return "No nametable for this culture";

		return table.generateName(gender, option);
	}

}
