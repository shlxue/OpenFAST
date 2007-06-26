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


package org.openfast.template;

import org.openfast.NumericValue;


public class LongValue extends NumericValue {
    public final long value;

    public LongValue(long value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof LongValue)) {
            return false;
        }

        return equals((LongValue) obj);
    }

    private boolean equals(LongValue otherValue) {
        return value == otherValue.value;
    }

    public boolean equalsValue(String defaultValue) {
        return Integer.parseInt(defaultValue) == value;
    }

    public NumericValue increment() {
        return new LongValue(value + 1);
    }

    public NumericValue decrement() {
        return new LongValue(value - 1);
    }

    public String toString() {
        return String.valueOf(value);
    }

    public NumericValue subtract(NumericValue subend) {
        return new LongValue(this.value - subend.getLong());
    }

    public NumericValue add(NumericValue addend) {
        return new LongValue(this.value + addend.getLong());
    }

    public String serialize() {
        return String.valueOf(value);
    }

    public boolean equals(int value) {
        return value == this.value;
    }

    public long getLong() {
        return value;
    }

    public int getInt() {
        return (int) value;
    }
}
