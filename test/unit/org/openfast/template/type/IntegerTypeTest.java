package org.openfast.template.type;

import junit.framework.TestCase;

public class IntegerTypeTest extends TestCase {

	public void testGetSignedIntegerSize()
	{
		assertEquals(1, IntegerType.getSignedIntegerSize(63));
		assertEquals(1, IntegerType.getSignedIntegerSize(-64));
		assertEquals(2, IntegerType.getSignedIntegerSize(64));
		assertEquals(2, IntegerType.getSignedIntegerSize(8191));
		assertEquals(2, IntegerType.getSignedIntegerSize(-8192));
		assertEquals(2, IntegerType.getSignedIntegerSize(-65));
		assertEquals(4, IntegerType.getSignedIntegerSize(134217727));
		assertEquals(4, IntegerType.getSignedIntegerSize(-134217728));
	}
}
