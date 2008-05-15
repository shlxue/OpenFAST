package org.openfast.session.multicast;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class MulticastInputStream extends InputStream {
    private static final int BUFFER_SIZE = 256 * 1024;
    private MulticastSocket socket;
    private ByteBuffer buffer;

    public MulticastInputStream(MulticastSocket socket) {
        this(socket, BUFFER_SIZE);
    }
    
    public MulticastInputStream(MulticastSocket socket, int bufferSize) {
        this.socket = socket;
        this.buffer = ByteBuffer.allocate(bufferSize);
        buffer.flip();
    }

    public int read() throws IOException {
        if (socket.isClosed()) return -1;
        if (!buffer.hasRemaining()) {
            buffer.clear();
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.capacity());
            socket.receive(packet);
            buffer.flip();
            buffer.limit(packet.getLength());
        }
        return (int)(buffer.get() & 0xFF);
    }
}
