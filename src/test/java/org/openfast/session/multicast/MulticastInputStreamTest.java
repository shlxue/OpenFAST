package org.openfast.session.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.lib.legacy.ClassImposteriser;
import org.openfast.ByteUtil;
import org.openfast.test.OpenFastTestCase;

public class MulticastInputStreamTest extends OpenFastTestCase {
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    private MulticastInputStream in;
    private MulticastSocket mocket;
    
    protected void setUp() throws Exception {
        this.mocket = (MulticastSocket) mockery.mock(MulticastSocket.class);
        this.in = new MulticastInputStream(mocket);
    }
    public void testRead() throws IOException {
        mockery.checking(new Expectations() {{
            allowing(mocket).isClosed(); will(returnValue(false));
            one(mocket).receive(with(any(DatagramPacket.class))); will(fillBuffer("01 10"));
            one(mocket).receive(with(any(DatagramPacket.class))); will(fillBuffer("80 08"));
        }});
        assertEquals(1, in.read());
        assertEquals(16, in.read());
        assertEquals(-128, in.read());
        assertEquals(8, in.read());
        mockery.assertIsSatisfied();
    }
    
    protected static Action fillBuffer(String bitString) {
        return new FillBufferAction(bitString);
    }
    
    private static class FillBufferAction implements Action {
        private final byte[] bytes;
        public FillBufferAction(String hexString) {
            this.bytes = ByteUtil.convertHexStringToByteArray(hexString);
        }
        
        public void describeTo(Description description) {}

        public Object invoke(Invocation invocation) throws Throwable {
            DatagramPacket packet = ((DatagramPacket)invocation.getParameter(0));
            byte[] buffer = packet.getData();
            System.arraycopy(bytes, 0, buffer, 0, bytes.length);
            packet.setLength(bytes.length);
            return null;
        }
        
    }
}
