package org.openfast.scenario;


import org.openfast.Context;
import org.openfast.Message;
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
		
		Context context = new Context();
		Message message = new Message(template, 100);
		message.setDecimal(1, 0.63);
		assertEquals("11010000 11100100 10111111", template.encode(message, context));
		
		message = new Message(template, 100);
		assertEquals("10100000 10000000", template.encode(message, context));
	}
}
