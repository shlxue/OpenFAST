package org.openfast;

public interface MessageStream extends TemplateRegistry {
	void addMessageHandler(int templateId, MessageHandler handler);
	void close();
}
