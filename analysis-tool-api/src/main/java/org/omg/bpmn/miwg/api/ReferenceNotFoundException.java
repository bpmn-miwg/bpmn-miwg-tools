package org.omg.bpmn.miwg.api;

public class ReferenceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReferenceNotFoundException(String resourceName) {
		super("Cannot find reference resource " + resourceName
				+ " (null ressource)");
	}

}
