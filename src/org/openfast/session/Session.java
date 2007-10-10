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


package org.openfast.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.MessageOutputStream;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;


public class Session implements ErrorHandler {
    private ErrorHandler errorHandler = ErrorHandler.NULL;
    public final MessageInputStream in;
    public final MessageOutputStream out;
    
    private final SessionProtocol protocol;
    private final Connection connection;
    
    private Client client;
	private MessageListener messageListener;
	private boolean listening;
	private Thread listeningThread;
	private Map templateDefinitions;

    public Session(Connection connection, SessionProtocol protocol) {
        Context inContext = new Context();
        Context outContext = new Context();
        inContext.setErrorHandler(this);
        
        this.connection = connection;
        this.protocol = protocol;
        try {
			this.in = new MessageInputStream(connection.getInputStream(), inContext);
			this.out = new MessageOutputStream(connection.getOutputStream(), outContext);
		} catch (IOException e) {
			errorHandler.error(null, "Error occurred in connection.", e);
			throw new IllegalStateException(e);
		}
        
        protocol.configureSession(this);
    }

    public void close() throws FastConnectionException {
        in.close();
        out.close();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void error(ErrorCode code, String message) {
		if (code.equals(FastConstants.D9_TEMPLATE_NOT_REGISTERED)) {
			code = SessionConstants.TEMPLATE_NOT_SUPPORTED;
			message = "Template Not Supported";
		}
		protocol.onError(this, code, message);
        errorHandler.error(code, message);
    }

    public void error(ErrorCode code, String message, Throwable t) {
    	protocol.onError(this, code, message);
        errorHandler.error(code, message, t);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            this.errorHandler = ErrorHandler.NULL;
        }

        this.errorHandler = errorHandler;
    }

	public void reset() {
		out.reset();
		in.reset();
		out.writeMessage(protocol.getResetMessage());
	}

	public Connection getConnection() {
		return connection;
	}

	public void setMessageHandler(MessageListener messageListener) {
		this.messageListener = messageListener;
		setListening(true);
	}

	private void listenForMessages() {
		if (listeningThread == null) {
			Runnable messageReader = new Runnable() {
				public void run() {
					while (listening) {
						Message message = in.readMessage();
						if (message == null) {
							listening = false;
							break;
						}
						if (protocol.isProtocolMessage(message)) {
							protocol.handleMessage(Session.this, message);
						} else if (messageListener != null) {
							messageListener.onMessage(message);
						} else {
							throw new IllegalStateException("Received non-protocol message without a message listener.");
						}
					}
				}};
			listeningThread = new Thread(messageReader, "FAST Session Message Reader");
		}
		if (listeningThread.isAlive()) return;
		listeningThread.start();
	}

	public void setListening(boolean listening) {
		this.listening = listening;
		if (listening)
			listenForMessages();
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void sendTemplates(TemplateRegistry registry) {
		if (!protocol.supportsTemplateExchange()) {
			throw new UnsupportedOperationException("The procotol " + protocol + " does not support template exchange.");
		}
		MessageTemplate[] templates = registry.getTemplates();
		for (int i=0; i<templates.length; i++) {
			MessageTemplate template = templates[i];
			out.writeMessage(protocol.createTemplateDefinitionMessage(template));
			out.writeMessage(protocol.createTemplateDeclarationMessage(template, registry.getId(template)));
			if (!out.getTemplateRegistry().isRegistered(template))
				out.registerTemplate(registry.getId(template), template);
		}
	}
	
	public void addDynamicTemplateDefinition(MessageTemplate template) {
		if (templateDefinitions == null) {
			templateDefinitions = new HashMap();
		}
		templateDefinitions.put(template.getName(), template);
	}

	public void registerDynamicTemplate(String templateName, int id) {
		if (!templateDefinitions.containsKey(templateName))
			throw new IllegalStateException("Template " + templateName + " has not been defined.");
		in.registerTemplate(id, (MessageTemplate) templateDefinitions.get(templateName));
	}
}
