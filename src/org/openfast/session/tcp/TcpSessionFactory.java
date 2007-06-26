package org.openfast.session.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;
import org.openfast.session.Session;
import org.openfast.session.SessionFactory;

public class TcpSessionFactory implements SessionFactory {

	private int port;
	private ServerSocket server;
	private Socket socket;

	public TcpSessionFactory(int port) {
		this.port = port;
	}

	public Session getSession() throws FastConnectionException {
		try {
			server = getServer();
			socket = server.accept();
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			return new Session(in, out);
		} catch (IOException e) {
			throw new FastConnectionException(e);
		}
	}

	public Client getClient(String serverName) {
		return new TcpClient(serverName, socket);
	}

	private ServerSocket getServer() throws IOException {
		if (server == null)
			server = new ServerSocket(port);
		return server;
	}
	
	protected Socket getSocket() {
		return socket;
	}

	public void close() throws FastConnectionException {
		if (server != null)
			try {
				server.close();
			} catch (IOException e) {
				throw new FastConnectionException(e);
			}
	}

}
