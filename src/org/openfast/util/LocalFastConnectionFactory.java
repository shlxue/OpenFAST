/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.util;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;
import org.openfast.session.FastConnectionFactory;
import org.openfast.session.Session;
import org.openfast.session.SessionFactory;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;


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
                }
            };
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
