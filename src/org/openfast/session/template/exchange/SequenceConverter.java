package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;

public class SequenceConverter extends AbstractFieldInstructionConverter {

	public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
		String name = fieldDef.getString("Name");
		Field[] fields = GroupConverter.parseFieldInstructions(fieldDef, templateRegistry, context);
		boolean optional = fieldDef.getBool("Optional");
		return new Sequence(name, fields, optional);
	}

	public GroupValue convert(Field field, ConversionContext context) {
		Sequence sequence = (Sequence) field;
		Message seqDef = GroupConverter.convert(sequence.getGroup(), new Message(SessionControlProtocol_1_1.SEQUENCE_INSTR), context);
		if (!sequence.isImplicitLength()) {
			GroupValue seqLenDef = new GroupValue(SessionControlProtocol_1_1.SEQUENCE_INSTR.getGroup("Length"));
			Scalar length = sequence.getLength();
			GroupValue lengthName = new GroupValue(SessionControlProtocol_1_1.SEQUENCE_INSTR.getGroup("Length").getGroup("Name"));
			lengthName.setString("Name", length.getName());
			setNameAndId(length, lengthName);
			seqLenDef.setFieldValue("Name", lengthName);
			seqDef.setFieldValue("Length", seqLenDef);
		}
		return seqDef;
	}

	public Class getFieldClass() {
		return Sequence.class;
	}

	public Group[] getTemplateExchangeTemplates() {
		return new Group[] { SessionControlProtocol_1_1.SEQUENCE_INSTR };
	}

}
