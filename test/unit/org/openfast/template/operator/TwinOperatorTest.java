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


package org.openfast.template.operator;

import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.IntegerValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;


public class TwinOperatorTest extends OpenFastTestCase {
    private OperatorCodec operator;
    private Scalar field;

    protected void setUp() throws Exception {
        operator = new TwinOperatorCodec(Operator.COPY, Operator.COPY);
        field = new Scalar(new QName(""), Type.DECIMAL, operator, ScalarValue.UNDEFINED, true);
    }

    public void testGetValueToEncode() {
        TwinValue value = (TwinValue) operator.getValueToEncode(d(9427.55),
                ScalarValue.UNDEFINED, field, new BitVectorBuilder(2));
        assertEquals(-2, ((IntegerValue) value.first).value);
        assertEquals(942755, ((IntegerValue) value.second).value);

        value = (TwinValue) operator.getValueToEncode(d(9427.61),
                d(9427.55), field, new BitVectorBuilder(2));
        assertEquals(null, value.first);
        assertEquals(942761, ((IntegerValue) value.second).value);
    }

    public void testDecodeValue() {
        assertEquals(d(9427.55),
            operator.decodeValue(twin(i(-2), i(942755)), ScalarValue.UNDEFINED,
                field));
        assertEquals(d(9427.61),
            operator.decodeValue(twin(null, i(942761)), twin(i(-2), i(942755)),
                field));
        assertEquals(null,
            operator.decodeValue(null, twin(i(-2), i(942761)), field));
    }
    
    public void testAttemptToEncodeUnencodeableValue() {
    	Scalar scalar = new Scalar(new QName("price"), Type.DECIMAL, new TwinOperatorCodec(Operator.CONSTANT, Operator.COPY), twin(i(-2), ScalarValue.UNDEFINED), false);
    	MessageTemplate template = new MessageTemplate("quote", new Field[] { scalar });
    	try {
			scalar.encode(d(100.535), template, new Context(), new BitVectorBuilder(5));
    	} catch (FastException e) {
    		assertEquals(FastConstants.D3_CANT_ENCODE_VALUE, e.getCode());
    	}
    }
}
