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


package org.openfast;

import org.openfast.session.Client;
import org.openfast.session.ConnectionListener;
import org.openfast.session.FastConnectionException;
import org.openfast.session.FastServer;
import org.openfast.session.Session;
import org.openfast.session.SessionFactory;
import org.openfast.session.tcp.TcpSessionFactory;

import org.openfast.template.MessageTemplate;

import org.openfast.util.RecordingSessionFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class EchoServer {
    private static final int DEFAULT_PORT = 9001;
    public List sessions = new ArrayList();
    private FastServer server;
    private SessionFactory sessionFactory;
    private ClientRegistry templateRegistry;

    public EchoServer(SessionFactory sessionFactory,
        ClientRegistry templateRegistry) {
        this.sessionFactory = sessionFactory;
        this.templateRegistry = templateRegistry;
    }

    public void start() throws FastConnectionException {
        server = new FastServer("EchoServer", sessionFactory);
        server.setConnectionListener(new ConnectionListener() {
                public boolean isValid(Client client) {
                    return true;
                }

                public void onConnect(Session session) {
                    Map templateMap = templateRegistry.getTemplates(session.getClient());

                    for (Iterator i = templateMap.keySet().iterator();
                            i.hasNext();) {
                        Integer templateId = (Integer) i.next();
                        session.in.registerTemplate(templateId.intValue(),
                            (MessageTemplate) templateMap.get(templateId));
                        session.out.registerTemplate(templateId.intValue(),
                            (MessageTemplate) templateMap.get(templateId));
                    }

                    System.out.println("Connected to " +
                        session.getClient().getName());
                    new Thread(new EchoSession(session)).start();
                }
            });

        try {
            server.listen();
        } catch (Throwable t) {
            t.printStackTrace();
            close();
        }
    }

    private void close() {
        try {
            server.close();
        } catch (FastConnectionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < sessions.size(); i++) {
            try {
                ((Session) sessions.get(i)).close();
            } catch (FastConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws FastConnectionException {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        RecordingSessionFactory sessionFactory = new RecordingSessionFactory(new TcpSessionFactory(
                    port));
        new EchoServer(sessionFactory,
            new DirectoryBasedClientRegistry("CheckForNull:\templates")).start();
    }

    private class EchoSession implements Runnable {
        private Session session;

        public EchoSession(Session session) {
            this.session = session;
            sessions.add(session);
        }

        public void run() {
            try {
                while (true) {
                    Message message = session.in.readMessage();
                    session.out.writeMessage(message);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                close();
            }

            System.out.println("IN: " +
                session.in.getUnderlyingStream().toString());
            System.out.println("OUT: " +
                session.out.getUnderlyingStream().toString());
        }

        public void close() {
            try {
                session.close();
            } catch (FastConnectionException e) {
                e.printStackTrace();
            }

            sessions.remove(session);
        }
    }
}
