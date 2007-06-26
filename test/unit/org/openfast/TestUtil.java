package org.openfast;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class TestUtil {
	public static void assertBitVectorEquals(String bitString, byte[] encoding) {
		try {
			assertByteArrayEquals(ByteUtil.convertBitStringToFastByteArray(bitString), encoding);
		} catch (AssertionFailedError e) {
			System.out.println("Expected: " + bitString);
			System.out.println("Actual:   " + ByteUtil.convertByteArrayToBitString(encoding));
			throw e;
		}
	}

	public static void assertByteArrayEquals(byte[] expected, byte[] actual) {
		TestCase.assertEquals("expected byte[] of length " + expected.length + ", but was " + actual.length, expected.length, actual.length);
		for (int i=0; i<expected.length; i++)
		{
			String error = "expected: <" + ByteUtil.convertByteArrayToBitString(expected) + "> but was: <" + ByteUtil.convertByteArrayToBitString(actual) + ">";
			TestCase.assertEquals(error, expected[i], actual[i]);
		}
	}
}
