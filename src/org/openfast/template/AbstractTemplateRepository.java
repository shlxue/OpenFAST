package org.openfast.template;

import org.openfast.QName;

public abstract class AbstractTemplateRepository implements TemplateRepository{
	public MessageTemplate getTemplate(String name) {
		return getTemplate(new QName(name, ""));
	}
	
	public boolean hasTemplate(String name) {
		return hasTemplate(new QName(name, ""));
	}
	
}
