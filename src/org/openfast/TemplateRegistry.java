package org.openfast;

import org.openfast.template.MessageTemplate;

public interface TemplateRegistry {
	void registerTemplate(int templateId, MessageTemplate template);
}
