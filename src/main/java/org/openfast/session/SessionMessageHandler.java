package org.openfast.session;

import org.openfast.Message;

public interface SessionMessageHandler {
	void handleMessage(Session session, Message message);
}
