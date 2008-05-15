package org.openfast.session;

import org.openfast.Message;
import org.openfast.error.ErrorCode;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public interface SessionProtocol {
    public void configureSession(Session session);
    public Session connect(String senderName, Connection connection, TemplateRegistry inboundRegistry, TemplateRegistry outboundRegistry, MessageListener messageListener, SessionListener sessionListener);
    public Session onNewConnection(String serverName, Connection connection);
    public void onError(Session session, ErrorCode code, String message);
    public Message getResetMessage();
    public boolean isProtocolMessage(Message message);
    public void handleMessage(Session session, Message message);
    // Template Exchange
    public boolean supportsTemplateExchange();
    public Message createTemplateDefinitionMessage(MessageTemplate messageTemplate);
    public Message createTemplateDeclarationMessage(MessageTemplate messageTemplate, int templateId);
    public Message getCloseMessage();
}
