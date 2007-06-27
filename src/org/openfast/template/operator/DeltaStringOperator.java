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

import org.openfast.ScalarValue;
import org.openfast.StringValue;

import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;

import org.openfast.util.Util;


final class DeltaStringOperator extends AlwaysPresentOperator {
    DeltaStringOperator() {
        super(Operator.DELTA, new String[] { Type.STRING });
    }

    public ScalarValue getValueToEncode(ScalarValue value,
        ScalarValue priorValue, Scalar field) {
        if (value == null) {
            return ScalarValue.NULL;
        }

        if (priorValue == null) {
            throw new IllegalStateException(Operator.ERR_D9);
        }

        ScalarValue base = (priorValue.isUndefined()) ? field.getInitialValue()
                                                      : priorValue;

        return Util.getDifference((StringValue) value, (StringValue) base);
    }

    public ScalarValue decodeValue(ScalarValue newValue,
        ScalarValue previousValue, Scalar field) {
        if ((newValue == null) || newValue.isNull()) {
            return null;
        }

        TwinValue diffValue = (TwinValue) newValue;
        ScalarValue base = (previousValue.isUndefined())
            ? field.getInitialValue() : previousValue;

        return Util.applyDifference((StringValue) base, diffValue);
    }

    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        throw new IllegalStateException(
            "As of FAST v1.1 Delta values must be present in stream");
    }
}
