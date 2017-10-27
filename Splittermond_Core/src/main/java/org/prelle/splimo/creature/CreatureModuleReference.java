/**
 *
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.UniqueObject;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.persist.CreatureModuleConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "creatmodref")
public class CreatureModuleReference extends UniqueObject implements Comparable<CreatureModuleReference> {

	public static class NecessaryChoice {
		
		public transient CreatureModuleReference originModule;
		@Element(name="origin")
		private ModificationList originChoice = new ModificationList();
		@Element(name="result")
		private ModificationList madeChoice = new ModificationList();
		public Boolean disabled;
		
		
		//-------------------------------------------------------------------
		public NecessaryChoice() {
		}
		//-------------------------------------------------------------------
		public NecessaryChoice(Modification orig, Modification made) {
			this();
			setOriginChoice(orig);
			setMadeChoice(made);
		}
		//-------------------------------------------------------------------
		public String toString() {
			return originChoice+" -> "+madeChoice;
		}
		//-------------------------------------------------------------------
		public Modification getOriginChoice() {
			return originChoice.get(0);
		}
		public void setOriginChoice(Modification mod) {
			originChoice.clear();
			originChoice.add(mod);
		}
		public Modification getMadeChoice() {
			if (madeChoice.isEmpty())
				return null;
			return madeChoice.get(0);
		}
		public void setMadeChoice(Modification mod) {
			madeChoice.clear();
			madeChoice.add(mod);
		}
	}

	@Attribute(name="ref",required=true)
	@AttribConvert(CreatureModuleConverter.class)
	private CreatureModule ref;
//	@Element
	private transient ModificationList modifications;
	@ElementList(entry="choice",type=NecessaryChoice.class)
    private List<NecessaryChoice> choices;

	//-------------------------------------------------------------------
	public CreatureModuleReference() {
		modifications = new ModificationList();
		choices = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public CreatureModuleReference(CreatureModule ref) {
		this();
		this.ref = ref;
	}

	//-------------------------------------------------------------------
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (ref==null)
			buf.append("CreatRef(null");
		else
			buf.append("CreatRef("+ref.getId());
		buf.append(", mod="+modifications+")");
		return buf.toString();
	}

	//-------------------------------------------------------------------
	public CreatureModule getModule() {
		return ref;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CreatureModuleReference other) {
		return ref.compareTo(other.getModule());
	}

	//-------------------------------------------------------------------
	public void addModification(Modification... mods) {
		for (Modification mod : mods)
			modifications.add(mod);
	}

	//--------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public List<Modification> getModifications() {
		return new ArrayList<>(modifications);
	}

	//-------------------------------------------------------------------
	public void addChoice(NecessaryChoice choice) {
		choices.add(choice);
	}

	//--------------------------------------------------------------------
	public List<NecessaryChoice> getChoices() {
		return new ArrayList<>(choices);
	}


}
