/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.Reward;

/**
 * @author prelle
 *
 */
@ElementList(entry="reward", type=RewardImpl.class)
public class RewardList extends ArrayList<Reward> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public RewardList() {
	}

	//-------------------------------------------------------------------
	public RewardList(Collection<? extends RewardImpl> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<Reward> getData() {
		return this;
	}
}
