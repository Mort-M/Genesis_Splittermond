/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Education;

/**
 * @author prelle
 *
 */
public interface EducationController extends Controller, Generator {

	public List<Education> getAvailableEducations();
	
	public void selectEducation(Education value);
	
}
