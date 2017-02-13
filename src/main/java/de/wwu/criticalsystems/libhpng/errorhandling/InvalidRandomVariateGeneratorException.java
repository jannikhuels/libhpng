package de.wwu.criticalsystems.libhpng.errorhandling;

public class InvalidRandomVariateGeneratorException extends Exception {

	private static final long serialVersionUID = -4249383803223601115L;

	public InvalidRandomVariateGeneratorException() {
		super();
	}

	public InvalidRandomVariateGeneratorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidRandomVariateGeneratorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidRandomVariateGeneratorException(String message) {
		super(message);
	}

	public InvalidRandomVariateGeneratorException(Throwable cause) {
		super(cause);
	}
}
