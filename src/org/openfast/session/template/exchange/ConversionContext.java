package org.openfast.session.template.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openfast.template.Field;
import org.openfast.template.Group;

public class ConversionContext {

	private Map converterTemplateMap = new HashMap();
	private List converters = new ArrayList();
	
	public void addFieldInstructionConverter(FieldInstructionConverter converter) {
		Group[] templateExchangeTemplates = converter.getTemplateExchangeTemplates();
		for (int i=0; i<templateExchangeTemplates.length; i++) {
			converterTemplateMap.put(templateExchangeTemplates[i], converter);
		}
		converters.add(converter);
	}

	public FieldInstructionConverter getConverter(Group group) {
		return (FieldInstructionConverter) converterTemplateMap.get(group);
	}

	public FieldInstructionConverter getConverter(Field field) {
		for (int i=converters.size()-1; i>=0; i--) {
			FieldInstructionConverter converter = (FieldInstructionConverter) converters.get(i);
			if (converter.shouldConvert(field))
				return converter;
		}
		throw new IllegalStateException("No valid converter found for the field: " + field);
	}
}
