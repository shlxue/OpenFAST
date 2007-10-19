package org.openfast.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openfast.QName;

public class BasicTemplateRegistry extends AbstractTemplateRegistry {
	private Map nameMap = new HashMap();
	private Map idMap = new HashMap();
	private Map templateMap = new HashMap();
	private List templates = new ArrayList();

	public void register(int id, MessageTemplate template) {
		define(template);
		Integer tid = new Integer(id);
		idMap.put(tid, template);
		templateMap.put(template, tid);
		notifyTemplateRegistered(template, id);
	}
	
	public void register(int id, QName name) {
		if (!nameMap.containsKey(name))
			throw new IllegalArgumentException("The template named " + name + " is not defined.");
		Integer tid = new Integer(id);
		MessageTemplate template = (MessageTemplate) nameMap.get(name);
		templateMap.put(template, tid);
		idMap.put(tid, template);
		notifyTemplateRegistered(template, id);
	}
	
	public void define(MessageTemplate template) {
		nameMap.put(template.getQName(), template);
		templates.add(template);
	}

	public int getId(QName name) {
		Object template = nameMap.get(name);
		if (template == null || !templateMap.containsKey(template)) return -1;
		return ((Integer)templateMap.get(template)).intValue();
	}

	public MessageTemplate get(int templateId) {
		return (MessageTemplate) idMap.get(new Integer(templateId));
	}

	public MessageTemplate get(QName name) {
		return (MessageTemplate) nameMap.get(name);
	}

	public int getId(MessageTemplate template) {
		if (!isRegistered(template)) return -1;
		return ((Integer)templateMap.get(template)).intValue();
	}

	public boolean isRegistered(QName name) {
		return nameMap.containsKey(name);
	}

	public boolean isRegistered(int templateId) {
		return idMap.containsKey(new Integer(templateId));
	}

	public boolean isRegistered(MessageTemplate template) {
		return templateMap.containsKey(template);
	}
	
	public boolean isDefined(QName name) {
		return nameMap.containsKey(name);
	}
	
	public MessageTemplate[] getTemplates() {
		return (MessageTemplate[]) templateMap.keySet().toArray(new MessageTemplate[templateMap.size()]);
	}

	public void remove(QName name) {
		MessageTemplate template = (MessageTemplate) nameMap.remove(name);
		Object id = templateMap.remove(template);
		idMap.remove(id);
		templates.remove(template);
	}

	public void remove(MessageTemplate template) {
		Object id = templateMap.remove(template);
		nameMap.remove(template.getName());
		idMap.remove(id);
	}

	public void remove(int id) {
		MessageTemplate template = (MessageTemplate) idMap.remove(new Integer(id));
		templateMap.remove(template);
		nameMap.remove(template.getName());
	}

	public void registerAll(TemplateRegistry registry) {
		MessageTemplate[] templates = registry.getTemplates();
		for (int i=0; i<templates.length; i++) {
			register(registry.getId(templates[i]), templates[i]);
		}
	}

	public Iterator nameIterator() {
		return nameMap.keySet().iterator();
	}

	public Iterator iterator() {
		return templates.iterator();
	}
}
