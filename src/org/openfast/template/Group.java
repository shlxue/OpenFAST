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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.openfast.BitVector;
import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorValue;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.template.type.codec.TypeCodec;


public class Group extends Field {
    private String typeReference = null;
    protected final Field[] fields;
    protected final Map fieldIndexMap;
    protected final Map fieldNameMap;
    protected final boolean usesPresenceMap;

    protected Group(String name, Field[] fields, boolean optional, boolean usesPresenceMap) {
        super(name, optional);
        this.fields = fields;
        this.fieldIndexMap = constructFieldIndexMap(fields);
        this.fieldNameMap = constructFieldNameMap(fields);
        this.usesPresenceMap = usesPresenceMap;
    	
    }
    
    /**
     * 
     * @param name The name of the Group
     * @param fields The Field object array to be created for the group
     * @param optional The optional boolean
     */
    public Group(String name, Field[] fields, boolean optional) {
    	this (name, fields, optional, determinePresenceMapUsage(fields));
    }

    private static boolean determinePresenceMapUsage(Field[] fields) {
    	for (int i=0; i<fields.length; i++)
    		if (fields[i].usesPresenceMapBit())
    			return true;
    	return false;
	}

	/**
     * If your FieldValue already has a BitVector, use this encode method.  The MapBuilder index is kept track of and stored through this process.
     * The supplied data is stored to a byte buffer array and returned.
     * @param value The value of the FieldValue to be encoded
     * @param template The Group object to be encoded
     * @param context The Context object to be encoded
     * @param presenceMapBuilder The BitVector object that will be used to encode.
     * @return Return thes the encoded byte array 
     */
	public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
		byte[] encoding = encode(value, template, context);
		if (optional) {
			if (encoding.length != 0)
				presenceMapBuilder.set();
			else
				presenceMapBuilder.skip();
		}
		return encoding;
	}
	
    /**
     * If there is no BitVector, this encoding method will create one.  The supplied data is stored to a byte buffer array and returned.  
     * The MapBuilder index is kept track of and stored through this process.
     * @param value The value of the FieldValue to be encoded
     * @param template The Group object to be encoded
     * @param context The Context object to be encoded
     * @return Returns an new byte array if there are no FieldValue to encode, otherwise returns the buffer to the 
     * byte array that the data was stored to
     */
    public byte[] encode(FieldValue value, Group template, Context context) {
        if (value == null) {
            return new byte[] { };
        }

        GroupValue groupValue = (GroupValue) value;
        BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(fields.length);
        try {
            byte[][] fieldEncodings = new byte[fields.length][];

            for (int fieldIndex = 0; fieldIndex < fields.length;
                    fieldIndex++) {
                FieldValue fieldValue = groupValue.getValue(fieldIndex);
                Field field = getField(fieldIndex);
                byte[] encoding = field.encode(fieldValue, template, context, presenceMapBuilder);
                fieldEncodings[fieldIndex] = encoding;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            
            if (usesPresenceMap)
            	buffer.write(presenceMapBuilder.getBitVector().getTruncatedBytes());
            for (int i = 0; i < fieldEncodings.length; i++) {
                if (fieldEncodings[i] != null) {
                    buffer.write(fieldEncodings[i]);
                }
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public FieldValue decode(InputStream in, Group group, Context context,
        boolean present) {
        return new GroupValue(this, decodeFieldValues(in, group, context));
    }

    /**
     * If there is not a vector map created for the inputStream, a vector map will be created to pass to the public
     * decodeFieldValues method.  
     * @param in The InputStream to be decoded
     * @param template The Group object to be decoded
     * @param context The Context object to be decoded
     * @return Returns the FieldValue array of the decoded field values passed to it
     * @see public FieldValue[] decodeFieldValues
     */
    protected FieldValue[] decodeFieldValues(InputStream in, Group template,
        Context context) {
    	if (usesPresenceMap) {
    		BitVector pmap = ((BitVectorValue) TypeCodec.BIT_VECTOR.decode(in)).value;
    		return decodeFieldValues(in, template, pmap, context, 0);
    	} else {
    		return decodeFieldValues(in, template, context, 0);
    	}
    }

    private FieldValue[] decodeFieldValues(InputStream in, Group template, Context context, int start) {
        FieldValue[] values = new FieldValue[fields.length];

        for (int fieldIndex = start; fieldIndex < fields.length; fieldIndex++) {
            values[fieldIndex] = getField(fieldIndex).decode(in, template, context, true);
        }
        return values;
	}

	/**
     * Goes through the all the field value array, starting with the index passed, checks to see if a map actually created for the field to pass
     * to the decoder - the field index is created as a new Group object and stored to the the FieldValue array.  Once all the field values have
     * beed gone through, the method returns.
     * @param in The InputStream to be decoded
     * @param template The Group object
     * @param pmap The BitVector to be decoded
     * @param context The Context object to be decoded
     * @param start The index of the Field to start decoding from
     * @return Returns a FieldValue array of the decoded field values passed to it.  
     * @throws Throws RuntimeException if there is an problem in the decoding
     *
     */
    public FieldValue[] decodeFieldValues(InputStream in, Group template,
        BitVector pmap, Context context, int start) {
        FieldValue[] values = new FieldValue[fields.length];
        int presenceMapIndex = start;

        for (int fieldIndex = start; fieldIndex < fields.length;
                fieldIndex++) {
            Field field = getField(fieldIndex);

                boolean present = isPresent(pmap, presenceMapIndex, field);
                values[fieldIndex] = fields[fieldIndex].decode(in, template,
                        context, present);

                if (field.usesPresenceMapBit()) {
                    presenceMapIndex++;
                }
        }
        return values;
    }

    /**
     * 
     * @param pmap The vector map that is to be tested
     * @param i The index of the vector map to be checked 
     * @param field The field object that is being tested to see if one is present
     * @return Returns true 
     */
    private boolean isPresent(BitVector pmap, int i, Field field) {
        if (!field.usesPresenceMapBit()) {
            return true;
        }

        return pmap.isSet(i);
    }

    /**
     * Determine if there is a Map of the passed byte array and fieldValue
     * @param encoding The byte array to be checked
     * @param fieldValue The fieldValue to be checked
     * @return Returns true if there is a PrecenceMapBit of the specified 
     * byte array and field, false otherwise
     */
    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return encoding.length != 0;
    }

    /**
     * @return Returns the optional boolean of the MapBit
     */
    public boolean usesPresenceMapBit() {
        return optional;
    }
    
    /**
     * Find the number of total fields
     * @return Returns the number of fields
     */
    public int getFieldCount() {
        return fields.length;
    }

    /**
     * Find the field object of the index passed
     * @param index The index within the field that is being searched for
     * @return Returns a field object of the specified index 
     */
    public Field getField(int index) {
        return fields[index];
    }

    /**
     * @return Returns the class of the GroupValue
     */
    public Class getValueType() {
        return GroupValue.class;
    }

    /**
     * @param The value that the fieldValue that is to be created
     * @return Returns a new GroupValue
     */
    public FieldValue createValue(String value) {
        return new GroupValue(this, new FieldValue[fields.length]);
    }

    /**
     * @return Returns the string 'group'
     */
    public String getTypeName() {
        return "group";
    }

    /**
     * Find the field object of the passed field name
     * @param fieldName The field name of the field object that is to be returned
     * @return Returns the field object of the passed field name
     */
    public Field getField(String fieldName) {
        return (Field) fieldNameMap.get(fieldName);
    }

    /**
     * Creates a map of the passed field array by the field name and the field index number
     * @param fields The name of the field array that is going to be placed into a new map object
     * @return Returns a map object of the field array passed to it
     */
    protected Map constructFieldNameMap(Field[] fields) {
        Map map = new HashMap();

        for (int i = 0; i < fields.length; i++)
            map.put(fields[i].getName(), fields[i]);

        return map;
    }

    /**
     * Creates a map of the passed field array by the field index number, numbered 0 to n
     * @param fields The name of the field array that is going to be placed into a new map object
     * @return Returns a map object of the field array passed to it
     */
    protected Map constructFieldIndexMap(Field[] fields) {
        Map map = new HashMap();

        for (int i = 0; i < fields.length; i++)
            map.put(fields[i], new Integer(i));

        return map;
    }

    /**
     * Find the index of the passed field name as an integer
     * @param fieldName The field name that is being searched for
     * @return Returns an integer of the field index of the specified field name
     */
    public int getFieldIndex(String fieldName) {
        return ((Integer) fieldIndexMap.get(getField(fieldName))).intValue();
    }

    /**
     * Get the Sequence of the passed fieldName
     * @param fieldName The field name that is being searched for
     * @return Returns a sequence object of the specified fieldName
     */
    public Sequence getSequence(String fieldName) {
        return (Sequence) getField(fieldName);
    }

    /**
     * Get the Scalar Value of the passed fieldName
     * @param fieldName The field name that is being searched for
     * @return Returns a Scalar value of the specified fieldName
     */
    public Scalar getScalar(String fieldName) {
        return (Scalar) getField(fieldName);
    }

    /**
     * Find the group with the passed fieldName
     * @param fieldName The field name that is being searched for
     * @return Returns a Group object of the specified field name
     */
    public Group getGroup(String fieldName) {
        return (Group) getField(fieldName);
    }

    /**
     * Determine if the map has a specified field name.  
     * @param fieldName The name of the fieldName that is being searched for
     * @return Returns true if there is the field name that was passed in the Map, false otherwise
     */
    public boolean hasField(String fieldName) {
        return fieldNameMap.containsKey(fieldName);
    }

    /**
     *
     * @return Returns an array of Fields
     */
    public Field[] getFields() {
        return fields;
    }

    /**
     * Set the name of the type referenced by this group
     * @param typeReference The name of the application type referenced by this goup
     */
    public void setTypeReference(String typeReference) {
        this.typeReference = typeReference;
    }
    
    /**
     * 
     * @return Returns the application type referenced by this group
     */
    public String getTypeReference() {
        return typeReference;
    }
    
    public boolean hasTypeReference() {
    	return typeReference != null;
    }
    
    
}
