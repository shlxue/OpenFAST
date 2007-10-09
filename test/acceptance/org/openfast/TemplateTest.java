package org.openfast;

import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import junit.framework.TestCase;

public class TemplateTest extends TestCase {
	private static final String PRE_TRADE_NS = "http://www.openfast.org/fix44/preTrade";
	private static final String COMPONENTS_NS = "http://www.openfast.org/fix44/components";
	private static final String FIX_44_NS = "http://www.openfast.org/fix44";
	private static final String EXT_NS = "http://www.openfast.org/ext";

	public void testTemplates() {
		MessageTemplateLoader loader = new XMLMessageTemplateLoader();
		loader.load(this.getClass().getResourceAsStream("components.xml"));
		loader.load(this.getClass().getResourceAsStream("quote.xml"));
		MessageTemplate quote = loader.getTemplate(new QName("Quote", PRE_TRADE_NS));
		
		assertEquals(FIX_44_NS, quote.getField("QuoteID").getQName().getNamespace());
		assertNotNull(quote.getField(new QName("Group", EXT_NS)));
		
		assertEquals(1, quote.getStaticTemplateReferences().length);
		assertNotNull(quote.getStaticTemplateReference(new QName("Instrument", COMPONENTS_NS)));
	}
}
