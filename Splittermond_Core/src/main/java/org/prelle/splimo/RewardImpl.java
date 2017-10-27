/**
 * 
 */
package org.prelle.splimo;

import java.util.Date;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ModificationList;

import de.rpgframework.genericrpg.Datable;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "reward")
public class RewardImpl implements Datable, Reward {

	@Element
	private String title;
	@Attribute
	private int exp;
	@Attribute(required=false)
	protected Date date;
	@Attribute(required=false)
	private String id;
	@Element
	private ModificationList modifications;
	
	//-------------------------------------------------------------------
	public RewardImpl() {
		modifications = new ModificationList();
	}
	
	//-------------------------------------------------------------------
	public RewardImpl(int exp, String name) {
		this.exp = exp;
		this.title = name;
		modifications = new ModificationList();
	}
	
	//-------------------------------------------------------------------
	public String toString() {
		return "Reward '"+title+"' (EP "+exp+") at "+date+"  (id="+id+")";
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#getExperiencePoints()
	 */
	@Override
	public int getExperiencePoints() {
		return exp;
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#setExperiencePoints(int)
	 */
	@Override
	public void setExperiencePoints(int exp) {
		this.exp = exp;
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#getModifications()
	 */
	@Override
	public List<Modification> getModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#setModifications(java.util.List)
	 */
	@Override
	public void setModifications(List<Modification> mods) {
		this.modifications = new ModificationList(mods);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#addModification(de.rpgframework.genericrpg.modification.Modification)
	 */
	@Override
	public void addModification(Modification mod) {
		if (mod instanceof ModificationChoice) {
			if (((ModificationChoice)mod).getOptions().length>2 )
				throw new IllegalArgumentException("ModificationChoice without options: "+mod);
		}
		modifications.add(mod);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.Modifyable#removeModification(de.rpgframework.genericrpg.modification.Modification)
	 */
	@Override
	public void removeModification(Modification mod) {
		modifications.remove(mod);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#getDate()
	 */
	@Override
	public Date getDate() {
		return date;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#setDate(java.util.Date)
	 */
	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Reward#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

}
