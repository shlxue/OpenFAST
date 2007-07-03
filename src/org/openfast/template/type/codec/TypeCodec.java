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


package org.openfast.template.type.codec;

import java.io.InputStream;

import org.openfast.ScalarValue;


public abstract class TypeCodec {
    protected static final byte STOP_BIT = (byte) 0x80;
    static final byte[] NULL_VALUE_ENCODING = new byte[] { STOP_BIT };

    // Type Definitions
    public static final TypeCodec UINT = new UnsignedInteger();
    public static final TypeCodec INTEGER = new SignedInteger();
    public static final TypeCodec ASCII = new AsciiString();
    public static final TypeCodec UNICODE = new UnicodeString();
    public static final TypeCodec BIT_VECTOR = new BitVectorType();
    public static final TypeCodec BYTE_VECTOR_TYPE = new ByteVectorType();
    public static final TypeCodec SF_SCALED_NUMBER = new SingleFieldDecimal();
    public static final TypeCodec TF_SCALED_NUMBER = new TwinFieldDecimal();
    public static final TypeCodec STRING_DELTA = new StringDelta();
    
    public static final TypeCodec NULLABLE_UNSIGNED_INTEGER = new NullableUnsignedInteger();
    public static final TypeCodec NULLABLE_INTEGER = new NullableSignedInteger();
    public static final TypeCodec NULLABLE_ASCII = new NullableAsciiString();
    public static final TypeCodec NULLABLE_UNICODE = new NullableUnicodeString();
    public static final TypeCodec NULLABLE_BYTE_VECTOR_TYPE = new NullableByteVector();
    public static final TypeCodec NULLABLE_SF_SCALED_NUMBER = new NullableSingleFieldDecimal();
    public static final TypeCodec NULLABLE_STRING_DELTA = new NullableStringDelta();
    
    public abstract byte[] encodeValue(ScalarValue value);
    public abstract ScalarValue decode(InputStream in);

    /**
     * Encode the passed object
     * @param value The ScalarValue object to be encoded
     * @return Returns an encoded byte array with an added stop bit at the end
     */
    public byte[] encode(ScalarValue value) {
        byte[] encoding = encodeValue(value);
        encoding[encoding.length - 1] |= 0x80; // add stop bit;
        return encoding;
    }


    /**
     * 
     * @return Returns false
     */
    public boolean isNullable() {
    	return false;
    }

}
