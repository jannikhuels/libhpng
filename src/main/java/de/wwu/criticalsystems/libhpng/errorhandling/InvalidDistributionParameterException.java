package de.wwu.criticalsystems.libhpng.errorhandling;

public class InvalidDistributionParameterException extends Exception {

	private static final long serialVersionUID = -6142038234032695164L;

	public InvalidDistributionParameterException() {
		super();
	}

	public InvalidDistributionParameterException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidDistributionParameterException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidDistributionParameterException(String message) {
		super(message);
	}

	public InvalidDistributionParameterException(Throwable cause) {
		super(cause);
	}
}
