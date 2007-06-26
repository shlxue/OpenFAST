package org.openfast.template.type;

import java.io.InputStream;

import org.openfast.ByteUtil;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.TestUtil;
import org.openfast.template.TwinValue;

import junit.framework.TestCase;

public class NullableStringDeltaTest extends TestCase {

	public void testEncodeValue() {
		TestUtil.assertBitVectorEquals("10000000", Type.NULLABLE_STRING_DELTA.encodeValue(ScalarValue.NULL));
		TestUtil.assertBitVectorEquals("10000010 11000001", Type.NULLABLE_STRING_DELTA.encodeValue(new TwinValue(new IntegerValue(1), new StringValue("A"))));
		TestUtil.assertBitVectorEquals("11111111 11000001", Type.NULLABLE_STRING_DELTA.encodeValue(new TwinValue(new IntegerValue(-1), new StringValue("A"))));
	}

	public void testDecode() {
		InputStream in = ByteUtil.createByteStream("10000001 11000001");
		assertEquals(new TwinValue(new IntegerValue(0), new StringValue("A")), Type.NULLABLE_STRING_DELTA.decode(in));
		in = ByteUtil.createByteStream("10000010 11000001");
		assertEquals(new TwinValue(new IntegerValue(1), new StringValue("A")), Type.NULLABLE_STRING_DELTA.decode(in));
		in = ByteUtil.createByteStream("11111111 11000001");
		assertEquals(new TwinValue(new IntegerValue(-1), new StringValue("A")), Type.NULLABLE_STRING_DELTA.decode(in));
		in = ByteUtil.createByteStream("10000000");
		assertEquals(null, Type.NULLABLE_STRING_DELTA.decode(in));
	}

}
