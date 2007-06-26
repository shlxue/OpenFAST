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

import org.openfast.ScalarValue;
import org.openfast.StringValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


final class NullableStringType extends Type {
    private static final byte[] NULLABLE_EMPTY_STRING = new byte[] { 0x00, 0x00 };

    NullableStringType(Integer type, String name, String[] names,
        boolean nullable) {
        super(type, name, names, nullable);
    }

    public byte[] encodeValue(ScalarValue value) {
        if (value.isNull()) {
            return Type.NULL_VALUE_ENCODING;
        }

        String string = ((StringValue) value).value;

        if ((string != null) && (string.length() == 0)) {
            return NULLABLE_EMPTY_STRING;
        }

        return string.getBytes();
    }

    public ScalarValue decode(InputStream in) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byt;

        try {
            do {
                byt = in.read();
                buffer.write(byt);
            } while ((byt & 0x80) == 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = buffer.toByteArray();
        bytes[bytes.length - 1] &= 0x7f;

        if ((bytes.length == 1) && (bytes[0] == 0)) {
            return ScalarValue.NULL;
        } else if ((bytes.length == 2) && (bytes[0] == 0) && (bytes[1] == 0)) {
            return new StringValue("");
        }

        return new StringValue(new String(bytes));
    }

    public ScalarValue parse(String value) {
        return new StringValue(value);
    }

    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }
}
