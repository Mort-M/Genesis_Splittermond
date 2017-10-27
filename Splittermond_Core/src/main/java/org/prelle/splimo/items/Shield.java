/**
 * 
 */
package org.prelle.splimo.items;

import java.util.List;
import java.util.stream.Collectors;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.RequirementList;

/**
 * @author apr
 *
 */
@Root(name = "shield")
public class Shield extends ItemTypeData {

	@Attribute(name="def")
	protected int defense;
	@Attribute(name="hc")
	protected int handicap;
	@Attribute
	protected int tickMalus;
	@Element
	protected RequirementList requires;
	@Element
	protected FeatureList features;

	//--------------------------------------------------------------------
	public Shield() {
		super(ItemType.SHIELD);
		requires = new RequirementList();
		features = new FeatureList();
	}
	
	//--------------------------------------------------------------------
	public String toString() {
		return "Shield(def="+defense+",hc="+handicap+",tm="+tickMalus+",req="+requires+")";
	}

	//--------------------------------------------------------------------
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Shield) {
			Shield other = (Shield)o;
			if (!super.equals(other)) return false;
			if (defense!=other.getDefense()) return false;
			if (handicap!=other.getHandicap()) return false;
			if (tickMalus!=other.getTickMalus()) return false;
			if (!requires.equals(other.getRequirements())) return false;
			if (!features.equals(other.deliverFeaturesAsDeepClone())) return false;
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the requires
	 */
	public List<Requirement> getRequirements() {
		return requires;
	}

	//--------------------------------------------------------------------
	/**
	 * @param req the requires to set
	 */
	public void addRequirement(Requirement req) {
		requires.add(req);
	}

	//--------------------------------------------------------------------
	/**
	 * @return the {@link Feature} entries as deep copied {@link List}
	 */
	public List<Feature> deliverFeaturesAsDeepClone() { 
		// make deep copy 		
		return features.stream().map(Feature::clone).collect(Collectors.toList());
	}	

	// TODO Pastore 10.04.2017: It's not a good solution. CarriedItem should copy the member features and Weapon should always return its member features without shallow or deep cloning.
	
	/** 
	 * @return the {@link Feature} entries of the member list
	 */
	public List<Feature> getFeatures() { 
		return features;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @param req the requires to set
	 */
	public void addFeature(Feature feat) {
		features.add(feat);
	}

	//--------------------------------------------------------------------
	/**
	 * @return the defense
	 */
	public int getDefense() {
		return defense;
	}

	//--------------------------------------------------------------------
	/**
	 * @param defense the defense to set
	 */
	public void setDefense(int defense) {
		this.defense = defense;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the handicap
	 */
	public int getHandicap() {
		return handicap;
	}

	//--------------------------------------------------------------------
	/**
	 * @param handicap the handicap to set
	 */
	public void setHandicap(int handicap) {
		this.handicap = handicap;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the tickMalus
	 */
	public int getTickMalus() {
		return tickMalus;
	}

	//--------------------------------------------------------------------
	/**
	 * @param tickMalus the tickMalus to set
	 */
	public void setTickMalus(int tickMalus) {
		this.tickMalus = tickMalus;
	}

}
