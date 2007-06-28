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

import org.openfast.error.FastConstants;
import org.openfast.template.LongValue;


public class IntegerValue extends NumericValue {
    public final int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof IntegerValue)) {
            return false;
        }

        return equals((IntegerValue) obj);
    }

    private boolean equals(IntegerValue otherValue) {
        return value == otherValue.value;
    }

    public boolean equalsValue(String defaultValue) {
        return Integer.parseInt(defaultValue) == value;
    }

    public NumericValue increment() {
        return new IntegerValue(value + 1);
    }

    public NumericValue decrement() {
        return new IntegerValue(value - 1);
    }

    public NumericValue subtract(NumericValue subend) {
        if (subend instanceof LongValue) {
            return new LongValue(this.value - subend.toLong());
        }

        return new IntegerValue(this.value - subend.toInt());
    }

    public NumericValue add(NumericValue addend) {
        if (addend instanceof LongValue) {
            return addend.add(this);
        }

        return new IntegerValue(this.value + addend.toInt());
    }

    public String serialize() {
        return String.valueOf(value);
    }

    public boolean equals(int value) {
        return value == this.value;
    }

    public long toLong() {
        return value;
    }

    public int toInt() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
    
    public byte toByte() {
    	if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE)
    		FastConstants.handleError(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, "The value \"" + value + "\" is too large for a byte.");
    	return (byte) value;
    }
    
    public short toShort() {
    	if (value > Short.MAX_VALUE || value < Short.MIN_VALUE)
    		FastConstants.handleError(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, "The value \"" + value + "\" is too large for a short.");
    	return (short) value;
    }
    
    public double toDouble() {
    	return value;
    }
    
    public BigDecimal toBigDecimal() {
    	return new BigDecimal(value);
    }
}
