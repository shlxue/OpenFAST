package org.openfast.template.type;

import org.openfast.test.OpenFastTestCase;

public class NullableUnicodeStringTypeTest extends OpenFastTestCase {

	public void testEncodeValue() throws Exception {
		assertEncodeDecode(string("Yo"), "10000011 01011001 01101111", Type.NULLABLE_UNICODE_STRING);
		assertEncodeDecode(string("\u00f1"), "10000011 11000011 10110001", Type.NULLABLE_UNICODE_STRING);
		assertEncodeDecode(string("A\u00ea\u00f1\u00fcC"), "10001001 01000001 11000011 10101010 11000011 10110001 11000011 10111100 01000011", Type.NULLABLE_UNICODE_STRING);
		assertEncodeDecode(null, "10000000", Type.NULLABLE_UNICODE_STRING);
	}

}
