/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class MoonsignProcessor implements Generator, SpliMoCharacterProcessor {

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen.moon");

	private List<ToDoElement> todos;

	//-------------------------------------------------------------------
	public MoonsignProcessor() {
		todos     = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	@Override
	public List<ToDoElement> getToDos() {
		return todos;
	}

	//-------------------------------------------------------------------
	@Override
	public List<DecisionToMake> getDecisionsToMake() {
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	@Override
	public void decide(DecisionToMake choice, List<Modification> choosen) {
	}

	//-------------------------------------------------------------------
	@Override
	public int getPointsLeft() {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.trace("START: process");
		try {
			todos.clear();

			/*
			 * Make sure a moonsign has been selected
			 */
			Moonsign selected = model.getSplinter();
			if (selected==null) {
				todos.add(new ToDoElement(Severity.STOPPER, RES.getString("moonsign.todo")));
			}			
		} finally {
			logger.trace("STOP : process");
		}
		return unprocessed;
	}

}
