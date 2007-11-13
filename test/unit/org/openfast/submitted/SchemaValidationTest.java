package org.openfast.submitted;

import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class SchemaValidationTest extends TestCase {

	public void testValidateSchema() throws Exception {
		InputStream templateSource = this.getClass().getResourceAsStream("fastapi.xml");
		XMLMessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		templateLoader.setLoadTemplateIdFromAuxId(true);
		templateLoader.setValidate(true);
		MessageTemplate[] templates = templateLoader.load(templateSource);
		assertEquals(1, templates.length);
	}
}
