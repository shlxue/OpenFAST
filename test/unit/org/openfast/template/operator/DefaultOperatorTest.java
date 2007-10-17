package org.openfast.template.operator;

import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class DefaultOperatorTest extends OpenFastTestCase {

	public void testNullsNoInitialValue() throws Exception {
		Scalar field = new Scalar(new QName("mostlyNull"), Type.I32, Operator.DEFAULT, ScalarValue.UNDEFINED, true);
		MessageTemplate template = template(field);
		FastEncoder encoder = encoder(template);
		
		Message message = (Message) template.createValue(null);
		assertEquals("11000000 10000001", encoder.encode(message));
		assertEquals("10000000", encoder.encode(message));
	}
	
	public void testNullsWithInitialValue() throws Exception {
		Scalar field = new Scalar(new QName("sometimesNull"), Type.I32, Operator.DEFAULT, new IntegerValue(10), true);
		MessageTemplate template = template(field);
		FastEncoder encoder = encoder(template);
		
		Message message = (Message) template.createValue(null);
		assertEquals("11100000 10000001 10000000", encoder.encode(message));
		assertEquals("10100000 10000000", encoder.encode(message));
		message.setInteger(1, 10);
		assertEquals("10000000", encoder.encode(message));
	}
}
