package org.openfast;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.openfast.session.Session;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class DictionaryTest extends TestCase {
	
	private Session session;
	private ByteArrayOutputStream out;

	protected void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		session = new Session(null, out);
	}
	
	public void testMultipleDictionaryTypes() throws Exception {
		Scalar bid = new Scalar("bid", Type.DECIMAL, Operator.COPY, false);
		bid.setDictionary(Dictionary.TEMPLATE);
		MessageTemplate quote = new MessageTemplate("quote", new Field[] {
				bid
		});
		
		Scalar bidR = new Scalar("bid", Type.DECIMAL, Operator.COPY, false);
		MessageTemplate request = new MessageTemplate("request", new Field[] { bidR });
		
		Message quote1 = new Message(quote, 2);
		quote1.setFieldValue(1, new DecimalValue(10.2));
		
		Message request1 = new Message(request, 1);
		request1.setFieldValue(1, new DecimalValue(10.3));
		
		Message quote2 = new Message(quote, 2);
		quote2.setFieldValue(1, new DecimalValue(10.2));
		
		Message request2 = new Message(request, 1);
		request2.setFieldValue(1, new DecimalValue(10.2));
		
		session.out.registerTemplate(1, request);
		session.out.registerTemplate(2, quote);
		session.out.writeMessage(quote1);
		session.out.writeMessage(request1);
		session.out.writeMessage(quote2);
		session.out.writeMessage(request2);
		
		
		String expected = "11100000 10000010 11111111 00000000 11100110 " + 
						  "11100000 10000001 11111111 00000000 11100111 " +
						  "11000000 10000010 " +
						  "11100000 10000001 11111111 00000000 11100110";
		TestUtil.assertBitVectorEquals(expected, out.toByteArray());
		
	}
}
