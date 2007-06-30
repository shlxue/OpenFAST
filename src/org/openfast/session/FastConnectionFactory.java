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

import java.io.IOException;

import org.openfast.Message;

import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;


public abstract class FastConnectionFactory {
    private ErrorHandler errorHandler = ErrorHandler.DEFAULT;

    public Session connect(String clientName) throws FastConnectionException {
        Session session = connectInternal();
        Message message = Session.createHelloMessage(clientName);
        session.out.writeMessage(message, true);
        try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        long time = System.currentTimeMillis();
        Message hello = session.in.readMessage();
        System.out.println(System.currentTimeMillis() - time);

        if (hello.getTemplateId() == Session.FAST_ALERT_TEMPLATE_ID) {
            errorHandler.error(ErrorCode.getAlertCode(hello),
                "Unable to connect.");
        }

        if (hello.getTemplateId() != Session.FAST_HELLO_TEMPLATE_ID) {
            throw new FastConnectionException(
                "Unable to connect: Message with template id " +
                hello.getTemplateId() +
                " received, expected hello message (id=" +
                Session.FAST_HELLO_TEMPLATE_ID + ")");
        }

        Client client = getClient(session, hello.getString(1));
        session.setClient(client);

        return session;
    }

    protected abstract Client getClient(Session session, String clientName);

    protected abstract Session connectInternal() throws FastConnectionException;

    public void setErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            throw new NullPointerException();
        }

        this.errorHandler = errorHandler;
    }
}
