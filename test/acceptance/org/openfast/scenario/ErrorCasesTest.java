package org.openfast.scenario;


import org.openfast.Message;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
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
}
