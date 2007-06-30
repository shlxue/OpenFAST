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


package org.openfast.util;

import org.openfast.session.Client;
import org.openfast.session.FastConnectionException;
import org.openfast.session.Session;
import org.openfast.session.SessionFactory;


public class RecordingSessionFactory implements SessionFactory {
    private SessionFactory underlyingFactory;
    private RecordingInputStream in;
    private RecordingOutputStream out;

    public RecordingSessionFactory(SessionFactory underlyingFactory) {
        this.underlyingFactory = underlyingFactory;
    }

    public void close() throws FastConnectionException {
        underlyingFactory.close();
    }

    public Client getClient(String serverName) {
        return underlyingFactory.getClient(serverName);
    }

    public Session getSession() throws FastConnectionException {
        Session session = underlyingFactory.getSession();
        if (session == null) return null;
        in = new RecordingInputStream(session.in.getUnderlyingStream());
        out = new RecordingOutputStream(session.out.getUnderlyingStream());

        return new Session(in, out);
    }
}
