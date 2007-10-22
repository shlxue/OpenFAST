package org.openfast.template.loader;

import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;

public class ScalarParserTest extends OpenFastTestCase {

	public void testParseElementBooleanParsingContext() throws Exception {
		Element scalar = document("<int32 name=\"value\"><copy/></int32>").getDocumentElement();
		new ScalarParser().parse(scalar, XMLMessageTemplateLoader.createInitialContext());
	}

}
