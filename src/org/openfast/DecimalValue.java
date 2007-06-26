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

import java.math.BigDecimal;


public class DecimalValue extends NumericValue {
    public static final DecimalValue NULL = new DecimalValue(0.0) {
            public boolean isNull() {
                return true;
            }

            public String toString() {
                return "DecimalValue [NULL]";
            }

            public NumericValue add(NumericValue value) {
                return this;
            }

            public NumericValue subtract(NumericValue value) {
                return this;
            }
        };

    public final double value;
    public final int exponent;
    public final int mantissa;

    public DecimalValue(double value) {
        if (value == 0.0) {
            this.value = 0.0;
            this.exponent = 0;
            this.mantissa = 0;

            return;
        }

        this.value = value;

        BigDecimal decimalValue = BigDecimal.valueOf(value);
        int exponent = decimalValue.scale();
        int mantissa = decimalValue.unscaledValue().intValue();

        while (((mantissa % 10) == 0) && (mantissa != 0)) {
            mantissa /= 10;
            exponent -= 1;
        }

        this.mantissa = mantissa;
        this.exponent = -exponent;
    }

    public DecimalValue(int mantissa, int exponent) {
        this.mantissa = mantissa;
        this.exponent = exponent;

        if (exponent < 0) {
            this.value = mantissa / Math.pow(10, -exponent);
        } else {
            this.value = mantissa * Math.pow(10, exponent);
        }
    }

    public NumericValue increment() {
        return null;
    }

    public NumericValue decrement() {
        return null;
    }

    public boolean isNull() {
        return false;
    }

    public String toString() {
        return "DecimalValue [" + value + "]";
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof DecimalValue)) {
            return false;
        }

        return equals((DecimalValue) obj);
    }

    public boolean equals(DecimalValue other) {
        return other.value == this.value;
    }

    public NumericValue subtract(NumericValue priorValue) {
        return new DecimalValue(this.value - ((DecimalValue) priorValue).value);
    }

    public NumericValue add(NumericValue addend) {
        return new DecimalValue(((DecimalValue) addend).value + this.value);
    }

    public String serialize() {
        return String.valueOf(value);
    }

    public boolean equals(int value) {
        return (double) value == this.value;
    }

    public long getLong() {
        return (long) value;
    }

    public int getInt() {
        return (int) value;
    }
}
