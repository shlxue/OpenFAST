package org.openfast;


public class BitVectorValue extends ScalarValue {
	public BitVector value;
	public BitVectorValue(BitVector value) {
		this.value = value;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof BitVectorValue)) return false;
		return equals((BitVectorValue) obj);
	}
	
	public boolean equals(BitVectorValue other)
	{
		return other.value.equals(this.value);
	}

}
