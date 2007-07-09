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

import java.io.InputStream;

import org.openfast.BitVector;
import org.openfast.BitVectorValue;
import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.type.Type;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.test.OpenFastTestCase;
import org.openfast.test.TestUtil;


public class TypeTest extends OpenFastTestCase {
	public void testGetType() {
		assertEquals(Type.U32, Type.getType("uInt32"));
		try {
			Type.getType("u32");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The type named u32 does not exist.  Existing types are {uInt8,uInt16,uInt32,uInt64,int8,int16,int32,int64,string,ascii,unicode,byte,decimal,date}", e.getMessage());
		}
	}
	
	public void testIncompatibleDefaultValue() {
		try {
			template(
					"<template>" +
					"  <decimal><copy value=\"10a\"/></decimal>" +
					"</template>");
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.S3_INITIAL_VALUE_INCOMP, e.getCode());
			assertEquals("The value \"10a\" is not compatible with type decimal", e.getMessage());
		}
	}
	
    /*** SIGNED INTEGER ***/
    public void testSignedIntegerEncoding() {
        TestUtil.assertBitVectorEquals("10111111",
            TypeCodec.INTEGER.encode(new IntegerValue(63)));
        TestUtil.assertBitVectorEquals("00000000 11000000",
            TypeCodec.INTEGER.encode(new IntegerValue(64)));
        TestUtil.assertBitVectorEquals("11111111",
            TypeCodec.INTEGER.encode(new IntegerValue(-1)));
        TestUtil.assertBitVectorEquals("11111110",
            TypeCodec.INTEGER.encode(new IntegerValue(-2)));
        TestUtil.assertBitVectorEquals("11000000",
            TypeCodec.INTEGER.encode(new IntegerValue(-64)));
        TestUtil.assertBitVectorEquals("01111111 10111111",
            TypeCodec.INTEGER.encode(new IntegerValue(-65)));
        //newly added tests
        TestUtil.assertBitVectorEquals("00000100 11111111",
            TypeCodec.INTEGER.encode(new IntegerValue(639)));
        TestUtil.assertBitVectorEquals("00111001 01000101 10100011",
            TypeCodec.INTEGER.encode(new IntegerValue(942755)));
        TestUtil.assertBitVectorEquals("01000110 00111010 11011101",
            TypeCodec.INTEGER.encode(new IntegerValue(-942755)));
        TestUtil.assertBitVectorEquals("01111100 00011011 00011011 10011101",
            TypeCodec.INTEGER.encode(new IntegerValue(-7942755)));
        TestUtil.assertBitVectorEquals("00000000 01000000 10000001",
            TypeCodec.INTEGER.encode(new IntegerValue(8193)));
        TestUtil.assertBitVectorEquals("01111111 00111111 11111111",
            TypeCodec.INTEGER.encode(new IntegerValue(-8193)));
        assertEquals(false, TypeCodec.INTEGER.isNullable());
    }

    public void testSignedIntegerDecoding() {
        InputStream in = ByteUtil.createByteStream("10111111");
        assertEquals(63, ((IntegerValue) TypeCodec.INTEGER.decode(in)).value);

        in = ByteUtil.createByteStream("00000000 11000000");
        assertEquals(64, ((IntegerValue) TypeCodec.INTEGER.decode(in)).value);

        in = ByteUtil.createByteStream("11111111");
        assertEquals(-1, ((IntegerValue) TypeCodec.INTEGER.decode(in)).value);

        in = ByteUtil.createByteStream("11111110");
        assertEquals(-2, ((IntegerValue) TypeCodec.INTEGER.decode(in)).value);

        in = ByteUtil.createByteStream("11000000");
        assertEquals(-64, ((IntegerValue) TypeCodec.INTEGER.decode(in)).value);

        in = ByteUtil.createByteStream("01111111 10111111");
        assertEquals(-65, ((IntegerValue) TypeCodec.INTEGER.decode(in)).value);
    }

    /*** SIGNED INTEGER WITH NULL SUPPORT ***/
    public void testSignedIntegerWithNullSupportEncoding() {
        //TestUtil.assertBitVectorEquals("10000000", Type.NULLABLE_INTEGER.encode(IntegerValue.NULL));
        TestUtil.assertBitVectorEquals("10000001",
            TypeCodec.NULLABLE_INTEGER.encode(new IntegerValue(0)));
        TestUtil.assertBitVectorEquals("00000100 11111111",
            TypeCodec.NULLABLE_INTEGER.encode(new IntegerValue(638)));
        // TODO - Support long values
        //TestUtil.assertBitVectorEquals("00001000 00000000 00000000 00000000 10000000", Type.NULLABLE_SIGNED_INTEGER.encode(new IntegerValue(2147483647)));
        TestUtil.assertBitVectorEquals("01111000 00000000 00000000 00000000 10000000",
            TypeCodec.NULLABLE_INTEGER.encode(new IntegerValue(-2147483648)));
        //newly added tests
        TestUtil.assertBitVectorEquals("11101111",
            TypeCodec.NULLABLE_INTEGER.encode(new IntegerValue(-17)));
        TestUtil.assertBitVectorEquals("00000100 10100100",
            TypeCodec.NULLABLE_INTEGER.encode(new IntegerValue(547)));
        TestUtil.assertBitVectorEquals("11111011",
            TypeCodec.NULLABLE_INTEGER.encode(new IntegerValue(-5)));
        assertEquals(true, TypeCodec.NULLABLE_INTEGER.isNullable());
    }

    public void testSignedIntegerWithNullSupportDecoding() {
        InputStream in = ByteUtil.createByteStream("10000000");
        ScalarValue decodedValue = TypeCodec.NULLABLE_INTEGER.decode(in);
        assertNull(decodedValue);

        in = ByteUtil.createByteStream("10000001");
        decodedValue = ((IntegerValue) TypeCodec.NULLABLE_INTEGER.decode(in));
        assertFalse(decodedValue.isNull());
        assertEquals(new IntegerValue(0), decodedValue);

        in = ByteUtil.createByteStream("00000100 11111111");
        decodedValue = ((IntegerValue) TypeCodec.NULLABLE_INTEGER.decode(in));
        assertFalse(decodedValue.isNull());
        assertEquals(new IntegerValue(638), decodedValue);

        // TODO - Support long values
        //		in = ByteUtil.createByteStream("00001000 00000000 00000000 00000000 10000000");
        //		decodedValue = ((IntegerValue)Type.NULLABLE_SIGNED_INTEGER.decode(in));
        //		assertFalse(decodedValue.isNull());
        //		assertEquals(new IntegerValue(2147483647), decodedValue);
        in = ByteUtil.createByteStream(
                "01111000 00000000 00000000 00000000 10000000");
        decodedValue = ((IntegerValue) TypeCodec.NULLABLE_INTEGER.decode(in));
        assertFalse(decodedValue.isNull());
        assertEquals(new IntegerValue(-2147483648), decodedValue);
    }

    /*** UNSIGNED INTEGER ***/
    public void testUnsignedIntegerEncoding() {
        TestUtil.assertBitVectorEquals("11111111",
            TypeCodec.UINT.encode(new IntegerValue(127)));
        TestUtil.assertBitVectorEquals("01111111 11111111",
            TypeCodec.UINT.encode(new IntegerValue(16383)));
        TestUtil.assertBitVectorEquals("10000101",
            TypeCodec.UINT.encode(new IntegerValue(5)));
        //		newly added tests
        TestUtil.assertBitVectorEquals("10000000",
            TypeCodec.UINT.encode(new IntegerValue(0)));
        TestUtil.assertBitVectorEquals("00111001 01000101 10100011",
            TypeCodec.UINT.encode(new IntegerValue(942755)));
        TestUtil.assertBitVectorEquals("01111111 01111111 01111111 11111100",
            TypeCodec.UINT.encode(new IntegerValue(268435452)));
        TestUtil.assertBitVectorEquals("00000001 00000000 00111101 00000100 10111100",
            TypeCodec.UINT.encode(new IntegerValue(269435452)));
        TestUtil.assertBitVectorEquals("00000111 01111111 01111111 01111111 01111111 11111111",
            TypeCodec.UINT.encode(new LongValue(274877906943L)));
        TestUtil.assertBitVectorEquals("00100010 00101111 01011111 01011101 01111100 10100000",
            TypeCodec.UINT.encode(new LongValue(1181048340000L)));
    }

    public void testUnsignedIntegerDecoding() {
        InputStream in = ByteUtil.createByteStream("11111111");
        assertEquals(127, ((IntegerValue) TypeCodec.UINT.decode(in)).value);

        in = ByteUtil.createByteStream("01111111 11111111");
        assertEquals(16383, ((IntegerValue) TypeCodec.UINT.decode(in)).value);

        in = ByteUtil.createByteStream("11111111 11111111");
        assertEquals(127, ((IntegerValue) TypeCodec.UINT.decode(in)).value);

        in = ByteUtil.createByteStream(
                "10000010 10000001 10000010 10000011 10000100");
        assertEquals(2, ((IntegerValue) TypeCodec.UINT.decode(in)).value);

        in = ByteUtil.createByteStream(
                "00000111 01111111 01111111 01111111 01111111 11111111");
        assertEquals(274877906943L, ((LongValue) TypeCodec.UINT.decode(in)).value);

        in = ByteUtil.createByteStream(
                "00100010 00101111 01011111 01011101 01111100 10100000");
        assertEquals(1181048340000L, ((LongValue) TypeCodec.UINT.decode(in)).value);
    }

    /*** NULLABLE UNSIGNED INTEGER ***/
    public void testNullableUnsignedIntegerEncoding() {
        TestUtil.assertBitVectorEquals("10000000",
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(IntegerValue.NULL));
        TestUtil.assertBitVectorEquals("11111111",
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(126)));
        TestUtil.assertBitVectorEquals("01111111 11111111",
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(16382)));
        // TODO - Support long values
        //TestUtil.assertBitVectorEquals("00010000 00000000 00000000 00000000 10000000", Type.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(2147483647)));
        //newly added tests
        TestUtil.assertBitVectorEquals("10001000",
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(7)));
        TestUtil.assertBitVectorEquals("10000001",
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(0)));
        TestUtil.assertBitVectorEquals("10000010",
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(1)));
        TestUtil.assertBitVectorEquals("00111001 01000101 10100100",
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue(942755)));
    }

    public void testNullableUnsignedIntegerDecoding() {
        assertEquals(null,
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.decode(ByteUtil.createByteStream(
                    "10000000")));
        assertEquals(new IntegerValue(126),
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.decode(ByteUtil.createByteStream(
                    "11111111")));
        assertEquals(new IntegerValue(16382),
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.decode(ByteUtil.createByteStream(
                    "01111111 11111111")));
        assertEquals(new IntegerValue(126),
            TypeCodec.NULLABLE_UNSIGNED_INTEGER.decode(ByteUtil.createByteStream(
                    "11111111")));

        // TODO - Support long values
        // TestUtil.assertBitVectorEquals("00010000 00000000 00000000 00000000 10000000", Type.NULLABLE_UNSIGNED_INTEGER.encode(new IntegerValue (2147483647)));
    }

    /*** SINGLE FIELD SCALED NUMBER ***/
    public void testSingleFieldScaledNumberEncoding() {
        TestUtil.assertBitVectorEquals("10000000",
            TypeCodec.NULLABLE_SF_SCALED_NUMBER.encode(ScalarValue.NULL));
        TestUtil.assertBitVectorEquals("10000000 10000100",
            TypeCodec.SF_SCALED_NUMBER.encode(new DecimalValue(4)));
        TestUtil.assertBitVectorEquals("10000010 10000100",
            TypeCodec.SF_SCALED_NUMBER.encode(new DecimalValue(400)));
        TestUtil.assertBitVectorEquals("11111111 10000100",
            TypeCodec.SF_SCALED_NUMBER.encode(new DecimalValue(0.4)));
        TestUtil.assertBitVectorEquals("10000011 11111111",
            TypeCodec.SF_SCALED_NUMBER.encode(new DecimalValue(-1000)));

        // From FAST Specification 1.x.1 Appendix 3.2.5:
        TestUtil.assertBitVectorEquals("11111110 00111001 01000101 10100011",
            TypeCodec.SF_SCALED_NUMBER.encode(new DecimalValue(9427.55)));

        //newly added tests
        TestUtil.assertBitVectorEquals("10000000 10000101",
            TypeCodec.SF_SCALED_NUMBER.encode(new DecimalValue(5)));
        TestUtil.assertBitVectorEquals("10000001 10000101",
            TypeCodec.SF_SCALED_NUMBER.encode(new DecimalValue(50)));
    }

    /*** TWIN FIELD SCALED NUMBER ***/
    public void testTwinFieldScaledNumberEncoding() {
        TestUtil.assertBitVectorEquals("10000000 10000100",
            TypeCodec.TF_SCALED_NUMBER.encode(twin(i(0), i(4))));
        TestUtil.assertBitVectorEquals("10000010 10000100",
            TypeCodec.TF_SCALED_NUMBER.encode(twin(i(2), i(4))));
        TestUtil.assertBitVectorEquals("11111111 10000100",
            TypeCodec.TF_SCALED_NUMBER.encode(twin(i(-1), i(4))));
        TestUtil.assertBitVectorEquals("10000011 11111111",
            TypeCodec.TF_SCALED_NUMBER.encode(twin(i(3), i(-1))));
        //newly added tests
        TestUtil.assertBitVectorEquals("10000000 00000101 10111110",
            TypeCodec.TF_SCALED_NUMBER.encode(twin(i(0), i(702))));
        TestUtil.assertBitVectorEquals("10000001 00000101 10111110",
            TypeCodec.TF_SCALED_NUMBER.encode(twin(i(1), i(702))));
        TestUtil.assertBitVectorEquals("10000000 11001011",
            TypeCodec.TF_SCALED_NUMBER.encode(twin(i(0), i(-53))));
    }

    public void testTwinFieldScaledNumberDecoding() {
        assertEquals(twin(i(0), i(4)),
            TypeCodec.TF_SCALED_NUMBER.decode(ByteUtil.createByteStream(
                    "10000000 10000100")));
        assertEquals(twin(i(2), i(4)),
            TypeCodec.TF_SCALED_NUMBER.decode(ByteUtil.createByteStream(
                    "10000010 10000100")));
        assertEquals(twin(i(-1), i(4)),
            TypeCodec.TF_SCALED_NUMBER.decode(ByteUtil.createByteStream(
                    "11111111 10000100")));
        assertEquals(twin(i(3), i(-1)),
            TypeCodec.TF_SCALED_NUMBER.decode(ByteUtil.createByteStream(
                    "10000011 11111111")));
    }

    /*** STRING ***/
    public void testStringEncoding() {
        byte[] encoding = TypeCodec.ASCII.encode(new StringValue("hey"));
        TestUtil.assertBitVectorEquals("01101000 01100101 11111001", encoding);
        //newly dded tests
        encoding = TypeCodec.ASCII.encode(new StringValue("lasalle"));
        TestUtil.assertBitVectorEquals("01101100 01100001 01110011 01100001 01101100 01101100 11100101",
            encoding);
        encoding = TypeCodec.ASCII.encode(new StringValue("z"));
        TestUtil.assertBitVectorEquals("11111010", encoding);
        encoding = TypeCodec.ASCII.encode(new StringValue("ABC"));
        TestUtil.assertBitVectorEquals("01000001 01000010 11000011", encoding);
    }

    public void testStringDecoding() {
        InputStream in = ByteUtil.createByteStream("01101000 01100101 11111001");
        assertEquals("hey", ((StringValue) TypeCodec.ASCII.decode(in)).value);
    }

    /*** BIT VECTOR ***/
    public void testBitVectorEncoding() {
        BitVector bitVector = new BitVector(14);
        bitVector.set(2);
        bitVector.set(4);
        bitVector.set(6);
        bitVector.set(8);
        bitVector.set(10);

        byte[] encoding = TypeCodec.BIT_VECTOR.encode(new BitVectorValue(bitVector));
        TestUtil.assertBitVectorEquals("00010101 10101000", encoding);
        //newly added tests
        bitVector.set(0);
        encoding = TypeCodec.BIT_VECTOR.encode(new BitVectorValue(bitVector));
        TestUtil.assertBitVectorEquals("01010101 10101000", encoding);
        //below two values are equal?
        bitVector.set(1);
        encoding = TypeCodec.BIT_VECTOR.encode(new BitVectorValue(bitVector));
        TestUtil.assertBitVectorEquals("01110101 10101000", encoding);
        bitVector.set(2);
        encoding = TypeCodec.BIT_VECTOR.encode(new BitVectorValue(bitVector));
        TestUtil.assertBitVectorEquals("01110101 10101000", encoding);
    }

    public void testBitVectorDecoding() {
        BitVector vector = new BitVector(ByteUtil.convertBitStringToFastByteArray(
                    "11010101"));
        InputStream in = ByteUtil.createByteStream("11010101");
        assertEquals(vector, ((BitVectorValue) TypeCodec.BIT_VECTOR.decode(in)).value);
    }

    /*** BYTE VECTOR ***/
    public void testByteVectorEncoding() {
        byte[] vector = new byte[] { 0x01, 0x02, 0x04, 0x08, 0x10 };
        byte[] encoding = TypeCodec.BYTE_VECTOR_TYPE.encode(new ByteVectorValue(
                    vector));
        TestUtil.assertBitVectorEquals("10000101 00000001 00000010 00000100 00001000 00010000",
            encoding);

        //newly added tests
        byte[] vector1 = new byte[] { 0x16, 0x32, 0x64, 0x0f };
        encoding = TypeCodec.BYTE_VECTOR_TYPE.encode(new ByteVectorValue(vector1));
        TestUtil.assertBitVectorEquals("10000100 00010110 00110010 01100100 00001111",
            encoding);

        byte[] vector2 = new byte[] { 0x57, 0x4e };
        encoding = TypeCodec.BYTE_VECTOR_TYPE.encode(new ByteVectorValue(vector2));
        TestUtil.assertBitVectorEquals("10000010 01010111 01001110", encoding);
    }

    public void testByteVectorDecoding() {
        byte[] vector = new byte[] { 0x01, 0x02, 0x04, 0x08, 0x10 };
        InputStream in = ByteUtil.createByteStream(
                "10000101 00000001 00000010 00000100 00001000 00010000");
        TestUtil.assertByteArrayEquals(vector,
            ((ByteVectorValue) TypeCodec.BYTE_VECTOR_TYPE.decode(in)).value);
    }
}
