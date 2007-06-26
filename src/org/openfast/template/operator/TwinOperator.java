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

import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;

import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;


public class TwinOperator extends Operator {
    private Operator exponentOperator;
    private Operator mantissaOperator;

    public TwinOperator(String exponentOperator, String mantissaOperator) {
        super("twin", new Integer[] { Type.DECIMAL });
        this.exponentOperator = Operator.getOperator(exponentOperator,
                Type.SIGNED_INTEGER);
        this.mantissaOperator = Operator.getOperator(mantissaOperator,
                Type.SIGNED_INTEGER);
    }

    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        return null;
    }

    public ScalarValue decodeValue(ScalarValue val, ScalarValue priorVal,
        Scalar field) {
        if ((val == ScalarValue.NULL) || (val == null)) {
            return null;
        }

        TwinValue priorValue;

        if (priorVal == ScalarValue.UNDEFINED) {
            priorValue = new TwinValue(new IntegerValue(0), new IntegerValue(0));
        } else {
            priorValue = (TwinValue) priorVal;
        }

        TwinValue value = (TwinValue) val;
        IntegerValue exponent = (value.first == null)
            ? (IntegerValue) exponentOperator.decodeEmptyValue(priorValue.first,
                field)
            : (IntegerValue) exponentOperator.decodeValue(value.first,
                priorValue.first, field);
        IntegerValue mantissa = (value.second == null)
            ? (IntegerValue) mantissaOperator.decodeEmptyValue(priorValue.second,
                field)
            : (IntegerValue) mantissaOperator.decodeValue(value.second,
                priorValue.second, field);

        return new DecimalValue(mantissa.value, exponent.value);
    }

    public ScalarValue getValueToEncode(ScalarValue val, ScalarValue priorVal,
        Scalar field) {
        if (priorVal == null) {
            throw new IllegalStateException(ERR_D9);
        }

        if (val == null) {
            if (field.isOptional()) {
                return ScalarValue.NULL;
            } else {
                throw new IllegalArgumentException(
                    "Mandatory fields can't be null.");
            }
        }

        DecimalValue value = (DecimalValue) val;

        if (priorVal.isUndefined() && field.getDefaultValue().isUndefined()) {
            return new TwinValue(new IntegerValue(value.exponent),
                new IntegerValue(value.mantissa));
        }

        TwinValue priorValue = priorVal.isUndefined()
            ? (TwinValue) field.getDefaultValue() : (TwinValue) priorVal;

        if (priorValue.equals(val)) {
            return null;
        }

        ScalarValue exponentValue = exponentOperator.getValueToEncode(new IntegerValue(
                    value.exponent), priorValue.first, field);
        ScalarValue mantissaValue = mantissaOperator.getValueToEncode(new IntegerValue(
                    value.mantissa), priorValue.second, field);

        return new TwinValue(exponentValue, mantissaValue);
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof TwinOperator)) {
            return false;
        }

        return equals((TwinOperator) obj);
    }

    private boolean equals(TwinOperator other) {
        return exponentOperator.equals(other.exponentOperator) &&
        mantissaOperator.equals(other.mantissaOperator);
    }
}
