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
package org.openfast.template.type;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.openfast.ByteVectorValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;


final class NullableUnicodeString extends NotStopBitEncodedType {
    NullableUnicodeString() { }

    public byte[] encodeValue(ScalarValue value) {
    	if (value.isNull())
    		return Type.NULLABLE_BYTE_VECTOR_TYPE.encodeValue(ScalarValue.NULL);
        try {
			byte[] utf8encoding = ((StringValue) value).value.getBytes("UTF8");
			return Type.NULLABLE_BYTE_VECTOR_TYPE.encode(new ByteVectorValue(utf8encoding));
		} catch (UnsupportedEncodingException e) {
			throw new FastException(FastConstants.IMPOSSIBLE_EXCEPTION, "Apparently Unicode is no longer supported by Java.", e);
		}
    }

    public ScalarValue decode(InputStream in) {
    	ScalarValue decodedValue = Type.NULLABLE_BYTE_VECTOR_TYPE.decode(in);
    	if (decodedValue == null) return null;
		ByteVectorValue value = (ByteVectorValue) decodedValue;
        try {
			return new StringValue(new String(value.value, "UTF8"));
		} catch (UnsupportedEncodingException e) {
			throw new FastException(FastConstants.IMPOSSIBLE_EXCEPTION, "Apparently Unicode is no longer supported by Java.", e);
		}
    }

    public ScalarValue parse(String value) {
        return new StringValue(value);
    }

    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }
}
