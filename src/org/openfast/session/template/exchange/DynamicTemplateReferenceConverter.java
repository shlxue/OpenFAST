package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.TemplateRegistry;

public class DynamicTemplateReferenceConverter implements FieldInstructionConverter {

	public Field convert(GroupValue groupValue, TemplateRegistry templateRegistry, ConversionContext context) {
		return DynamicTemplateReference.INSTANCE;
	}

	public GroupValue convert(Field field, ConversionContext context) {
		return SessionControlProtocol_1_1.DYN_TEMP_REF_MESSAGE;
	}

	public Class getFieldClass() {
		return DynamicTemplateReference.class;
	}

	public Group[] getTemplateExchangeTemplates() {
		return new Group[] { SessionControlProtocol_1_1.DYN_TEMP_REF_INSTR };
	}

}
