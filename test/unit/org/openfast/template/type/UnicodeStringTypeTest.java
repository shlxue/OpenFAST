package org.openfast.template.type;

import org.openfast.test.OpenFastTestCase;

public class UnicodeStringTypeTest extends OpenFastTestCase {

	public void testEncodeValue() throws Exception {
		assertEncodeDecode(string("Yo"), "10000010 01011001 01101111", Type.UNICODE_STRING_TYPE);
		assertEncodeDecode(string("\u00f1"), "10000010 11000011 10110001", Type.UNICODE_STRING_TYPE);
		assertEncodeDecode(string("A\u00ea\u00f1\u00fcC"), "10001000 01000001 11000011 10101010 11000011 10110001 11000011 10111100 01000011", Type.UNICODE_STRING_TYPE);
	}

}
