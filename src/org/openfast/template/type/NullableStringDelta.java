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


package org.openfast.template.type;

import org.openfast.ScalarValue;
import org.openfast.StringValue;

import org.openfast.template.TwinValue;

import java.io.InputStream;


public class NullableStringDelta extends TypeCodec {
    public NullableStringDelta() { }

    public ScalarValue decode(InputStream in) {
        ScalarValue subtractionLength = TypeCodec.NULLABLE_INTEGER.decode(in);
        if (subtractionLength == null) return null;

        ScalarValue difference = TypeCodec.ASCII.decode(in);

        return new TwinValue(subtractionLength, difference);
    }

    public byte[] encodeValue(ScalarValue value) {
        if (value.isNull()) return TypeCodec.NULL_VALUE_ENCODING;
        
        TwinValue diff = (TwinValue) value;
        byte[] subtractionLength = TypeCodec.NULLABLE_INTEGER.encode(diff.first);
        byte[] difference = TypeCodec.ASCII.encode(diff.second);
        byte[] encoded = new byte[subtractionLength.length + difference.length];
        System.arraycopy(subtractionLength, 0, encoded, 0,
            subtractionLength.length);
        System.arraycopy(difference, 0, encoded, subtractionLength.length,
            difference.length);

        return encoded;
    }

    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }

    public ScalarValue fromString(String value) {
        return new StringValue(value);
    }
    
    public boolean isNullable() {
    	return true;
    }
}
