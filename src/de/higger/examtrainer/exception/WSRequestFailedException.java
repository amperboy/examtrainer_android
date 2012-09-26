package de.higger.examtrainer.exception;

public class WSRequestFailedException extends Exception {
	private static final long serialVersionUID = -8769185598608437741L;

	public WSRequestFailedException() {
		super();
	}

	public WSRequestFailedException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public WSRequestFailedException(String detailMessage) {
		super(detailMessage);
	}

	public WSRequestFailedException(Throwable throwable) {
		super(throwable);
	}
}
