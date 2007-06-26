package org.openfast;

import junit.framework.TestCase;

import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class MessageTest extends TestCase {

	/*
	 * Test method for 'org.openfast.Message.equals(Object)'
	 */
	public void testEquals() {
		
		MessageTemplate template = new MessageTemplate(null, new Field[] {
				new Scalar("1", Type.UNSIGNED_INTEGER, Operator.COPY, false)
		});
		GroupValue message = new Message(template, 2);
		message.setInteger(1, 1);
		GroupValue other = new Message(template, 2);
		other.setInteger(1, 1);
		
		assertEquals(message, other);
	}
	
	public void testNotEquals() {
		MessageTemplate template = new MessageTemplate(null, new Field[] {
				new Scalar("1", Type.UNSIGNED_INTEGER, Operator.COPY, false)
		});
		Message message = new Message(template, 2);
		message.setInteger(1, 2);
		Message other = new Message(template, 2);
		assertFalse(message.equals(other));
		assertFalse(other.equals(message));
		other.setInteger(1, 1);
		
		assertFalse(message.equals(other));
		assertFalse(other.equals(message));
	}

}
