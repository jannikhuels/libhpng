package de.wwu.criticalsystems.libhpng.errorhandling;

public class InvalidSimulationParameterError extends Exception {

	private static final long serialVersionUID = 3223470993924978006L;

	public InvalidSimulationParameterError() {
		super();
	}

	public InvalidSimulationParameterError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidSimulationParameterError(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidSimulationParameterError(String message) {
		super(message);
	}

	public InvalidSimulationParameterError(Throwable cause) {
		super(cause);
	}
}
