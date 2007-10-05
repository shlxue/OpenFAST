package org.openfast.session;

public interface Endpoint {
	Connection connect() throws FastConnectionException;
	void setConnectionListener(ConnectionListener listener);
	void accept() throws FastConnectionException;
}
