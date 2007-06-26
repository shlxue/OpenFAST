package org.openfast.template;

import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

import junit.framework.TestCase;

public class MessageTemplateTest extends TestCase {

	public void testEncodeMessageUsingTemplate()
	{
		Field[] fields = new Field[2];
		fields[0] = new Scalar("code", Type.STRING, Operator.COPY, false);
		fields[1] = new Scalar("value", Type.UNSIGNED_INTEGER, Operator.DELTA, false);
		
//		MessageTemplate template = new MessageTemplate(null, fields);
	}
}
