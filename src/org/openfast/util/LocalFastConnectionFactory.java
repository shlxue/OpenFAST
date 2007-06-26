package org.openfast.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;
import org.openfast.session.FastConnectionFactory;
import org.openfast.session.Session;
import org.openfast.session.SessionFactory;

public class LocalFastConnectionFactory extends FastConnectionFactory {
	private final Semaphore sessionLock = new Semaphore(0);
	private final Queue newSessions = new LinkedList();
	
	public LocalFastConnectionFactory() {
	}
	
	public SessionFactory createLocalSessionFactory() {
		return new SessionFactory() {
			public void close() throws FastConnectionException {
			}
			public Client getClient(String serverName) {
				return new Client(serverName);
			}
			public Session getSession() throws FastConnectionException {
				try {
					sessionLock.acquire();
					Session serverSession = (Session) newSessions.remove();
					return serverSession;
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new FastConnectionException(e);
				}
			}};
	}

	protected Session connectInternal() throws FastConnectionException {
		try {
			PipedOutputStream serverOut = new PipedOutputStream();
			PipedInputStream clientIn = new PipedInputStream(serverOut);
			PipedOutputStream clientOut = new PipedOutputStream();
			PipedInputStream serverIn = new PipedInputStream(clientOut);
			Session session = new Session(clientIn, clientOut);
			Session serverSession = new Session(serverIn, serverOut);
			newSessions.add(serverSession);
			sessionLock.release();
			return session;

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected Client getClient(Session session, String clientName) {
		return new Client(clientName);
	}

}
