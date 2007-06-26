package org.openfast.session;

public interface ConnectionListener {
	ConnectionListener NULL = new ConnectionListener() {
		public boolean isValid(Client client) {
			return true;
		}
		public void onConnect(Session session) {
		}};
		
	boolean isValid(Client client);
	void onConnect(Session session);
}
