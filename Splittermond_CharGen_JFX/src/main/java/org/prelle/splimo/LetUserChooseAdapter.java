/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class LetUserChooseAdapter implements LetUserChooseListener {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	interface ModificationFilter {
		public boolean canBeApplied(Modification mod);
	}

	private ScreenManagerProvider provider;
	private List<Predicate<Modification>> filter;
	
	//-------------------------------------------------------------------
	public LetUserChooseAdapter(ScreenManagerProvider provider) {
		this.provider = provider;
		filter = new ArrayList<Predicate<Modification>>();
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public LetUserChooseAdapter(ScreenManagerProvider provider, Predicate<Modification>... myFilter) {
		this(provider);
		for (Predicate<Modification> tmp : myFilter)
			filter.add(tmp);
	}

	//-------------------------------------------------------------------
	@Override
	public void addPrefilter(Predicate<Modification> toAdd) {
		if (!filter.contains(toAdd))
			filter.add(toAdd);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.LetUserChooseListener#letUserChoose(java.lang.String, org.prelle.splimo.modifications.ModificationChoice)
	 */
	@Override
	public Modification[] letUserChoose(String choiceReason, ModificationChoice choice) {
		logger.info("letUserChoose "+choice);
		
		if (!filter.isEmpty()) {
			logger.debug("Apply filter");
			ModificationChoice filtered = new ModificationChoice(choice.getNumberOfChoices());
			filtered.setDistribute(choice.getDistribute());
			filtered.setValues(choice.getValues());
			
			List<Modification> toTest = new ArrayList<>(choice.getOptionList());
			for (Predicate<Modification> tmp : filter) {
				for (Modification mod : new ArrayList<>(toTest)) {
					if (!tmp.test(mod)) {
						logger.debug("  Remove "+mod+" from options");
						toTest.remove(mod);
					}
				}
			}
			for (Modification tmp : toTest)
				filtered.add(tmp);
			choice = filtered;
		}
		

		if (choice.getValues()!=null && choice.getValues().length>0) {
			return showUserDistributeDialog(choiceReason, choice);
		} else if (choice.getDistribute()>0) {
			return showUserDistributePointsDialog(choiceReason, choice);
		} else
			return showUserChoiceDialog(choiceReason, choice);
	}

	//-------------------------------------------------------------------
	private Modification[] showUserChoiceDialog(String choiceReason, ModificationChoice choice) {
		logger.debug("showUserChoiceDialog: "+choice);
		UserChoiceDialog dialog = new UserChoiceDialog(choiceReason, choice);
		dialog.setOnAction(CloseType.OK, event -> {
				provider.getScreenManager().close(dialog, CloseType.OK);
		});
		CloseType ret = (CloseType) provider.getScreenManager().showAndWait(dialog);
		dialog.setCloseType(ret);
		logger.debug("Choice was "+Arrays.toString(dialog.getChoice())+" and CloseType "+dialog.getCloseType());
		if (dialog.getCloseType()==CloseType.OK) {
			return dialog.getChoice();
		}
		return new Modification[0];
	}

	//-------------------------------------------------------------------
	private Modification[] showUserDistributeDialog(String choiceReason, ModificationChoice choice) {
		UserDistributeDialog dialog = new UserDistributeDialog(choiceReason, choice);
		dialog.setOnAction(CloseType.OK, event -> {
			provider.getScreenManager().close(dialog, CloseType.OK);
		});

		provider.getScreenManager().showAndWait(dialog);
		if (dialog.getCloseType()==CloseType.OK)
			return dialog.getChoice();
		return new Modification[0];
	}

	//-------------------------------------------------------------------
	private Modification[] showUserDistributePointsDialog(String choiceReason, ModificationChoice choice) {
		UserDistributePointsDialog dialog = new UserDistributePointsDialog(choiceReason, choice);
		dialog.setOnAction(CloseType.OK, event -> {
			provider.getScreenManager().close(dialog, CloseType.OK);
		});

		provider.getScreenManager().showAndWait(dialog);
		if (dialog.getCloseType()==CloseType.OK)
			return dialog.getChoice();
		return new Modification[0];
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.LetUserChooseListener#letUserChoose(java.lang.String, org.prelle.splimo.modifications.MastershipModification)
	 */
	@Override
	public MastershipModification letUserChoose(String choiceReason, MastershipModification vagueMod) {
		logger.error("Not implemented: choose real mastership over vague one: "+vagueMod);
		logger.debug("Skill to choose mastership from = "+vagueMod.getSkill()+"  and max level = "+vagueMod.getLevel());
		
		List<Modification> options = new ArrayList<>();
		for (Mastership mast : vagueMod.getSkill().getMasterships()) {
			if (mast.getLevel()<=vagueMod.getLevel()) {
				options.add(new MastershipModification(mast));
				logger.error("Adding mastership "+mast+" has options, but did not check with character");
			}				
		}
		ModificationChoice choice = new ModificationChoice(options, 1);
		
		Modification[] mod =  letUserChoose(choiceReason, choice);
		if (mod==null || mod.length==0) {
			logger.error("User did not choose anything from "+choice);
			return null; 
		}
		return (MastershipModification)mod[0];
	}
	

}
