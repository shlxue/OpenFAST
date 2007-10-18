package org.openfast.session.template.exchange;

import java.util.HashMap;
import java.util.Map;

import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class ScalarConverter extends AbstractFieldInstructionConverter {

	public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
		Type type = (Type) TEMPLATE_TYPE_MAP.get(fieldDef.getGroup());
		GroupValue operatorGroup = fieldDef.getGroup("Operator").getGroup(0);
		boolean optional = fieldDef.getBool("Optional");
		Operator operator = getOperator(operatorGroup.getGroup());
		Scalar scalar = new Scalar(fieldDef.getString("Name"), type, operator, null, optional);
		if (operatorGroup.isDefined("Dictionary"))
			scalar.setDictionary(operatorGroup.getString("Dictionary"));
		if (operatorGroup.isDefined("Key")) {
			String name = operatorGroup.getGroup("Key").getString("Name");
			String ns = operatorGroup.getGroup("Key").getString("Ns");
			scalar.setKey(new QName(name, ns));
		}
		return scalar;
	}

	public GroupValue convert(Field field, ConversionContext context) {
		Scalar scalar = (Scalar) field;
		MessageTemplate scalarTemplate = (MessageTemplate) TYPE_TEMPLATE_MAP.get(scalar.getType());
		Message scalarMsg = new Message(scalarTemplate);
		setNameAndId(scalar, scalarMsg);
		scalarMsg.setInteger("Optional", scalar.isOptional() ? 1 : 0);
		scalarMsg.setFieldValue("Operator", new GroupValue(scalarTemplate.getGroup("Operator"), new FieldValue[] { createOperator(scalar) }));
		return scalarMsg;
	}

	public Group[] getTemplateExchangeTemplates() {
		return (Group[]) TEMPLATE_TYPE_MAP.keySet().toArray(new Group[TEMPLATE_TYPE_MAP.size()]);
	}
	
	public Class getFieldClass() {
		return Scalar.class;
	}

	private static final Map/*<Type, MessageTemplate>*/ TYPE_TEMPLATE_MAP = new HashMap();
	private static final Map/*<Type, MessageTemplate>*/ TEMPLATE_TYPE_MAP = new HashMap();
	
	static {
		TYPE_TEMPLATE_MAP.put(Type.I32,         SessionControlProtocol_1_1.INT32_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.U32,         SessionControlProtocol_1_1.UINT32_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.I64,         SessionControlProtocol_1_1.INT64_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.U64,         SessionControlProtocol_1_1.UINT64_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.DECIMAL,     SessionControlProtocol_1_1.DECIMAL_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.UNICODE,     SessionControlProtocol_1_1.UNICODE_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.ASCII,       SessionControlProtocol_1_1.ASCII_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.STRING,      SessionControlProtocol_1_1.ASCII_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.BYTE_VECTOR, SessionControlProtocol_1_1.BYTE_VECTOR_INSTR);
		
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.INT32_INSTR,       Type.I32);
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.UINT32_INSTR,      Type.U32);
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.INT64_INSTR,       Type.I64);
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.UINT64_INSTR,      Type.U64);
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.DECIMAL_INSTR,     Type.DECIMAL);
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.UNICODE_INSTR,     Type.UNICODE);
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.ASCII_INSTR,		 Type.ASCII);
		TEMPLATE_TYPE_MAP.put(SessionControlProtocol_1_1.BYTE_VECTOR_INSTR, Type.BYTE_VECTOR);
	}
}
