/**
 * 
 */
package org.prelle.splimo;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.persist.MastershipConverter;
import org.prelle.splimo.persist.SkillSpecializationValueConverter;

/**
 * @author prelle
 *
 */
@Root(name = "masterref")
public class MastershipReference implements Comparable<MastershipReference> {

	/*
	 * Jagdkunst:  Bärenartiger, Fisch, Großwild, Hundeartiger, Insekt, 
	 *     Katzenartiger, Kleinwild, Reptil, Spinnentier, Vogel
	 *     --> Kreaturentype
	 * Handwerk:  HOlzbearbeitung, Schneiderei/Lederbearbeitung,
	 *   Schmiedekunst, Steinbearbeitung
	 * Edelhandwerk: Architektur, Glasbläserei, Gravur, Juwelier,
	 *   Kalligraphie, Kartographie, Malerei, Tätowieren, Kochen
	 * Geschichte und Mythen: frei wählbar
	 * Verwandlungsmagie: Tier  (#2160)
	 */
	
	
	@Attribute(name="ref",required=false)
	@AttribConvert(MastershipConverter.class)
	private Mastership data;
	
	@Attribute(name="spec",required=false)
	@AttribConvert(SkillSpecializationValueConverter.class)
	private SkillSpecializationValue spec;
	@Attribute(required=false)
	private int free;

	//-------------------------------------------------------------------
	public MastershipReference() {
	}

	//-------------------------------------------------------------------
	public MastershipReference(Mastership power) {
		this.data = power;
	}

	//-------------------------------------------------------------------
	public MastershipReference(SkillSpecialization special, int level) {
		this.spec = new SkillSpecializationValue(special, level);
	}

	//-------------------------------------------------------------------
	public MastershipReference(SkillSpecializationValue special) {
		this.spec = special;
	}

	//-------------------------------------------------------------------
	public String toString() {
		if (data!=null)
			return String.valueOf(data);
		else
			return String.valueOf(spec);
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof MastershipReference) {
			MastershipReference other = (MastershipReference)o;
			if (data!=null) return data.equals(other.getMastership());
			if (spec!=null) return spec.equals(other.getSpecialization());
			return false;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Mastership getMastership() {
		return data;
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MastershipReference other) {
		return toString().compareTo(other.toString());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the spec
	 */
	public SkillSpecializationValue getSpecialization() {
		return spec;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the free
	 */
	public int getFree() {
		return free;
	}

	//-------------------------------------------------------------------
	/**
	 * @param free the free to set
	 */
	public void setFree(int free) {
		this.free = free;
	}

}
