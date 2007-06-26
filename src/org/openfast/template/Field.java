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


package org.openfast.template;

import org.openfast.Context;
import org.openfast.FieldValue;

import java.io.InputStream;


public abstract class Field {
    protected final String name;
    protected final String key;
    protected final boolean optional;

    public Field(String name, boolean optional) {
        this.name = name;
        this.key = name;
        this.optional = optional;
    }

    public Field(String name, String key, boolean optional) {
        this.name = name;
        this.key = key;
        this.optional = optional;
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getKey() {
        return key;
    }

    public abstract byte[] encode(FieldValue value, Group template,
        Context context);

    public abstract FieldValue decode(InputStream in, Group template,
        Context context, boolean present);

    public abstract boolean usesPresenceMapBit();

    public abstract boolean isPresenceMapBitSet(byte[] encoding,
        FieldValue fieldValue);

    public abstract Class getValueType();

    public abstract FieldValue createValue(String value);

    public abstract String getTypeName();
}
