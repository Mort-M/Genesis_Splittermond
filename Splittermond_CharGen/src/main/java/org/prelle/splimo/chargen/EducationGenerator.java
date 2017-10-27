/**
 * 
 */
package org.prelle.splimo.chargen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Education;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.Controller;
import org.prelle.splimo.modifications.ModificationImpl;

/**
 * @author prelle
 *
 */
public class EducationGenerator implements Controller {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private List<ModificationImpl> modifications;
	private List<Education> available;
	private Education selected;

	//-------------------------------------------------------------------
	public EducationGenerator() {
			available     = new ArrayList<Education>();
		modifications = new ArrayList<ModificationImpl>();
		
		updateAvailableData();
	}

	//-------------------------------------------------------------------
	private void updateAvailableData() {
		// List of backgrounds to remove - starts with all that exist
		List<Education> toRemove = new ArrayList<Education>(available);
		
		available = SplitterMondCore.getEducations();
		
		// All previous modifications that have not been renewed
		// shall be removed
		logger.debug("avail1: "+available);
		available.removeAll(toRemove);
		logger.debug("avail2: "+available);
	}

	//-------------------------------------------------------------------
	/**
	 * Return the list of resources that can be added
	 */
	public List<Education> getAvailableEducations() {
		Collections.sort(available);
		return available;
	}

//	//-------------------------------------------------------------------
//	void addModification(EducationModification mod) {
//		logger.debug("Add modification: "+mod);
//		if (!modifications.contains(mod)) {
//			modifications.add(mod);
//			updateAvailableData();
//		}
//	}
//
//	//-------------------------------------------------------------------
//	void addModification(NotEducationModification mod) {
//		logger.debug("Add modification: "+mod);
//		if (!modifications.contains(mod)) {
//			modifications.add(mod);
//			updateAvailableData();
//		}
//	}

	//-------------------------------------------------------------------
	void removeModification(ModificationImpl mod) {
		logger.debug("remove modification: "+mod);
		if (modifications.contains(mod)) {
			modifications.remove(mod);
			updateAvailableData();
		}
	}

	//-------------------------------------------------------------------
	public Education getSelected() {
		return selected;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		if (selected==null)
			return Arrays.asList(RES.getString("educationgen.todo"));
		return new ArrayList<>();
	}

}
