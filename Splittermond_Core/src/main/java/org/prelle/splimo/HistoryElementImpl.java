/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.rpgframework.genericrpg.HistoryElement;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.products.Adventure;

/**
 * @author prelle
 *
 */
public class HistoryElementImpl implements HistoryElement {
	
	/** Optional */
	private Adventure adventure;
	private String name;
	private List<Reward> gained;
	private List<Modification> spent;

	//-------------------------------------------------------------------
	public HistoryElementImpl() {
		gained = new ArrayList<Reward>();
		spent   = new ArrayList<Modification>();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return name+"(GAIN="+gained+", spent="+spent+")";
	}

	//-------------------------------------------------------------------
	public void addGained(Reward mod) {
		gained.add(mod);
		Collections.sort(gained, new Comparator<Reward>() {
			public int compare(Reward o1, Reward o2) {
				if (o1.getDate()!=null && o2.getDate()!=null)
					return o1.getDate().compareTo(o2.getDate());
				return 0;
			}
		});
	}

	//-------------------------------------------------------------------
	public void addSpent(Modification mod) {
		spent.add(mod);
		Collections.sort(spent, new Comparator<Modification>() {
			public int compare(Modification o1, Modification o2) {
				if (o1.getDate()!=null && o2.getDate()!=null)
					return o1.getDate().compareTo(o2.getDate());
				return 0;
			}
		});
	}

		//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.HistoryElement#getStart()
	 */
	@Override
	public Date getStart() {
		if (gained.isEmpty())
			return spent.get(0).getDate();
		return gained.get(0).getDate();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.HistoryElement#getEnd()
	 */
	@Override
	public Date getEnd() {
		if (gained.isEmpty())
			return spent.get(spent.size()-1).getDate();
		return gained.get(gained.size()-1).getDate();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.HistoryElement#getAdventure()
	 */
	@Override
	public Adventure getAdventure() {
		return adventure;
	}

	//-------------------------------------------------------------------
	/**
	 * @param adventure the adventure to set
	 */
	public void setAdventure(Adventure adventure) {
		this.adventure = adventure;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.HistoryElement#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	//-------------------------------------------------------------------
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.HistoryElement#getGained()
	 */
	@Override
	public List<Reward> getGained() {
		return gained;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.HistoryElement#getSpent()
	 */
	@Override
	public List<Modification> getSpent() {
		return spent;
	}

}
