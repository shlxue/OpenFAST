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
