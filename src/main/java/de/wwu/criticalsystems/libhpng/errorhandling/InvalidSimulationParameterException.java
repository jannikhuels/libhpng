package de.wwu.criticalsystems.libhpng.errorhandling;

public class InvalidSimulationParameterException extends Exception {

	private static final long serialVersionUID = 3223470993924978006L;

	
	public InvalidSimulationParameterException(String message) {
		super(message);
	}
}
