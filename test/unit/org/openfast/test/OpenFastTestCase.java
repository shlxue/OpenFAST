package org.openfast.test;

import java.io.InputStream;

import org.openfast.ByteUtil;
import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.TestUtil;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;

import junit.framework.TestCase;

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

	protected static void assertEncodeDecode(ScalarValue value, String bitString, Type type) {
		assertEquals(bitString, type.encode(value));
		assertEquals(value, type.decode(ByteUtil.createByteStream(bitString)));
	}

	protected static InputStream stream(String bitString) {
		return ByteUtil.createByteStream(bitString);
	}

	protected DecimalValue d(int mantissa, int exponent) {
		return new DecimalValue(mantissa, exponent);
	}

}
