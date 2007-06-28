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


package org.openfast.session;

import junit.framework.TestCase;

import org.openfast.ByteUtil;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.StringValue;

import org.openfast.error.ErrorHandler;
import org.openfast.error.FastException;

import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.type.Type;

import org.openfast.test.ObjectMother;
import org.openfast.test.TestUtil;

import org.openfast.util.LocalFastConnectionFactory;
import org.openfast.util.RecordingInputStream;
import org.openfast.util.RecordingSessionFactory;


public class SessionTest extends TestCase {
    private LocalFastConnectionFactory factory;
    private FastServer server;
    private Session serverSession;

    protected void setUp() throws Exception {
        factory = new LocalFastConnectionFactory();
        server = new FastServer("server",
                new RecordingSessionFactory(factory.createLocalSessionFactory()));
        server.setErrorHandler(ErrorHandler.NULL);
        server.setConnectionListener(new ConnectionListener() {
                public boolean isValid(Client client) {
                    return true;
                }

                public void onConnect(Session session) {
                    serverSession = session;
                }
            });
        new Thread(new Runnable() {
                public void run() {
                    try {
                        server.listen();
                    } catch (FastConnectionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "FAST Server").start();
    }

    protected void tearDown() throws Exception {
        server.close();
    }

    public void testBasicSessionInitialization()
        throws FastConnectionException, InterruptedException {
        factory.connect("client");

        //                             --PMAP-- ----TID=16000---- -----------------name="client"-----------------------
        String expectedServerInput = "11000000 01111101 10000000 01100011 01101100 01101001 01100101 01101110 11110100";

        //                             --PMAP-- ----TID=16000---- -----------------name="server"-----------------------
        String expectedServerOutput = "11000000 01111101 10000000 01110011 01100101 01110010 01110110 01100101 11110010";
        assertEquals(expectedServerInput,
            serverSession.in.getUnderlyingStream().toString());
        assertEquals(expectedServerOutput,
            serverSession.out.getUnderlyingStream().toString());
    }

    public void testUnregisteredTemplateError()
        throws FastConnectionException, InterruptedException {
        Session session = factory.connect("client");
        MessageTemplate messageTemplate = new MessageTemplate(null,
                new Field[] {  });
        session.out.registerTemplate(1, messageTemplate);
        session.out.writeMessage(new Message(messageTemplate, 1));

        //                             --PMAP-- ----TID=16000---- -----------------name="client"-----------------------
        String expectedServerInput = "11000000 01111101 10000000 01100011 01101100 01101001 01100101 01101110 11110100 " +
            //                             --PMAP-- -TID=1--
            "11000000 10000001";

        //                             --PMAP-- ----TID=16000---- -----------------name="server"-----------------------
        String expectedServerOutput = "11000000 01111101 10000000 01110011 01100101 01110010 01110110 01100101 11110010 " +
            //                             --PMAP-- ----TID=16001---- ---1=2-- --2=11-- -3=NULL- -4=<ERR MESSAGE>
            "11000000 01111101 10000001 10000010 10001011 10000000 " +
            ByteUtil.convertByteArrayToBitString(Type.ASCII_STRING_TYPE.encode(
                    new StringValue("Template not supported")));
        serverSession.in.readMessage();
        assertEquals(expectedServerInput,
            serverSession.in.getUnderlyingStream().toString());
        assertEquals(expectedServerOutput,
            serverSession.out.getUnderlyingStream().toString());

        GroupValue alertMessage = session.in.readMessage();
        assertEquals(2, alertMessage.getInt(1));
        assertEquals(11, alertMessage.getInt(2));
        assertEquals("Template not supported", alertMessage.getString(4));
    }

    public void testAuthenticationError()
        throws InterruptedException, FastConnectionException {
        server.setConnectionListener(new ConnectionListener() {
                public boolean isValid(Client client) {
                    return false;
                }

                public void onConnect(Session session) {
                    serverSession = session;
                }
            });

        try {
            factory.connect("client");
            fail();
        } catch (FastException e) {
            assertEquals("Unable to connect.", e.getMessage());
            assertEquals(SessionConstants.UNAUTHORIZED, e.getCode());
        }
    }

    public void testFastReset() throws FastConnectionException {
        Session session = factory.connect("client");

        MessageTemplate quoteTemplate = ObjectMother.quoteTemplate();
        Message quote = ObjectMother.quote(103.4, 104.2);
        Message quote2 = ObjectMother.quote(103.3, 104.1);
        Message quote3 = ObjectMother.quote(103.4, 104.2);

        serverSession.in.registerTemplate(ObjectMother.QUOTE_TEMPLATE_ID,
            quoteTemplate);
        session.out.registerTemplate(ObjectMother.QUOTE_TEMPLATE_ID,
            quoteTemplate);
        session.out.writeMessage(quote);
        session.out.writeMessage(quote2);
        session.out.reset();
        session.out.writeMessage(quote3);

        GroupValue receivedQuote = serverSession.in.readMessage();
        assertEquals(quote, receivedQuote);

        receivedQuote = serverSession.in.readMessage();
        assertEquals(quote2, receivedQuote);

        RecordingInputStream recorder = ((RecordingInputStream) serverSession.in.getUnderlyingStream());
        recorder.clear();

        receivedQuote = serverSession.in.readMessage();
        assertEquals(quote3, receivedQuote);

        //                         --PMAP-- -reset-- --PMAP-- -tid=10- exponent --bid  mantissa-- exponent --ask  mantissa--
        String expectedEncoding = "11000000 11111000 11000000 10001010 11111111 00001000 10001010 11111111 00001000 10010010";
        TestUtil.assertBitVectorEquals(expectedEncoding, recorder.getBuffer());
    }
}
