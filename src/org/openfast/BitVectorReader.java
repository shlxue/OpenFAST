package org.openfast;

public class BitVectorReader {

	public static final BitVectorReader NULL = new BitVectorReader(null) {
		public boolean read() {
			throw new IllegalStateException();
		}
	};

	public static final BitVectorReader INFINITE_TRUE = new BitVectorReader(null) {
		public boolean read() {
			return true;
		}
	};
	
	private final BitVector vector;
	private int index=0;

	public BitVectorReader(BitVector vector) {
		this.vector = vector;
	}
	
	public boolean read() {
		return vector.isSet(index++);
	}

	public BitVector getBitVector() {
		return vector;
	}

	public boolean hasMoreBitsSet() {
		return vector.indexOfLastSet() > index;
	}
	
	public String toString() {
		return vector.toString();
	}

	public boolean peek() {
		return vector.isSet(index);
	}
}
