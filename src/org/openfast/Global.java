package org.openfast;

import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;

public final class Global {
	private static ErrorHandler errorHandler = ErrorHandler.DEFAULT;
	private static int currentImplicitId = (int) (System.currentTimeMillis() % 10000);
	
	public static void setErrorHandler(ErrorHandler handler) {
	    if (handler == null) {
	        throw new NullPointerException();
	    }
	
	    Global.errorHandler = handler;
	}

	public static void handleError(ErrorCode error, String message) {
	    errorHandler.error(error, message);
	}

	public static void handleError(ErrorCode error, String message, Throwable source) {
		errorHandler.error(error, message, source);
	}

	public static String createImplicitName(QName name) {
		return name + "@" + currentImplicitId++;
	}

	private Global() {}
}
