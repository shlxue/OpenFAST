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

    public void testSendSimpleMessageToEchoServer() {
        MessageTemplate messageTemplate = new MessageTemplate(null,
                new Field[] {  });
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
        session = new TcpFastConnectionFactory(9001).connect(
                "MessageRoundTripTest");
        System.out.println("Connected to " + session.getClient().getName());
    }
}
