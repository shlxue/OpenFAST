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


package org.openfast;

import org.openfast.template.Group;

import org.openfast.util.ArrayIterator;

import java.math.BigDecimal;
import java.util.Iterator;


public class GroupValue implements FieldValue {
    protected final FieldValue[] values;
    private final Group group;

    public GroupValue(Group group, FieldValue[] values) {
        if (group == null) {
            throw new NullPointerException();
        }

        this.group = group;
        this.values = values;
    }

    public GroupValue(Group group) {
        this(group, new FieldValue[group.getFieldCount()]);
    }

    public Iterator iterator() {
        return new ArrayIterator(values);
    }

    public void setInteger(int fieldIndex, int value) {
        values[fieldIndex] = new IntegerValue(value);
    }

    public int getInt(int fieldIndex) {
        return getScalar(fieldIndex).toInt();
    }
    
    public int getInt(String fieldName) {
	    return getScalar(fieldName).toInt();
	}

    public long getLong(int fieldIndex) {
        return getScalar(fieldIndex).toLong();
    }
    
    public long getLong(String fieldName) {
	    return getScalar(fieldName).toLong();
	}

	public byte getByte(int fieldIndex) {
    	return getScalar(fieldIndex).toByte();
    }

	public byte getByte(String fieldName) {
    	return getScalar(fieldName).toByte();
    }

	public short getShort(int fieldIndex) {
    	return getScalar(fieldIndex).toShort();
    }

	public short getShort(String fieldName) {
    	return getScalar(fieldName).toShort();
    }

    public String getString(int index) {
	    return getValue(index).toString();
	}

	public String getString(String fieldName) {
        return getValue(fieldName).toString();
    }

    public double getDouble(int fieldIndex) {
	    return getScalar(fieldIndex).toDouble();
	}

    public double getDouble(String fieldName) {
	    return getScalar(fieldName).toDouble();
	}

    public BigDecimal getBigDecimal(int fieldIndex) {
	    return getScalar(fieldIndex).toBigDecimal();
	}

    public BigDecimal getBigDecimal(String fieldName) {
	    return getScalar(fieldName).toBigDecimal();
	}

	public SequenceValue getSequence(int fieldIndex) {
	    return (SequenceValue) getValue(fieldIndex);
	}

	public SequenceValue getSequence(String fieldName) {
	    return (SequenceValue) getValue(fieldName);
	}
	
	public ScalarValue getScalar(int fieldIndex) {
	    return (ScalarValue) getValue(fieldIndex);
	}

	public ScalarValue getScalar(String fieldName) {
		return (ScalarValue) getValue(fieldName);
	}

	public GroupValue getGroup(int fieldIndex) {
	    return (GroupValue) getValue(fieldIndex);
	}

	public GroupValue getGroup(String fieldName) {
	    return (GroupValue) getValue(fieldName);
	}

	public FieldValue getValue(int fieldIndex) {
	    return values[fieldIndex];
	}

	public FieldValue getValue(String fieldName) {
	    if (!group.hasField(fieldName)) {
	        throw new IllegalArgumentException("The field \"" + fieldName +
	            "\" does not exist.");
	    }
	
	    return values[group.getFieldIndex(fieldName)];
	}

	public Group getGroup() {
	    return group;
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

	public void setByteVector(String fieldName, byte[] bytes) {
		setFieldValue(fieldName, new ByteVectorValue(bytes));
	}

    public void setDecimal(int fieldIndex, double value) {
        values[fieldIndex] = new DecimalValue(value);
    }

	public void setDecimal(String fieldName, double value) {
		setFieldValue(fieldName, new DecimalValue(value));
	}

    public void setString(int fieldIndex, String value) {
        values[fieldIndex] = new StringValue(value);
    }

    public void setString(String fieldName, String value) {
        setFieldValue(fieldName, group.getField(fieldName).createValue(value));
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if ((other == null) || !(other instanceof GroupValue)) {
            return false;
        }

        return equals((GroupValue) other);
    }

    private boolean equals(GroupValue other) {
        if (values.length != other.values.length) {
            return false;
        }

        for (int i = 0; i < values.length; i++) {
            if (!values[i].equals(other.values[i])) {
                return false;
            }
        }

        return true;
    }
    
    public int hashCode() {
    	return values.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            builder.append(values[i]).append(", ");
        }

        if (values.length > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }

        return builder.toString();
    }

    public void setFieldValue(String fieldName, FieldValue value) {
        int index = group.getFieldIndex(fieldName);
        setFieldValue(index, value);
    }

    public int getFieldCount() {
        return values.length;
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
}
