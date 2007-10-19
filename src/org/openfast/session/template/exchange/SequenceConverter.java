package org.openfast.session.template.exchange;

import org.openfast.Global;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class SequenceConverter extends AbstractFieldInstructionConverter {

	public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
		String name = fieldDef.getString("Name");
		String ns = fieldDef.getString("Ns");
		QName qname = new QName(name, ns);
		Field[] fields = GroupConverter.parseFieldInstructions(fieldDef, templateRegistry, context);
		boolean optional = fieldDef.getBool("Optional");
		Scalar length = null;
		if (fieldDef.isDefined("Length")) {
			GroupValue lengthDef = fieldDef.getGroup("Length");
			QName lengthName;
			String id = null;
			if (lengthDef.isDefined("Name")) {
				GroupValue nameDef = lengthDef.getGroup("Name");
				lengthName = new QName(nameDef.getString("Name"), nameDef.getString("Ns"));
				if (nameDef.isDefined("AuxId"))
					id = nameDef.getString("AuxId");
			} else 
				lengthName = Global.createImplicitName(qname);
			Operator operator = Operator.NONE;
			if (lengthDef.isDefined("Operator"))
				operator = getOperator(lengthDef.getGroup("Operator").getGroup(0).getGroup());
			ScalarValue initialValue = ScalarValue.UNDEFINED;
			if (lengthDef.isDefined("InitialValue"))
				initialValue = (ScalarValue) lengthDef.getValue("InitialValue");
			length = new Scalar(lengthName, Type.U32, operator, initialValue, optional);
			length.setId(id);
		}
		
		return new Sequence(qname, length, fields, optional);
	}

	public GroupValue convert(Field field, ConversionContext context) {
		Sequence sequence = (Sequence) field;
		Message seqDef = GroupConverter.convert(sequence.getGroup(), new Message(SessionControlProtocol_1_1.SEQUENCE_INSTR), context);
		if (!sequence.isImplicitLength()) {
			Group lengthGroup = SessionControlProtocol_1_1.SEQUENCE_INSTR.getGroup("Length");
			GroupValue lengthDef = new GroupValue(lengthGroup);
			Scalar length = sequence.getLength();
			GroupValue nameDef = new GroupValue(lengthGroup.getGroup("Name"));
			setNameAndId(length, nameDef);
			lengthDef.setFieldValue("Name", nameDef);
			seqDef.setFieldValue("Length", lengthDef);
			
			if (!length.getOperator().equals(Operator.NONE)) {
				GroupValue operatorDef = new GroupValue(lengthGroup.getGroup("Operator"));
				operatorDef.setFieldValue(0, createOperator(length));
				lengthDef.setFieldValue("Operator", operatorDef);
			}
			
			if (!length.getInitialValue().isUndefined()) {
				lengthDef.setFieldValue("InitialValue", length.getInitialValue());
			}
		}
		return seqDef;
	}

	public boolean shouldConvert(Field field) {
		return field.getClass().equals(Sequence.class);
	}

	public Group[] getTemplateExchangeTemplates() {
		return new Group[] { SessionControlProtocol_1_1.SEQUENCE_INSTR };
	}

}
