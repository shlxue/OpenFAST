package org.openfast.template.type;

import org.openfast.test.OpenFastTestCase;

public class NullableByteVectorTypeTest extends OpenFastTestCase {

	public void testEncoding() {
		assertEncodeDecode(null, "10000000", Type.NULLABLE_BYTE_VECTOR_TYPE);
		assertEncodeDecode(byt(new byte[] { 0x00 }), "10000010 00000000", Type.NULLABLE_BYTE_VECTOR_TYPE);
		assertEncodeDecode(byt(new byte[] { 0x00, 0x7F }), "10000011 00000000 01111111", Type.NULLABLE_BYTE_VECTOR_TYPE);
	}
	
}
