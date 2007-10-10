package org.openfast.session;

import java.util.ArrayList;
import java.util.List;

public class LocalEndpoint implements Endpoint {
	
	private LocalEndpoint server;
	private ConnectionListener listener;
	private List connections;

	public LocalEndpoint() {
		connections = new ArrayList(3);
	}
	
	public LocalEndpoint(LocalEndpoint server) {
		this.server = server;
	}

	public void accept() throws FastConnectionException {
		if (!connections.isEmpty()) {
			synchronized(this) {
				Connection connection = (Connection) connections.remove(0);
				listener.onConnect(connection);
			}
		}
	}

	public Connection connect() throws FastConnectionException {
		LocalConnection localConnection = new LocalConnection(server, this);
		LocalConnection remoteConnection = new LocalConnection(localConnection);
		synchronized (server) {
			server.connections.add(remoteConnection);
		}
		return localConnection;
	}

	public void setConnectionListener(ConnectionListener listener) {
		this.listener = listener;
	}

}
