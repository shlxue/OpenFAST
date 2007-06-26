package org.openfast.template;

import junit.framework.TestCase;

import org.openfast.Context;
import org.openfast.IntegerValue;
import org.openfast.TestUtil;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class ScalarTest extends TestCase {

	private Context context;

	public void setUp() {
		context = new Context();
	}
	
	/*
	 * Test method for 'org.openfast.template.Scalar.encode(FieldValue, FieldValue)'
	 */
	public void testCopyEncodeWithNoPreviousValue() {
		Scalar scalar = new Scalar("a", Type.UNSIGNED_INTEGER, Operator.COPY, false);
		
		byte[] encoding = scalar.encode(new IntegerValue(1), null, context);
		TestUtil.assertBitVectorEquals("10000001", encoding);
	}
	
	/*
	 * Test method for 'org.openfast.template.Scalar.encode(FieldValue, FieldValue)'
	 */
	public void testCopyEncodeWithPreviousValue() {
		Scalar scalar = new Scalar("a", Type.UNSIGNED_INTEGER, Operator.COPY, false);
		scalar.encode(new IntegerValue(1), null, context);
		byte[] encoding = scalar.encode(new IntegerValue(1), null, context);
		TestUtil.assertBitVectorEquals("", encoding);
	}

}
