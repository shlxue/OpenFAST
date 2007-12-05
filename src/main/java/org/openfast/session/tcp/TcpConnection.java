package org.openfast.session.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.openfast.session.Connection;

class TcpConnection implements Connection {

	private final Socket socket;

	public TcpConnection(Socket socket) {
		if (socket == null) throw new NullPointerException();
		this.socket = socket;
	}

	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
}
