package org.openfast.session.template.exchange;

import java.util.HashMap;
import java.util.Map;

import org.openfast.template.Group;

public class ConversionContext {

	private Map converterTemplateMap = new HashMap();
	private Map converterTypeMap = new HashMap();
	
	public void addFieldInstructionConverter(FieldInstructionConverter converter) {
		Group[] templateExchangeTemplates = converter.getTemplateExchangeTemplates();
		for (int i=0; i<templateExchangeTemplates.length; i++) {
			converterTemplateMap.put(templateExchangeTemplates[i], converter);
		}
		converterTypeMap.put(converter.getFieldClass(), converter);
	}

	public FieldInstructionConverter getConverter(Group group) {
		return (FieldInstructionConverter) converterTemplateMap.get(group);
	}

	public FieldInstructionConverter getConverter(Class clazz) {
		return (FieldInstructionConverter) converterTypeMap.get(clazz);
	}
}
