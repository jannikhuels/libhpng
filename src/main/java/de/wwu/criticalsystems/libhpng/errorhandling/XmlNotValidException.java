package de.wwu.criticalsystems.libhpng.errorhandling;

public class XmlNotValidException extends Exception {
	
	private static final long serialVersionUID = -6863265035245356579L;

	public XmlNotValidException() {
		super();
	}

	public XmlNotValidException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public XmlNotValidException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public XmlNotValidException(String message) {
		super(message);
	}

	public XmlNotValidException(Throwable cause) {
		super(cause);
	}
}
