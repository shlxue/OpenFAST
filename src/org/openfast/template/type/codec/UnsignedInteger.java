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

import java.io.IOException;
import java.io.InputStream;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;


public final class UnsignedInteger extends IntegerCodec {
    UnsignedInteger() { }

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * @param scalarValue The value to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue scalarValue) {
        long value;

        if (scalarValue instanceof IntegerValue) {
            value = scalarValue.toLong();
        } else {
            value = scalarValue.toLong();
        }

        int size = getUnsignedIntegerSize(value);
        byte[] encoded = new byte[size];

        for (int factor = 0; factor < size; factor++) {
            encoded[size - factor - 1] = (byte) ((value >> (factor * 7)) &
                0x7f);
        }

        return encoded;
    }

    /**
     * 
     * @param in The InputStream to be decoded
     * @return the decoded value from the fast input stream
     */
    public ScalarValue decode(InputStream in) {
        long value = 0;
        int byt;

        try {
            do {
                byt = in.read();
                value = (value << 7) | (byt & 0x7f);
            } while ((byt & 0x80) == 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return createValue(value);
    }
    
    public boolean equals(Object obj) {
    	return obj != null && getClass() == obj.getClass();
    }
}
