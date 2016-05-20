package de.wwu.criticalsystems.libhpng.errorhandling;

public class InvalidModelConnectionException extends Exception {

	private static final long serialVersionUID = 5463835188101445959L;
	
	public InvalidModelConnectionException() {
		super();
	}

	public InvalidModelConnectionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidModelConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidModelConnectionException(String message) {
		super(message);
	}

	public InvalidModelConnectionException(Throwable cause) {
		super(cause);
	}

}
