package org.openfast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.util.Key;

public final class Global {
	private static ErrorHandler errorHandler = ErrorHandler.DEFAULT;
	private static int currentImplicitId = (int) (System.currentTimeMillis() % 10000);  // why? because I felt like it
	private static DateFormat timestampFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	private static Map codecMap = new HashMap();
	
	static {
		registerCodec(Type.TIMESTAMP, true, TypeCodec.EPOCH_TIMESTAMP);
		registerCodec(Type.DATE, true, TypeCodec.DATE_STRING);
	}
	
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

	public static String createImplicitName(String prefix) {
		return prefix + "@" + currentImplicitId++;  // that should be unique enough
	}

	public static DateFormat getTimestampFormatter() {
		return timestampFormat;
	}
	
	public static void setTimestampFormat(String format) {
		Global.timestampFormat = new SimpleDateFormat(format);
	}

	public static TypeCodec getCodec(Type type, Operator operator, boolean optional) {
		if (!codecMap.containsKey(new Key(type, operator, Boolean.valueOf(optional)))) {
			handleError(FastConstants.GENERAL_ERROR, "No codec has been registered for type " + type);
		}
		return (TypeCodec) codecMap.get(new Key(type, operator, Boolean.valueOf(optional)));
	}
	
	public static void registerCodec(Type type, boolean optional, TypeCodec codec) {
		
	}
	
	private Global() {}
}
