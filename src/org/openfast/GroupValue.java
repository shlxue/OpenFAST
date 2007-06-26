package org.openfast;

import java.util.Iterator;

import org.openfast.template.Group;
import org.openfast.util.ArrayIterator;

public class GroupValue implements FieldValue {

	protected final FieldValue[] values;
	private final Group group;

	public GroupValue(Group group, FieldValue[] values) {
		if (group == null) throw new NullPointerException();
		this.group = group;
		this.values = values;
	}
	
	public GroupValue(Group group) {
		this(group, new FieldValue[group.getFieldCount()]);
	}

	public Iterator iterator() {
		return new ArrayIterator(values);
	}

	public FieldValue getValue(int fieldIndex) {
		return values[fieldIndex];
	}

	public void setInteger(int fieldIndex, int value) {
		values[fieldIndex] = new IntegerValue(value);
	}

	public int getInteger(int fieldIndex) {
		return ((IntegerValue) values[fieldIndex]).value;
	}

	public String getString(String fieldName) {
		return getValue(fieldName).serialize();
	}

	public int getInteger(String fieldName) {
		return ((IntegerValue) getValue(fieldName)).value;
	}

	public void setFieldValue(int fieldIndex, FieldValue value) {
		values[fieldIndex] = value;
	}

	public void setBitVector(int fieldIndex, BitVector vector) {
		values[fieldIndex] = new BitVectorValue(vector);
	}

	public void setByteVector(int fieldIndex, byte[] bytes) {
		values[fieldIndex] = new ByteVectorValue(bytes);
	}

	public void setDecimal(int fieldIndex, double value) {
		values[fieldIndex] = new DecimalValue(value);
	}

	public void setString(int fieldIndex, String value) {
		values[fieldIndex] = new StringValue(value);
	}

	public void setString(String fieldName, String value) {
		setFieldValue(fieldName, group.getField(fieldName).createValue(value));
	}

	public String getString(int index) {
		return getValue(index).serialize();
	}
	
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null || !(other instanceof GroupValue)) return false;
		return equals((GroupValue) other);
	}
	
	private boolean equals(GroupValue other) {
		if (values.length != other.values.length) return false;
		for (int i=0; i<values.length; i++) {
			if (!values[i].equals(other.values[i]))
				return false;
		}
		return true;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<values.length; i++) {
			builder.append(values[i]).append(", ");
		}
		if (values.length > 0)
			builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

	public String serialize() {
		return "";
	}

	public void setFieldValue(String fieldName, FieldValue value) {
		int index = group.getFieldIndex(fieldName);
		setFieldValue(index, value);
	}

	public GroupValue getGroup(String fieldName) {
		return (GroupValue) getValue(fieldName);
	}
	
	public SequenceValue getSequence(String fieldName) {
		return (SequenceValue) getValue(fieldName);
	}

	public FieldValue getValue(String fieldName) {
		if (!group.hasField(fieldName)) throw new IllegalArgumentException("The field \"" + fieldName + "\" does not exist.");
		return values[group.getFieldIndex(fieldName)];
	}

	public int getFieldCount() {
		return values.length;
	}

	public Group getGroup() {
		return group;
	}

	public void setFieldValue(String fieldName, String value) {
		setFieldValue(fieldName, group.getScalar(fieldName).createValue(value));
	}

	public boolean isDefined(int fieldIndex) {
		return values[fieldIndex] != null;
	}
	
	public boolean isDefined(String fieldName) {
		return getValue(fieldName) != null;
	}

	public double getDouble(String fieldName) {
		return ((DecimalValue) getValue(fieldName)).value;
	}

	public SequenceValue getSequence(int fieldIndex) {
		return (SequenceValue) getValue(fieldIndex);
	}

	public ScalarValue getScalar(int fieldIndex) {
		return (ScalarValue) getValue(fieldIndex);
	}

	public GroupValue getGroup(int fieldIndex) {
		return (GroupValue) getValue(fieldIndex);
	}
}
