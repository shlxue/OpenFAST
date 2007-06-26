package org.openfast;

import junit.framework.TestCase;

import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class TemplateDictionaryTest extends TestCase {

	public void testTemplateValueLookup() throws Exception {
		Dictionary dictionary = new TemplateDictionary();
		Group template = new MessageTemplate("Position", new Field[] {new Scalar("exchange", Type.STRING, Operator.COPY, false) });
		ScalarValue value = new StringValue("NYSE");
		dictionary.store(template, "exchange", value);
		
		assertEquals(value, dictionary.lookup(template, "exchange"));
		
		Group quoteTemplate = new MessageTemplate("Quote", new Field[] { new Scalar("bid", Type.DECIMAL, Operator.DELTA, false) });
		assertEquals(ScalarValue.UNDEFINED, dictionary.lookup(quoteTemplate, "exchange"));
	}
	
	public void testLookupMultipleValuesForTemplate() throws Exception {
		Dictionary dictionary = new TemplateDictionary();
		Group template = new MessageTemplate("Position", new Field[] {new Scalar("exchange", Type.STRING, Operator.COPY, false) });
		ScalarValue value = new StringValue("NYSE");
		ScalarValue marketValue = new DecimalValue(100000.00); 
		dictionary.store(template, "exchange", value);
		dictionary.store(template, "marketValue", marketValue);
		
		assertFalse(value.equals(ScalarValue.UNDEFINED));
		assertEquals(value, dictionary.lookup(template, "exchange"));
		assertEquals(marketValue, dictionary.lookup(template, "marketValue"));
	}
	
	public void testReset() {

		Dictionary dictionary = new TemplateDictionary();
		Group template = new MessageTemplate("Position", new Field[] {new Scalar("exchange", Type.STRING, Operator.COPY, false) });
		ScalarValue value = new StringValue("NYSE");
		dictionary.store(template, "exchange", value);
		
		assertEquals(value, dictionary.lookup(template, "exchange"));
		dictionary.reset();
		assertEquals(ScalarValue.UNDEFINED, dictionary.lookup(template, "exchange"));
	}
	
	public void testExistingTemplateValueLookup() throws Exception {
		Dictionary dictionary = new TemplateDictionary();
		Group template = new MessageTemplate("Position", new Field[] {new Scalar("exchange", Type.STRING, Operator.COPY, false) });
		ScalarValue value = new StringValue("NYSE");
		dictionary.store(template, "exchange", value);
		
		assertEquals(ScalarValue.UNDEFINED, dictionary.lookup(template, "bid"));
	}
}
