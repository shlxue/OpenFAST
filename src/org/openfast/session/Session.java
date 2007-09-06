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

import java.io.InputStream;
import java.io.OutputStream;

import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.MessageHandler;
import org.openfast.MessageInputStream;
import org.openfast.MessageOutputStream;
import org.openfast.MessageStream;
import org.openfast.ScalarValue;
import org.openfast.codec.Coder;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;


public class Session implements ErrorHandler {
    public static final int FAST_HELLO_TEMPLATE_ID = 16000;
    public static final int FAST_ALERT_TEMPLATE_ID = 16001;
    public static final int FAST_RESET_TEMPLATE_ID = 120;
    public final static MessageTemplate FAST_ALERT_TEMPLATE = new MessageTemplate("",
            new Field[] {
                new Scalar("Severity", Type.U32, Operator.NONE,
                    ScalarValue.UNDEFINED, false),
                new Scalar("Code", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false),
                new Scalar("Value", Type.U32, Operator.NONE,
                    ScalarValue.UNDEFINED, true),
                new Scalar("Description", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, false),
            });
    public final static MessageTemplate FAST_RESET_TEMPLATE = new MessageTemplate("",
            new Field[] {  });
    public final static MessageTemplate FAST_HELLO_TEMPLATE = new MessageTemplate("",
            new Field[] {
                new Scalar("SenderName", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, false)
            });
    public static final Message RESET = new Message(FAST_RESET_TEMPLATE) {
            private static final long serialVersionUID = 1L;

			public void setFieldValue(int fieldIndex, FieldValue value) {
                throw new IllegalStateException(
                    "Cannot set values on a fast reserved message.");
            }
        };
    private ErrorHandler errorHandler = ErrorHandler.NULL;
    public final MessageInputStream in;
    public final MessageOutputStream out;
    private Client client;

    public Session(InputStream inputStream, OutputStream outputStream) {
        Context inContext = new Context();
        Context outContext = new Context();
        inContext.setErrorHandler(this);

        in = new MessageInputStream(inputStream, inContext);
        registerReservedTemplates(in);

        out = new MessageOutputStream(outputStream, outContext);
        registerReservedTemplates(out);

        MessageHandler resetHandler = new MessageHandler() {
                public void handleMessage(GroupValue readMessage,
                    Context context, Coder coder) {
                    coder.reset();
                }
            };

        in.addMessageHandler(FAST_RESET_TEMPLATE, resetHandler);
        out.addMessageHandler(FAST_RESET_TEMPLATE, resetHandler);
    }

    private void registerReservedTemplates(MessageStream stream) {
        stream.registerTemplate(FAST_HELLO_TEMPLATE_ID, FAST_HELLO_TEMPLATE);
        stream.registerTemplate(FAST_ALERT_TEMPLATE_ID, FAST_ALERT_TEMPLATE);
        stream.registerTemplate(FAST_RESET_TEMPLATE_ID, FAST_RESET_TEMPLATE);
    }

    public void close() throws FastConnectionException {
        if (client != null) {
            client.disconnect();
        }

        in.close();
        out.close();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public static Message createHelloMessage(String name) {
        Message message = new Message(FAST_HELLO_TEMPLATE);
        message.setString(1, name);

        return message;
    }

    public static Message createFastAlertMessage(ErrorCode code) {
        Message alert = new Message(FAST_ALERT_TEMPLATE);
        alert.setInteger(1, code.getSeverity().getCode());
        alert.setInteger(2, code.getCode());
        alert.setString(4, code.getDescription());

        return alert;
    }

    public void error(ErrorCode code, String message) {
		if (code.equals(FastConstants.D9_TEMPLATE_NOT_REGISTERED)) {
			code = SessionConstants.TEMPLATE_NOT_SUPPORTED;
			message = "Template Not Supported";
		}
        out.writeMessage(createFastAlertMessage(code));
        errorHandler.error(code, message);
    }

    public void error(ErrorCode code, String message, Throwable t) {
        out.writeMessage(createFastAlertMessage(code));
        errorHandler.error(code, message, t);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            this.errorHandler = ErrorHandler.NULL;
        }

        this.errorHandler = errorHandler;
    }
}
