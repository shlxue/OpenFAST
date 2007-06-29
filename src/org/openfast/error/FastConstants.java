/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.error;



public final class FastConstants {
    // Error Types
    public static final ErrorType DYNAMIC = new ErrorType("Dynamic");
    public static final ErrorType STATIC = new ErrorType("Static");
    public static final ErrorType REPORTABLE = new ErrorType("Reportable");

    // Static Errors
    public static final ErrorCode S1_INVALID_XML = new ErrorCode(STATIC, 1,
            "ERR S1", "Invalid XML", FastAlertSeverity.ERROR);
    public static final ErrorCode S2_OPERATOR_TYPE_INCOMP = new ErrorCode(STATIC,
            2, "ERR S2", "Incompatible operator and type",
            FastAlertSeverity.ERROR);
    public static final ErrorCode S3_INITIAL_VALUE_INCOMP = new ErrorCode(STATIC,
            3, "ERR S3", "Incompatible initial value", FastAlertSeverity.ERROR);
    public static final ErrorCode S4_NO_INITIAL_VALUE_FOR_CONST = new ErrorCode(STATIC, 4, "ERR S4", "Fields with constant operators must have a default value defined.", FastAlertSeverity.ERROR);
    public static final ErrorCode S5_NO_INITVAL_MNDTRY_DFALT = new ErrorCode(STATIC,
            5, "ERR S5",
            "No initial value for mandatory field with default operator",
            FastAlertSeverity.ERROR);

    // Dynamic Errors
    public static final ErrorCode D1_FIELD_APP_INCOMP = new ErrorCode(DYNAMIC, 1,
            "ERR D1", "Field cannot be converted to type of application field",
            FastAlertSeverity.ERROR);
    public static final ErrorCode D2_INT_OUT_OF_RANGE = new ErrorCode(DYNAMIC, 2, "ERR D2", "The integer value is out of range for the specified integer type.", FastAlertSeverity.WARN);
    public static final ErrorCode D4_INVALID_TYPE = new ErrorCode(DYNAMIC, 4, "ERR D4", "The previous value is not the same type as the type of the current field.", FastAlertSeverity.ERROR);
    public static final ErrorCode D5_NO_DEFAULT_VALUE = new ErrorCode(DYNAMIC, 5,
            "ERR D5",
            "If no prior value is set and the field is not present, there must be a default value or the optional flag must be set.",
            FastAlertSeverity.ERROR);
    public static final ErrorCode D6_MNDTRY_FIELD_NOT_PRESENT = new ErrorCode(DYNAMIC,
            6, "ERR D6", "A mandatory field must have a value",
            FastAlertSeverity.ERROR);

    // Reportable Errors
    public static final ErrorCode R1_LARGE_DECIMAL = new ErrorCode(REPORTABLE, 1,
            "ERR R1", "Decimal exponent does not fit into range -63...63",
            FastAlertSeverity.ERROR);
    public static final ErrorCode R4_NUMERIC_VALUE_TOO_LARGE = new ErrorCode(REPORTABLE, 4, "ERR R4", "The value is too large.", FastAlertSeverity.ERROR);
    public static final ErrorCode R5_DECIMAL_CANT_CONVERT_TO_INT = new ErrorCode(REPORTABLE, 5, "ERR R5", "The decimal value cannot convert to an integer because of trailing decimal part.", FastAlertSeverity.ERROR);
    
    // Errors not defined in the FAST specification
	public static final ErrorCode IMPOSSIBLE_EXCEPTION = new ErrorCode(DYNAMIC, 101, "IMPOSSIBLE", "This should never happen.", FastAlertSeverity.ERROR);
	public static final ErrorCode IO_ERROR = new ErrorCode(DYNAMIC, 102, "IOERROR", "An IO error occurred.", FastAlertSeverity.FATAL);
	public static final String ANY = "any";
}
