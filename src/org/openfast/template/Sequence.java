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
import java.util.Iterator;

import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.Global;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.SequenceValue;
import org.openfast.error.FastConstants;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;


public class Sequence extends Field implements FieldSet {
    private final Group group;
    private final Scalar length;
    private boolean implicitLength;
	private String typeReference = FastConstants.ANY;

    /**
     * Sequence Constructor - Sets the implicitLength to true
     * @param name The name of the sequence as a string
     * @param fields Field array object
     * @param optional Determines if the Field is required or not for the data
     * 
     */
    public Sequence(String name, Field[] fields, boolean optional) {
        this(name, createLength(name, optional), fields, optional);
        implicitLength = true;
    }

    /**
     * Sequence Constructor - Calls the first constructor, passing the optional boolean as false
     * @param name The name of the sequence as a string
     * @param length The length of the sequence as a Scalar value
     * @param fields Field array
     */
    public Sequence(String name, Scalar length, Field[] fields) {
        this(name, length, fields, false);
    }

    /**
     * Sequence Constructor - If no length, a length is created and the implicitLength is set to true.  A new Group is also created with
     * with the respected information.
     * @param name Name of the sequence, a string
     * @param length Length of the sequence, a Scalar value
     * @param fields Field array
     * @param optional Determines if the Field is required or not for the data
     */
    public Sequence(String name, Scalar length, Field[] fields, boolean optional) {
        super(name, optional);
        this.group = new Group(name, fields, optional);

        if (length == null) {
            this.length = createLength(name, optional);
            implicitLength = true;
        } else {
            this.length = length;
        }
    }

    /**
     * Creates a Scalar value length
     * @param name The name of the Scalar object
     * @param optional Determines if the Field is required or not for the data
     * @return A Scalar value
     */
    private static Scalar createLength(String name, boolean optional) {
        return new Scalar(Global.createImplicitName(name), Type.U32,
            Operator.NONE, ScalarValue.UNDEFINED, optional);
    }

    /**
     * Find the number of fields in the current group
     * @return Returns an integer of the number of fields
     */
    public int getFieldCount() {
        return group.getFieldCount();
    }

    /**
     * Find a specific field
     * @param index The field index that is passed
     * @return Returns a Field object of the requested index
     */
    public Field getField(int index) {
        return group.getField(index);
    }

    /**
     * Find the length of a Scalar value
     * @return The length of the Scalar value
     */
    public Scalar getLength() {
        return length;
    }

    /**
     * 
     * @return True if there is a current MapBit, false otherwise
     */
    public boolean usesPresenceMapBit() {
        return length.usesPresenceMapBit();
    }

    /**
     * @param encoding Byte array to be checked if there is a MapBit
     * @param fieldValue FieldValue object
     * @return True if there is a Map Bit set, false otherwise
     */
    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return length.isPresenceMapBitSet(encoding, fieldValue);
    }

    /**
     * Store the data passed to a byte array
     * @param value The FieldValue object
     * @param template The Group that is to be stored
     * @param context The previous object to keep the data in sync
     * @return Returns the buffer of the byte array
     */
    public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
    	if (hasTypeReference())
    		context.setCurrentApplicationType(getTypeReference());
        if (value == null) {
            return length.encode(null, template, context, presenceMapBuilder);
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SequenceValue val = (SequenceValue) value;
        int len = val.getLength();

        //		System.out.print(getName() + "[");
        try {
            buffer.write(length.encode(new IntegerValue(len), template, context, presenceMapBuilder));

            Iterator iter = val.iterator();

            while (iter.hasNext()) {
                buffer.write(group.encode((FieldValue) iter.next(), template,
                        context));
            }
        } catch (IOException e) {
        }

        //		System.out.print("]");
        return buffer.toByteArray();
    }

    /**
     * Decode the specified stream of data
     * @param in The input stream to be decoded
     * @param template Which Group template is to be decoded
     * @param context The previous object to keep the data in sync
     * @param present 
     * @return If there is nothing to decode - returns null, otherwise 
     * returns a sequenceValue object that has the decoded information stored.
     */
    public FieldValue decode(InputStream in, Group template, Context context,
        boolean present) {
        SequenceValue sequenceValue = new SequenceValue(this);
        FieldValue lengthValue = length.decode(in, template, context, present);

        if ((lengthValue == ScalarValue.NULL) || (lengthValue == null)) {
            return null;
        }

        int len = ((IntegerValue) lengthValue).value;

        for (int i = 0; i < len; i++)
            sequenceValue.add((GroupValue) group.decode(in, template, context,
                    present));

        return sequenceValue;
    }

    /**
     * @return Returns the class of the current SequenceValue
     */
    public Class getValueType() {
        return SequenceValue.class;
    }

    /**
     * @param value String of the new SequenceValue to create
     * @return Returns a new SequenceValue with the specified value
     */
    public FieldValue createValue(String value) {
        return new SequenceValue(this);
    }

    /**
     * @return Returns the string 'sequence'
     */
    public String getTypeName() {
        return "sequence";
    }

    /**
     * 
     * @return Return the current Group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * 
     * @param fieldName String of the FieldName that is to be found
     * @return Returns true if there is a field with the specified name, false otherwise
     */
    public boolean hasField(String fieldName) {
        return group.hasField(fieldName);
    }

    /**
     * 
     * @return Returns the implicitLength, true or false - whichever is set
     */
    public boolean isImplicitLength() {
        return implicitLength;
    }

    /**
     * Set the type reference
     * @param typeReference The type reference name as a string
     */
	public void setTypeReference(String typeReference) {
		this.typeReference = typeReference;
		this.group.setTypeReference(typeReference);
	}

	/**
	 * 
	 * @return Returns the typeReference as a string
	 */
	public String getTypeReference() {
		return typeReference;
	}
	
	/**
	 * 
	 * @return Returns true if there is a type reference
	 */
	public boolean hasTypeReference() {
		return typeReference != null;
	}
}
