package de.wwu.criticalsystems.libhpng.errorhandling;

public class ModelNotReadableException extends Exception {

	private static final long serialVersionUID = -8917648771463479854L;

	public ModelNotReadableException() {
		super();
	}

	public ModelNotReadableException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ModelNotReadableException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ModelNotReadableException(String message) {
		super(message);
	}

	public ModelNotReadableException(Throwable cause) {
		super(cause);
	}

}
