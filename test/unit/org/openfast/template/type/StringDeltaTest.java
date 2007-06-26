package org.openfast.template.type;

import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.ByteUtil;
import org.openfast.IntegerValue;
import org.openfast.StringValue;
import org.openfast.TestUtil;
import org.openfast.template.TwinValue;

public class StringDeltaTest extends TestCase {

	public void testEncodeValue() {
		TestUtil.assertBitVectorEquals("10000001 11000001", Type.STRING_DELTA.encodeValue(new TwinValue(new IntegerValue(1), new StringValue("A"))));
	}

	public void testDecode() {
		InputStream in = ByteUtil.createByteStream("10000001 11000001");
		assertEquals(new TwinValue(new IntegerValue(1), new StringValue("A")), Type.STRING_DELTA.decode(in));
		in = ByteUtil.createByteStream("10000000");
	}

}
