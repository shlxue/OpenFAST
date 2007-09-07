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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openfast.codec.FastDecoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegisteredListener;
import org.openfast.template.TemplateRegistry;


public class MessageInputStream implements MessageStream {
    private InputStream in;
    private FastDecoder decoder;
    private Context context;
    private Map handlers = Collections.EMPTY_MAP;

    public MessageInputStream(InputStream inputStream) {
        this(inputStream, new Context());
    }

    public MessageInputStream(InputStream inputStream, Context context) {
        this.in = inputStream;
        this.context = context;
        this.decoder = new FastDecoder(context, in);
    }

    /**
     * @throws java.io.EOFException
     * @return the next message in the stream
     */
    public Message readMessage() {
        Message message = decoder.readMessage();

        if (message == null) {
            return null;
        }

        if (handlers.containsKey(message.getTemplate())) {
            MessageHandler handler = (MessageHandler) handlers.get(message.getTemplate());
			handler.handleMessage(message, context,
                decoder);

            return readMessage();
        }

        return message;
    }

    public void registerTemplate(int templateId, MessageTemplate template) {
        context.registerTemplate(templateId, template);
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getUnderlyingStream() {
        return in;
    }

    public void addMessageHandler(MessageTemplate template, MessageHandler handler) {
        if (handlers == Collections.EMPTY_MAP) {
            handlers = new HashMap();
        }

        handlers.put(template, handler);
    }

	public void setTemplateRegistry(TemplateRegistry registry) {
		context.setTemplateRegistry(registry);
	}

	public MessageTemplate get(int templateId) {
		return context.get(templateId);
	}

	public MessageTemplate get(String templateName) {
		return context.get(templateName);
	}

	public int getTemplateId(String templateName) {
		return context.getTemplateId(templateName);
	}

	public int getTemplateId(MessageTemplate template) {
		return context.getTemplateId(template);
	}

	public boolean isRegistered(String templateName) {
		return context.isRegistered(templateName);
	}

	public boolean isRegistered(int templateId) {
		return context.isRegistered(templateId);
	}

	public boolean isRegistered(MessageTemplate template) {
		return context.isRegistered(template);
	}

	public MessageTemplate[] getTemplates() {
		return context.getTemplates();
	}

	public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
		context.addTemplateRegisteredListener(templateRegisteredListener);
	}

	public void removeTemplate(String name) {
		context.removeTemplate(name);
	}

	public void removeTemplate(MessageTemplate template) {
		context.removeTemplate(template);
	}

	public void removeTemplate(int id) {
		context.removeTemplate(id);
	}
}
