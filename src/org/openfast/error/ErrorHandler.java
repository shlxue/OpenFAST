package org.openfast.error;



public interface ErrorHandler {
	public static final ErrorHandler DEFAULT = new ErrorHandler() {
		public void error(ErrorCode code, String message) {
			code.throwException(message);
		}

		public void error(ErrorCode code, String message, Throwable t) {
			throw new FastException(code, message, t);
		}};
		
	public static final ErrorHandler NULL = new ErrorHandler() {
		public void error(ErrorCode code, String message) {
		}

		public void error(ErrorCode code, String message, Throwable t) {
		}};
		
	public abstract void error(ErrorCode code, String message);
	public abstract void error(ErrorCode code, String message, Throwable t);
}
