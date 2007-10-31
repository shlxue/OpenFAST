package org.openfast.session.template.exchange;

import java.util.HashMap;
import java.util.Map;

import org.openfast.Dictionary;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;

public abstract class AbstractFieldInstructionConverter implements FieldInstructionConverter {
	public static void setNameAndId(Field field, GroupValue fieldDef) {
		setName(field, fieldDef);
		if (field.getId() != null)
			fieldDef.setString("AuxId", field.getId());
	}

	public static void setName(Field field, GroupValue fieldDef) {
		fieldDef.setString("Name", field.getName());
		fieldDef.setString("Ns", field.getQName().getNamespace());
	}

	public static GroupValue createOperator(Scalar scalar) {
		if (!OPERATOR_TEMPLATE_MAP.containsKey(scalar.getOperator()))
			return null;
		MessageTemplate operatorTemplate = (MessageTemplate) OPERATOR_TEMPLATE_MAP.get(scalar.getOperator());
		GroupValue operatorMessage = new Message(operatorTemplate);
		if (!scalar.getDictionary().equals(Dictionary.GLOBAL))
			operatorMessage.setString("Dictionary", scalar.getDictionary());
		if (!scalar.getKey().equals(scalar.getQName())) {
			Group key = operatorTemplate.getGroup("Key");
			GroupValue keyValue = new GroupValue(key);
			keyValue.setString("Name", scalar.getKey().getName());
			keyValue.setString("Ns", scalar.getKey().getNamespace());
			operatorMessage.setFieldValue(key, keyValue);
		}
		return operatorMessage;
	}

	public static Operator getOperator(Group group) {
		return (Operator) TEMPLATE_OPERATOR_MAP.get(group);
	}
	
	private static final Map/*<Operator, MessageTemplate>*/ OPERATOR_TEMPLATE_MAP = new HashMap();
	private static final Map/*<MessageTemplate, Operator>*/ TEMPLATE_OPERATOR_MAP = new HashMap();
	
	static {
		OPERATOR_TEMPLATE_MAP.put(Operator.CONSTANT,  SessionControlProtocol_1_1.CONSTANT_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.DEFAULT,   SessionControlProtocol_1_1.DEFAULT_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.COPY,      SessionControlProtocol_1_1.COPY_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.INCREMENT, SessionControlProtocol_1_1.INCREMENT_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.DELTA,     SessionControlProtocol_1_1.DELTA_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.TAIL,      SessionControlProtocol_1_1.TAIL_OP);
		
		TEMPLATE_OPERATOR_MAP.put(SessionControlProtocol_1_1.CONSTANT_OP,  Operator.CONSTANT);
		TEMPLATE_OPERATOR_MAP.put(SessionControlProtocol_1_1.DEFAULT_OP,   Operator.DEFAULT);
		TEMPLATE_OPERATOR_MAP.put(SessionControlProtocol_1_1.COPY_OP,      Operator.COPY);
		TEMPLATE_OPERATOR_MAP.put(SessionControlProtocol_1_1.INCREMENT_OP, Operator.INCREMENT);
		TEMPLATE_OPERATOR_MAP.put(SessionControlProtocol_1_1.DELTA_OP,     Operator.DELTA);
		TEMPLATE_OPERATOR_MAP.put(SessionControlProtocol_1_1.TAIL_OP,      Operator.TAIL);
	}
}
