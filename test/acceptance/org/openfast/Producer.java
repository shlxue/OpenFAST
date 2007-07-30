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

import org.openfast.session.Client;
import org.openfast.session.ConnectionListener;
import org.openfast.session.FastConnectionException;
import org.openfast.session.FastServer;
import org.openfast.session.Session;
import org.openfast.session.tcp.TcpSessionFactory;
import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;


public class Producer implements ConnectionListener {
    private MessageTemplate template;

    public Producer() {
        MessageTemplateLoader messageTemplateLoader = new XMLMessageTemplateLoader();
        template = messageTemplateLoader.load(this.getClass()
                                                  .getResourceAsStream("template.xml"))[0];
    }

    public void start() {
        FastServer acceptor = new FastServer("Producer", new TcpSessionFactory(2020));
        acceptor.setConnectionListener(this);

        try {
            acceptor.listen();
        } catch (FastConnectionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean isValid(Client client) {
        return (client.getName().equals("Consumer"));
    }

    public void onConnect(Session session) {
        session.in.registerTemplate(1, template);

        Message message = createMessage("John Doe", "555-555-5555");
        session.out.writeMessage(message);
    }

    private Message createMessage(String name, String phoneNumber) {
        return new Message(template);
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        producer.start();
    }
}
