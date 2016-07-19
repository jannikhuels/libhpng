package de.wwu.criticalsystems.libhpng.errorhandling;

public class InvalidPropertyException extends Exception {

	private static final long serialVersionUID = -2382427696993035850L;

	public InvalidPropertyException() {
		super();
	}

	public InvalidPropertyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidPropertyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidPropertyException(String message) {
		super(message);
	}

	public InvalidPropertyException(Throwable cause) {
		super(cause);
	}
}
