package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.LongValue;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.util.Util;

public class ComposedDecimalConverter extends AbstractFieldInstructionConverter {
    public Field convert(GroupValue fieldDef, TemplateRegistry templateRegistry, ConversionContext context) {
        QName name = new QName(fieldDef.getString("Name"), fieldDef.getString("Ns"));
        boolean optional = fieldDef.getBool("Optional");
        GroupValue exponentDef = fieldDef.getGroup("Exponent");
        GroupValue exponentOperatorDef = exponentDef.getGroup("Operator").getGroup(0);
        Operator exponentOperator = getOperator(exponentOperatorDef.getGroup());
        ScalarValue exponentDefaultValue = ScalarValue.UNDEFINED;
        if (exponentDef.isDefined("InitialValue"))
            exponentDefaultValue = new IntegerValue(exponentDef.getInt("InitialValue"));
        GroupValue mantissaDef = fieldDef.getGroup("Mantissa");
        GroupValue mantissaOperatorDef = mantissaDef.getGroup("Operator").getGroup(0);
        Operator mantissaOperator = getOperator(mantissaOperatorDef.getGroup());
        ScalarValue mantissaDefaultValue = ScalarValue.UNDEFINED;
        if (mantissaDef.isDefined("InitialValue"))
            mantissaDefaultValue = new LongValue(mantissaDef.getInt("InitialValue"));
        return Util.composedDecimal(name, exponentOperator, exponentDefaultValue, mantissaOperator, mantissaDefaultValue, optional);
    }
    public GroupValue convert(Field field, ConversionContext context) {
        ComposedScalar composedScalar = (ComposedScalar) field;
        Message message = new Message(SessionControlProtocol_1_1.COMP_DECIMAL_INSTR);
        setNameAndId(field, message);
        message.setInteger("Optional", field.isOptional() ? 1 : 0);
        GroupValue exponentDef = createComponent(composedScalar.getFields()[0], "Exponent");
        GroupValue mantissaDef = createComponent(composedScalar.getFields()[1], "Mantissa");
        message.setFieldValue("Exponent", exponentDef);
        message.setFieldValue("Mantissa", mantissaDef);
        return message;
    }
    private GroupValue createComponent(Scalar component, String componentName) {
        Group componentGroup = SessionControlProtocol_1_1.COMP_DECIMAL_INSTR.getGroup(componentName);
        GroupValue componentDef = new GroupValue(componentGroup);
        GroupValue componentOperatorDef = createOperator(component);
        GroupValue componentOperatorGroup = new GroupValue(componentGroup.getGroup("Operator"));
        componentDef.setFieldValue("Operator", componentOperatorGroup);
        componentOperatorGroup.setFieldValue(0, componentOperatorDef);
        if (!component.getDefaultValue().isUndefined())
            componentDef.setInteger("InitialValue", component.getDefaultValue().toInt());
        return componentDef;
    }
    public boolean shouldConvert(Field field) {
        return field.getClass().equals(ComposedScalar.class);
    }
    public Group[] getTemplateExchangeTemplates() {
        return new Group[] { SessionControlProtocol_1_1.COMP_DECIMAL_INSTR };
    }
}
