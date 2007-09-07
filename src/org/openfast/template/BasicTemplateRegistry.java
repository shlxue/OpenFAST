package org.openfast.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BasicTemplateRegistry implements TemplateRegistry {
	private Map templateNameMap = new HashMap();
	private Map templateIdMap = new HashMap();
	private Map templateMap = new HashMap();
	private List listeners = Collections.EMPTY_LIST;

	public void registerTemplate(int templateId, MessageTemplate template) {
		templateNameMap.put(template.getName(), template);
		Integer tid = new Integer(templateId);
		templateIdMap.put(tid, template);
		templateMap.put(template, tid);
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
        	((TemplateRegisteredListener) iter.next()).templateRegistered(template, templateId);
        }
	}

	public int getTemplateId(String templateName) {
		Object template = templateNameMap.get(templateName);
		if (template == null || !templateMap.containsKey(template)) return -1;
		return ((Integer)templateMap.get(template)).intValue();
	}

	public boolean isRegistered(String templateName) {
		return templateNameMap.containsKey(templateName);
	}

	public MessageTemplate get(int templateId) {
		return (MessageTemplate) templateIdMap.get(new Integer(templateId));
	}

	public MessageTemplate get(String templateName) {
		return (MessageTemplate) templateNameMap.get(templateName);
	}

	public int getTemplateId(MessageTemplate template) {
		if (!isRegistered(template)) return -1;
		return ((Integer)templateMap.get(template)).intValue();
	}

	public boolean isRegistered(int templateId) {
		return templateIdMap.containsKey(new Integer(templateId));
	}

	public boolean isRegistered(MessageTemplate template) {
		return templateMap.containsKey(template);
	}

	public MessageTemplate[] getTemplates() {
		return (MessageTemplate[]) templateMap.keySet().toArray(new MessageTemplate[templateMap.size()]);
	}

	public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
		if (listeners.isEmpty()) {
			listeners = new ArrayList();
		}
		listeners.add(templateRegisteredListener);
	}

	public void removeTemplate(String name) {
		MessageTemplate template = (MessageTemplate) templateNameMap.remove(name);
		Object id = templateMap.remove(template);
		templateIdMap.remove(id);
	}

	public void removeTemplate(MessageTemplate template) {
		Object id = templateMap.remove(template);
		templateNameMap.remove(template.getName());
		templateIdMap.remove(id);
	}

	public void removeTemplate(int id) {
		MessageTemplate template = (MessageTemplate) templateIdMap.remove(new Integer(id));
		templateMap.remove(template);
		templateNameMap.remove(template.getName());
	}
}
