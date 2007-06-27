package org.openfast.template.type;

import org.openfast.test.OpenFastTestCase;

public class ByteVectorTypeTest extends OpenFastTestCase {

	public void testEncode() {
		assertEncodeDecode(byt(new byte[] { 0x00 }), "10000001 00000000", Type.BYTE_VECTOR_TYPE);
		assertEncodeDecode(byt(new byte[] { 0x00, 0x7f }), "10000010 00000000 01111111", Type.BYTE_VECTOR_TYPE);
	}

}
