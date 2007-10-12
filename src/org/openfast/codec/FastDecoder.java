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


package org.openfast.codec;

import org.openfast.BitVector;
import org.openfast.BitVectorReader;
import org.openfast.BitVectorValue;
import org.openfast.Context;
import org.openfast.IntegerValue;
import org.openfast.Message;

import org.openfast.template.MessageTemplate;
import org.openfast.template.type.codec.TypeCodec;

import java.io.InputStream;


public class FastDecoder implements Coder {
    private final InputStream in;
    private final Context context;

    public FastDecoder(Context session, InputStream in) {
        this.in = in;
        this.context = session;
    }

    public Message readMessage() {
        BitVectorValue bitVectorValue = (BitVectorValue) TypeCodec.BIT_VECTOR.decode(in);

        if (bitVectorValue == null) {
            return null; // Must have reached end of stream;
        }

        BitVector pmap = (bitVectorValue).value;
		BitVectorReader presenceMapReader = new BitVectorReader(pmap);
		
        // if template id is not present, use previous, else decode template id
        int templateId = (presenceMapReader.read()) ? ((IntegerValue) TypeCodec.UINT.decode(in)).value : context.getLastTemplateId();
        MessageTemplate template = context.getTemplate(templateId);

        if (template == null) {
            return null;
        }
        context.newMessage(template);

        context.setLastTemplateId(templateId);

        return template.decode(in, templateId, presenceMapReader, context);
    }

    public void reset() {
        context.reset();
    }
}
