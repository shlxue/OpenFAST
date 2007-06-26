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


package org.openfast;

import org.openfast.template.type.Type;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class ScalarValue implements FieldValue {
    public static final ScalarValue UNDEFINED = new ScalarValue() {
            public boolean isUndefined() {
                return true;
            }

            public String toString() {
                return "UNDEFINED";
            }
        };

    static public final ScalarValue NULL = new ScalarValue() {
            public boolean isNull() {
                return true;
            }

            public String toString() {
                return "NULL";
            }
        };

    public boolean equalsValue(String defaultValue) {
        return false;
    }

    public boolean isUndefined() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public static ScalarValue getValue(Integer type, String value) {
        if ((type == Type.SIGNED_INTEGER) || (type == Type.UNSIGNED_INTEGER)) {
            return new IntegerValue(Integer.parseInt(value));
        } else if (type == Type.DECIMAL) {
            return new DecimalValue(Double.parseDouble(value));
        } else if (type == Type.STRING) {
            return new StringValue(value);
        } else if (type == Type.BYTE_VECTOR) {
            throw new NotImplementedException();
        }

        throw new IllegalArgumentException("The type specified does not exist.");
    }

    public String serialize() {
        return "";
    }
}
