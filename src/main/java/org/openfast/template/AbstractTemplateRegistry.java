package org.openfast.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openfast.QName;

public abstract class AbstractTemplateRegistry implements TemplateRegistry {
	private List listeners = Collections.EMPTY_LIST;

	public MessageTemplate get(String name) {
		return get(new QName(name, ""));
	}

	public int getId(String name) {
		return getId(new QName(name, ""));
	}

	public boolean isDefined(String name) {
		return isDefined(new QName(name, ""));
	}

	public boolean isRegistered(String name) {
		return isRegistered(new QName(name, ""));
	}

	public void register(int templateId, String name) {
		register(templateId, new QName(name, ""));
	}

	public void remove(String name) {
		remove(new QName(name, ""));
	}
	
	protected void notifyTemplateRegistered(MessageTemplate template, int id) {
		for (int i=0; i<listeners.size(); i++)
			((TemplateRegisteredListener) listeners.get(i)).templateRegistered(template, id);
	}

	public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
		if (this.listeners == Collections.EMPTY_LIST)
			this.listeners = new ArrayList(3);
		this.listeners.add(templateRegisteredListener);
	}

	public void removeTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
		this.listeners.remove(templateRegisteredListener);
	}

}
