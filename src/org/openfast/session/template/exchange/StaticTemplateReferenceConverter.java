package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.StaticTemplateReference;
import org.openfast.template.TemplateRegistry;

public class StaticTemplateReferenceConverter extends AbstractFieldInstructionConverter {

	public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
		QName name = new QName(fieldDef.getString("Name"), fieldDef.getString("Ns"));
		if (!templateRegistry.isDefined(name))
			throw new IllegalStateException("Referenced template " + name + " not defined.");
		return new StaticTemplateReference(templateRegistry.get(name));
	}

	public GroupValue convert(Field field, ConversionContext context) {
		Message strDef = new Message(SessionControlProtocol_1_1.STAT_TEMP_REF_INSTR);
		setNameAndId(field, strDef);
		return strDef;
	}

	public Class getFieldClass() {
		return StaticTemplateReference.class;
	}

	public Group[] getTemplateExchangeTemplates() {
		return new Group[] { SessionControlProtocol_1_1.STAT_TEMP_REF_INSTR };
	}

}
