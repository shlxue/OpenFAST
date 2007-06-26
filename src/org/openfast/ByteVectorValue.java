package org.openfast;

public class ByteVectorValue extends ScalarValue {

	public final byte[] value;

	public ByteVectorValue(byte[] value) {
		this.value = value;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ByteVectorValue)) return false;
		return equals((ByteVectorValue) obj);
	}
	
	public boolean equals(ByteVectorValue other) {
		if (this.value.length != other.value.length) return false;
		for (int i=0; i<this.value.length; i++)
			if (this.value[i] != other.value[i])
				return false;
		return true;
	}
}
