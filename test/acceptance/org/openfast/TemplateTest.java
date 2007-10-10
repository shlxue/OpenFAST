package org.openfast;

import junit.framework.TestCase;

import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class TemplateTest extends TestCase {
	private static final String SCP_1_1_NS = "http://www.fixprotocol.org/ns/fast/scp/1.1";
	private static final String PRE_TRADE_NS = "http://www.openfast.org/fix44/preTrade";
	private static final String SESSION_NS = "http://www.openfast.org/fix44/session";
	private static final String COMPONENTS_NS = "http://www.openfast.org/fix44/components";
	private static final String FIX_44_NS = "http://www.openfast.org/fix44";
	private static final String EXT_NS = "http://www.openfast.org/ext";
	private MessageTemplateLoader loader;

	protected void setUp() throws Exception {
		loader = new XMLMessageTemplateLoader(true);
		loader.load(this.getClass().getResourceAsStream("components.xml"));
		loader.load(this.getClass().getResourceAsStream("preTrade.xml"));
		loader.load(this.getClass().getResourceAsStream("session.xml"));
	}
	
	public void testTemplates() {
		MessageTemplate quote = loader.getTemplateRegistry().get(new QName("Quote", PRE_TRADE_NS));
		
		assertEquals(FIX_44_NS, quote.getField("QuoteID").getQName().getNamespace());
		assertNotNull(quote.getField(new QName("Group", EXT_NS)));
		
		assertEquals(1, quote.getStaticTemplateReferences().length);
		assertNotNull(quote.getStaticTemplateReference(new QName("Instrument", COMPONENTS_NS)));
	}
	
	public void testTemplateExtension() {
		MessageTemplate logon = loader.getTemplateRegistry().get(new QName("Logon", SESSION_NS));
		assertTrue(logon.hasAttribute(new QName("reset", SCP_1_1_NS)));
	}
}
