package org.openfast.template.loader;

import org.w3c.dom.Element;

public class StringParser extends VariableLengthScalarParser {

	public StringParser() {
		super ("string");
	}

	public boolean canParse(Element element, ParsingContext context) {
		return element.getNodeName().equals("string");
	}
	
	protected String getTypeName(Element fieldNode) {
    	if (fieldNode.hasAttribute("charset"))
    		return fieldNode.getAttribute("charset");
		return "ascii";
	}
}
