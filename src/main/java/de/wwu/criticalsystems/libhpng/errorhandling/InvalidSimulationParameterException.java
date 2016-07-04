package de.wwu.criticalsystems.libhpng.errorhandling;

public class InvalidSimulationParameterException extends Exception {

	private static final long serialVersionUID = 3223470993924978006L;

	public InvalidSimulationParameterException() {
		super();
	}

	public InvalidSimulationParameterException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidSimulationParameterException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidSimulationParameterException(String message) {
		super(message);
	}

	public InvalidSimulationParameterException(Throwable cause) {
		super(cause);
	}
}
