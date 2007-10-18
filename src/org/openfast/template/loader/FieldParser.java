package org.openfast.template.loader;

import org.openfast.template.Field;
import org.w3c.dom.Element;

public interface FieldParser {
	Field parse(Element fieldNode, ParsingContext context);
	boolean canParse(Element element, ParsingContext context);
}
