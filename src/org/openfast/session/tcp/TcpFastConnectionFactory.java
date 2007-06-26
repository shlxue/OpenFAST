package org.openfast.session.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;
import org.openfast.session.FastConnectionFactory;
import org.openfast.session.Session;


public class TcpFastConnectionFactory extends FastConnectionFactory {

	private final int port;
	private String host;
	private Map sessionMap = new HashMap();

	public TcpFastConnectionFactory(int port) {
		this.port = port;
	}
	
	public TcpFastConnectionFactory(String host, int port) {
		this(port);
		this.host = host;
	}

	protected Session connectInternal() throws FastConnectionException {
		Socket socket;
		try {
			socket = new Socket(host, port);
			Session session = new Session(socket.getInputStream(), socket.getOutputStream());
			sessionMap.put(session, socket);
			return session;
		} catch (UnknownHostException e) {
			throw new FastConnectionException(e);
		} catch (IOException e) {
			throw new FastConnectionException(e);
		}
	}

	protected Client getClient(Session session, String clientName) {
		Socket socket = (Socket) sessionMap.get(session);
		TcpClient client = new TcpClient(clientName, socket);
		return client;
	}
}
