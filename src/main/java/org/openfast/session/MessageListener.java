package org.openfast.session;

import org.openfast.Message;

public interface MessageListener {
	MessageListener NULL = new MessageListener() {
        public void onMessage(Session session, Message message) {}};

    void onMessage(Session session, Message message);
}
