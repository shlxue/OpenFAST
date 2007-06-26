package org.openfast.error;


public class FastException extends RuntimeException {
	private static final long serialVersionUID = 2L;
	
	private final ErrorCode code;

	public FastException(String message, ErrorCode code) {
		super(message);
		this.code = code;
	}

	public FastException(ErrorCode code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ErrorCode getCode() {
		return code;
	}

}
