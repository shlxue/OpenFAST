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


package org.openfast.template.operator;

import org.openfast.ScalarValue;

import org.openfast.error.FastConstants;

import org.openfast.template.Scalar;
import org.openfast.template.type.Type;


public class CopyOperator extends OptionallyPresentOperator {
    protected CopyOperator() {
        super(COPY, Type.ALL_TYPES);
    }

    protected ScalarValue getValueToEncode(ScalarValue value,
        ScalarValue priorValue, ScalarValue defaultValue) {
        if ((priorValue == ScalarValue.UNDEFINED) &&
                value.equals(defaultValue)) {
            return null;
        }

        return (value.equals(priorValue)) ? null : value;
    }

    protected ScalarValue getInitialValue(Scalar field) {
        if (!field.getDefaultValue().isUndefined()) {
            return field.getDefaultValue();
        }

        if (field.isOptional()) {
            return null;
        }

        FastConstants.handleError(FastConstants.NO_DEFAULT_VALUE, "");

        return null;
    }

    protected ScalarValue getEmptyValue(ScalarValue priorValue) {
        return priorValue;
    }

    public ScalarValue decodeValue(ScalarValue newValue,
        ScalarValue priorValue, Scalar field) {
        return newValue;
    }
}
