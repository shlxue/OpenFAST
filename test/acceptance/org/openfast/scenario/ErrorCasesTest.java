package org.openfast.scenario;


import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class ErrorCasesTest extends OpenFastTestCase {

	public void testMantissaIsntPresentWhenExponentIsNull() throws Exception {
        String templateXml = 
	        "<template name=\"SampleTemplate\">" +
	        "  <decimal name=\"bid\" presence=\"optional\">" +
	        "    <mantissa><copy /></mantissa>" +
	        "    <exponent><copy value=\"-2\" /></exponent>" +
	        "  </decimal>" +
	        "</template>";
		MessageTemplate template = template(templateXml);
		FastEncoder encoder = encoder(template);
		
		Message message = new Message(template);
		message.setDecimal(1, 0.63);
		assertEquals("11010000 10000001 10111111", encoder.encode(message));
		
		message = new Message(template);
		assertEquals("10100000 10000000", encoder.encode(message));
	}
	
	public void testEncodeDecodeNestedSequence() {
		Sequence nestedSequence = new Sequence("nested", new Field[] { new Scalar("string", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false) }, true);
		Group group = new Group("group", new Field[] { nestedSequence }, true);
		MessageTemplate t = new MessageTemplate("template", new Field[] { group });
		Message message = new Message(t);
		
		FastEncoder encoder = encoder(t);
		assertEquals("11000000 10000001", encoder.encode(message));
		
		FastDecoder decoder = decoder("11000000 10000001", t);
		assertEquals(message, decoder.readMessage());
	}
}
