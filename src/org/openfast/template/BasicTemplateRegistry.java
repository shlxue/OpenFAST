package org.openfast.template;

import java.util.HashMap;
import java.util.Map;

public class BasicTemplateRegistry implements TemplateRegistry {
	private Map templateNameMap = new HashMap();
	private Map templateIdMap = new HashMap();
	private Map templateMap = new HashMap();

	public void registerTemplate(int templateId, MessageTemplate template) {
		templateNameMap.put(template.getName(), template);
		Integer tid = new Integer(templateId);
		templateIdMap.put(tid, template);
		templateMap.put(template, tid);
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
}
