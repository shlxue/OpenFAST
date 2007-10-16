package org.openfast;

import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;

public final class Global {
	private static final int STEP = 2;
	private static ErrorHandler errorHandler = ErrorHandler.DEFAULT;
	private static int currentImplicitId = (int) (System.currentTimeMillis() % 10000);
	private static boolean trace;
	private static int level;
	
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

	public static QName createImplicitName(QName name) {
		return new QName(name + "@" + currentImplicitId++, name.getNamespace());
	}

	private Global() {}

	public static boolean isTraceEnabled() {
		return trace;
	}
	
	public static void enableTrace() {
		trace = true;
	}

	public static void trace(String message) {
		if (trace) {
			indent();
			System.out.println(message);
		}
	}

	private static void indent() {
		for (int i=0; i<level; i++)
			System.out.print(' ');
	}

	public static void traceDown() {
		level += STEP;
	}

	public static void traceUp() {
		level -= STEP;
	}

	public static void trace(Object message) {
		trace(message.toString());
	}
}
