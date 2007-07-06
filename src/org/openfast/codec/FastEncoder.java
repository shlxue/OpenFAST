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

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.TemplateRegistry;
import org.openfast.template.MessageTemplate;


/* In 1.5 version use : static import java.lang.Math.*; */
public class FastEncoder implements Coder, TemplateRegistry {
    private Context context;

    public FastEncoder(Context context) {
        this.context = context;
    }

    /**
     * WARNING: Not thread-safe.
     * @param message
     * @return
     */
    public byte[] encode(Message message) {
        MessageTemplate template = message.getTemplate();
        message.setInteger(0, context.getId(template));
        context.newMessage(template);
        return template.encode(message, context);
    }

    public void reset() {
        context.reset();
    }

    public void registerTemplate(int templateId, MessageTemplate template) {
        context.registerTemplate(templateId, template);
    }
}
