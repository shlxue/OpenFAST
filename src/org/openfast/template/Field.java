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

import org.openfast.BitVector;
import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.FieldValue;

import java.io.InputStream;

/**
 * 
 * @see FieldSet
 *
 */
public abstract class Field {
    protected final String name;
    protected String key;
    protected final boolean optional;

    /**
     * Field Constructor
     * @param name The name of the Field, a string
     * @param optional An optional boolean 
     */
    public Field(String name, boolean optional) {
        this.name = name;
        this.key = name;
        this.optional = optional;
    }

    /**
     * Field Constructor
     * @param name The name of the Field, a string
     * @param key The key of the Field, a string
     * @param optional An optional boolean
     */
    public Field(String name, String key, boolean optional) {
        this.name = name;
        this.key = key;
        this.optional = optional;
    }

    /**
     * Find the name
     * @return Returns the name as a string
     */
    public String getName() {
        return name;
    }

    /**
     * Check to see what the optional boolean is set to
     * @return Returns true if the optional boolean is set to true, false otherwise
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Find the key
     * @return Returns the Key as a string
     */
    public String getKey() {
        return key;
    }

	public void setKey(String key) {
		this.key = key;
	}

    /**
     * byte[] encode method declaration
     * @param value The FieldValue value to be encoded
     * @param template The template of the Group to be encoded
     * @param context The context of the Context to be encoded
     * @param presenceMapBuilder 
     */
    public abstract byte[] encode(FieldValue value, Group template,
        Context context, BitVectorBuilder presenceMapBuilder);

    /**
     * FieldValue decode method declaration
     * @param in The inputStream to be decoded
     * @param template The template of the Group to be decoded
     * @param context The context of the Context to be decoded
     * @param present
     * @return
     */
    public abstract FieldValue decode(InputStream in, Group template,
        Context context, boolean present);

    /**
     * 
     * usesPresenceMapBit method declaration
     * 
     */
    public abstract boolean usesPresenceMapBit();

    /**
     * isPresenceMapBitSet method declaration
     * @param encoding The byte array to check if it is present
     * @param fieldValue The fieldValue value
     */
    public abstract boolean isPresenceMapBitSet(byte[] encoding,
        FieldValue fieldValue);

    /**
     * getValueType method declaration
     */
    public abstract Class getValueType();

    /**
     * createValue method declaration
     * @param value The string of the FieldValue that is to be created
     */
    public abstract FieldValue createValue(String value);

    /**
     * getTypeName method declaration
     */
    public abstract String getTypeName();

	public int encodePresenceMap(BitVector presenceMap, int presenceMapIndex, byte[] encoding, FieldValue fieldValue) {
		if (usesPresenceMapBit()) {
			if (isPresenceMapBitSet(encoding, fieldValue))
				presenceMap.set(presenceMapIndex);
			return presenceMapIndex++;
		}
		return presenceMapIndex;
	}
}
