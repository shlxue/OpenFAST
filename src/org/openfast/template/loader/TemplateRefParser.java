package org.openfast.template.loader;

import org.openfast.QName;
import org.openfast.error.FastConstants;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.StaticTemplateReference;
import org.w3c.dom.Element;

public class TemplateRefParser implements FieldParser {
    public Field parse(Element element, ParsingContext context) {
    	if (element.hasAttribute("name")) {
    		QName templateName;
    		if (element.hasAttribute("templateNs"))
    			templateName = new QName(element.getAttribute("name"), element.getAttribute("templateNs"));
    		else
    			templateName = new QName(element.getAttribute("name"), "");
    			
    		if (context.getTemplateRegistry().isDefined(templateName))
    			return new StaticTemplateReference(context.getTemplateRegistry().get(templateName));
    		else {
    			context.getErrorHandler().error(FastConstants.D8_TEMPLATE_NOT_EXIST, "The template \"" + templateName + "\" was not found.");
    			return null;
    		}
    	} else {
    		return new DynamicTemplateReference();
    	}
	}

	public boolean canParse(Element element, ParsingContext context) {
		return "templateRef".equals(element.getNodeName());
	}
}
