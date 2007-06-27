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


package org.openfast.test;

import junit.framework.TestCase;

import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.TestUtil;

import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;

import java.io.InputStream;


public abstract class OpenFastTestCase extends TestCase {
    public static DecimalValue d(double value) {
        return new DecimalValue(value);
    }

    protected static IntegerValue i(int value) {
        return new IntegerValue(value);
    }

    protected static TwinValue twin(ScalarValue first, ScalarValue second) {
        return new TwinValue(first, second);
    }

    protected static void assertEquals(String bitString, byte[] encoding) {
        TestUtil.assertBitVectorEquals(bitString, encoding);
    }

    protected static void assertEncodeDecode(ScalarValue value,
        String bitString, Type type) {
        assertEquals(bitString, type.encode(value == null ? ScalarValue.NULL : value));
        assertEquals(value, type.decode(ByteUtil.createByteStream(bitString)));
    }

    protected static InputStream stream(String bitString) {
        return ByteUtil.createByteStream(bitString);
    }

    protected static ByteVectorValue byt(byte[] value) {
		return new ByteVectorValue(value);
	}

	protected DecimalValue d(int mantissa, int exponent) {
        return new DecimalValue(mantissa, exponent);
    }

	protected ScalarValue string(String value) {
		return new StringValue(value);
	}
}
