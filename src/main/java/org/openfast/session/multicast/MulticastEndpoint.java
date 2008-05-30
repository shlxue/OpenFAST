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
package org.openfast.session.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.openfast.session.Connection;
import org.openfast.session.ConnectionListener;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;

public class MulticastEndpoint implements Endpoint {
    private int port;
    private String group;
    public MulticastEndpoint(int port, String group) {
        this.port = port;
        this.group = group;
    }
    public void accept() throws FastConnectionException {
        throw new UnsupportedOperationException();
    }
    public void close() {}
    public Connection connect() throws FastConnectionException {
        try {
            MulticastSocket socket = new MulticastSocket(port);
            InetAddress groupAddress = InetAddress.getByName(group);
            socket.joinGroup(groupAddress);
            return new MulticastConnection(socket, groupAddress);
        } catch (IOException e) {
            throw new FastConnectionException(e);
        }
    }
    public void setConnectionListener(ConnectionListener listener) {
        throw new UnsupportedOperationException();
    }
}
