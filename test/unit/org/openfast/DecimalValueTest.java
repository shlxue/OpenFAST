package org.openfast;

import junit.framework.TestCase;

public class DecimalValueTest extends TestCase {
	public void testMantissaAndExponent() {
		DecimalValue value = new DecimalValue(9427.55);
		assertEquals(942755, value.mantissa);
		assertEquals(-2, value.exponent);
		
		value = new DecimalValue(942755, -2);
		assertEquals(9427.55, value.value, .01);
	}
}
