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


package org.openfast.session.tcp;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;
import org.openfast.session.FastConnectionFactory;
import org.openfast.session.Session;

import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.Map;


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

            Session session = new Session(socket.getInputStream(),
                    socket.getOutputStream());
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
