package org.openfast;

public class BitVectorBuilder {

	private final BitVector vector;
	private int index=0;

	public BitVectorBuilder(int size) {
		vector = new BitVector(size);
	}
	
	public void set() {
		vector.set(index);
		index++;
	}
	
	public void skip() {
		index++;
	}

	public BitVector getBitVector() {
		return vector;
	}

	public void setOnValueSkipOnNull(Object value) {
		if (value == null)
			skip();
		else
			set();
	}

	public int getIndex() {
		return index;
	}

}
