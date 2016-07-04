package de.wwu.criticalsystems.libhpng.errorhandling;

public class PropertyException extends Exception {

	private static final long serialVersionUID = -2382427696993035850L;

	public PropertyException() {
		super();
	}

	public PropertyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PropertyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PropertyException(String message) {
		super(message);
	}

	public PropertyException(Throwable cause) {
		super(cause);
	}
}
