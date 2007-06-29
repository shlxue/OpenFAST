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


package org.openfast.template;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.codec.FastDecoder;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.openfast.test.TestUtil;


public class GroupTest extends OpenFastTestCase {
    private Group template;
    private Context context;

    protected void setUp() throws Exception {
        template = new MessageTemplate("", new Field[] {  });
        context = new Context();
    }

    public void testEncode() {
        Scalar firstName = new Scalar("First Name", Type.U32,
                Operator.COPY, ScalarValue.UNDEFINED, true);
        Scalar lastName = new Scalar("Last Name", Type.U32,
                Operator.NONE, ScalarValue.UNDEFINED, false);

        Group theGroup = new Group("guy", new Field[] { firstName, lastName },
                false);

        byte[] actual = theGroup.encode(new GroupValue(
                    new Group("", new Field[] {  }, false),
                    new FieldValue[] { new IntegerValue(1), new IntegerValue(2) }),
                template, context);

        String expected = "11000000 10000010 10000010";

        TestUtil.assertBitVectorEquals(expected, actual);
    }

    public void testDecode() {
        String message = "11000000 10000010 10000010";
        InputStream in = new ByteArrayInputStream(ByteUtil.convertBitStringToFastByteArray(
                    message));
        Scalar firstname = new Scalar("firstName", Type.U32,
                Operator.COPY, ScalarValue.UNDEFINED, true);
        Scalar lastName = new Scalar("lastName", Type.U32,
                Operator.NONE, ScalarValue.UNDEFINED, false);

        //		MessageInputStream in = new MessageInputStream(new ByteArrayInputStream(message.getBytes()));
        Group group = new Group("person", new Field[] { firstname, lastName },
                false);
        GroupValue groupValue = (GroupValue) group.decode(in, template,
                context, true);
        assertEquals(1, ((IntegerValue) groupValue.getValue(0)).value);
        assertEquals(2, ((IntegerValue) groupValue.getValue(1)).value);
    }
    
    public void testGroupWithoutPresenceMap() {
    	MessageTemplate template = template(
    			"<template>" +
    			"  <group name=\"priceGroup\" presence=\"optional\">" +
    			"    <decimal name=\"price\"><delta/></decimal>" +
    			"  </group>" +
    			"</template>");
    	Context encodingContext = new Context();
    	Context decodingContext = new Context();
    	encodingContext.registerTemplate(1, template);
    	decodingContext.registerTemplate(1, template);
    	
    	String encodedBits = "11100000 10000001 11111110 10111111";
    	
    	FastDecoder decoder = new FastDecoder(decodingContext, stream(encodedBits));
		Message message = decoder.readMessage();
    	assertEquals(0.63, message.getGroup("priceGroup").getDouble("price"), 0.01);
    	
    	byte[] encoding = template.encode(message, encodingContext);
    	assertEquals(encodedBits, encoding);
    }
}
