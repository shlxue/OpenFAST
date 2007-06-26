package org.openfast.template;

import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.IntegerValue;
import org.openfast.SequenceValue;
import org.openfast.TestUtil;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class SequenceTest extends TestCase {

	private Group template;
	private Context context;

	protected void setUp() throws Exception {
		template = new MessageTemplate("", new Field[] {});
		context = new Context();
	}
	
	public void testEncode() {
		Scalar firstName = new Scalar("First Name", Type.SIGNED_INTEGER, Operator.COPY, false);
		Scalar lastName = new Scalar("Last Name", Type.SIGNED_INTEGER, Operator.COPY, false);
		Sequence sequence1 = new Sequence("Contacts", new Field[] {firstName, lastName}, false);
		
		SequenceValue sequenceValue = new SequenceValue(sequence1);
		sequenceValue.add(new FieldValue[] {new IntegerValue(1), new IntegerValue(2)});
		sequenceValue.add(new FieldValue[] {new IntegerValue(3), new IntegerValue(4)});
		
		byte[] actual = sequence1.encode(sequenceValue, template, context);
		String expected = "10000010 11100000 10000001 10000010 11100000 10000011 10000100";
		TestUtil.assertBitVectorEquals(expected, actual);
	}

	public void testDecode() {
		
		String actual = "10000010 11100000 10000001 10000010 11100000 10000011 10000100";
		InputStream stream = ByteUtil.createByteStream(actual);
		
		Scalar firstNumber = new Scalar("First Number", Type.SIGNED_INTEGER, Operator.COPY, false);
		Scalar lastNumber = new Scalar("Second Number", Type.SIGNED_INTEGER, Operator.COPY, false);
		Sequence sequence1 = new Sequence("Contants", new Field[] {firstNumber, lastNumber}, false);
		
		SequenceValue sequenceValue = new SequenceValue(sequence1);
		sequenceValue.add(new FieldValue[] {new IntegerValue(1), new IntegerValue(2)});
		sequenceValue.add(new FieldValue[] {new IntegerValue(3), new IntegerValue(4)});
		
		FieldValue result = sequence1.decode(stream, template, context, true);
		assertEquals(sequenceValue, result);
	}

}
