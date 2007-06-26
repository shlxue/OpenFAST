package org.openfast.template.operator;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;

import junit.framework.TestCase;

public class DeltaStringOperatorTest extends TestCase {
	
	private Scalar field;

	protected void setUp() throws Exception {
	}

	public void testGetValueToEncodeMandatory() {
		field = new Scalar(null, Type.STRING, Operator.DELTA, false);
		
		assertEquals(tv(0, "ABCD"), encode("ABCD", ScalarValue.UNDEFINED));
		assertEquals(tv(1, "E"), encode("ABCE", s("ABCD")));
		assertEquals(tv(-2, "Z"), encode("ZBCE", s("ABCE")));
		assertEquals(tv(-1, "Y"), encode("YZBCE", s("ZBCE")));
		assertEquals(tv(0, "F"), encode("YZBCEF", s("YZBCE")));
	}

	public void testDecodeValueMandatory() {
		field = new Scalar(null, Type.STRING, Operator.DELTA, false);
		
		assertEquals(new StringValue("ABCD"), decode(tv(0, "ABCD"), ScalarValue.UNDEFINED));
		assertEquals(new StringValue("ABCE"), decode(tv(1, "E"), s("ABCD")));
		assertEquals(new StringValue("ZBCE"), decode(tv(-2, "Z"), s("ABCE")));
		assertEquals(new StringValue("YZBCE"), decode(tv(-1, "Y"), s("ZBCE")));
		assertEquals(new StringValue("YZBCEF"), decode(tv(0, "F"), s("YZBCE")));
	}
	
	public void testGetValueToEncodeOptional() {
		field = new Scalar (null, Type.STRING, Operator.DELTA, ScalarValue.UNDEFINED, true);
		
		assertEquals(tv(0, "ABCD"), encode("ABCD", ScalarValue.UNDEFINED));
		assertEquals(tv(1, "E"), encode("ABCE", s("ABCD")));
		assertEquals(tv(-2, "Z"), encode("ZBCE", s("ABCE")));
		assertEquals(tv(-1, "Y"), encode("YZBCE", s("ZBCE")));
		assertEquals(tv(0, "F"), encode("YZBCEF", s("YZBCE")));
		assertEquals(ScalarValue.NULL, encode(null, s("YZBCEF")));
	}

	public void testDecodeValueOptional() {
		field = new Scalar (null, Type.STRING, Operator.DELTA, ScalarValue.UNDEFINED, true);
		
		assertEquals(new StringValue("ABCD"), decode(tv(0, "ABCD"), ScalarValue.UNDEFINED));
		assertEquals(new StringValue("ABCE"), decode(tv(1, "E"), s("ABCD")));
		assertEquals(new StringValue("ZBCE"), decode(tv(-2, "Z"), s("ABCE")));
		assertEquals(new StringValue("YZBCE"), decode(tv(-1, "Y"), s("ZBCE")));
		assertEquals(new StringValue("YZBCEF"), decode(tv(0, "F"), s("YZBCE")));
		assertEquals(null, decode(ScalarValue.NULL, s("YZBCEF")));
	}	

	private ScalarValue s(String value) {
		return new StringValue(value);
	}

	private ScalarValue encode(String value, ScalarValue priorValue) {
		if (value == null)
			return Operator.DELTA_STRING.getValueToEncode(null, priorValue, field);
		return Operator.DELTA_STRING.getValueToEncode(new StringValue(value), priorValue, field);
	}
	
	private ScalarValue decode(ScalarValue diff, ScalarValue priorValue) {
		return Operator.DELTA_STRING.decodeValue(diff, priorValue, field);
	}

	private TwinValue tv(int subtraction, String diff) {
		return new TwinValue(new IntegerValue(subtraction), new StringValue(diff));
	}
}
