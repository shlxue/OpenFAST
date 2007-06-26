package org.openfast.session;


public class FastConnectionException extends Exception {
	private static final long serialVersionUID = 1L;

	public FastConnectionException(Throwable t) {
		super(t);
	}

	public FastConnectionException(String message) {
		super(message);
	}

}
