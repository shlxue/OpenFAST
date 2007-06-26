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
import org.openfast.session.Session;
import org.openfast.session.SessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;


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
        if (server == null) {
            server = new ServerSocket(port);
        }

        return server;
    }

    protected Socket getSocket() {
        return socket;
    }

    public void close() throws FastConnectionException {
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                throw new FastConnectionException(e);
            }
        }
    }
}
