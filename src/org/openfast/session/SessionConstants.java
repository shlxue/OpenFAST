package org.openfast.session;

import org.openfast.FastAlertSeverity;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorType;

public interface SessionConstants {
	ErrorType SESSION = new ErrorType("Session");

	// Session Control Protocol (SCP) Errors
	ErrorCode TEMPLATE_NOT_SUPPORTED  = new ErrorCode(SESSION, 11, "TNOTSUPP",  "Template not supported", FastAlertSeverity.ERROR);
	ErrorCode TEMPLATE_UNKNOWN        = new ErrorCode(SESSION, 12, "TUNKNOWN",  "Template unknown",       FastAlertSeverity.ERROR);
	ErrorCode UNAUTHORIZED            = new ErrorCode(SESSION, 13, "EAUTH",     "Unauthorized",           FastAlertSeverity.FATAL);
	ErrorCode PROTCOL_ERROR           = new ErrorCode(SESSION, 14, "EPROTO",    "Protocol Error",         FastAlertSeverity.ERROR);
	ErrorCode UNDEFINED               = new ErrorCode(SESSION, -1, "UNDEFINED", "Undefined Alert Code",   FastAlertSeverity.ERROR);
}
