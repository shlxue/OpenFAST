package org.openfast.submitted;

import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class ComposedDecimalNullPointerTest extends TestCase {
	public void testNullPointerOnTwinValue() throws FileNotFoundException {
		InputStream templateSource = this.getClass().getResourceAsStream("FASTTestTemplate.xml");
		MessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = templateLoader.load(templateSource);
		
		InputStream is = this.getClass().getResourceAsStream("messages.fast");
		MessageInputStream mis = new MessageInputStream(is);
		mis.registerTemplate(35, templates[0]);
		Message msg = mis.readMessage();
		msg = mis.readMessage();
		msg = mis.readMessage();
		assertEquals(templates[0], msg.getTemplate());
	}
}
