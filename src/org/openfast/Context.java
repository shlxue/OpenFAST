/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;


public class Context implements TemplateRegistry {
	private Map templates = new HashMap();
	private Map templateIds = new HashMap();
    private int lastTemplateId;
    private Map dictionaries = new HashMap();
    private ErrorHandler errorHandler = ErrorHandler.DEFAULT;
	private String currentApplicationType;

    public Context() {
        dictionaries.put("global", new GlobalDictionary());
        dictionaries.put("template", new TemplateDictionary());
        dictionaries.put("type", new ApplicationTypeDictionary());
    }

    public int getId(MessageTemplate template) {
        if (!templateIds.containsKey(template)) {
            errorHandler.error(FastConstants.D9_TEMPLATE_NOT_REGISTERED, "The template " + template + " has not been registered.");
            return 0;
        }

		return ((Integer) templateIds.get(template)).intValue();
	}

	public MessageTemplate getTemplate(int templateId) {
        if (!templates.containsKey(new Integer(templateId))) {
            errorHandler.error(FastConstants.D9_TEMPLATE_NOT_REGISTERED, "The template with id " + templateId + " has not been registered.");
            return null;
        }

        return (MessageTemplate) templates.get(new Integer(templateId));
    }

    public void registerTemplate(int templateId, MessageTemplate template) {
        Integer id = new Integer(templateId);
		templates.put(id, template);
		templateIds.put(template, id);
    }

    public int getLastTemplateId() {
        return lastTemplateId;
    }

    public void setLastTemplateId(int templateId) {
        lastTemplateId = templateId;
    }

    public ScalarValue lookup(String dictionary, Group group, String key) {
    	if (group.hasTypeReference())
    		currentApplicationType = group.getTypeReference();
        return getDictionary(dictionary).lookup(group, key, currentApplicationType);
    }

    private Dictionary getDictionary(String dictionary) {
        return (Dictionary) dictionaries.get(dictionary);
    }

    public void store(String dictionary, Group group, String key, ScalarValue valueToEncode) {
    	if (group.hasTypeReference())
    		currentApplicationType = group.getTypeReference();
        getDictionary(dictionary).store(group, currentApplicationType, key, valueToEncode);
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

	public void newMessage(MessageTemplate template) {
		currentApplicationType = (template.hasTypeReference()) ? template.getTypeReference() : FastConstants.ANY;
	}

	public void setCurrentApplicationType(String typeReference) {
		currentApplicationType = typeReference;
	}
}
