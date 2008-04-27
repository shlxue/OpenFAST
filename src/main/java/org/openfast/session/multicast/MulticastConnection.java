package org.openfast.session.multicast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.openfast.session.Connection;

public class MulticastConnection implements Connection {
    private MulticastSocket socket;
    private InetAddress group;

    public MulticastConnection(MulticastSocket socket, InetAddress group) {
        this.socket = socket;
        this.group = group;
    }

    public void close() {
        try {
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
        }
    }

    public InputStream getInputStream() throws IOException {
        return new MulticastInputStream(socket);
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Multicast sending not currently supported.");
    }
}
