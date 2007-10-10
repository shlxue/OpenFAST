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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegisteredListener;
import org.openfast.template.TemplateRegistry;


public class Context {
	private TemplateRegistry templateRegistry = new BasicTemplateRegistry();
    private int lastTemplateId;
    private Map dictionaries = new HashMap();
    private ErrorHandler errorHandler = ErrorHandler.DEFAULT;
	private String currentApplicationType;
	private List listeners = Collections.EMPTY_LIST;

    public Context() {
        dictionaries.put("global", new GlobalDictionary());
        dictionaries.put("template", new TemplateDictionary());
        dictionaries.put("type", new ApplicationTypeDictionary());
    }

    public int getTemplateId(MessageTemplate template) {
        if (!templateRegistry.isRegistered(template)) {
            errorHandler.error(FastConstants.D9_TEMPLATE_NOT_REGISTERED, "The template " + template + " has not been registered.");
            return 0;
        }

		return templateRegistry.getId(template);
	}

	public MessageTemplate getTemplate(int templateId) {
        if (!templateRegistry.isRegistered(templateId)) {
            errorHandler.error(FastConstants.D9_TEMPLATE_NOT_REGISTERED, "The template with id " + templateId + " has not been registered.");
            return null;
        }

        return templateRegistry.get(templateId);
    }

    public void registerTemplate(int templateId, MessageTemplate template) {
        templateRegistry.registerTemplate(templateId, template);
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
        	((TemplateRegisteredListener) iter.next()).templateRegistered(template, templateId);
        }
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

	public TemplateRegistry getTemplateRegistry() {
		return templateRegistry;
	}

	public void setTemplateRegistry(TemplateRegistry registry) {
		this.templateRegistry = registry;
	}
}
