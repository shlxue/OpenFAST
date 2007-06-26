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

import java.io.IOException;

import java.net.Socket;


public class TcpClient extends Client {
    private final Socket socket;

    public TcpClient(String name, Socket socket) {
        super(name);
        this.socket = socket;
    }

    public String toString() {
        return name + "@" + socket.getInetAddress() + ":" + socket.getPort();
    }

    public Socket getSocket() {
        return socket;
    }

    public void disconnect() throws FastConnectionException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new FastConnectionException(e);
        }
    }
}
