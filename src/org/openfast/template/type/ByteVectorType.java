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

import org.openfast.ByteVectorValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;


final class ByteVectorType extends Type {
    ByteVectorType() { }

    public byte[] encode(ScalarValue value) {
        byte[] bytes = value.getBytes();
        int lengthSize = IntegerType.getUnsignedIntegerSize(bytes.length);
        byte[] encoding = new byte[bytes.length + lengthSize];
        byte[] length = Type.UINT.encode(new IntegerValue(
        		bytes.length));
        System.arraycopy(length, 0, encoding, 0, lengthSize);
        System.arraycopy(bytes, 0, encoding, lengthSize,
        		bytes.length);

        return encoding;
    }

    public ScalarValue decode(InputStream in) {
        int length = ((IntegerValue) Type.UINT.decode(in)).value;
        byte[] encoding = new byte[length];

        for (int i = 0; i < length; i++)
            try {
                encoding[i] = (byte) in.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        return new ByteVectorValue(encoding);
    }

    public byte[] encodeValue(ScalarValue value) {
        throw new NotImplementedException();
    }

    public ScalarValue fromString(String value) {
        return new ByteVectorValue(value.getBytes());
    }

    public ScalarValue getDefaultValue() {
        return new ByteVectorValue(new byte[] {  });
    }
}
