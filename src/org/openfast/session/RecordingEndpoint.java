package org.openfast.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openfast.util.RecordingInputStream;
import org.openfast.util.RecordingOutputStream;

public class RecordingEndpoint implements Endpoint {

	private Endpoint underlyingEndpoint;

	public RecordingEndpoint(Endpoint endpoint) {
		this.underlyingEndpoint = endpoint;
	}

	public void accept() throws FastConnectionException {
		underlyingEndpoint.accept();
	}

	public Connection connect() throws FastConnectionException {
		final Connection connection = underlyingEndpoint.connect();
		Connection connectionWrapper = new RecordingConnection(connection);
		return connectionWrapper;
	}

	public void setConnectionListener(ConnectionListener listener) {
		underlyingEndpoint.setConnectionListener(listener);
	}

	private final class RecordingConnection implements Connection {
		private final RecordingInputStream recordingInputStream;
		private final RecordingOutputStream recordingOutputStream;

		private RecordingConnection(Connection connection) {
			try {
				this.recordingInputStream = new RecordingInputStream(connection.getInputStream());
				this.recordingOutputStream = new RecordingOutputStream(connection.getOutputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void close() {
		}

		public InputStream getInputStream() throws IOException {
			return recordingInputStream;
		}

		public OutputStream getOutputStream() throws IOException {
			return recordingOutputStream;
		}
	}

	public void close() {
		underlyingEndpoint.close();
	}
}
