package org.openfast.session;

public interface SessionFactory {
	SessionFactory NULL = new SessionFactory() {
		public void close() throws FastConnectionException {
		}
		public Client getClient(String serverName) {
			return null;
		}
		public Session getSession() throws FastConnectionException {
			return null;
		}};
		
	Session getSession() throws FastConnectionException;
	Client getClient(String serverName);
	void close() throws FastConnectionException;
}
