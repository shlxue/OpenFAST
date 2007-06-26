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


/**
 *
 */
package org.openfast.template.operator;

import org.openfast.DecimalValue;
import org.openfast.ScalarValue;

import org.openfast.template.Scalar;
import org.openfast.template.type.Type;


final class DeltaDecimalOperator extends AlwaysPresentOperator {
    DeltaDecimalOperator() {
        super(DELTA, new Integer[] { Type.DECIMAL });
    }

    public ScalarValue getValueToEncode(ScalarValue val, ScalarValue priorVal,
        Scalar field) {
        if (priorVal == null) {
            throw new IllegalStateException(Operator.ERR_D9);
        }

        if (val == null) {
            if (field.isOptional()) {
                return ScalarValue.NULL;
            } else {
                throw new IllegalArgumentException(
                    "Mandatory fields can't be null.");
            }
        }

        if (priorVal.isUndefined() && field.getDefaultValue().isUndefined()) {
            return val;
        }

        DecimalValue priorValue = priorVal.isUndefined()
            ? (DecimalValue) field.getDefaultValue() : (DecimalValue) priorVal;
        DecimalValue value = (DecimalValue) val;

        return new DecimalValue(value.mantissa - priorValue.mantissa,
            value.exponent - priorValue.exponent);
    }

    public ScalarValue decodeValue(ScalarValue val, ScalarValue priorVal,
        Scalar field) {
        if (priorVal == null) {
            throw new IllegalStateException(Operator.ERR_D9);
        }

        if (val == null) {
            return null;
        }

        DecimalValue priorValue = null;

        if (priorVal.isUndefined()) {
            if (field.getDefaultValue().isUndefined()) {
                priorValue = (DecimalValue) field.getInitialValue();
            } else if (val == null) {
                if (field.isOptional()) {
                    return ScalarValue.NULL;
                } else {
                    throw new IllegalStateException("Field cannot be null.");
                }
            } else {
                priorValue = (DecimalValue) field.getDefaultValue();
            }
        } else {
            priorValue = (DecimalValue) priorVal;
        }

        DecimalValue value = (DecimalValue) val;

        return new DecimalValue(value.mantissa + priorValue.mantissa,
            value.exponent + priorValue.exponent);
    }

    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        if (field.getDefaultValue().isUndefined()) {
            if (field.isOptional()) {
                return ScalarValue.NULL;
            } else if (previousValue.isUndefined()) {
                throw new IllegalStateException(
                    "Mandatory fields without a previous value or default value must be present.");
            } else {
                return previousValue;
            }
        } else {
            return field.getDefaultValue();
        }
    }
}
