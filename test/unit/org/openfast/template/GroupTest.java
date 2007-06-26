package org.openfast.template;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.TestUtil;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class GroupTest extends TestCase {

	private Group template;
	private Context context;
	
	protected void setUp() throws Exception {
		template = new MessageTemplate("", new Field[] {});
		context = new Context();
	}

	public void testEncode() {
		Scalar firstName = new Scalar("First Name", Type.UNSIGNED_INTEGER, Operator.COPY, true);
		Scalar lastName = new Scalar("Last Name", Type.UNSIGNED_INTEGER, Operator.NONE, false);
		
		Group theGroup = new Group("guy", new Field[]{firstName, lastName}, false);
		
		byte[] actual = theGroup.encode(new GroupValue(new Group("", new Field[] {}, false), new FieldValue[] {new IntegerValue(1), new IntegerValue(2)}), template, context);
		
		String expected = "11000000 10000010 10000010";
		
		TestUtil.assertBitVectorEquals(expected, actual);
	}
	
	public void testDecode() {
		String message = "11000000 10000010 10000010";
		InputStream in = new ByteArrayInputStream(ByteUtil.convertBitStringToFastByteArray(message));
		Scalar firstname = new Scalar("firstName", Type.UNSIGNED_INTEGER, Operator.COPY, true);
		Scalar lastName = new Scalar("lastName", Type.UNSIGNED_INTEGER, Operator.NONE, false);
		//		MessageInputStream in = new MessageInputStream(new ByteArrayInputStream(message.getBytes()));
		Group group = new Group("person", new Field[]{ firstname, lastName}, false);
		GroupValue groupValue = (GroupValue) group.decode(in, template, context, true);
		assertEquals(1, ((IntegerValue)groupValue.getValue(0)).value);
		assertEquals(2, ((IntegerValue)groupValue.getValue(1)).value);
	}

}
