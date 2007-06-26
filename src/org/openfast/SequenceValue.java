package org.openfast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openfast.template.Sequence;

public class SequenceValue implements FieldValue {

	private List elements = Collections.EMPTY_LIST;
	private Sequence sequence;

	public SequenceValue(Sequence sequence) {
		if (sequence == null) throw new NullPointerException();
		this.sequence = sequence;
	}
	
	public int getLength() {
		return elements.size();
	}

	public Iterator iterator() {
		return elements.iterator();
	}

	public void add(GroupValue value) {
		if (elements == Collections.EMPTY_LIST)
			elements = new ArrayList();
		elements.add(value);
	}

	public void add(FieldValue[] values) {
		if (elements == Collections.EMPTY_LIST)
			elements = new ArrayList();
		elements.add(new GroupValue(sequence.getGroup(), values));
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null || !(other instanceof SequenceValue))
			return false;
		return equals((SequenceValue) other);
	}

	private boolean equals(SequenceValue other) {
		if (getLength() != other.getLength())
			return false;
		for (int i = 0; i < getLength(); i++) {
			if (!elements.get(i).equals(other.elements.get(i)))
				return false;
		}
		return true;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		Iterator iter = elements.iterator();
		builder.append("[ ");
		while (iter.hasNext()) {
			GroupValue value = (GroupValue) iter.next();
			builder.append('[').append(value).append("] ");
		}
		builder.append("]");
		return builder.toString();
	}

	public String serialize() {
		return "";
	}

	public GroupValue get(int index) {
		return (GroupValue) elements.get(index);
	}

	public Sequence getSequence() {
		return sequence;
	}
}
