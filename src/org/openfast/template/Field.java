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

import java.io.InputStream;
import java.io.Serializable;

import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.QName;

public abstract class Field implements Serializable {
    protected final QName name;
    protected String key;
    protected final boolean optional;
    protected String id;

    /**
     * Field Constructor
     * @param name The name of the Field, a string
     * @param optional Determines if the Field is required or not for the data
     */
    public Field(QName name, boolean optional) {
        this.name = name;
        this.key = name.getName();
        this.optional = optional;
    }

    /**
     * Field Constructor
     * @param name The name of the Field, a string
     * @param key The key of the Field, a string
     * @param optional Determines if the Field is required or not for the data
     */
    public Field(QName name, String key, boolean optional) {
        this.name = name;
        this.key = key;
        this.optional = optional;
    }
    
    /**
     * Field Constructor
     * @param name The name of the Field, a string
     * @param key The key of the Field, a string
     * @param optional Determines if the Field is required or not for the data
     * @param id The id string
     */
    public Field(String name, String key, boolean optional, String id) {
    	this.name = new QName(name);
    	this.key = key;
    	this.optional = optional;
    	this.id = id;
    }

    /**
     * Find the name
     * @return Returns the name of the Field as a string
     */
    public String getName() {
        return name.getName();
    }
    
    public QName getQName() {
    	return name;
    }

    /**
     * Check to see if the Field is required 
     * @return Returns true if the Field isn't required, false otherwise
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

    /**
     * Sets the passed key to the current field key
     * @param key The key to be set
     */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
     * Find the ID
     * @return Returns the ID as a string
     */
	public String getId() {
		return id;
	}
	
	/**
     * Set the ID
     * @param id The new ID to set the Field's ID to
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * byte[] encode method declaration
     * @param value The FieldValue object to be encoded
     * @param template The Group object to be encoded
     * @param context The previous object to keep the data in sync
     * @param presenceMapBuilder The BitVectorBuilder object to be encoded
     */
    public abstract byte[] encode(FieldValue value, Group template,
        Context context, BitVectorBuilder presenceMapBuilder);

    /**
     * FieldValue decode method declaration
     * @param in The inputStream to be decoded
     * @param template The Group object to be decoded
     * @param context The previous object to keep the data in sync
     * @param present
     *
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
     * @param fieldValue The fieldValue object
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
}
