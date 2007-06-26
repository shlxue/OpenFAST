package org.openfast.error;

public class FastDynamicError extends RuntimeException {
	private static final long serialVersionUID = 2L;

	private final ErrorCode error;
	
	public FastDynamicError(ErrorCode error)
	{
		super(error.getShortName() + ": " + error.getDescription());
		this.error = error;
	}

	public ErrorCode getError() {
		return error;
	}
}
