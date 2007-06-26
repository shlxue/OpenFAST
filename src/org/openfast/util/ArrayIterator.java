package org.openfast.util;

import java.util.Iterator;

public class ArrayIterator implements Iterator {

	private int position;
	private final Object[] array;

	public ArrayIterator(final Object[] array) {
		this.array = array;
	}

	public boolean hasNext() {
		return position < array.length;
	}

	public Object next() {
		return array[position++];
	}

	public void remove() {
	}

}
