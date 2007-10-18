package org.openfast.session.template.exchange;

import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public class GroupConverter extends AbstractFieldInstructionConverter {

	public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
		String name = fieldDef.getString("Name");
		Field[] fields = parseFieldInstructions(fieldDef, templateRegistry, context);
		boolean optional = fieldDef.getBool("Optional");
		return new Group(name, fields, optional);
	}

	public GroupValue convert(Field field, ConversionContext context) {
		return convert((Group) field, new Message(SessionControlProtocol_1_1.GROUP_INSTR), context);
	}

	public Class getFieldClass() {
		return Group.class;
	}

	public Group[] getTemplateExchangeTemplates() {
		return new Group[] { SessionControlProtocol_1_1.GROUP_INSTR };
	}
	
	public static Message convert(Group group, Message groupMsg, ConversionContext context) {
		setNameAndId(group, groupMsg);
		SequenceValue instructions = new SequenceValue(SessionControlProtocol_1_1.TEMPLATE_DEFINITION.getSequence("Instructions"));
		int i = group instanceof MessageTemplate ? 1 : 0;
		Field[] fields = group.getFieldDefinitions();
		for (; i<fields.length; i++) {
			Field field = fields[i];
			FieldInstructionConverter converter = context.getConverter(field.getClass());
			if (converter == null)
				throw new IllegalStateException("No converter found for type " + field.getClass());
			FieldValue value = converter.convert(field, context);
			instructions.add(new FieldValue[] { value });
		}
		groupMsg.setFieldValue("Instructions", instructions);
		return groupMsg;
	}

	public static Field[] parseFieldInstructions(GroupValue groupDef, TemplateRegistry registry, ConversionContext context) {
		SequenceValue instructions = groupDef.getSequence("Instructions");
		Field[] fields = new Field[instructions.getLength()];
		for (int i=0; i<fields.length; i++) {
			GroupValue fieldDef = instructions.get(i).getGroup(0);
			FieldInstructionConverter converter = context.getConverter(fieldDef.getGroup());
			if (converter == null)
				throw new IllegalStateException("Encountered unknown group " + fieldDef.getGroup() + "while processing field instructions " + groupDef.getGroup());
			fields[i] = converter.convert(fieldDef, registry, context);
		}
		return fields;
	}
}
