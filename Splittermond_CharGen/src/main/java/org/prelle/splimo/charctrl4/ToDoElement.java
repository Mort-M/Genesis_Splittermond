/**
 * 
 */
package org.prelle.splimo.charctrl4;

/**
 * @author prelle
 *
 */
public class ToDoElement {
	
	public enum Severity {
		STOPPER,
		WARNING
	}
	
	private Severity severity;
	private String message;

	//-------------------------------------------------------------------
	public ToDoElement(Severity sev, String mess) {
		this.severity = sev;
		this.message  = mess;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the severity
	 */
	public Severity getSeverity() {
		return severity;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
