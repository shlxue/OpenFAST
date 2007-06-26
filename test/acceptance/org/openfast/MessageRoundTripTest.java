package org.openfast;

import junit.framework.TestCase;

import org.openfast.session.FastConnectionException;
import org.openfast.session.Session;
import org.openfast.session.tcp.TcpFastConnectionFactory;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;

/**
 * NOTE: EchoServer must be running before this test is run.
 */
public class MessageRoundTripTest extends TestCase {

	private Session session;

	public void testSendSimpleMessageToEchoServer()
	{
		MessageTemplate messageTemplate = new MessageTemplate(null, new Field[] {});
		Message outgoingMessage = new Message(messageTemplate, 1);
		
		session.out.registerTemplate(3, messageTemplate);
		session.out.writeMessage(outgoingMessage);
		GroupValue incomingMessage = session.in.readMessage();
		
		assertEquals(outgoingMessage, incomingMessage);
	}

	public void tearDown() throws FastConnectionException {
		session.close();
	}

	// @Override
	public void setUp() throws FastConnectionException {
		session = new TcpFastConnectionFactory(9001).connect("MessageRoundTripTest");
		System.out.println("Connected to " + session.getClient().getName());
	}
}
