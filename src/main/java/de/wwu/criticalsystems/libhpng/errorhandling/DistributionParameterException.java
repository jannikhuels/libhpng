package de.wwu.criticalsystems.libhpng.errorhandling;

public class DistributionParameterException extends Exception {

	private static final long serialVersionUID = -6142038234032695164L;

	public DistributionParameterException() {
		super();
	}

	public DistributionParameterException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DistributionParameterException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DistributionParameterException(String message) {
		super(message);
	}

	public DistributionParameterException(Throwable cause) {
		super(cause);
	}
}
