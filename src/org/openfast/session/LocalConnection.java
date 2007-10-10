package org.openfast.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class LocalConnection implements Connection {

	private PipedInputStream in;
	private PipedOutputStream out;

	public LocalConnection(LocalEndpoint remote, LocalEndpoint local) {
		this.in = new PipedInputStream();
		this.out = new PipedOutputStream();
	}

	public LocalConnection(LocalConnection localConnection) {
		try {
			this.in = new PipedInputStream((PipedOutputStream) localConnection.getOutputStream());
			this.out = new PipedOutputStream((PipedInputStream) localConnection.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			in.close();
		} catch (IOException e) {
		}
		try {
			out.close();
		} catch (IOException e) {
		}
	}

	public InputStream getInputStream() throws IOException {
		return in;
	}

	public OutputStream getOutputStream() throws IOException {
		return out;
	}

}
