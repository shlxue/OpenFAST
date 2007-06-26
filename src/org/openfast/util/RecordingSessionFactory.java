package org.openfast.util;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;
import org.openfast.session.Session;
import org.openfast.session.SessionFactory;

public class RecordingSessionFactory implements SessionFactory {

	private SessionFactory underlyingFactory;
	private RecordingInputStream in;
	private RecordingOutputStream out;

	public RecordingSessionFactory(SessionFactory underlyingFactory) {
		this.underlyingFactory = underlyingFactory;
	}
	
	public void close() throws FastConnectionException {
		underlyingFactory.close();
	}

	public Client getClient(String serverName) {
		return underlyingFactory.getClient(serverName);
	}

	public Session getSession() throws FastConnectionException {
		Session session = underlyingFactory.getSession();
		in = new RecordingInputStream(session.in.getUnderlyingStream());
		out = new RecordingOutputStream(session.out.getUnderlyingStream());
		return new Session(in, out);
	}
	
}
