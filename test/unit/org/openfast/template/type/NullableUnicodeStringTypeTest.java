package org.openfast.template.type;

import org.openfast.test.OpenFastTestCase;

public class NullableUnicodeStringTypeTest extends OpenFastTestCase {

	public void testEncodeValue() throws Exception {
		assertEncodeDecode(string("Yo"), "10000011 01011001 01101111", TypeCodec.NULLABLE_UNICODE);
		assertEncodeDecode(string("\u00f1"), "10000011 11000011 10110001", TypeCodec.NULLABLE_UNICODE);
		assertEncodeDecode(string("A\u00ea\u00f1\u00fcC"), "10001001 01000001 11000011 10101010 11000011 10110001 11000011 10111100 01000011", TypeCodec.NULLABLE_UNICODE);
		assertEncodeDecode(null, "10000000", TypeCodec.NULLABLE_UNICODE);
	}

}
