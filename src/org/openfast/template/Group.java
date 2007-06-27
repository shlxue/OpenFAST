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
import org.openfast.BitVectorValue;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;


public class Group extends Field {
    protected final Field[] fields;
    protected final Map fieldIndexMap;
    protected final Map fieldNameMap;

    /**
     * 
     * @param name
     * @param fields
     * @param optional
     */
    public Group(String name, Field[] fields, boolean optional) {
        super(name, optional);
        this.fields = fields;
        this.fieldIndexMap = constructFieldIndexMap(fields);
        this.fieldNameMap = constructFieldNameMap(fields);
    }

    /**
     * 
     */
    public byte[] encode(FieldValue value, Group template, Context context) {
        if (value == null) {
            return new byte[] {  };
        }

        GroupValue groupValue = (GroupValue) value;
        ByteArrayOutputStream buffer;
        BitVector presenceMap = new BitVector(fields.length);

        try {
            buffer = new ByteArrayOutputStream();

            byte[][] fieldEncodings = new byte[fields.length][];
            int presenceMapIndex = 0;

            for (int fieldIndex = 0; fieldIndex < fields.length;
                    fieldIndex++) {
                FieldValue fieldValue = groupValue.getValue(fieldIndex);
                Field field = getField(fieldIndex);
                byte[] encoding = field.encode(fieldValue, template, context);

                if (field.usesPresenceMapBit()) {
                    if ((field.isPresenceMapBitSet(encoding, fieldValue) &&
                            (encoding.length != 0)) ||
                            (field instanceof Scalar &&
                            (((Scalar) field).getOperatorName() == Operator.CONSTANT) &&
                            (fieldValue != null))) {
                        presenceMap.set(presenceMapIndex);
                    }

                    presenceMapIndex++;
                }

                fieldEncodings[fieldIndex] = encoding;
            }

            buffer.write(presenceMap.getTruncatedBytes());

            for (int i = 0; i < fieldEncodings.length; i++) {
                if (fieldEncodings[i] != null) {
                    buffer.write(fieldEncodings[i]);
                }
            }

            //			System.out.println(this.getName() + ": " + ByteUtil.convertByteArrayToBitString(buffer.toByteArray()));
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     */
    public FieldValue decode(InputStream in, Group group, Context context,
        boolean present) {
        return new GroupValue(this, decodeFieldValues(in, group, context));
    }

    protected FieldValue[] decodeFieldValues(InputStream in, Group template,
        Context context) {
        BitVector pmap = ((BitVectorValue) Type.BIT_VECTOR.decode(in)).value;

        return decodeFieldValues(in, template, pmap, context, 0);
    }

    public FieldValue[] decodeFieldValues(InputStream in, Group template,
        BitVector pmap, Context context, int start) {
        FieldValue[] values = new FieldValue[fields.length];
        int presenceMapIndex = start;

        //		System.out.print(getName() + "[");
        for (int fieldIndex = start; fieldIndex < fields.length;
                fieldIndex++) {
            Field field = getField(fieldIndex);

            try {
                boolean present = isPresent(pmap, presenceMapIndex, field);
                values[fieldIndex] = fields[fieldIndex].decode(in, template,
                        context, present);

                if (field.usesPresenceMapBit()) {
                    presenceMapIndex++;
                }
            } catch (Exception e) {
                throw new RuntimeException(
                    "Error occurred while decoding field \"" + field.getName() +
                    "\" in group \"" + getName() + "\"", e);
            }

            //			if (values[fieldIndex] != null)
            //				System.out.print(", ");
        }

        //		System.out.print("]");
        return values;
    }

    /**
     * 
     * @param pmap
     * @param i
     * @param field
     * @return
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
}
