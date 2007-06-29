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

import org.openfast.ScalarValue;
import org.openfast.StringValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


final class AsciiString extends TypeCodec {
    AsciiString() { }

    public byte[] encodeValue(ScalarValue value) {
        if ((value == null) || value.isNull()) {
            throw new IllegalStateException(
                "Only nullable strings can represent null values.");
        }

        String string = value.toString();

        if ((string != null) && (string.length() == 0)) {
            return TypeCodec.NULL_VALUE_ENCODING;
        }

        return string.getBytes();
    }

    /**
     * @param 
     * @return
     */
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
            return new StringValue("");
        }

        return new StringValue(new String(bytes));
    }

    /**
     * @return Returns a new StringValue object with the pass value
     */
    public ScalarValue fromString(String value) {
        return new StringValue(value);
    }
}
