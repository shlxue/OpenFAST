package org.openfast.template.loader;

import org.w3c.dom.Element;



public class ByteVectorParser extends VariableLengthScalarParser {
	public ByteVectorParser() {
		super("byteVector");
	}
	
	public boolean canParse(Element element, ParsingContext context) {
		return "byteVector".equals(element.getNodeName());
	}
}
