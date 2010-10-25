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
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import junit.framework.TestCase;

public class MulticastOutputStreamTest extends TestCase {
    final static byte[] MESSAGE_A = "MESSAGE_A".getBytes();
    final static byte[] MESSAGE_B = "MESSAGE_B".getBytes();
    final static byte[] MESSAGE_C = "MESSAGE_C".getBytes();
    int port;
    InetAddress group;
    MockMulticastSocket socket;
    MulticastOutputStream multicastOutputStream;

    public class MockMulticastSocket extends MulticastSocket {
        public Deque packetsSent = new LinkedList();
        
        public MockMulticastSocket(int port) throws IOException {
            super(port);
        }

        public void send(DatagramPacket packet) throws IOException {
            packetsSent.push(packet);
        }
    }

    public static void assertPacketEquals(byte[] expected, DatagramPacket actual) {
        final String msg = "Expected '" + new String(expected) + "', but was '" + new String(actual.getData()) + "'";
        assertTrue(msg, Arrays.equals(expected, actual.getData()));
    }

    public void setUp() throws Exception {
        port = 4242;
        group = InetAddress.getByName("230.0.0.1");
        socket = new MockMulticastSocket(port);
        multicastOutputStream = new MulticastOutputStream(socket, port, group);
    }

    public void testWrite() {
        multicastOutputStream.write(MESSAGE_A);
        multicastOutputStream.write(MESSAGE_B);
        multicastOutputStream.write(MESSAGE_C);
        assertPacketEquals(MESSAGE_A, (DatagramPacket)socket.packetsSent.removeLast());
        assertPacketEquals(MESSAGE_B, (DatagramPacket)socket.packetsSent.removeLast());
        assertPacketEquals(MESSAGE_C, (DatagramPacket)socket.packetsSent.removeLast());
    }

    public void testWriteSingleByteNotSupported() {
        try {
            multicastOutputStream.write('X');
            fail("Expected write of single byte to cause exception, but none was thrown.");
        }
        catch(final UnsupportedOperationException e) {
        }
    }

    public void testWriteOffsetMustEqualZeroAndLengthMustEqualDataLength() throws UnsupportedOperationException {
        multicastOutputStream.write(MESSAGE_A, 0, MESSAGE_A.length); // offset and length OK
        multicastOutputStream.write(MESSAGE_B, 0, MESSAGE_B.length); // offset and length OK
        multicastOutputStream.write(MESSAGE_C, 0, MESSAGE_C.length); // offset and length OK

        try {
            multicastOutputStream.write(MESSAGE_A, 1, MESSAGE_A.length);
            fail("Expected illegal offset to cause exception, but none was thrown.");
        }
        catch(final UnsupportedOperationException e) {
        }
        
        try {
            multicastOutputStream.write(MESSAGE_A, 0, MESSAGE_A.length - 1);
            fail("Expected illegal length to cause exception, but none was thrown.");
        }
        catch(final UnsupportedOperationException e) {
        }
        
        try {
            multicastOutputStream.write(MESSAGE_A, 0, MESSAGE_A.length + 1);
            fail("Expected illegal length to cause exception, but none was thrown.");
        }
        catch(final UnsupportedOperationException e) {
        }
    }
}

