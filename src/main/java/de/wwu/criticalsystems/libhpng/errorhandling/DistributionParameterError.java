package de.wwu.criticalsystems.libhpng.errorhandling;

public class DistributionParameterError extends Exception {

	private static final long serialVersionUID = -6142038234032695164L;

	public DistributionParameterError() {
		super();
	}

	public DistributionParameterError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DistributionParameterError(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DistributionParameterError(String message) {
		super(message);
	}

	public DistributionParameterError(Throwable cause) {
		super(cause);
	}
}
