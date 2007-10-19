package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

import junit.framework.TestCase;

public class VariableLengthInstructionConverterTest extends TestCase {
	private VariableLengthInstructionConverter converter;
	private ConversionContext context;

	protected void setUp() throws Exception {
		converter = new VariableLengthInstructionConverter();
		context = SessionControlProtocol_1_1.createInitialContext();
	}
	
	public void testConvertOnByteVector() {
		Scalar bytes = new Scalar("bytes", Type.BYTE_VECTOR, Operator.NONE, ScalarValue.UNDEFINED, true);
		assertTrue(converter.shouldConvert(bytes));
		
		bytes.addAttribute(FastConstants.LENGTH_FIELD, "numBytes");
		GroupValue fieldDef = converter.convert(bytes, context);
		Scalar converted = (Scalar) converter.convert(fieldDef, TemplateRegistry.NULL, context);
		
		assertEquals(bytes, converted);
		assertEquals("numBytes", converted.getAttribute(FastConstants.LENGTH_FIELD));
	}
	
	public void testConvertOnUnicodeString() {
		Scalar message = new Scalar("message", Type.UNICODE, Operator.COPY, ScalarValue.UNDEFINED, true);
		assertTrue(converter.shouldConvert(message));
		
		message.addAttribute(FastConstants.LENGTH_FIELD, "messageLength");
		GroupValue fieldDef = converter.convert(message, context);
		Scalar converted = (Scalar) converter.convert(fieldDef, TemplateRegistry.NULL, context);
		
		assertEquals(message, converted);
		assertEquals("messageLength", converted.getAttribute(FastConstants.LENGTH_FIELD));
	}
}
