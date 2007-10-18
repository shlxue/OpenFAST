package org.openfast.template.loader;

import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.util.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComposedDecimalParser extends AbstractFieldParser {

	public ComposedDecimalParser() {
		super("decimal");
	}
	
	public boolean canParse(Element element, ParsingContext context) {
		NodeList children = element.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			String nodeName = children.item(i).getNodeName();
			if (nodeName.equals("mantissa") || nodeName.equals("exponent"))
				return true;
		}
		return false;
	}
	
	protected Field parse(Element fieldNode, boolean optional, ParsingContext context) {
        NodeList fieldChildren = fieldNode.getChildNodes();
        Node mantissaNode = null;
        Node exponentNode = null;

        for (int i = 0; i < fieldChildren.getLength(); i++) {
            if ("mantissa".equals(fieldChildren.item(i).getNodeName())) {
                mantissaNode = fieldChildren.item(i);
            } else if ("exponent".equals(fieldChildren.item(i).getNodeName())) {
                exponentNode = fieldChildren.item(i);
            }
        }
		return createComposedDecimal(fieldNode, context.getName(), optional, mantissaNode, exponentNode, context);
	}
	
	/**
     * Create a new Scalar object with a new TwinValue and a new TwinOperator with the mantissa and exponent nodes.
     * If there are nodes or child nodes within the passed Nodes, those values are stored as well
     * @param fieldNode The dom element object
     * @param name The name of the create Scalar object
     * @param optional Determines if the Field is required or not for the data
     * @param mantissaNode The passed mantissaNode
     * @param exponentNode The passed exponentNode
     * @return Returns a new Scalar object with the newly create TwinValue object and TwinOperator object.
     */
    private Field createComposedDecimal(Element fieldNode, QName name, boolean optional, Node mantissaNode, Node exponentNode, ParsingContext context) {
        String mantissaOperator = "none";
        String exponentOperator = "none";
        ScalarValue mantissaDefaultValue = ScalarValue.UNDEFINED;
        ScalarValue exponentDefaultValue = ScalarValue.UNDEFINED;
        
        if ((mantissaNode != null) && mantissaNode.hasChildNodes()) {
            Node operatorNode = getElement((Element) mantissaNode, 1);
            mantissaOperator = operatorNode.getNodeName();

            String value = ((Element) operatorNode).getAttribute("value");

            if ((value != null) && !value.equals("")) {
                mantissaDefaultValue = Type.U32.getValue(value);
            }
        }

        if ((exponentNode != null) && exponentNode.hasChildNodes()) {
            Node operatorNode = getElement((Element) exponentNode, 1);
            exponentOperator = operatorNode.getNodeName();

            String value = ((Element) operatorNode).getAttribute("value");

            if ((value != null) && !value.equals("")) {
                exponentDefaultValue = Type.U32.getValue(value);
            }
        }

        ComposedScalar scalar = Util.composedDecimal(name, Operator.getOperator(exponentOperator), exponentDefaultValue, Operator.getOperator(mantissaOperator), mantissaDefaultValue, optional);
        		
        if (fieldNode.hasAttribute("id"))
    		scalar.setId(fieldNode.getAttribute("id"));
		return scalar;
    }


}
