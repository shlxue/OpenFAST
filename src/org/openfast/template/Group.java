package org.openfast.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openfast.BitVector;
import org.openfast.BitVectorValue;
import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class Group extends Field {
	protected final Field[] fields;
	protected final Map fieldIndexMap;
	protected final Map fieldNameMap;

	public Group(String name, Field[] fields, boolean optional) {
		super(name, optional);
		this.fields = fields;
		this.fieldIndexMap = constructFieldIndexMap(fields);
		this.fieldNameMap = constructFieldNameMap(fields);
	}

	public byte[] encode(FieldValue value, Group template, Context context) {
		if (value == null) return new byte[]{};
		GroupValue groupValue = (GroupValue) value;
		ByteArrayOutputStream buffer;
		BitVector presenceMap = new BitVector(fields.length);
		try {
			buffer = new ByteArrayOutputStream();
			byte[][] fieldEncodings = new byte[fields.length][];
			int presenceMapIndex = 0;
			for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++)
			{
				FieldValue fieldValue = groupValue.getValue(fieldIndex);
				Field field = getField(fieldIndex);
				byte[] encoding = field.encode(fieldValue, template, context);
				if (field.usesPresenceMapBit())
				{
					if ((field.isPresenceMapBitSet(encoding, fieldValue) && encoding.length != 0) || 
					    (field instanceof Scalar && ((Scalar) field).getOperatorName() == Operator.CONSTANT && fieldValue != null)) {
						presenceMap.set(presenceMapIndex);
					}
					presenceMapIndex++;
				}
				fieldEncodings[fieldIndex] = encoding;
			}
			
			buffer.write(presenceMap.getTruncatedBytes());
			for (int i=0; i<fieldEncodings.length; i++)
			{
				if (fieldEncodings[i] != null)
					buffer.write(fieldEncodings[i]);
			}
//			System.out.println(this.getName() + ": " + ByteUtil.convertByteArrayToBitString(buffer.toByteArray()));
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FieldValue decode(InputStream in, Group group, Context context, boolean present) {
		return new GroupValue(this, decodeFieldValues(in, group, context));
	}

	protected FieldValue[] decodeFieldValues(InputStream in, Group template, Context context) {
		BitVector pmap = ((BitVectorValue)Type.BIT_VECTOR.decode(in)).value;
		return decodeFieldValues(in, template, pmap, context, 0);
	}
	
	public FieldValue[] decodeFieldValues(InputStream in, Group template, BitVector pmap, Context context, int start) {
		FieldValue[] values = new FieldValue[fields.length];
		int presenceMapIndex = start;
//		System.out.print(getName() + "[");
		for (int fieldIndex = start; fieldIndex < fields.length; fieldIndex++)
		{
			Field field = getField(fieldIndex);
			try {
				boolean present = isPresent(pmap, presenceMapIndex, field);
				values[fieldIndex] = fields[fieldIndex].decode(in, template, context, present);
				if (field.usesPresenceMapBit())
					presenceMapIndex++;
			} catch (Exception e) {
				throw new RuntimeException("Error occurred while decoding field \"" + field.getName() +"\" in group \"" + getName() + "\"", e);
			}
//			if (values[fieldIndex] != null)
//				System.out.print(", ");
		}
//		System.out.print("]");
		return values;
	}
	
	private boolean isPresent(BitVector pmap, int i, Field field) {
		if (!field.usesPresenceMapBit()) return true;
		return pmap.isSet(i);
	}

	public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
		return encoding.length != 0;
	}

	public boolean usesPresenceMapBit() {
		return optional;
	}

	public int getFieldCount() {
		return fields.length;
	}

	public Field getField(int index) {
		return fields[index];
	}

	public Class getValueType() {
		return GroupValue.class;
	}

	public FieldValue createValue(String value) {
		return new GroupValue(this, new FieldValue[fields.length]);
	}

	public String getTypeName() {
		return "group";
	}

	public Field getField(String fieldName) {
		return (Field) fieldNameMap.get(fieldName);
	}

	protected Map constructFieldNameMap(Field[] fields) {
		Map map = new HashMap();
		for (int i=0; i<fields.length; i++)
			map.put(fields[i].getName(), fields[i]);
		return map;
	}

	protected Map constructFieldIndexMap(Field[] fields) {
		Map map = new HashMap();
		for (int i=0; i<fields.length; i++)
			map.put(fields[i], new Integer(i));
		return map;
	}

	public int getFieldIndex(String fieldName) {
		return ((Integer)fieldIndexMap.get(getField(fieldName))).intValue();
	}

	public Sequence getSequence(String fieldName) {
		return (Sequence) getField(fieldName);
	}

	public Scalar getScalar(String fieldName) {
		return (Scalar) getField(fieldName);
	}

	public Group getGroup(String fieldName) {
		return (Group) getField(fieldName);
	}

	public boolean hasField(String fieldName) {
		return fieldNameMap.containsKey(fieldName);
	}

	public Field[] getFields() {
		return fields;
	}
}
