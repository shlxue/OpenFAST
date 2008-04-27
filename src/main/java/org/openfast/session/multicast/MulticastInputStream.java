package org.openfast.session.multicast;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class MulticastInputStream extends InputStream {
    private static final int BUFFER_SIZE = 4 * 1024 * 1024;
    private MulticastSocket socket;
    private ByteBuffer buffer;

    public MulticastInputStream(MulticastSocket socket) {
        this.socket = socket;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffer.flip();
    }

    public int read() throws IOException {
        if (socket.isClosed()) return -1;
        if (!buffer.hasRemaining()) {
            buffer.flip();
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length);
            socket.receive(packet);
            buffer.limit(packet.getLength());
        }
        return buffer.get();
    }
}
