package org.openfast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openfast.error.ErrorHandler;
import org.openfast.session.SessionConstants;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;

public class Context {
	private Map templates = new HashMap();
	private int lastTemplateId;
	private Map dictionaries = new HashMap();
	private ErrorHandler errorHandler = ErrorHandler.DEFAULT;

	public Context() {
		dictionaries.put("global", new GlobalDictionary());
		dictionaries.put("template", new TemplateDictionary());
	}
	
	public MessageTemplate getTemplate(int templateId) {
		if (!templates.containsKey(new Integer(templateId))) {
			errorHandler.error(SessionConstants.TEMPLATE_NOT_SUPPORTED, "The template with id " + templateId + " has not been registered.");
			return null;	
		}
		return (MessageTemplate) templates.get(new Integer(templateId));
	}

	public void registerTemplate(int templateId, Group template) {
		templates.put(new Integer(templateId), template);
	}

	public int getLastTemplateId() {
		return lastTemplateId;
	}

	public void setLastTemplateId(int templateId) {
		lastTemplateId = templateId;
	}

	public ScalarValue lookup(String dictionary, Group template, String key) {
		return getDictionary(dictionary).lookup(template, key);
	}

	private Dictionary getDictionary(String dictionary) {
		return (Dictionary) dictionaries.get(dictionary);
	}

	public void store(String dictionary, Group template, String key, ScalarValue valueToEncode) {
		getDictionary(dictionary).store(template, key, valueToEncode);
	}

	public void reset() {
		for (Iterator iter = dictionaries.values().iterator(); iter.hasNext();) {
			Dictionary dict = (Dictionary) iter.next();
			dict.reset();
		}
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
}
