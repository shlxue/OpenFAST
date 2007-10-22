package org.openfast.template.loader;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.template.ComposedScalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;

public class ComposedDecimalParserTest extends OpenFastTestCase {

	private FieldParser parser;
	private ParsingContext context;

	protected void setUp() throws Exception {
		parser = new ComposedDecimalParser();
		context = XMLMessageTemplateLoader.createInitialContext();
	}
	
	public void testParse() throws Exception {
		Element decimalDef = document("<decimal name=\"composed\"><mantissa><delta/></mantissa><exponent><constant value=\"-2\"/></exponent></decimal>").getDocumentElement();
		assertTrue(parser.canParse(decimalDef, context));
		ComposedScalar decimal = (ComposedScalar) parser.parse(decimalDef, context);
		assertComposedScalarField(decimal, Type.DECIMAL, "composed", Operator.CONSTANT, new IntegerValue(-2), Operator.DELTA, ScalarValue.UNDEFINED);
	}

}
