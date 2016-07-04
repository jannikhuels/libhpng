package de.wwu.criticalsystems.libhpng.errorhandling;

public class PropertyError extends Exception {

	private static final long serialVersionUID = -2382427696993035850L;

	public PropertyError() {
		super();
	}

	public PropertyError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PropertyError(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PropertyError(String message) {
		super(message);
	}

	public PropertyError(Throwable cause) {
		super(cause);
	}
}
