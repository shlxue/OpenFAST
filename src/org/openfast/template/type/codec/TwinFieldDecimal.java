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
package org.openfast.template.type.codec;

import java.io.InputStream;

import org.openfast.ByteUtil;
import org.openfast.DecimalValue;
import org.openfast.ScalarValue;
import org.openfast.template.TwinValue;


final class TwinFieldDecimal extends TypeCodec {
    TwinFieldDecimal() { }

    public byte[] encodeValue(ScalarValue v) {
    	if (v.isNull())
    		return NULL_VALUE_ENCODING;
        TwinValue value = (TwinValue) v;
        if (value.first != null && value.second != null)
        	return ByteUtil.combine(TypeCodec.INTEGER.encode(value.first), TypeCodec.INTEGER.encode(value.second));
        if (value.second != null)
        	return TypeCodec.INTEGER.encode(value.second);
        if (value.first != null)
        	return TypeCodec.INTEGER.encode(value.first);
        return new byte[] {};
    }

    public ScalarValue decode(InputStream in) {
        return new TwinValue(TypeCodec.INTEGER.decode(in), TypeCodec.INTEGER.decode(in));
    }

    public ScalarValue fromString(String value) {
        return new DecimalValue(Double.parseDouble(value));
    }

    public ScalarValue getDefaultValue() {
        return new DecimalValue(0.0);
    }
}
