package org.openfast.session.tcp;

import java.io.IOException;
import java.net.Socket;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;

public class TcpClient extends Client {
	private final Socket socket;

	public TcpClient(String name, Socket socket) {
		super(name);
		this.socket = socket;
	}

	public String toString() {
		return name + "@" + socket.getInetAddress() + ":" + socket.getPort();
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void disconnect() throws FastConnectionException {
		try {
			socket.close();
		} catch (IOException e) {
			throw new FastConnectionException(e);
		}
	}
}
