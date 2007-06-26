package org.openfast;

import junit.framework.TestCase;

public class ByteVectorValueTest extends TestCase {

	public void testEquals() {
		ByteVectorValue expected = new ByteVectorValue(new byte[] { (byte) 0xff });
		ByteVectorValue actual = new ByteVectorValue(new byte[] { (byte) 0xff });
		assertEquals(expected, actual);
	}

}
