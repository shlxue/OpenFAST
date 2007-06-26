package org.openfast.template.operator;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TwinOperatorTest extends OpenFastTestCase {

	private Operator operator;
	private Scalar field;

	protected void setUp() throws Exception {
		operator = new TwinOperator("copy", "copy");
		field = new Scalar("", Type.DECIMAL, operator, ScalarValue.UNDEFINED, true);
	}
	
	public void testGetValueToEncode() {
		TwinValue value = (TwinValue) operator.getValueToEncode(d(9427.55), ScalarValue.UNDEFINED, field);
		assertEquals(-2, ((IntegerValue) value.first).value);
		assertEquals(942755, ((IntegerValue) value.second).value);
		
		value = (TwinValue) operator.getValueToEncode(d(9427.61), twin(i(-2), i(942755)), field);
		assertEquals(null, value.first);
		assertEquals(942761, ((IntegerValue)value.second).value);
	}

	public void testDecodeValue() {
		assertEquals(d(9427.55), operator.decodeValue(twin(i(-2), i(942755)), ScalarValue.UNDEFINED, field));
		assertEquals(d(9427.61), operator.decodeValue(twin(null, i(942761)), twin(i(-2), i(942755)), field));
		assertEquals(null, operator.decodeValue(null, twin(i(-2), i(942761)), field));
	}
}
