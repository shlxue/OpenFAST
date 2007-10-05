package org.openfast.session;

public interface SessionHandler {
	SessionHandler NULL = new SessionHandler() {
		public void newSession(Session session) {
		}};

	void newSession(Session session);
}
