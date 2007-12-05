package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.error.FastConstants;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.type.Type;

public class VariableLengthInstructionConverter extends ScalarConverter {

	public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
		Scalar scalar = (Scalar) super.convert(fieldDef, templateRegistry, context);
		if (fieldDef.isDefined("Length")) {
			scalar.addAttribute(FastConstants.LENGTH_FIELD, fieldDef.getGroup("Length").getString("Name"));
		}
		return scalar;
	}

	public GroupValue convert(Field field, ConversionContext context) {
		Scalar scalar = (Scalar) field;
		GroupValue fieldDef = super.convert(field, context);
		if (scalar.hasAttribute(FastConstants.LENGTH_FIELD)) {
			GroupValue lengthDef = new GroupValue(fieldDef.getGroup().getGroup("Length"));
			lengthDef.setString("Name", scalar.getAttribute(FastConstants.LENGTH_FIELD));
			fieldDef.setFieldValue("Length", lengthDef);
		}
		return fieldDef;
	}

	public boolean shouldConvert(Field field) {
		if (!field.getClass().equals(Scalar.class)) return false;
		Type type = ((Scalar) field).getType();
		return type.equals(Type.BYTE_VECTOR) || type.equals(Type.UNICODE);
	}

	public Group[] getTemplateExchangeTemplates() {
		return new Group[] { SessionControlProtocol_1_1.BYTE_VECTOR_INSTR, SessionControlProtocol_1_1.UNICODE_INSTR };
	}

}
