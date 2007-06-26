package org.openfast.session;

import org.openfast.Message;
import org.openfast.error.ErrorHandler;

public class FastServer {
	private final SessionFactory sessionFactory;
	private final String serverName;
	private ErrorHandler errorHandler = ErrorHandler.DEFAULT;
	private ConnectionListener connectionListener = ConnectionListener.NULL;
	
	public FastServer(String serverName, SessionFactory sessionFactory) {
		if (serverName == null || serverName.trim().equals("") || sessionFactory == null) throw new NullPointerException();
		this.serverName = serverName;
		this.sessionFactory = sessionFactory;
	}
	
	public void listen() throws FastConnectionException {
		while (true) {
			Session session = sessionFactory.getSession();
			Message helloMessage = session.in.readMessage();
			if (helloMessage.getTemplateId() != Session.FAST_HELLO_TEMPLATE_ID) {
				throw new FastConnectionException("Client tried to connect without sending a hello message.");
			}
			String name = helloMessage.getString(1);
			Client client = sessionFactory.getClient(name);
			session.setClient(client);
			if (connectionListener.isValid(client)) {
				session.out.writeMessage(Session.createHelloMessage(serverName));
				connectionListener.onConnect(session);
			} else {
				session.out.writeMessage(Session.createFastAlertMessage(SessionConstants.UNAUTHORIZED));
				errorHandler.error(SessionConstants.UNAUTHORIZED, "Client \"" + client.toString() + "\" tried to connect, but was not authorized.");
			}
		}
	}
	
	public void close() throws FastConnectionException {
		sessionFactory.close();
	}
	
	// ************* OPTIONAL DEPENDENCY SETTERS **************
	public void setConnectionListener(ConnectionListener listener) {
		if (listener == null) throw new NullPointerException();
		this.connectionListener = listener;
	}
	
	public void setErrorHandler(ErrorHandler errorHandler) {
		if (errorHandler == null) throw new NullPointerException();
		this.errorHandler = errorHandler;
	}
}
