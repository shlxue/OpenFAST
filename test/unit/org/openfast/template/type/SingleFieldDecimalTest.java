package org.openfast.template.type;


import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.test.OpenFastTestCase;

public class SingleFieldDecimalTest extends OpenFastTestCase {

	public void testEncodeDecode() {
		assertEncodeDecode(94275500, "10000010 00111001 01000101 10100011");
		assertEncodeDecode(9427.55,  "11111110 00111001 01000101 10100011");
		assertEncodeDecode(4,  "10000000 10000100");
		assertEncodeDecode(400,  "10000010 10000100");
		assertEncodeDecode(0.4,  "11111111 10000100");
		assertEncodeDecode(1000,  "10000011 10000001");
		assertEncodeDecode(d(9427550, 1), "10000001 00000100 00111111 00110100 11011110", Type.SF_SCALED_NUMBER);
	}
	
	public void testEncodeLargeDecimalReportsError() {
		try {
			Type.SF_SCALED_NUMBER.encode(d(150, 64));
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.LARGE_DECIMAL, e.getCode());
			assertEquals("Encountered exponent of size 64", e.getMessage());
		}
	}
	
	public void testDecodeLargeDecimalReportsError() {
		try {
			Type.SF_SCALED_NUMBER.decode(stream("00000001 11111111 10000001"));
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.LARGE_DECIMAL, e.getCode());
			assertEquals("Encountered exponent of size 255", e.getMessage());
		}
	}

	private void assertEncodeDecode(double value, String bitString) {
		assertEncodeDecode(d(value), bitString, Type.SF_SCALED_NUMBER);
	}
}
