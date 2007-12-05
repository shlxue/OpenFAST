package org.openfast.template.operator;

import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TailOperatorCodecTest extends OpenFastTestCase {

	public void testGetValueToEncodeForByteVector() throws Exception {
		Scalar byteVectorField = new Scalar("bv", Type.BYTE_VECTOR, Operator.TAIL, ScalarValue.UNDEFINED, true);
		
		ScalarValue priorValue = new ByteVectorValue(ByteUtil.convertBitStringToFastByteArray("10001000 01001000 10101010 11111111"));
		ScalarValue value = new ByteVectorValue(ByteUtil.convertBitStringToFastByteArray("10001000 01001000 10101010 01010101"));
		
		ScalarValue expected = new ByteVectorValue(ByteUtil.convertBitStringToFastByteArray("01010101"));
		assertEquals(expected, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
	}

	public void testGetValueToEncodeForUnicodeString() throws Exception {
		Scalar byteVectorField = new Scalar("str", Type.UNICODE, Operator.TAIL, ScalarValue.UNDEFINED, true);
		
		ScalarValue priorValue = new StringValue("abcde");
		ScalarValue value = new StringValue("abcce");
		
		ScalarValue expected = new StringValue("ce");
		assertEquals(expected, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
	}

	public void testGetValueToEncodeForAsciiString() throws Exception {
		Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
		
		ScalarValue priorValue = new StringValue("abcde");
		ScalarValue value = new StringValue("abcce");
		
		ScalarValue expected = new StringValue("ce");
		assertEquals(expected, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
	}
	
	public void testGetValueToEncodeAsciiStringTooLong() {
		Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
		
		ScalarValue priorValue = new StringValue("abcde");
		ScalarValue value = new StringValue("dbcdef");
		
		assertEquals(value, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
	}
	
	public void testGetValueToEncodeAsciiStringLengthMismatch() {
		Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
		
		ScalarValue priorValue = new StringValue("abcde");
		ScalarValue value = new StringValue("abcdef");
		
		assertEquals(value, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
	}
	
	public void testGetValueToEncodeAsciiStringSameValue() {
		Scalar byteVectorField = new Scalar("str", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, true);
		
		ScalarValue priorValue = new StringValue("abcde");
		ScalarValue value = new StringValue("abcde");
		
		assertEquals(null, OperatorCodec.TAIL.getValueToEncode(value, priorValue, byteVectorField));
	}
}
