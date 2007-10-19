package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.TemplateRegistry;

public interface FieldInstructionConverter {
	boolean shouldConvert(Field field);
	Group[] getTemplateExchangeTemplates();
	Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context);
	GroupValue convert(Field field, ConversionContext context);
}
