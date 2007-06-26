package org.openfast.error;

import org.openfast.FastAlertSeverity;

public final class FastConstants {
	// Error Types
	public static final ErrorType DYNAMIC = new ErrorType("Dynamic");
	public static final ErrorType STATIC = new ErrorType("Static");
	public static final ErrorType REPORTABLE = new ErrorType("Reportable");
	
	// Static Errors
	public static final ErrorCode INVALID_XML              = new ErrorCode(STATIC,     1, "ERR S1",    "Invalid XML",            FastAlertSeverity.ERROR);
	public static final ErrorCode OPERATOR_TYPE_INCOMP     = new ErrorCode(STATIC,     2, "ERR S2",    "Incompatible operator and type", FastAlertSeverity.ERROR);
	public static final ErrorCode INITIAL_VALUE_INCOMP     = new ErrorCode(STATIC,     3, "ERR S3",    "Incompatible initial value", FastAlertSeverity.ERROR);
	public static final ErrorCode NO_INITIAL_VALUE         = new ErrorCode(STATIC,     4, "ERR S4",    "No initial value",       FastAlertSeverity.ERROR);
	public static final ErrorCode NO_INITVAL_MNDTRY_DFALT  = new ErrorCode(STATIC,     5, "ERR S5",    "No initial value for mandatory field with default operator", FastAlertSeverity.ERROR);
	
	// Dynamic Errors
	public static final ErrorCode FIELD_APP_INCOMP         = new ErrorCode(DYNAMIC,    1, "ERR D1",    "Field cannot be converted to type of application field", FastAlertSeverity.ERROR);
	public static final ErrorCode NO_DEFAULT_VALUE         = new ErrorCode(DYNAMIC,    5, "ERR D5",    "If no prior value is set and the field is not present, there must be a default value or the optional flag must be set.", FastAlertSeverity.ERROR);
	public static final ErrorCode MNDTRY_FIELD_NOT_PRESENT = new ErrorCode(DYNAMIC,    6, "ERR D6",    "A mandatory field must have a value", FastAlertSeverity.ERROR);
	
	// Reportable Errors
	public static final ErrorCode LARGE_DECIMAL            = new ErrorCode(REPORTABLE, 1, "ERR R1",    "Decimal exponent does not fit into range -63...63", FastAlertSeverity.ERROR);

	public static void setGlobalHandler(ErrorHandler handler) {
		if (handler == null) throw new NullPointerException();
		globalHandler = handler;
	}
	
	public static void handleError(ErrorCode error, String message) {
		globalHandler.error(error, message);
	}
	
	private static ErrorHandler globalHandler = ErrorHandler.DEFAULT;
}
