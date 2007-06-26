package org.openfast;

public class FastAlertSeverity {
	public static final FastAlertSeverity FATAL = new FastAlertSeverity(1, "FATAL", "Fatal");
	public static final FastAlertSeverity ERROR = new FastAlertSeverity(2, "ERROR", "Error");
	public static final FastAlertSeverity WARN  = new FastAlertSeverity(3, "WARN", "Warning");
	public static final FastAlertSeverity INFO  = new FastAlertSeverity(4, "INFO", "Information");

	private int code;
	private String shortName;
	private String description;

	public FastAlertSeverity(int code, String shortName, String description) {
		this.code = code;
		this.shortName = shortName;
		this.description = description;
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

}
