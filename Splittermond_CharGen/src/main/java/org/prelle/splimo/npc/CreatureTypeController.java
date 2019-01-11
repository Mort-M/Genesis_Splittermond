/**
 * 
 */
package org.prelle.splimo.npc;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.creature.CreatureTypeValue;


/**
 * @author Stefan
 *
 */
public class CreatureTypeController {
	
	private Collator collator = Collator.getInstance();

	private Creature model;
	private List<CreatureType> available;

	//--------------------------------------------------------------------
	public CreatureTypeController(Creature model) {
		this.model = model;
		available = new ArrayList<CreatureType>();
		updateAvailable();
	}

	//--------------------------------------------------------------------
	public void updateAvailable() {
		available.clear();
		outer:
		for (CreatureType tmp : SplitterMondCore.getCreatureTypes()) {
			for (CreatureTypeValue val : model.getCreatureTypes()) {
				if (val.getType()==tmp)
					continue outer;
			}
			// Is not selected yet
			available.add(tmp);
		}
		
		Collections.sort(available, new Comparator<CreatureType>() {
			@Override
			public int compare(CreatureType arg0, CreatureType arg1) {
				return collator.compare(arg0.getName(), arg1.getName());
			}
		});
	}

	//--------------------------------------------------------------------
	public List<CreatureType> getAvailable() {
		return available;
	}

	//--------------------------------------------------------------------
	public CreatureTypeValue select(CreatureType data) {
		CreatureTypeValue ret = new CreatureTypeValue(data);
		if (data.hasLevel())
			ret.setLevel(1);
		
		model.addCreatureType(ret);
		
		updateAvailable();
		GenerationEventDispatcher.fireEvent(new GenerationEvent(
				GenerationEventType.CREATURETYPES_CHANGED, 
				available,
				model.getCreatureTypes()));
		
		return ret;
	}

	//--------------------------------------------------------------------
	public void deselect(CreatureTypeValue data) {
		if (!model.getCreatureTypes().contains(data))
			return;
		
		model.removeCreatureType(data);
		
		updateAvailable();
		GenerationEventDispatcher.fireEvent(new GenerationEvent(
				GenerationEventType.CREATURETYPES_CHANGED, 
				available,
				model.getCreatureTypes()));
	}

	//--------------------------------------------------------------------
	public void increase(CreatureTypeValue data) {
		data.setLevel(data.getLevel()+1);		
		GenerationEventDispatcher.fireEvent(new GenerationEvent(
				GenerationEventType.CREATURETYPES_CHANGED, 
				available,
				model.getCreatureTypes()));
	}

	//--------------------------------------------------------------------
	public void decrease(CreatureTypeValue data) {
		if (data.getLevel()<=1)
			return;
		data.setLevel(data.getLevel()-1);		
		GenerationEventDispatcher.fireEvent(new GenerationEvent(
				GenerationEventType.CREATURETYPES_CHANGED, 
				available,
				model.getCreatureTypes()));
	}

}
