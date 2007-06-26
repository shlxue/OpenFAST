package org.openfast.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.SequenceValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;


public class Sequence extends Field implements FieldSet {

	private final Group group;
	private final Scalar length;
	private boolean implicitLength;

	public Sequence(String name, Field[] fields, boolean optional) {
		this (name, createLength(optional), fields, optional);
		implicitLength = true;
	}

	public Sequence(String name, Scalar length, Field[] fields)
	{
		this(name, length, fields, false);
	}
	
	public Sequence(String name, Scalar length, Field[] fields, boolean optional)
	{
		super(name, optional);
		this.group = new Group(name, fields, optional);
		if (length == null) { 
			this.length = createLength(optional);
			implicitLength = true;
		} else {
			this.length = length;
		}
	}
	
	private static Scalar createLength(boolean optional) {
		return new Scalar(createUniqueName(), Type.UNSIGNED_INTEGER, Operator.NONE, optional);
	}

	public static String createUniqueName() {
		return null;
	}

	public int getFieldCount() {
		return group.getFieldCount();
	}

	public Field getField(int index) {
		return group.getField(index);
	}

	public Scalar getLength() {
		return length;
	}

	public boolean usesPresenceMapBit() {
		return length.usesPresenceMapBit();
	}

	public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
		return length.isPresenceMapBitSet(encoding, fieldValue);
	}

	public byte[] encode(FieldValue value, Group template, Context context) {
		if (value == null) return length.encode(null, template, context);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		SequenceValue val = (SequenceValue) value;
		int len = val.getLength();
//		System.out.print(getName() + "[");
		try {
			buffer.write(length.encode(new IntegerValue(len), template, context));
			Iterator iter = val.iterator();
			while (iter.hasNext()) {
				buffer.write(group.encode((FieldValue) iter.next(), template, context));
			}
		} catch (IOException e) {
		}
//		System.out.print("]");
		return buffer.toByteArray();
	}

	public FieldValue decode(InputStream in, Group template, Context context, boolean present) {
		SequenceValue sequenceValue = new SequenceValue(this);
		FieldValue lengthValue = length.decode(in, template, context, present);
		if (lengthValue == ScalarValue.NULL || lengthValue == null) return null;
		int len = ((IntegerValue)lengthValue).value;
		for (int i = 0; i < len; i++)
			sequenceValue.add((GroupValue) group.decode(in, template, context, present));
		return sequenceValue;
	}

	public Class getValueType() {
		return SequenceValue.class;
	}

	public FieldValue createValue(String value) {
		return new SequenceValue(this);
	}

	public String getTypeName() {
		return "sequence";
	}

	public Group getGroup() {
		return group;
	}

	public boolean hasField(String fieldName) {
		return group.hasField(fieldName);
	}

	public boolean isImplicitLength() {
		return implicitLength;
	}
}
