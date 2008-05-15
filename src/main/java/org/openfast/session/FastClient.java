package org.openfast.session;

import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.TemplateRegistry;

public class FastClient {
    private final String clientName;
    private final Endpoint endpoint;
    private final SessionProtocol sessionProtocol;
    private TemplateRegistry inboundRegistry = new BasicTemplateRegistry();
    private TemplateRegistry outboundRegistry = new BasicTemplateRegistry();
    private MessageListener messageListener = MessageListener.NULL;
    private SessionListener sessionListener = SessionListener.NULL;

    public FastClient(String clientName, SessionProtocol sessionProtocol, Endpoint endpoint) {
        this.clientName = clientName;
        this.sessionProtocol = sessionProtocol;
        this.endpoint = endpoint;
    }
    
    public void setInboundTemplateRegistry(TemplateRegistry registry) {
        this.inboundRegistry = registry;
    }
    
    public TemplateRegistry getInboundTemplateRegistry() {
        return this.inboundRegistry;
    }
    
    public void setOutboundTemplateRegistry(TemplateRegistry registry) {
        this.outboundRegistry = registry;
    }
    
    public TemplateRegistry getOutboundTemplateRegistry() {
        return this.outboundRegistry;
    }
    
    public Session connect() throws FastConnectionException {
        Connection connection = endpoint.connect();
        Session session = sessionProtocol.connect(clientName, connection, inboundRegistry, outboundRegistry,
                                                  messageListener, sessionListener);
        return session;
    }

    public void setMessageHandler(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
