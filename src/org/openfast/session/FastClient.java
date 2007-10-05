package org.openfast.session;

public class FastClient {
	private final String clientName;
	private final Endpoint endpoint;
	private final SessionProtocol sessionProtocol;

	public FastClient(String clientName, SessionProtocol sessionProtocol, Endpoint endpoint) {
		this.clientName = clientName;
		this.sessionProtocol = sessionProtocol;
		this.endpoint = endpoint;
	}
	
	public Session connect() throws FastConnectionException {
		Connection connection = endpoint.connect();
		Session session = sessionProtocol.connect(clientName, connection);
		return session;
	}
}
