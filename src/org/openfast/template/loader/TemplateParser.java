package org.openfast.template.loader;

import org.openfast.QName;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.w3c.dom.Element;

public class TemplateParser extends GroupParser {

	private boolean loadTemplateIdFromAuxId;

	public TemplateParser(boolean loadTemplateIdFromAuxId) {
		this.loadTemplateIdFromAuxId = loadTemplateIdFromAuxId;
	}

	/**
     * Creates a MessageTemplate object from the dom template element
	 * @param context 
	 * @param templateElement The dom element object
     * @return Returns a newly created MessageTemplate object
     */
	protected Field parse(Element templateElement, boolean optional, ParsingContext context) {
		MessageTemplate messageTemplate = new MessageTemplate(getTemplateName(templateElement, context), parseFields(templateElement, context));
		parseMore(templateElement, messageTemplate, context);
		if (loadTemplateIdFromAuxId && templateElement.hasAttribute("id")) {
			try {
				int templateId = Integer.parseInt(templateElement.getAttribute("id"));
				context.getTemplateRegistry().register(templateId, messageTemplate);
			} catch (NumberFormatException e) {
				context.getTemplateRegistry().define(messageTemplate);
			}
		} else
			context.getTemplateRegistry().define(messageTemplate);
		return messageTemplate;
	}

	private QName getTemplateName(Element templateElement, ParsingContext context) {
		return new QName(templateElement.getAttribute("name"), context.getTemplateNamespace());
	}
}
