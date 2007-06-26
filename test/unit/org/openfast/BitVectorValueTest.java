package org.openfast;

import junit.framework.TestCase;

public class BitVectorValueTest extends TestCase {

	public void testEquals()
	{
		BitVectorValue expected = new BitVectorValue(new BitVector(new byte[] { (byte) 0xf0 }));
		BitVectorValue actual = new BitVectorValue(new BitVector(7));
		actual.value.set(0);
		actual.value.set(1);
		actual.value.set(2);
		assertEquals(expected, actual);
	}
}
