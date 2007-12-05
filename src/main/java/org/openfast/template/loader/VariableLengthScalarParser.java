package org.openfast.template.loader;

import org.openfast.error.FastConstants;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.w3c.dom.Element;

public class VariableLengthScalarParser extends ScalarParser {

	public VariableLengthScalarParser(String nodeName) {
		super(nodeName);
	}
	
	public Field parse(Element fieldNode, boolean optional, ParsingContext context) {
		Scalar scalar = (Scalar) super.parse(fieldNode, optional, context);
		Element element = getElement(fieldNode, 1);
    	if (element != null && element.getNodeName().equals("length")) {
    		String length = element.getAttribute("name");
    		scalar.addAttribute(FastConstants.LENGTH_FIELD, length);
    	}
		return scalar;
	}
	
	protected Element getOperatorElement(Element fieldNode) {
		Element operatorElement = super.getOperatorElement(fieldNode);
    	if (operatorElement != null && operatorElement.getNodeName().equals("length"))
    		return getElement(fieldNode, 2);
		return operatorElement;
	}
}
