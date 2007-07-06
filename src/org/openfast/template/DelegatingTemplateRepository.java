package org.openfast.template;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DelegatingTemplateRepository implements TemplateRepository {
	private TemplateRepository repository;
	private Map templateNameMap = new LinkedHashMap();
	private Map templateIdMap = new HashMap();

	public DelegatingTemplateRepository(TemplateRepository repository) {
		this.repository = repository;
	}

	public MessageTemplate getTemplate(String name) {
		if (!templateNameMap.containsKey(name))
			return repository.getTemplate(name);
		return (MessageTemplate) templateNameMap.get(name);
	}

	public MessageTemplate getTemplate(int id) {
		if (!templateIdMap.containsKey(new Integer(id)))
			return repository.getTemplate(id);
		return (MessageTemplate) templateIdMap.get(new Integer(id));
	}

	public boolean hasTemplate(String name) {
		return templateNameMap.containsKey(name) || repository.hasTemplate(name);
	}

	public boolean hasTemplate(int id) {
		return templateIdMap.containsKey(new Integer(id)) || repository.hasTemplate(id);
	}

	public void setRepository(TemplateRepository repository) {
		this.repository = repository;
	}

	public void add(MessageTemplate template) {
		templateNameMap.put(template.getName(), template);
	}

	public MessageTemplate[] toArray() {
		return (MessageTemplate[]) templateNameMap.values().toArray(new MessageTemplate[templateNameMap.size()]);
	}
}
