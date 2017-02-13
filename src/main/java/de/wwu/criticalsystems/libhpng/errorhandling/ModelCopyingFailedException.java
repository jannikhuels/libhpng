package de.wwu.criticalsystems.libhpng.errorhandling;

public class ModelCopyingFailedException extends Exception {

	private static final long serialVersionUID = -6161536048599588250L;

	public ModelCopyingFailedException() {
		super();
	}

	public ModelCopyingFailedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ModelCopyingFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ModelCopyingFailedException(String message) {
		super(message);
	}

	public ModelCopyingFailedException(Throwable cause) {
		super(cause);
	}
}
