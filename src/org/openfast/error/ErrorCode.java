package org.openfast.error;

import java.util.HashMap;
import java.util.Map;

import org.openfast.FastAlertSeverity;
import org.openfast.GroupValue;

public class ErrorCode {
	private static final Map ALERT_CODES = new HashMap();

	private final int code;
	private final String shortName;
	private final String description;
	private final FastAlertSeverity severity;
	private final ErrorType type;

	public ErrorCode(ErrorType type, int code, String shortName, String description, FastAlertSeverity severity) {
		ALERT_CODES.put(new Integer(code), this);
		this.type = type;
		this.code = code;
		this.shortName = shortName;
		this.description = description;
		this.severity = severity;
	}
	
	public void throwException(String message) {
		throw new FastException(message, this);
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getShortName() {
		return shortName;
	}

	public FastAlertSeverity getSeverity() {
		return severity;
	}

	public static ErrorCode getAlertCode(GroupValue alertMsg) {
		return (ErrorCode) ALERT_CODES.get(new Integer(alertMsg.getInteger(2)));
	}

	public ErrorType getType() {
		return type;
	}
}
